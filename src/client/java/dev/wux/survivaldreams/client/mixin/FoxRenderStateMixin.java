package dev.wux.survivaldreams.client.mixin;

import dev.wux.survivaldreams.client.EndFoxRenderStateAccessor;
import dev.wux.survivaldreams.client.WuxFoxRenderStateAccessor;
import net.minecraft.client.renderer.entity.state.FoxRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(FoxRenderState.class)
public abstract class FoxRenderStateMixin implements WuxFoxRenderStateAccessor, EndFoxRenderStateAccessor {

    @Unique
    private boolean survivalDreams$isWuxFox = false;

    @Unique
    private boolean survivalDreams$isEndFox = false;

    @Override
    public boolean survivalDreams$isWuxFox() {
        return this.survivalDreams$isWuxFox;
    }

    @Override
    public void survivalDreams$setWuxFox(boolean value) {
        this.survivalDreams$isWuxFox = value;
    }

    @Override
    public boolean survivalDreams$isEndFox() {
        return this.survivalDreams$isEndFox;
    }

    @Override
    public void survivalDreams$setEndFox(boolean value) {
        this.survivalDreams$isEndFox = value;
    }
}