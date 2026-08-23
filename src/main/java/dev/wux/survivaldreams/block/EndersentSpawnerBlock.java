package dev.wux.survivaldreams.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class EndersentSpawnerBlock extends BaseEntityBlock {

    public static final MapCodec<EndersentSpawnerBlock> CODEC = simpleCodec(EndersentSpawnerBlock::new);

    public EndersentSpawnerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.INVISIBLE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new EndersentSpawnerBlockEntity(blockPos, blockState);
    }

    @Override
    protected net.minecraft.world.InteractionResult useWithoutItem(
            BlockState state,
            net.minecraft.world.level.Level level,
            BlockPos pos,
            net.minecraft.world.entity.player.Player player,
            net.minecraft.world.phys.BlockHitResult hitResult) {

        if (!player.isCreative()) {
            return net.minecraft.world.InteractionResult.PASS;
        }

        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof EndersentSpawnerBlockEntity spawner) {
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("Endersent Spawner: " + spawner.getState()),
                    false
            );
        }
        return net.minecraft.world.InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null :
                createTickerHelper(blockEntityType, ModBlockEntityTypes.ENDERSENT_SPAWNER, EndersentSpawnerBlockEntity::serverTick);
    }
}