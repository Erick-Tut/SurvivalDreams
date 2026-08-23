package dev.wux.survivaldreams.handler;

import dev.wux.survivaldreams.ModEffects;
import dev.wux.survivaldreams.entity.Firesnowgolem;
import dev.wux.survivaldreams.entity.Watchling;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.illager.Vindicator;
import net.minecraft.world.entity.monster.skeleton.Bogged;
import net.minecraft.world.entity.monster.skeleton.Parched;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.monster.skeleton.Stray;
import net.minecraft.world.entity.monster.skeleton.WitherSkeleton;
import net.minecraft.world.entity.monster.zombie.Drowned;
import net.minecraft.world.entity.monster.zombie.Husk;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MobMeleeEffectHandler {

    private static final int DURATION = 140;
    private static final int AMPLIFIER = 0;

    private static final float CHANCE_SWAP_BASE = 0.15F;

    private static final int[] COUNT_WEIGHTS = {70, 20, 8, 2};

    private static final List<Holder<MobEffect>> EXTRA_POOL_NO_DARKNESS = List.of(
            MobEffects.NAUSEA,
            MobEffects.BLINDNESS,
            MobEffects.WEAVING,
            MobEffects.OOZING,
            MobEffects.INFESTED
    );

    private static final Random RANDOM = new Random();

    public static void register() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamageTaken, damageTaken, blocked) -> {
            if (!(entity instanceof LivingEntity living)) return;

            Entity attackerEntity = source.getEntity();
            Holder<MobEffect> base = getBaseEffect(attackerEntity);
            if (base == null) return;

            int count = rollEffectCount();
            List<Holder<MobEffect>> toApply = new ArrayList<>();

            if (RANDOM.nextFloat() < CHANCE_SWAP_BASE) {
                toApply.add(randomFromPool());
            } else {
                toApply.add(base);
            }

            List<Holder<MobEffect>> shuffledPool = new ArrayList<>(EXTRA_POOL_NO_DARKNESS);
            Collections.shuffle(shuffledPool, RANDOM);

            int extraNeeded = count - 1;
            for (Holder<MobEffect> effect : shuffledPool) {
                if (extraNeeded <= 0) break;
                if (toApply.contains(effect)) continue;
                toApply.add(effect);
                extraNeeded--;
            }

            if (count >= 4 && !toApply.contains(MobEffects.DARKNESS)) {
                toApply.add(MobEffects.DARKNESS);
            }

            for (Holder<MobEffect> effect : toApply) {
                living.addEffect(new MobEffectInstance(effect, DURATION, AMPLIFIER));
            }
        });
    }

    private static Holder<MobEffect> randomFromPool() {
        return EXTRA_POOL_NO_DARKNESS.get(RANDOM.nextInt(EXTRA_POOL_NO_DARKNESS.size()));
    }

    private static int rollEffectCount() {
        int total = COUNT_WEIGHTS[0] + COUNT_WEIGHTS[1] + COUNT_WEIGHTS[2] + COUNT_WEIGHTS[3];
        int roll = RANDOM.nextInt(total);
        int cumulative = 0;
        for (int i = 0; i < COUNT_WEIGHTS.length; i++) {
            cumulative += COUNT_WEIGHTS[i];
            if (roll < cumulative) return i + 1;
        }
        return 1;
    }

    private static Holder<MobEffect> getBaseEffect(Entity attacker) {
        if (attacker == null) return null;

        if (attacker instanceof Watchling) return ModEffects.VOIDED_HOLDER;

        if (attacker instanceof Bogged) return MobEffects.POISON;
        if (attacker instanceof Parched) return MobEffects.HUNGER;
        if (attacker instanceof Stray) return MobEffects.SLOWNESS;
        if (attacker instanceof WitherSkeleton) return MobEffects.WITHER;
        if (attacker instanceof Skeleton) return MobEffects.WEAKNESS;

        if (attacker instanceof Husk) return MobEffects.HUNGER;
        if (attacker instanceof Drowned) return MobEffects.NAUSEA;
        if (attacker instanceof ZombifiedPiglin) return MobEffects.OOZING;
        if (attacker instanceof ZombieVillager) return MobEffects.WEAKNESS;
        if (attacker instanceof Zombie) return MobEffects.WEAKNESS;

        if (attacker instanceof Zoglin) return MobEffects.OOZING;
        if (attacker instanceof Creaking) return MobEffects.BLINDNESS;
        if (attacker instanceof Endermite) return MobEffects.SLOWNESS;
        if (attacker instanceof Silverfish) return MobEffects.INFESTED;
        if (attacker instanceof Blaze) return MobEffects.SLOW_FALLING;
        if (attacker instanceof Vex) return MobEffects.BLINDNESS;
        if (attacker instanceof Vindicator) return MobEffects.BLINDNESS;
        if (attacker instanceof Phantom) return MobEffects.SLOW_FALLING;
        if (attacker instanceof EnderMan) return ModEffects.VOIDED_HOLDER;

        if (attacker instanceof net.minecraft.world.entity.monster.breeze.Breeze) return MobEffects.LEVITATION;
        if (attacker instanceof net.minecraft.world.entity.monster.piglin.PiglinBrute) return MobEffects.WEAKNESS;
        if (attacker instanceof net.minecraft.world.entity.monster.piglin.Piglin) return MobEffects.HUNGER;
        if (attacker instanceof net.minecraft.world.entity.monster.spider.CaveSpider) return MobEffects.NAUSEA;
        if (attacker instanceof net.minecraft.world.entity.monster.spider.Spider) return MobEffects.POISON;
        if (attacker instanceof net.minecraft.world.entity.animal.nautilus.ZombieNautilus) return MobEffects.WITHER;
        if (attacker instanceof net.minecraft.world.entity.animal.nautilus.Nautilus) return MobEffects.MINING_FATIGUE;
        if (attacker instanceof net.minecraft.world.entity.monster.Guardian) return MobEffects.MINING_FATIGUE;
        if (attacker instanceof net.minecraft.world.entity.monster.Slime) return MobEffects.WEAKNESS;
        if (attacker instanceof net.minecraft.world.entity.monster.MagmaCube) return MobEffects.WEAKNESS;

        if (attacker instanceof Firesnowgolem) return MobEffects.WEAVING;
        if (attacker instanceof SnowGolem) return MobEffects.SLOWNESS;
        if (attacker instanceof IronGolem) return MobEffects.WEAKNESS;

        return null;
    }
}