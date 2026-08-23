package dev.wux.survivaldreams.handler;

import dev.wux.survivaldreams.SurvivalDreams;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.golem.IronGolem;
import dev.wux.survivaldreams.entity.Firesnowgolem;

public class RaidGolemDeathHandler {

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof IronGolem || entity instanceof Firesnowgolem) {
                RaidGolemTracker.removeGolem(entity.getUUID());
                SurvivalDreams.LOGGER.info(
                        "[RaidGolemDeathHandler] Golem removido del tracker por muerte real: "
                                + entity.getType() + " uuid=" + entity.getUUID());
            }
        });
    }
}