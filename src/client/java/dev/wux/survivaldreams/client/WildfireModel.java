package dev.wux.survivaldreams.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class WildfireModel extends EntityModel<WildfireRenderState> {

    private static final float ORBIT_SPEED = 0.05F;

    private final ModelPart shieldsGroup;
    private final ModelPart[] shields = new ModelPart[4];

    public WildfireModel(ModelPart root) {
        super(root);
        ModelPart wildfire = root.getChild("Wildfire");
        this.shieldsGroup = wildfire.getChild("shields");
        for (int i = 0; i < 4; i++) {
            this.shields[i] = this.shieldsGroup.getChild("shield" + i);
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition partDefinition = mesh.getRoot();

        PartDefinition wildfire = partDefinition.addOrReplaceChild(
                "Wildfire",
                CubeListBuilder.create()
                        .texOffs(32, 47)
                        .addBox(-8.0F, -6.0F, -0.015F, 8.0F, 9.0F, 0.0F, CubeDeformation.NONE)
                        .texOffs(32, 56).mirror()
                        .addBox(-8.0F, -5.0F, 8.015F, 8.0F, 6.0F, 0.0F, CubeDeformation.NONE).mirror(false)
                        .texOffs(16, 40).mirror()
                        .addBox(-8.01F, -5.0F, 0.0F, 0.0F, 8.0F, 8.0F, CubeDeformation.NONE).mirror(false)
                        .texOffs(48, 40).mirror()
                        .addBox(0.01F, -5.0F, 0.0F, 0.0F, 8.0F, 8.0F, CubeDeformation.NONE).mirror(false)
                        .texOffs(0, 0)
                        .addBox(-8.0F, -5.0F, 0.01F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE)
                        .texOffs(0, 16)
                        .addBox(-6.0F, 3.0F, 2.0F, 4.0F, 21.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offset(4.0F, 0.0F, -4.0F)
        );

        PartDefinition shields = wildfire.addOrReplaceChild(
                "shields",
                CubeListBuilder.create(),
                PartPose.offset(-4.0F, 0.0F, 4.0F)
        );

        CubeListBuilder shieldBox = CubeListBuilder.create()
                .texOffs(24, 18).mirror()
                .addBox(-5.0F, -8.5F, -1.0F, 10.0F, 17.0F, 2.0F, CubeDeformation.NONE).mirror(false);

        shields.addOrReplaceChild("shield0", shieldBox,
                PartPose.offsetAndRotation(10.949F, 10.615F, 0.0F, -0.2182F, -1.5708F, 0.0F));
        shields.addOrReplaceChild("shield1", shieldBox,
                PartPose.offsetAndRotation(0.0F, 10.615F, 10.949F, -0.2182F, 3.1416F, 0.0F));
        shields.addOrReplaceChild("shield2", shieldBox,
                PartPose.offsetAndRotation(-10.902F, 10.182F, 0.0F, 0.2182F, -1.5708F, 0.0F));
        shields.addOrReplaceChild("shield3", shieldBox,
                PartPose.offsetAndRotation(0.0F, 10.182F, -10.902F, 0.2182F, 3.1416F, 0.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(WildfireRenderState state) {
        super.setupAnim(state);

        this.shieldsGroup.yRot = state.ageInTicks * ORBIT_SPEED;

        for (int i = 0; i < 4; i++) {
            boolean broken = (state.brokenShieldsMask & (1 << i)) != 0;
            this.shields[i].visible = !broken;
        }
    }
}