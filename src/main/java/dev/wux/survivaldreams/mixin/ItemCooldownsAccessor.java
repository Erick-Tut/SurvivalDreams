package dev.wux.survivaldreams.mixin;

import net.minecraft.world.item.ItemCooldowns;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ItemCooldowns.class)
public interface ItemCooldownsAccessor {

    @SuppressWarnings("rawtypes")
    @Accessor("cooldowns")
    Map survivalDreams$getCooldownsMap();

    @Accessor("tickCount")
    int survivalDreams$getTickCount();
}