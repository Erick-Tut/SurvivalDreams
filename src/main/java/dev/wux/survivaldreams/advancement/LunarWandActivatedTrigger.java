package dev.wux.survivaldreams.advancement;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class LunarWandActivatedTrigger extends SimpleCriterionTrigger<LunarWandActivatedTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player) {
        trigger(player, conditions -> true);
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = EntityPredicate.ADVANCEMENT_CODEC
                .optionalFieldOf("player")
                .xmap(TriggerInstance::new, TriggerInstance::player)
                .codec();
    }
}