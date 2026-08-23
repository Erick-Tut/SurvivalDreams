package dev.wux.survivaldreams.handler;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RaidGolemTracker {

    public static class Group {
        public BlockPos center;
        public final Set<UUID> golemIds = new HashSet<>();

        public Group(BlockPos center) {
            this.center = center;
        }
    }

    public static final List<Group> activeGroups = new ArrayList<>();

    private static final Map<UUID, Group> golemToGroup = new HashMap<>();

    public static Group findOrCreateNear(BlockPos pos, double radius) {
        cleanupEmptyGroups();

        Group nearest = findNear(pos, radius);
        if (nearest != null) return nearest;

        Group g = new Group(pos);
        activeGroups.add(g);
        return g;
    }

    public static Group findNear(BlockPos pos, double radius) {
        cleanupEmptyGroups();

        Group best = null;
        double bestDistSqr = Double.MAX_VALUE;

        for (Group g : activeGroups) {
            double distSqr = g.center.distSqr(pos);
            if (distSqr <= radius * radius && distSqr < bestDistSqr) {
                best = g;
                bestDistSqr = distSqr;
            }
        }

        return best;
    }

    public static void addGolem(Group group, UUID golemId) {
        group.golemIds.add(golemId);
        golemToGroup.put(golemId, group);
    }

    public static void removeGolem(UUID golemId) {
        Group group = golemToGroup.remove(golemId);
        if (group != null) {
            group.golemIds.remove(golemId);
        }
        cleanupEmptyGroups();
    }

    public static Group getGroupForGolem(UUID golemId) {
        return golemToGroup.get(golemId);
    }

    private static void cleanupEmptyGroups() {
        activeGroups.removeIf(g -> {
            boolean empty = g.golemIds.isEmpty();
            if (empty) {
                golemToGroup.values().removeIf(group -> group == g);
            }
            return empty;
        });
    }
}