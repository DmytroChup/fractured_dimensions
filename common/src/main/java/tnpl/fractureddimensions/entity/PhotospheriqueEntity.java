package tnpl.fractureddimensions.entity;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import tnpl.fractureddimensions.component.DimensionData;
import tnpl.fractureddimensions.entity.projectile.BaseProjectile;
import tnpl.fractureddimensions.entity.projectile.PhotospheriqueRingEntity;
import tnpl.fractureddimensions.registry.ModEntityTypes;
import tnpl.fractureddimensions.registry.ModSounds;
import tnpl.fractureddimensions.worldgen.IslandManager;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class PhotospheriqueEntity extends Monster implements GeoEntity, RangedAttackMob {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    
    public int supernovaCooldown = 0;

    // 0 = Normal, 1 = Charging Supernova, 2 = Releasing Supernova
    private static final EntityDataAccessor<Integer> SUPERNOVA_STATE = SynchedEntityData.defineId(PhotospheriqueEntity.class, EntityDataSerializers.INT);

    public PhotospheriqueEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SUPERNOVA_STATE, 0);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("SupernovaCooldown", this.supernovaCooldown);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.supernovaCooldown = input.getIntOr("SupernovaCooldown", 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && this.supernovaCooldown > 0) {
            this.supernovaCooldown--;
        }
    }

    public int getSupernovaState() {
        return this.entityData.get(SUPERNOVA_STATE);
    }

    public void setSupernovaState(int state) {
        this.entityData.set(SUPERNOVA_STATE, state);
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
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.FLYING_SPEED, 0.4)
                .add(Attributes.SAFE_FALL_DISTANCE, 1000.0)
                .add(Attributes.FALL_DAMAGE_MULTIPLIER, 0.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SupernovaGoal(this));
        this.goalSelector.addGoal(3, new RangedAttackGoal(this, 1.25, 40, 15.0F));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomFlyingGoal(this, 1.0));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void performRangedAttack(LivingEntity target, float pullProgress) {
        if (this.getSupernovaState() != 0) return;

        PhotospheriqueRingEntity ring = new PhotospheriqueRingEntity(ModEntityTypes.PHOTOSPHERIQUE_RING.get(), this.level());
        BaseProjectile.setupAndShoot(ring, this, target, 1.0F, 0.0F);

        if (!this.isSilent()) {
            this.level().playSound(
                    null, this.getX(), this.getY(), this.getZ(),
                    ModSounds.PHOTOSPHERIQUE_SHOOT.get(),
                    this.getSoundSource(), 2.0F,
                    (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F
            );
        }
        
        this.level().addFreshEntity(ring);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        if (this.getSupernovaState() != 0 && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        }
        return super.hurtServer(level, source, damage);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("controller", 5, state -> {
            int novaState = this.getSupernovaState();
            if (novaState == 1) {
                return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("supernova_charge"));
            } else if (novaState == 2) {
                return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("supernova_release"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    static class SupernovaGoal extends Goal {
        private final PhotospheriqueEntity mob;
        private int tickCount;

        public SupernovaGoal(PhotospheriqueEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            if (this.mob.supernovaCooldown > 0) {
                return false;
            }
            LivingEntity target = this.mob.getTarget();
            if (target != null && target.isAlive()) {
                double distance = this.mob.distanceToSqr(target);
                return distance < 144.0; // 12 blocks activation radius
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.tickCount > 0;
        }

        @Override
        public void start() {
            this.tickCount = 60; // 60 ticks charge (3 sec)
            this.mob.setSupernovaState(1);
            this.mob.getNavigation().stop();
            
            if (!this.mob.isSilent()) {
                this.mob.level().playSound(null, this.mob.getX(), this.mob.getY(), this.mob.getZ(), 
                    ModSounds.PHOTOSPHERIQUE_CHARGE.get(), this.mob.getSoundSource(), 2.0F, 1.0F);
            }
        }

        @Override
        public void tick() {
            this.tickCount--;
            this.mob.getNavigation().stop();

            if (this.tickCount > 20 && this.mob.level().isClientSide()) {
                if (this.mob.random.nextFloat() < 0.5F) {
                    this.mob.level().addParticle(ParticleTypes.REVERSE_PORTAL, 
                            this.mob.getRandomX(1.5), this.mob.getRandomY(), this.mob.getRandomZ(1.5), 
                            0.0, 0.0, 0.0);
                }
            }

            // Trigger explosion when exactly 20 ticks remain (3 seconds into the charge)
            if (this.tickCount == 20) {
                this.mob.setSupernovaState(2);
                
                if (!this.mob.isSilent()) {
                    this.mob.level().playSound(null, this.mob.getX(), this.mob.getY(), this.mob.getZ(), 
                       ModSounds.PHOTOSPHERIQUE_SUPERNOVA.get(), this.mob.getSoundSource(), 3.0F, 1.0F);
                }

                if (!this.mob.level().isClientSide()) {
                    AABB aabb = this.mob.getBoundingBox().inflate(15.0);
                    List<LivingEntity> entities = this.mob.level().getEntitiesOfClass(LivingEntity.class, aabb);
                    
                    for (LivingEntity entity : entities) {
                        if (entity != this.mob) {
                            double distanceSq = this.mob.distanceToSqr(entity);
                            if (distanceSq < 225.0) { // 15 blocks
                                // Line-of-sight check to allow players to hide behind walls
                                if (this.mob.hasLineOfSight(entity)) {

                                    entity.hurtServer((ServerLevel) this.mob.level(),
                                        this.mob.damageSources().mobAttack(this.mob), 2.0F);

                                    double dx = entity.getX() - this.mob.getX();
                                    double dz = entity.getZ() - this.mob.getZ();
                                    double horizontalDistanceSq = dx * dx + dz * dz;
                                            
                                    double strength = 8.0 * (1.0 - (Math.sqrt(horizontalDistanceSq) / 15.0));
                                    strength = Math.max(0.1, strength);
                                            
                                    if (horizontalDistanceSq < 0.0001) {
                                        dx = (this.mob.random.nextFloat() - 0.5F) * 0.1;
                                        dz = (this.mob.random.nextFloat() - 0.5F) * 0.1;
                                    }
                                    
                                    Vec3 pushDir = new Vec3(dx, 0, dz).normalize();

                                    entity.setDeltaMovement(entity.getDeltaMovement().add(
                                            pushDir.x * strength, 
                                            0.8, 
                                            pushDir.z * strength
                                    ));

                                    entity.hurtMarked = true;
                                }
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void stop() {
            this.mob.setSupernovaState(0);
            this.mob.supernovaCooldown = 300; // 15 seconds cooldown
        }
    }

    public static boolean checkPhotospheriqueSpawnRules(
            EntityType<PhotospheriqueEntity> type,
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
                    
                    AABB box = new AABB(pos).inflate(64.0);
                    List<PhotospheriqueEntity> nearby = levelAccessor.getEntitiesOfClass(PhotospheriqueEntity.class, box);
                    
                    if (nearby.size() >= 3) {
                        return false; 
                    }

                    return levelAccessor.getDifficulty() != Difficulty.PEACEFUL &&
                           checkMobSpawnRules(type, levelAccessor, spawnReason, pos, random);
                }
            }
        }
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

}
