package dev.wux.survivaldreams.block;

import dev.wux.survivaldreams.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.Direction;

public class KeyholeBlock extends Block {


    public static final BooleanProperty UNLOCKED = BooleanProperty.create("unlocked");

    private static final int SEARCH_RADIUS = 8;

    public KeyholeBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(UNLOCKED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UNLOCKED);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                          Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (state.getValue(UNLOCKED)) {
            return InteractionResult.PASS;
        }

        if (!stack.is(ModItems.ENDER_KEY)) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ServerLevel serverLevel = (ServerLevel) level;
        int cured = unsealNearbyFrames(serverLevel, pos);

        if (cured == 0) {
            return InteractionResult.FAIL;
        }

        stack.shrink(1);
        level.setBlock(pos, state.setValue(UNLOCKED, true), 3);

        level.levelEvent(1503, pos, 0);
        serverLevel.sendParticles(ParticleTypes.END_ROD,
                pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                40, 0.5, 0.5, 0.5, 0.05);

        return InteractionResult.SUCCESS;
    }

    private int unsealNearbyFrames(ServerLevel level, BlockPos center) {
        int count = 0;
        for (BlockPos checkPos : BlockPos.betweenClosed(
                center.offset(-SEARCH_RADIUS, -SEARCH_RADIUS, -SEARCH_RADIUS),
                center.offset(SEARCH_RADIUS, SEARCH_RADIUS, SEARCH_RADIUS))) {

            BlockState checkState = level.getBlockState(checkPos);
            if (checkState.is(dev.wux.survivaldreams.block.ModBlocks.SEALED_END_PORTAL_FRAME)) {
                Direction facing = checkState.getValue(SealedEndPortalFrameBlock.FACING);
                level.setBlock(checkPos.immutable(),
                        Blocks.END_PORTAL_FRAME.defaultBlockState()
                                .setValue(EndPortalFrameBlock.FACING, facing)
                                .setValue(EndPortalFrameBlock.HAS_EYE, false),
                        2);
                count++;
            }
        }
        return count;
    }
}