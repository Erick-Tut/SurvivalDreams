package dev.wux.survivaldreams.mixin;

import dev.wux.survivaldreams.component.ModDataComponents;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class WildfireTooltipMixin {

    @Unique
    private static final TextColor WILDFIRE_ORANGE = TextColor.fromRgb(0xFF8C1A);

    @Inject(method = "getTooltipLines", at = @At("RETURN"))
    private void survivalDreams$addWildfireTooltip(
            Item.TooltipContext context,
            @Nullable Player player,
            TooltipFlag flag,
            CallbackInfoReturnable<List<Component>> cir) {

        ItemStack self = (ItemStack) (Object) this;

        if (!self.has(ModDataComponents.WILDFIRE_REINFORCED)) {
            return;
        }

        List<Component> tooltip = cir.getReturnValue();

        Style orangeStyle = Style.EMPTY.withColor(WILDFIRE_ORANGE);

        MutableComponent title = Component.translatable("upgrade.survivaldreams.wildfire_reinforcement")
                .withStyle(orangeStyle);

        MutableComponent damageLine = Component.literal("+5% ")
                .append(Component.translatable("upgrade.survivaldreams.damage_resistance"))
                .withStyle(orangeStyle);

        MutableComponent fireLine = Component.literal("+10% ")
                .append(Component.translatable("upgrade.survivaldreams.fire_resistance"))
                .withStyle(orangeStyle);

        tooltip.add(title);
        tooltip.add(damageLine);
        tooltip.add(fireLine);
    }
}