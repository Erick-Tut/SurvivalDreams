package dev.wux.survivaldreams.handler;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;

public class GolemHostileHandler {

    private static final double DETECTION_RANGE = 24.0;

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(level -> {
            for (var player : level.players()) {
                var area = player.getBoundingBox().inflate(DETECTION_RANGE);
                for (IronGolem golem : level.getEntitiesOfClass(IronGolem.class, area)) {
                    var currentTarget = golem.getTarget();
                    boolean isRegularHostile = currentTarget instanceof net.minecraft.world.entity.monster.Enemy
                            && !(currentTarget instanceof net.minecraft.world.entity.raid.Raider);
                    if (isRegularHostile) {
                        golem.setTarget(null);
                        continue;
                    }

                    if (golem.getTarget() != null) continue;

                    var raidGroup = RaidGolemTracker.getGroupForGolem(golem.getUUID());
                    if (raidGroup != null) {
                        double distSqrToCenter = golem.blockPosition().distSqr(raidGroup.center);
                        if (distSqrToCenter > 9.0 && golem.tickCount % 40 == 0) {
                            golem.getNavigation().moveTo(
                                    raidGroup.center.getX() + 0.5,
                                    raidGroup.center.getY(),
                                    raidGroup.center.getZ() + 0.5,
                                    1.0
                            );
                        }
                    }

                    Player nearestPlayer = level.getNearestPlayer(golem, DETECTION_RANGE);
                    if (nearestPlayer != null && !nearestPlayer.isCreative() && !nearestPlayer.isSpectator()) {
                        golem.setTarget(nearestPlayer);
                        continue;
                    }

                    Villager nearestVillager = level.getEntitiesOfClass(
                            Villager.class,
                            golem.getBoundingBox().inflate(DETECTION_RANGE)
                    ).stream().min((v1, v2) ->
                            Double.compare(golem.distanceToSqr(v1), golem.distanceToSqr(v2))
                    ).orElse(null);

                    if (nearestVillager != null) {
                        golem.setTarget(nearestVillager);
                    }
                }
            }

            for (var player : level.players()) {
                var area = player.getBoundingBox().inflate(DETECTION_RANGE);
                for (net.minecraft.world.entity.monster.Monster hostile : level.getEntitiesOfClass(net.minecraft.world.entity.monster.Monster.class, area)) {
                    if (hostile instanceof net.minecraft.world.entity.raid.Raider) continue;
                    if (hostile.getTarget() instanceof IronGolem) {
                        hostile.setTarget(null);
                    }
                }
            }
        });
    }
}