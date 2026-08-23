package dev.wux.survivaldreams.client;

import dev.wux.survivaldreams.SurvivalDreams;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;

public class ModEntityModelLayers {
    public static final ModelLayerLocation WILDFIRE = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, "wildfire"),
            "main"
    );

    public static final ModelLayerLocation WATCHLING = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, "watchling"),
            "main"
    );

    public static final ModelLayerLocation ENDERSENT = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, "endersent"),
            "main"
    );

    public static final ModelLayerLocation ENDERSENT_DEFAULT = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(SurvivalDreams.MOD_ID, "endersent_default"),
            "main"
    );

}