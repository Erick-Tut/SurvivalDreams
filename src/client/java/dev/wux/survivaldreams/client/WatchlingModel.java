package dev.wux.survivaldreams.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class WatchlingModel extends EntityModel<WatchlingRenderState> {

    private final ModelPart bone;
    private final ModelPart piernas;
    private final ModelPart pierna1;
    private final ModelPart pierna2;
    private final ModelPart cuerpo;
    private final ModelPart cara;
    private final ModelPart brazos;
    private final ModelPart brazo1;
    private final ModelPart brazo2;

    public WatchlingModel(ModelPart root) {
        super(root);
        this.bone = root.getChild("bone");
        this.piernas = this.bone.getChild("Piernas");
        this.pierna1 = this.piernas.getChild("Pierna1");
        this.pierna2 = this.piernas.getChild("Pierna2");
        this.cuerpo = this.bone.getChild("Cuerpo");
        this.cara = this.bone.getChild("Cara");
        this.brazos = this.bone.getChild("Brazos");
        this.brazo1 = this.brazos.getChild("Brazo1");
        this.brazo2 = this.brazos.getChild("Brazo2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone",
                CubeListBuilder.create(), PartPose.offset(2.0F, 24.0F, 0.0F));

        PartDefinition piernas = bone.addOrReplaceChild("Piernas",
                CubeListBuilder.create(), PartPose.offset(0.0F, -16.0F, 0.0F));

        piernas.addOrReplaceChild("Pierna1",
                CubeListBuilder.create().texOffs(56, 29)
                        .addBox(-1.0F, -1.0F, -1.0F, 2.0F, 17.0F, 2.0F, CubeDeformation.NONE),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        piernas.addOrReplaceChild("Pierna2",
                CubeListBuilder.create().texOffs(56, 29)
                        .addBox(-1.0F, -1.0F, -1.0F, 2.0F, 17.0F, 2.0F, CubeDeformation.NONE),
                PartPose.offset(-5.0F, 0.0F, 0.0F));

        bone.addOrReplaceChild("Cuerpo",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-10.0F, -15.0F, -1.0F, 11.0F, 15.0F, 6.0F, CubeDeformation.NONE),
                PartPose.offset(2.0F, -17.0F, -2.0F));

        bone.addOrReplaceChild("Cara",
                CubeListBuilder.create().texOffs(0, 30)
                        .addBox(-8.0F, -9.0F, -1.0F, 9.0F, 9.0F, 9.0F, CubeDeformation.NONE),
                PartPose.offset(1.0F, -32.0F, -3.0F));

        PartDefinition brazos = bone.addOrReplaceChild("Brazos",
                CubeListBuilder.create(), PartPose.offset(5.0F, -29.0F, 0.0F));

        brazos.addOrReplaceChild("Brazo1",
                CubeListBuilder.create().texOffs(39, 16)
                        .addBox(-2.0F, -3.0F, -2.0F, 4.0F, 28.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offset(-15.0F, 0.0F, 0.0F));

        brazos.addOrReplaceChild("Brazo2",
                CubeListBuilder.create().texOffs(39, 16)
                        .addBox(-2.0F, -3.0F, -2.0F, 4.0F, 28.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 48);
    }

    @Override
    public void setupAnim(WatchlingRenderState state) {
        super.setupAnim(state);

        this.pierna1.xRot = 0.0F;
        this.pierna2.xRot = 0.0F;
        this.brazo1.xRot = 0.0F;
        this.brazo2.xRot = 0.0F;
        this.bone.yRot = 0.0F;

        this.pierna1.xRot = Mth.cos(state.walkAnimationPos * 0.6662F) * 1.4F * state.walkAnimationSpeed;
        this.pierna2.xRot = Mth.cos(state.walkAnimationPos * 0.6662F + (float) Math.PI) * 1.4F * state.walkAnimationSpeed;

        if (state.isAttacking) {
            float swing = Mth.sin(state.attackTime * (float) Math.PI);
            this.brazo1.xRot = -1.8F * swing;
            this.brazo2.xRot = -1.8F * swing;
        } else {
            this.brazo1.xRot = Mth.cos(state.walkAnimationPos * 0.6662F + (float) Math.PI) * 1.0F * state.walkAnimationSpeed;
            this.brazo2.xRot = Mth.cos(state.walkAnimationPos * 0.6662F) * 1.0F * state.walkAnimationSpeed;
        }

        this.bone.yRot = Mth.sin(state.ageInTicks * 0.05F) * 0.05F;
    }
}