package dev.wux.survivaldreams.handler;

import dev.wux.survivaldreams.mixin.MobGoalSelectorAccessor;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.feline.Ocelot;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.npc.villager.Villager;

import java.util.ArrayList;
import java.util.List;

public class FearRemovalHandler {

    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (!(world instanceof ServerLevel)) return;
            if (!(entity instanceof Mob mob)) return;

            boolean isTarget = mob instanceof Spider
                    || mob instanceof Skeleton
                    || mob instanceof Creeper
                    || mob instanceof Phantom
                    || mob instanceof Piglin
                    || mob instanceof Cat
                    || mob instanceof Ocelot
                    || mob instanceof Villager;

            if (!isTarget) return;

            removeAvoidGoals(mob);

            if (mob instanceof Piglin) {
                removePiglinMalus(mob);
            }
        });
    }

    private static void removeAvoidGoals(Mob mob) {
        MobGoalSelectorAccessor accessor = (MobGoalSelectorAccessor) mob;

        List<Goal> toRemove = new ArrayList<>();
        for (WrappedGoal wrapped : accessor.survivalDreams$getGoalSelector().getAvailableGoals()) {
            if (wrapped.getGoal() instanceof AvoidEntityGoal) {
                toRemove.add(wrapped.getGoal());
            }
        }

        for (Goal goal : toRemove) {
            accessor.survivalDreams$getGoalSelector().removeGoal(goal);
        }
    }

    private static void removePiglinMalus(Mob mob) {
        mob.setPathfindingMalus(PathType.DAMAGE_OTHER, 0.0F);
        mob.setPathfindingMalus(PathType.DANGER_OTHER, 0.0F);
    }
}