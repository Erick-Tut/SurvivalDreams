package dev.wux.survivaldreams.handler;

import dev.wux.survivaldreams.entity.Firesnowgolem;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

import java.util.HashMap;
import java.util.Map;

public class BellGolemGlowHandler {

    private static final double DETECTION_RADIUS = 150.0;
    private static final int GLOW_DURATION = 200;

    private static final double FALLBACK_RADIUS = 24.0;

    private static final int REQUIRED_HITS = 3;
    private static final long HIT_WINDOW_MS = 3000L;

    private static final Map<BlockPos, ClickState> clickStates = new HashMap<>();

    private static class ClickState {
        int count;
        long lastClickTime;
    }

    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClientSide()) return net.minecraft.world.InteractionResult.PASS;
            if (!(world instanceof ServerLevel serverLevel)) return net.minecraft.world.InteractionResult.PASS;

            var state = world.getBlockState(hitResult.getBlockPos());
            if (!state.is(Blocks.BELL)) return net.minecraft.world.InteractionResult.PASS;

            BlockPos bellPos = hitResult.getBlockPos();
            long now = System.currentTimeMillis();

            ClickState clickState = clickStates.computeIfAbsent(bellPos, pos -> new ClickState());

            if (now - clickState.lastClickTime > HIT_WINDOW_MS) {
                clickState.count = 0;
            }

            clickState.count++;
            clickState.lastClickTime = now;

            dev.wux.survivaldreams.SurvivalDreams.LOGGER.info(
                    "Campana tocada en " + bellPos + " (" + clickState.count + "/" + REQUIRED_HITS + ")");

            if (clickState.count < REQUIRED_HITS) {
                return net.minecraft.world.InteractionResult.PASS;
            }

            clickState.count = 0;

            dev.wux.survivaldreams.SurvivalDreams.LOGGER.info(
                    "Campana activada en " + bellPos + ", grupos activos: " + RaidGolemTracker.activeGroups.size());

            var group = RaidGolemTracker.findNear(bellPos, DETECTION_RADIUS);

            if (group == null) {
                dev.wux.survivaldreams.SurvivalDreams.LOGGER.info(
                        "No se encontró ningún grupo registrado, buscando golems directamente en el mundo...");

                AABB area = new AABB(
                        bellPos.getX() - FALLBACK_RADIUS, bellPos.getY() - FALLBACK_RADIUS, bellPos.getZ() - FALLBACK_RADIUS,
                        bellPos.getX() + FALLBACK_RADIUS, bellPos.getY() + FALLBACK_RADIUS, bellPos.getZ() + FALLBACK_RADIUS
                );

                int glowed = 0;
                for (IronGolem golem : serverLevel.getEntitiesOfClass(IronGolem.class, area)) {
                    golem.addEffect(new MobEffectInstance(MobEffects.GLOWING, GLOW_DURATION, 0));
                    glowed++;
                }
                for (Firesnowgolem golem : serverLevel.getEntitiesOfClass(Firesnowgolem.class, area)) {
                    golem.addEffect(new MobEffectInstance(MobEffects.GLOWING, GLOW_DURATION, 0));
                    glowed++;
                }

                dev.wux.survivaldreams.SurvivalDreams.LOGGER.info(
                        "Glowing aplicado a " + glowed + " golems (busqueda directa).");
            } else {
                dev.wux.survivaldreams.SurvivalDreams.LOGGER.info(
                        "Grupo encontrado, centro=" + group.center + ", golems registrados=" + group.golemIds.size());

                int glowed = 0;
                for (var uuid : group.golemIds) {
                    Entity entity = serverLevel.getEntity(uuid);
                    if (entity instanceof LivingEntity living) {
                        living.addEffect(new MobEffectInstance(MobEffects.GLOWING, GLOW_DURATION, 0));
                        glowed++;
                    }
                }
                dev.wux.survivaldreams.SurvivalDreams.LOGGER.info("Glowing aplicado a " + glowed + " golems.");
            }

            return net.minecraft.world.InteractionResult.PASS;
        });
    }
}