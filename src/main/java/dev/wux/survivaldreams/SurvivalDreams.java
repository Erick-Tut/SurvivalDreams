package dev.wux.survivaldreams;

import dev.wux.survivaldreams.advancement.*;
import dev.wux.survivaldreams.component.*;
import dev.wux.survivaldreams.handler.*;
import dev.wux.survivaldreams.items.*;
import dev.wux.survivaldreams.attachments.*;
import dev.wux.survivaldreams.entity.*;
import dev.wux.survivaldreams.block.*;
import dev.wux.survivaldreams.network.*;
import dev.wux.survivaldreams.night.*;
import dev.wux.survivaldreams.recipe.*;
import dev.wux.survivaldreams.wand.*;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
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
		ModCriteriaTriggers.initialize();

		HappyGhastAttachment.initialize();
		ModAttachments.initialize();
		WuxFoxAttachment.initialize();
		ZombifiedPiglinAttachment.initialize();
		EndFoxAttachment.initialize();

		PayloadTypeRegistry.playS2C().register(WandSyncPayload.TYPE, WandSyncPayload.STREAM_CODEC);
		WandSync.initialize();
		NightCycleInitializer.initialize();
		NightCycleCommand.register();

		RainSlowFallingHandler.register();
		PaleGardenBlindnessHandler.register();

		WildfireSpawnerHandler.register();
		CatVariantHandler.register();
		WuxFoxSpawnHandler.register();
		EndFoxSpawnHandler.register();
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

		WitherSkeletonLootingDropHandler.register();
		PhantomFamiliarSpawner.register();
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