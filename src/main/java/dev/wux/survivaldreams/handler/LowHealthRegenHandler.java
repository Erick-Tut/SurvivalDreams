package dev.wux.survivaldreams.handler;

import dev.wux.survivaldreams.attachments.ModAttachments;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class LowHealthRegenHandler {

    private static final List<Holder<MobEffect>> RANDOM_EFFECTS = List.of(
            MobEffects.ABSORPTION,
            MobEffects.REGENERATION,
            MobEffects.INFESTED,
            MobEffects.STRENGTH,
            MobEffects.FIRE_RESISTANCE
    );

    private static final int EFFECT_DURATION_TICKS = 400;
    private static final int MIN_COOLDOWN_TICKS = 1200;
    private static final int MAX_COOLDOWN_TICKS = 2400;

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, damageSource, amount) -> {
            handlePossibleLowHealth(entity, amount);
            return true;
        });
    }

    private static void handlePossibleLowHealth(LivingEntity entity, float incomingDamage) {
        if (!(entity instanceof Mob mob)) return;
        if (entity instanceof Player) return;
        if (!(entity.level() instanceof ServerLevel serverLevel)) return;

        MobCategory category = mob.getType().getCategory();
        boolean isHostileOrNeutral = category == MobCategory.MONSTER;
        if (!isHostileOrNeutral) {
            isHostileOrNeutral = mob.getTarget() != null || mob.getLastHurtByMob() != null;
        }
        if (!isHostileOrNeutral) return;

        float healthAfterDamage = mob.getHealth() - incomingDamage;
        float maxHealth = mob.getMaxHealth();
        if (healthAfterDamage <= 0) return;

        if (healthAfterDamage > maxHealth * 0.50F) return;

        long currentTime = serverLevel.getGameTime();
        long lastTrigger = mob.getAttachedOrElse(ModAttachments.LOW_HEALTH_REGEN_COOLDOWN, 0L);

        if (currentTime < lastTrigger) return;

        Holder<MobEffect> chosenEffect = RANDOM_EFFECTS.get(serverLevel.random.nextInt(RANDOM_EFFECTS.size()));
        mob.addEffect(new MobEffectInstance(chosenEffect, EFFECT_DURATION_TICKS, 3));

        int cooldownDuration = MIN_COOLDOWN_TICKS + serverLevel.random.nextInt(MAX_COOLDOWN_TICKS - MIN_COOLDOWN_TICKS);
        mob.setAttached(ModAttachments.LOW_HEALTH_REGEN_COOLDOWN, currentTime + cooldownDuration);
    }
}