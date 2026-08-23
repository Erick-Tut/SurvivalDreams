package dev.wux.survivaldreams.handler;

import dev.wux.survivaldreams.SurvivalDreams;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SpiderEffectsHandler {

    private static final int INFINITE_DURATION = Integer.MAX_VALUE;

    public static void register() {
        SurvivalDreams.LOGGER.info("Registrando SpiderEffectsHandler");

        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (!(entity instanceof Spider spider)) return;
            if (!(world instanceof ServerLevel serverLevel)) return;

            SurvivalDreams.LOGGER.info("Araña detectada, aplicando efectos: " + spider.getType());

            RandomSource random = serverLevel.random;

            spider.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, INFINITE_DURATION, 0, true, false));

            applyRandomTier(spider, random, MobEffects.SPEED, 4);
            applyRandomTier(spider, random, MobEffects.STRENGTH, 3);
            applyRandomTier(spider, random, MobEffects.ABSORPTION, 4);
        });

        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, damageSource, baseDamageTaken, damageTaken, blocked) -> {
            SurvivalDreams.LOGGER.info("AFTER_DAMAGE disparado: victima=" + entity.getType() + " atacante=" + damageSource.getEntity());

            if (!(entity instanceof Player player)) return;
            var attacker = damageSource.getEntity();
            if (!(attacker instanceof Spider spider)) return;
            if (!(player.level() instanceof ServerLevel serverLevel)) return;

            SurvivalDreams.LOGGER.info("¡Colocando telaraña detrás del jugador!");
            trapPlayerInWeb(serverLevel, player, spider);
        });
    }

    private static void applyRandomTier(Spider entity, RandomSource random, net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effect, int maxTier) {
        int totalWeight = 0;
        int[] weights = new int[maxTier + 1];
        for (int i = 0; i <= maxTier; i++) {
            weights[i] = (int) Math.pow(2, maxTier - i);
            totalWeight += weights[i];
        }

        int roll = random.nextInt(totalWeight);
        int cumulative = 0;
        int chosenTier = 0;
        for (int i = 0; i <= maxTier; i++) {
            cumulative += weights[i];
            if (roll < cumulative) {
                chosenTier = i;
                break;
            }
        }

        if (chosenTier == 0) return;

        int amplifier = chosenTier - 1;
        entity.addEffect(new MobEffectInstance(effect, INFINITE_DURATION, amplifier, true, false));
    }

    private static void trapPlayerInWeb(ServerLevel level, Player player, Spider spider) {
        double dx = player.getX() - spider.getX();
        double dz = player.getZ() - spider.getZ();
        double length = Math.sqrt(dx * dx + dz * dz);

        if (length < 0.0001) {
            dx = -player.getLookAngle().x;
            dz = -player.getLookAngle().z;
            length = Math.sqrt(dx * dx + dz * dz);
        }

        dx /= length;
        dz /= length;

        BlockPos behindPlayer = BlockPos.containing(
                player.getX() + dx,
                player.getY(),
                player.getZ() + dz
        );

        BlockState currentState = level.getBlockState(behindPlayer);
        if (currentState.isAir() || currentState.canBeReplaced()) {
            level.setBlock(behindPlayer, Blocks.COBWEB.defaultBlockState(), 3);
        }
    }
}