package tnpl.fractureddimensions.entity.projectile;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class PhotospheriqueRingEntity extends BaseProjectile implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private int life = 0;

    public PhotospheriqueRingEntity(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    public void tick() {
        super.tick();
        this.life++;

        if (this.level().isClientSide()) {
            this.level().addParticle(net.minecraft.core.particles.ParticleTypes.END_ROD, 
                    this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5),
                    0.0, 0.0, 0.0);
        } else {
            if (this.life > 40) {
                if (this.getOwner() != null && this.getOwner().isAlive()) {
                    Vec3 targetPos = this.getOwner().position().add(0, this.getOwner().getBbHeight() * 0.3F, 0);
                    Vec3 vec3 = targetPos.subtract(this.position());
                    this.setDeltaMovement(vec3.normalize().scale(0.8));
                    if (vec3.length() < 1.5) {
                        this.discard();
                    }
                } else {
                    this.discard();
                }
            } else {
                if (this.getDeltaMovement().lengthSqr() < 0.1) {
                    this.setDeltaMovement(this.getDeltaMovement().normalize().scale(1.0));
                }
            }
        }

        this.applyMovementAndCollision();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (result.getEntity() != this.getOwner() && this.level() instanceof ServerLevel serverLevel) {
            Entity owner = this.getOwner();
            LivingEntity livingOwner = owner instanceof LivingEntity ? (LivingEntity) owner : null;
            result.getEntity().hurtServer(serverLevel, this.damageSources().mobProjectile(this, livingOwner), 5.0F);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("controller", 0,
                state -> state.setAndContinue(RawAnimation.begin().thenLoop("spin"))));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
