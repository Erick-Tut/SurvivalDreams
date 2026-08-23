package dev.wux.survivaldreams.handler;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.phys.Vec3;

public class GolemSwimHandler {

    private static final double FLOAT_PUSH = 0.06;

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(level -> {
            for (var player : level.players()) {
                var area = player.getBoundingBox().inflate(64.0);
                for (IronGolem golem : level.getEntitiesOfClass(IronGolem.class, area)) {
                    if (golem.isInWater() && !golem.onGround()) {
                        Vec3 motion = golem.getDeltaMovement();
                        if (motion.y < 0.1) {
                            golem.setDeltaMovement(motion.x, Math.min(motion.y + FLOAT_PUSH, 0.15), motion.z);
                        }
                    }
                }
            }
        });
    }
}