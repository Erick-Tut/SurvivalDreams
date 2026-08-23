package dev.wux.survivaldreams.handler;

import dev.wux.survivaldreams.entity.ModEntityTypes;
import dev.wux.survivaldreams.entity.Wildfire;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.monster.Blaze;

public class WildfireSpawnerHandler {

    public static final String NO_CONVERT_TAG = "survivaldreams_no_wildfire_convert";

    private static final float WILDFIRE_CHANCE = 0.08F;

    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (!(world instanceof ServerLevel serverLevel)) return;
            if (!(entity instanceof Blaze blaze) || entity.getClass() != Blaze.class) return;

            if (blaze.getTags().contains(NO_CONVERT_TAG)) return;

            if (serverLevel.random.nextFloat() >= WILDFIRE_CHANCE) return;

            Wildfire wildfire = ModEntityTypes.WILDFIRE.create(serverLevel, EntitySpawnReason.SPAWNER);
            if (wildfire == null) return;

            wildfire.setPos(blaze.getX(), blaze.getY(), blaze.getZ());
            wildfire.setYRot(blaze.getYRot());

            blaze.discard();
            serverLevel.addFreshEntity(wildfire);
        });
    }
}