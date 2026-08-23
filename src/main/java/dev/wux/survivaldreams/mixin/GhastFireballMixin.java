package dev.wux.survivaldreams.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.projectile.hurtingprojectile.DragonFireball;
import net.minecraft.world.entity.projectile.hurtingprojectile.LargeFireball;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.world.entity.monster.Ghast$GhastShootFireballGoal")
public abstract class GhastFireballMixin {

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"
            )
    )
    private boolean survivalDreams$replaceFireball(Level level, Entity entity) {
        if (entity instanceof LargeFireball largeFireball) {
            Entity ownerEntity = largeFireball.getOwner();
            LivingEntity shooter = (ownerEntity instanceof LivingEntity living) ? living : null;

            DragonFireball dragonFireball = new DragonFireball(
                    level,
                    shooter,
                    largeFireball.getDeltaMovement().normalize()
            );
            dragonFireball.setPos(largeFireball.getX(), largeFireball.getY(), largeFireball.getZ());

            return level.addFreshEntity(dragonFireball);
        }

        return level.addFreshEntity(entity);
    }
}