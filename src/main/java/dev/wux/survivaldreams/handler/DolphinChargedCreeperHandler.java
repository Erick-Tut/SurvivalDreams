package dev.wux.survivaldreams.handler;

import dev.wux.survivaldreams.mixin.CreeperPowerMixin;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.dolphin.Dolphin;
import net.minecraft.world.entity.monster.Creeper;

public class DolphinChargedCreeperHandler {

    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (!(entity instanceof Dolphin dolphin)) return;
            if (!dolphin.getPassengers().isEmpty()) return;
            if (!(world instanceof ServerLevel serverLevel)) return;

            Creeper creeper = EntityType.CREEPER.create(serverLevel, EntitySpawnReason.MOB_SUMMONED);
            if (creeper == null) return;

            creeper.setPos(dolphin.getX(), dolphin.getY() + 1.0, dolphin.getZ());

            creeper.getEntityData().set(CreeperPowerMixin.survivalDreams$getDataIsPowered(), true);

            serverLevel.addFreshEntity(creeper);
            creeper.startRiding(dolphin);
        });
    }
}