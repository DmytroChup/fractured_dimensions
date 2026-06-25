package tnpl.fractureddimensions.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class BaseProjectile extends Projectile {
    protected BaseProjectile(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    protected void applyMovementAndCollision() {
        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitresult.getType() != HitResult.Type.MISS) {
            this.hitTargetOrDeflectSelf(hitresult);
        }
        
        Vec3 vec3 = this.getDeltaMovement();
        this.setPos(this.getX() + vec3.x, this.getY() + vec3.y, this.getZ() + vec3.z);
    }

    public static void setupAndShoot(
            Projectile projectile,
            LivingEntity shooter,
            LivingEntity target,
            float velocity,
            float inaccuracy)
    {
        projectile.setOwner(shooter);
        projectile.setPos(shooter.getX(), shooter.getY() + shooter.getBbHeight() / 2.0F, shooter.getZ());
        
        double xd = target.getX() - shooter.getX();
        double yd = target.getY(0.3333333333333333) - projectile.getY();
        double zd = target.getZ() - shooter.getZ();
        
        projectile.shoot(xd, yd, zd, velocity, inaccuracy);
    }
}
