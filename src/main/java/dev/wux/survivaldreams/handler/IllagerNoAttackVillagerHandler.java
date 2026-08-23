package dev.wux.survivaldreams.handler;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.villager.Villager;

public class IllagerNoAttackVillagerHandler {

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(level -> {
            for (var player : level.players()) {
                var area = player.getBoundingBox().inflate(48.0);
                for (Mob mob : level.getEntitiesOfClass(Mob.class, area)) {
                    if (!isIllager(mob)) continue;
                    if (mob.getTarget() instanceof Villager) {
                        mob.setTarget(null);
                    }
                }
            }
        });
    }

    private static boolean isIllager(Mob mob) {
        String name = mob.getClass().getSimpleName();
        return name.equals("Pillager")
                || name.equals("Vindicator")
                || name.equals("Evoker")
                || name.equals("Illusioner")
                || name.equals("Vex")
                || name.equals("Ravager")
                || name.equals("PillagerCaptain");
    }
}