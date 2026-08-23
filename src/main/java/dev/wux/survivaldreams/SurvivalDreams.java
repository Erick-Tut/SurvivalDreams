package dev.wux.survivaldreams;

import dev.wux.survivaldreams.component.ModDataComponents;
import dev.wux.survivaldreams.handler.*;
import dev.wux.survivaldreams.items.*;
import dev.wux.survivaldreams.attachments.*;
import dev.wux.survivaldreams.entity.*;
import dev.wux.survivaldreams.block.*;
import dev.wux.survivaldreams.recipe.ModRecipeSerializers;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SurvivalDreams implements ModInitializer {
	public static final String MOD_ID = "survival-dreams";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		ModEntityTypes.initialize();
		ModBiomeModifications.initialize();

		ModBlocks.initialize();
		ModBlockEntityTypes.initialize();

		ModItems.initialize();
		ModSpawnEggs.initialize();
		ModItemGroups.initialize();

		ModSounds.initialize();
		ModEffects.initialize();
		ModDataComponents.initialize();
		ModRecipeSerializers.initialize();

		HappyGhastAttachment.initialize();
		ModAttachments.initialize();
		WuxFoxAttachment.initialize();
		ZombifiedPiglinAttachment.initialize();

		ForceNightHandler.register();
		RainSlowFallingHandler.register();
		PaleGardenBlindnessHandler.register();

		WildfireSpawnerHandler.register();
		CatVariantHandler.register();
		WuxFoxSpawnHandler.register();
		DolphinChargedCreeperHandler.register();
		BlazeBreezeDeathHandler.register();
		FearRemovalHandler.register();
		IllagerNoAttackVillagerHandler.register();
		PassiveMobRevengeHandler.register();
		ZombifiedPiglinRandomAggroHandler.register();
		PiglinBruteInGroupHandler.register();
		CreakingDamageHandler.register();
		HappyGhastAttackHandler.register();
		HappyGhastDeathExplosionHandler.register();

		GolemSwimHandler.register();
		GolemHostileHandler.register();
		GolemGroupAlertHandler.register();
		RaidGolemDeathHandler.register();
		BellGolemGlowHandler.register();

		LowHealthRegenHandler.register();
		MobRandomWeaponHandler.register();
		SkeletonMeleeSwitchHandler.register();
		TridentThrowHandler.register();
		SpiderEffectsHandler.register();
		MagmaCubeAttackHandler.register();
		BlazeShieldDisableHandler.register();
		MobMeleeEffectHandler.register();
		WildfireShieldHandler.register();
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}