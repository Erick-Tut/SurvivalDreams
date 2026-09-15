package dev.wux.survivaldreams.items;

import dev.wux.survivaldreams.ModSounds;
import dev.wux.survivaldreams.advancement.ModCriteriaTriggers;
import dev.wux.survivaldreams.wand.WandState;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class LunarWand extends Item {

    private static final int LUNAR_PURPLE = 0xB266FF;
    private static final DustParticleOptions LUNAR_DUST = new DustParticleOptions(LUNAR_PURPLE, 1.2F);

    public LunarWand(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.CONSUME;
        }

        ServerLevel overworld = serverLevel.getServer().overworld();
        WandState state = WandState.get(overworld);
        long now = overworld.getGameTime();

        if (state.tryActivate(now)) {
            overworld.sendParticles(LUNAR_DUST,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    20, 0.5, 0.5, 0.5, 0.05);

            overworld.playSound(null, player.blockPosition(),
                    ModSounds.LUNAR_WAND_ACTIVATE, SoundSource.PLAYERS, 1.0F, 1.0F);

            if (player instanceof ServerPlayer serverPlayer) {
                ModCriteriaTriggers.LUNAR_WAND_ACTIVATED.trigger(serverPlayer);
            }

            return InteractionResult.CONSUME;

        } else {
            return InteractionResult.PASS;
        }
    }
}