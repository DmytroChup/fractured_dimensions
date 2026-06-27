package tnpl.fractureddimensions.entity;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import tnpl.fractureddimensions.component.DimensionData;
import tnpl.fractureddimensions.entity.projectile.BaseProjectile;
import tnpl.fractureddimensions.entity.projectile.DysonProjectileEntity;
import tnpl.fractureddimensions.registry.ModBlocks;
import tnpl.fractureddimensions.registry.ModEntityTypes;
import tnpl.fractureddimensions.registry.ModSounds;
import tnpl.fractureddimensions.worldgen.IslandManager;

import java.util.List;
import java.util.Map;

public class DysonDroneEntity extends Monster implements GeoEntity, RangedAttackMob {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    // 0 = Idle, 1 = Attacking (opened plates)
    private static final EntityDataAccessor<Integer> ATTACK_STATE = SynchedEntityData.defineId(DysonDroneEntity.class, EntityDataSerializers.INT);

    public DysonDroneEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACK_STATE, 0);
    }

    public int getAttackState() {
        return this.entityData.get(ATTACK_STATE);
    }

    public void setAttackState(int state) {
        this.entityData.set(ATTACK_STATE, state);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        return navigation;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.FLYING_SPEED, 0.3)
                .add(Attributes.ARMOR, 5.0)
                .add(Attributes.SAFE_FALL_DISTANCE, 1000.0)
                .add(Attributes.FALL_DAMAGE_MULTIPLIER, 0.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new DroneRangedAttackGoal(this, 1.0, 5, 15.0F));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomFlyingGoal(this, 1.0));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void performRangedAttack(LivingEntity target, float pullProgress) {
        DysonProjectileEntity projectile = new DysonProjectileEntity(ModEntityTypes.DYSON_PROJECTILE.get(), this.level());
        BaseProjectile.setupAndShoot(projectile, this, target, 2.5F, 0.0F);

        if (!this.isSilent()) {
            this.playSound(ModSounds.DYSON_DRONE_SHOOT.get(), 2.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
        }
        
        this.level().addFreshEntity(projectile);
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("controller", 5, state -> {
            if (this.getAttackState() == 1) {
                return state.setAndContinue(RawAnimation.begin().thenPlay("attack"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    static class DroneRangedAttackGoal extends RangedAttackGoal {
        private final DysonDroneEntity drone;
        private final float attackRadiusSqr;
        
        public DroneRangedAttackGoal(DysonDroneEntity drone, double speedModifier, int attackInterval, float attackRadius) {
            super(drone, speedModifier, attackInterval, attackRadius);
            this.drone = drone;
            this.attackRadiusSqr = attackRadius * attackRadius;
        }

        @Override
        public void tick() {
            super.tick();
            
            LivingEntity target = this.drone.getTarget();
            if (target != null) {
                double distanceToTarget = this.drone.distanceToSqr(target.getX(), target.getY(), target.getZ());
                boolean canSee = this.drone.getSensing().hasLineOfSight(target);
                
                if (distanceToTarget <= this.attackRadiusSqr && canSee) {
                    this.drone.setAttackState(1);
                } else {
                    this.drone.setAttackState(0);
                }
            } else {
                this.drone.setAttackState(0);
            }
        }
        
        @Override
        public void stop() {
            super.stop();
            this.drone.setAttackState(0);
        }
    }

    public static boolean checkDroneSpawnRules(
            EntityType<DysonDroneEntity> type,
            ServerLevelAccessor levelAccessor,
            EntitySpawnReason spawnReason,
            BlockPos pos,
            RandomSource random
    ) {
        if (levelAccessor instanceof ServerLevel serverLevel) {
            IslandManager manager = IslandManager.getVoidInstance();
            if (manager == null) manager = IslandManager.get(serverLevel);

            Map.Entry<BlockPos, IslandManager.ActiveIsland> island = manager.findIslandAt(pos.getX(), pos.getZ());
            if (island != null) {
                DimensionData data = island.getValue().data();
                if (data.type() == 0 && data.variant() == 0) {
                    // Check nearby entities first to save performance
                    AABB box = new AABB(pos).inflate(32.0);
                    List<DysonDroneEntity> nearby = levelAccessor.getEntitiesOfClass(DysonDroneEntity.class, box);
                    
                    // Max 3 drones per wreckage area
                    if (nearby.size() >= 3) {
                        return false; 
                    }

                    boolean nearWreckage = false;
                    for (int i = 0; i < 40; i++) {
                        BlockPos checkPos = pos.offset(random.nextInt(31) - 15, random.nextInt(31) - 15, random.nextInt(31) - 15);
                        if (levelAccessor.getBlockState(checkPos).is(ModBlocks.DYSON_HULL.get())) {
                            nearWreckage = true;
                            break;
                        }
                    }
                    
                    if (!nearWreckage) return false;

                    return levelAccessor.getDifficulty() != Difficulty.PEACEFUL &&
                           checkMobSpawnRules(type, levelAccessor, spawnReason, pos, random);
                }
            }
        }
        return false;
    }
}
