package dev.wux.survivaldreams.items;

import dev.wux.survivaldreams.ModEffects;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MysteryBottleItem extends Item {

    private static final List<Holder<MobEffect>> GOOD_POOL = List.of(
            MobEffects.SPEED,
            MobEffects.HASTE,
            MobEffects.STRENGTH,
            MobEffects.JUMP_BOOST,
            MobEffects.REGENERATION,
            MobEffects.RESISTANCE,
            MobEffects.FIRE_RESISTANCE,
            MobEffects.WATER_BREATHING,
            MobEffects.INVISIBILITY,
            MobEffects.NIGHT_VISION,
            MobEffects.HEALTH_BOOST,
            MobEffects.ABSORPTION,
            MobEffects.SATURATION,
            MobEffects.LUCK,
            MobEffects.SLOW_FALLING,
            MobEffects.CONDUIT_POWER,
            MobEffects.DOLPHINS_GRACE,
            MobEffects.INSTANT_HEALTH,
            ModEffects.IMMUNITY_HOLDER
    );

    private static final List<Holder<MobEffect>> BAD_POOL = List.of(
            MobEffects.SLOWNESS,
            MobEffects.MINING_FATIGUE,
            MobEffects.INSTANT_DAMAGE,
            MobEffects.NAUSEA,
            MobEffects.HUNGER,
            MobEffects.WEAKNESS,
            MobEffects.POISON,
            MobEffects.WITHER,
            MobEffects.UNLUCK,
            MobEffects.BLINDNESS,
            MobEffects.DARKNESS,
            MobEffects.WIND_CHARGED,
            MobEffects.WEAVING,
            MobEffects.OOZING,
            MobEffects.INFESTED,
            ModEffects.VOIDED_HOLDER,
            ModEffects.AMBUSH_HOLDER
    );

    private static final Map<Holder<MobEffect>, List<Holder<MobEffect>>> CONFLICTS = new HashMap<>();

    static {
        addConflict(MobEffects.STRENGTH, MobEffects.WEAKNESS);
        addConflict(MobEffects.SPEED, MobEffects.SLOWNESS);
        addConflict(MobEffects.LUCK, MobEffects.UNLUCK);
        addConflict(MobEffects.REGENERATION, MobEffects.INSTANT_DAMAGE);
        addConflict(MobEffects.HUNGER, MobEffects.SATURATION);
        addConflict(MobEffects.HASTE, MobEffects.MINING_FATIGUE);
        addConflict(MobEffects.INSTANT_HEALTH, MobEffects.INSTANT_DAMAGE);
        addConflict(MobEffects.REGENERATION, ModEffects.AMBUSH_HOLDER);
        addConflict(MobEffects.REGENERATION, ModEffects.VOIDED_HOLDER);
        addConflict(MobEffects.INSTANT_HEALTH, ModEffects.AMBUSH_HOLDER);
        addConflict(MobEffects.INSTANT_HEALTH, ModEffects.VOIDED_HOLDER);
    }

    private static void addConflict(Holder<MobEffect> a, Holder<MobEffect> b) {
        CONFLICTS.computeIfAbsent(a, k -> new ArrayList<>()).add(b);
        CONFLICTS.computeIfAbsent(b, k -> new ArrayList<>()).add(a);
    }

    private static final int EFFECT_DURATION_TICKS = 400;

    public MysteryBottleItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);

        if (!level.isClientSide()) {
            RandomSource random = entity.getRandom();
            List<Holder<MobEffect>> pool = new ArrayList<>();
            pool.addAll(GOOD_POOL);
            pool.addAll(BAD_POOL);

            List<Holder<MobEffect>> shuffled = new ArrayList<>(pool);
            for (int i = shuffled.size() - 1; i > 0; i--) {
                int j = random.nextInt(i + 1);
                Holder<MobEffect> temp = shuffled.get(i);
                shuffled.set(i, shuffled.get(j));
                shuffled.set(j, temp);
            }

            int targetCount = rollEffectCount(random);
            List<Holder<MobEffect>> chosen = new ArrayList<>();

            for (Holder<MobEffect> candidate : shuffled) {
                if (chosen.size() >= targetCount) {
                    break;
                }
                if (conflicts(candidate, chosen)) {
                    continue;
                }
                chosen.add(candidate);
            }

            for (Holder<MobEffect> effect : chosen) {
                int amplifier = random.nextInt(3);
                int duration = isInstant(effect) ? 1 : EFFECT_DURATION_TICKS;
                entity.addEffect(new MobEffectInstance(effect, duration, amplifier));
            }
        }

        return result;
    }

    private static int rollEffectCount(RandomSource random) {
        int roll = random.nextInt(100);
        if (roll < 60) {
            return 1;
        } else if (roll < 90) {
            return 2;
        } else {
            return 3;
        }
    }

    private boolean isInstant(Holder<MobEffect> effect) {
        return effect.equals(MobEffects.INSTANT_HEALTH) || effect.equals(MobEffects.INSTANT_DAMAGE);
    }

    private boolean conflicts(Holder<MobEffect> candidate, List<Holder<MobEffect>> chosen) {
        if (chosen.contains(candidate)) {
            return true;
        }

        boolean chosenHasImmunity = chosen.contains(ModEffects.IMMUNITY_HOLDER);
        boolean candidateIsImmunity = candidate.equals(ModEffects.IMMUNITY_HOLDER);
        boolean chosenHasBad = chosen.stream().anyMatch(BAD_POOL::contains);
        boolean candidateIsBad = BAD_POOL.contains(candidate);

        if (candidateIsImmunity && chosenHasBad) {
            return true;
        }

        if (candidateIsBad && chosenHasImmunity) {
            return true;
        }

        List<Holder<MobEffect>> conflictList = CONFLICTS.get(candidate);
        return conflictList != null && conflictList.stream().anyMatch(chosen::contains);
    }
}