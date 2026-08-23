package dev.wux.survivaldreams.handler;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.creaking.Creaking;

public class CreakingDamageHandler {

    private static final double MIN_ATTACK_DAMAGE = 6.0;

    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (!(entity instanceof Creaking creaking)) return;
            if (!(world instanceof ServerLevel)) return;

            var attackDamageAttr = creaking.getAttribute(Attributes.ATTACK_DAMAGE);
            if (attackDamageAttr != null && attackDamageAttr.getBaseValue() < MIN_ATTACK_DAMAGE) {
                attackDamageAttr.setBaseValue(MIN_ATTACK_DAMAGE);
            }
        });
    }
}