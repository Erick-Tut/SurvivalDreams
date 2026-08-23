package dev.wux.survivaldreams.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class EndersentDefault extends Endersent {

    public EndersentDefault(EntityType<? extends Endersent> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Endersent.createAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D);
    }

    @Override
    protected boolean hasBossBar() {
        return false;
    }

    @Override
    protected boolean canDeadlyEscape() {
        return false;
    }
}