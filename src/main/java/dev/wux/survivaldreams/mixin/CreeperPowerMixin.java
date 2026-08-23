package dev.wux.survivaldreams.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Creeper.class)
public interface CreeperPowerMixin {

    @Accessor("DATA_IS_POWERED")
    static EntityDataAccessor<Boolean> survivalDreams$getDataIsPowered() {
        throw new AssertionError();
    }
}