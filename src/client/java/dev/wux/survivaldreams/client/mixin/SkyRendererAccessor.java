package dev.wux.survivaldreams.client.mixin;

import com.mojang.blaze3d.buffers.GpuBuffer;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SkyRenderer.class)
public interface SkyRendererAccessor {

    @Accessor("celestialsAtlas")
    TextureAtlas survivaldreams$getCelestialsAtlas();

    @Invoker("buildCelestialQuad")
    static GpuBuffer survivaldreams$buildCelestialQuad(String name, TextureAtlasSprite sprite) {
        throw new AssertionError("Mixin no aplicado");
    }
}