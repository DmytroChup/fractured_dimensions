package tnpl.fractureddimensions.entity;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;
import net.minecraft.sounds.SoundEvents;
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
import tnpl.fractureddimensions.entity.projectile.PhotospheriqueRingEntity;
import tnpl.fractureddimensions.registry.ModEntityTypes;

public class PhotospheriqueEntity extends Monster implements GeoEntity, RangedAttackMob {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public PhotospheriqueEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
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
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.25, 40, 15.0F));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomFlyingGoal(this, 1.0));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void performRangedAttack(LivingEntity target, float pullProgress) {
        PhotospheriqueRingEntity ring = new PhotospheriqueRingEntity(ModEntityTypes.PHOTOSPHERIQUE_RING.get(), this.level());
        ring.setOwner(this);
        ring.setPos(this.getX(), this.getY() + this.getBbHeight() / 2.0F, this.getZ());
        
        double xd = target.getX() - this.getX();
        double yd = target.getY(0.3333333333333333) - ring.getY();
        double zd = target.getZ() - this.getZ();
        
        ring.shoot(xd, yd, zd, 1.0F, 0.0F);

        if (!this.isSilent()) {
            this.level().playSound(
                    null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.AMETHYST_BLOCK_RESONATE,
                    this.getSoundSource(), 2.0F,
                    (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F
            );
        }
        
        this.level().addFreshEntity(ring);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("controller", 0,
                state -> state.setAndContinue(RawAnimation.begin().thenLoop("walk"))));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
