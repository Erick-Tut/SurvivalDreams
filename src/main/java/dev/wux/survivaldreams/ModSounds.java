package dev.wux.survivaldreams;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {

    public static final SoundEvent WILDFIRE_SHOOT = register("wildfire_shoot");
    public static final SoundEvent WILDFIRE_SHIELD_DEBRIS_IMPACT = register("wildfire_shield_debris_impact");
    public static final SoundEvent WILDFIRE_DEATH = register("wildfire_death");
    public static final SoundEvent WILDFIRE_HURT = register("wildfire_hurt");
    public static final SoundEvent WILDFIRE_CRACKLE = register("wildfire_crackle");
    public static final SoundEvent WILDFIRE_SHOOT_VOCAL = register("wildfire_shoot_vocal");
    public static final SoundEvent WILDFIRE_IDLE = register("wildfire_idle");
    public static final SoundEvent WILDFIRE_SHIELD_BROKEN = register("wildfire_shield_broken");
    public static final SoundEvent WILDFIRE_SHIELD_BREAK_VOCAL = register("wildfire_shield_break_vocal");
    public static final SoundEvent WILDFIRE_SHOCKWAVE = register("wildfire_shockwave");
    public static final SoundEvent WILDFIRE_STEP = register("wildfire_step");
    public static final SoundEvent WILDFIRE_STUN = register("wildfire_stun");

    public static final SoundEvent WATCHLING_ATTACK = register("watchling_attack");
    public static final SoundEvent WATCHLING_DEATH = register("watchling_death");
    public static final SoundEvent WATCHLING_HEAVY_ATTACK = register("watchling_heavy_attack");
    public static final SoundEvent WATCHLING_HURT = register("watchling_hurt");
    public static final SoundEvent WATCHLING_IDLE = register("watchling_idle");
    public static final SoundEvent WATCHLING_STEP = register("watchling_step");
    public static final SoundEvent WATCHLING_STUN = register("watchling_stun");

    public static final SoundEvent ENDERSENT_ATTACK = register("endersent_attack");
    public static final SoundEvent ENDERSENT_DEADLY_ESCAPE = register("endersent_deadly_escape");
    public static final SoundEvent ENDERSENT_DEATH = register("endersent_death");
    public static final SoundEvent ENDERSENT_HURT = register("endersent_hurt");
    public static final SoundEvent ENDERSENT_IDLE_SMASH = register("endersent_idle_smash");
    public static final SoundEvent ENDERSENT_IDLE = register("endersent_idle");
    public static final SoundEvent ENDERSENT_STEP = register("endersent_step");
    public static final SoundEvent ENDERSENT_STUN = register("endersent_stun");
    public static final SoundEvent ENDERSENT_TELEPORT_SMASH = register("endersent_teleport_smash");

    public static final SoundEvent LUNAR_WAND_ACTIVATE = register("lunar_wand_activate");

    private static SoundEvent register(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    public static void initialize() {
        SurvivalDreams.LOGGER.info("Registrando sonidos de " + SurvivalDreams.MOD_ID);
    }
}