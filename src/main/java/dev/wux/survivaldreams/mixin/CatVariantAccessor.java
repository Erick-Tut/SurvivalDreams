package dev.wux.survivaldreams.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.feline.CatVariant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Cat.class)
public interface CatVariantAccessor {

    @Invoker("setVariant")
    void survivalDreams$setVariant(Holder<CatVariant> variant);
}