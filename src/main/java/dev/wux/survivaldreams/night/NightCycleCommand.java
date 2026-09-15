package dev.wux.survivaldreams.night;

import com.mojang.brigadier.CommandDispatcher;
import dev.wux.survivaldreams.wand.WandState;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

import static net.minecraft.commands.Commands.literal;

public final class NightCycleCommand {

    private NightCycleCommand() {}

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                registerCommand(dispatcher));
    }

    private static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("nightcycle")
                .executes(context -> {
                    CommandSourceStack source = context.getSource();
                    ServerLevel level = source.getLevel();

                    float progress = NightCycleManager.getProgress(level);
                    NightCyclePhase phase = NightCycleManager.getPhase(level);
                    float intensity = NightCycleManager.getIntensity(level);

                    source.sendSuccess(() -> Component.literal(String.format(
                            "Fase: %s, Progreso: %.1f%%, Intensidad: %.1f%%",
                            phase.name(), progress * 100f, intensity * 100f
                    )), false);

                    return 1;
                })
                .then(literal("wand").executes(context -> {
                    CommandSourceStack source = context.getSource();
                    ServerLevel overworld = source.getServer().overworld();
                    WandState state = WandState.get(overworld);
                    long now = overworld.getGameTime();

                    boolean active = state.isActive(now);
                    boolean onCooldown = state.isOnCooldown(now);

                    String extra;
                    if (active) {
                        extra = String.format(" , Termina en: %s", formatTicks(state.ticksUntilInactive(now)));
                    } else if (onCooldown) {
                        extra = String.format(" , Reutilizable en: %s", formatTicks(state.ticksUntilReusable(now)));
                    } else {
                        extra = "";
                    }

                    String message = String.format(
                            "Varita activa: %s, En cooldown: %s%s",
                            active, onCooldown, extra
                    );
                    source.sendSuccess(() -> Component.literal(message), false);

                    return 1;
                }))
        );
    }

    private static String formatTicks(long ticks) {
        long totalSeconds = ticks / 20L;
        long minutes = totalSeconds / 60L;
        long seconds = totalSeconds % 60L;
        return String.format("%dm %ds", minutes, seconds);
    }
}