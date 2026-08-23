package dev.wux.survivaldreams.client.mixin;

import dev.wux.survivaldreams.client.HappyGhastRenderStateAccessor;
import net.minecraft.client.renderer.entity.state.HappyGhastRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(HappyGhastRenderState.class)
public abstract class HappyGhastRenderStateMixin implements HappyGhastRenderStateAccessor {

    @Unique
    private boolean survivalDreams$isAngry = false;

    @Override
    public boolean survivalDreams$isAngry() {
        return this.survivalDreams$isAngry;
    }

    @Override
    public void survivalDreams$setAngry(boolean value) {
        this.survivalDreams$isAngry = value;
    }
}