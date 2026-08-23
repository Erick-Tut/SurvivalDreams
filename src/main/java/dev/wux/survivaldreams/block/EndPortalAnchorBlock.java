package dev.wux.survivaldreams.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Set;

public class EndPortalAnchorBlock extends Block {

    public EndPortalAnchorBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }

        if (serverPlayer.level().dimension() != Level.OVERWORLD) {
            serverPlayer.displayClientMessage(
                    Component.translatable("block.survival-dreams.end_portal_anchor.wrong_dimension"),
                    true
            );
            return InteractionResult.FAIL;
        }

        ServerLevel endLevel = serverPlayer.level().getServer().getLevel(Level.END);
        if (endLevel == null) {
            return InteractionResult.FAIL;
        }

        teleportToEnd(serverPlayer, endLevel);
        return InteractionResult.SUCCESS;
    }

    private void teleportToEnd(ServerPlayer player, ServerLevel endLevel) {
        BlockPos endSpawn = ServerLevel.END_SPAWN_POINT;

        player.teleportTo(endLevel,
                endSpawn.getX() + 0.5D, endSpawn.getY(), endSpawn.getZ() + 0.5D,
                Set.of(), 0.0F, 0.0F, false);

        endLevel.playSound(null, endSpawn, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 1.0F, 1.0F);
        endLevel.sendParticles(ParticleTypes.PORTAL,
                endSpawn.getX() + 0.5, endSpawn.getY() + 1.0, endSpawn.getZ() + 0.5,
                32, 0.5, 1.0, 0.5, 0.3);
    }
}