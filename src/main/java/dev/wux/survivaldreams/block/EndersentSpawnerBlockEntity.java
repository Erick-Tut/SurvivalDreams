package dev.wux.survivaldreams.block;

import dev.wux.survivaldreams.entity.Endersent;
import dev.wux.survivaldreams.entity.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.UUID;

public class EndersentSpawnerBlockEntity extends BlockEntity {

    public enum SpawnState {
        WAITING,
        SPAWNED,
        DEFEATED
    }

    private static final int MISSING_GRACE_CHECKS = 3;

    private SpawnState state = SpawnState.WAITING;
    private UUID trackedEndersentUuid;
    private int checkCooldown = 0;
    private int missingChecksInARow = 0;

    public EndersentSpawnerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.ENDERSENT_SPAWNER, pos, blockState);
    }

    public static void serverTick(net.minecraft.world.level.Level level, BlockPos pos, BlockState state, EndersentSpawnerBlockEntity blockEntity) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        blockEntity.checkCooldown--;
        if (blockEntity.checkCooldown > 0) return;
        blockEntity.checkCooldown = 20;

        switch (blockEntity.state) {
            case WAITING -> blockEntity.tryTriggerSpawn(serverLevel, pos);
            case SPAWNED -> blockEntity.checkStillTracked(serverLevel);
            case DEFEATED -> { }
        }
    }

    private void tryTriggerSpawn(ServerLevel serverLevel, BlockPos pos) {
        java.util.List<Player> nearbyPlayers = serverLevel.getEntitiesOfClass(
                Player.class,
                new net.minecraft.world.phys.AABB(pos).inflate(6.0D)
        );

        if (nearbyPlayers.isEmpty()) return;

        Endersent endersent = ModEntityTypes.ENDERSENT.create(serverLevel, net.minecraft.world.entity.EntitySpawnReason.MOB_SUMMONED);
        if (endersent == null) return;

        endersent.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
        endersent.setSpawnerPos(pos.immutable());
        serverLevel.addFreshEntity(endersent);

        this.trackedEndersentUuid = endersent.getUUID();
        this.missingChecksInARow = 0;
        this.state = SpawnState.SPAWNED;
        this.setChanged();
    }

    private void checkStillTracked(ServerLevel serverLevel) {
        if (trackedEndersentUuid == null) {
            return;
        }

        net.minecraft.world.entity.Entity entity = serverLevel.getEntity(trackedEndersentUuid);
        if (entity == null || entity.isRemoved()) {
            missingChecksInARow++;
            if (missingChecksInARow >= MISSING_GRACE_CHECKS) {
                resetToWaiting();
            }
        } else {
            missingChecksInARow = 0;
        }
    }

    private void resetToWaiting() {
        this.state = SpawnState.WAITING;
        this.trackedEndersentUuid = null;
        this.missingChecksInARow = 0;
        this.setChanged();
    }

    public void markDefeated() {
        this.state = SpawnState.DEFEATED;
        this.trackedEndersentUuid = null;
        this.missingChecksInARow = 0;
        this.setChanged();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putString("SpawnState", state.name());
        if (trackedEndersentUuid != null) {
            output.putString("TrackedEndersent", trackedEndersentUuid.toString());
        }
    }

    public SpawnState getState() {
        return this.state;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        this.state = input.getString("SpawnState")
                .map(s -> {
                    try {
                        return SpawnState.valueOf(s);
                    } catch (IllegalArgumentException e) {
                        return SpawnState.WAITING;
                    }
                })
                .orElse(SpawnState.WAITING);

        input.getString("TrackedEndersent")
                .ifPresent(s -> this.trackedEndersentUuid = java.util.UUID.fromString(s));
    }
}