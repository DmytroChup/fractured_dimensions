package tnpl.fractureddimensions.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class DysonProjectileEntity extends BaseProjectile {
    private int life = 0;

    public DysonProjectileEntity(EntityType<? extends Projectile> entityType, Level level) {
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
            this.level().addParticle(ParticleTypes.CRIT, 
                    this.getRandomX(0.2), this.getRandomY(), this.getRandomZ(0.2),
                    0.0, 0.0, 0.0);
            this.level().addParticle(ParticleTypes.SMOKE, 
                    this.getRandomX(0.2), this.getRandomY(), this.getRandomZ(0.2),
                    0.0, 0.0, 0.0);
        } else {
            if (this.life > 100) {
                this.discard();
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
            result.getEntity().hurtServer(serverLevel, this.damageSources().mobProjectile(this, livingOwner), 3.0F);
        }
        if (!this.level().isClientSide()) {
            this.discard();
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide()) {
            this.discard();
        }
    }
}
