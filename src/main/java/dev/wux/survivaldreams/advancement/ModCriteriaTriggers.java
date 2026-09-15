package dev.wux.survivaldreams.advancement;

import dev.wux.survivaldreams.SurvivalDreams;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class ModCriteriaTriggers {

    public static final LunarWandActivatedTrigger LUNAR_WAND_ACTIVATED = register(
            "lunar_wand_activated",
            new LunarWandActivatedTrigger()
    );

    private static <T extends LunarWandActivatedTrigger> T register(String name, T trigger) {
        Identifier id = Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, name);
        return Registry.register(BuiltInRegistries.TRIGGER_TYPES, id, trigger);
    }

    public static void initialize() {
    }
}