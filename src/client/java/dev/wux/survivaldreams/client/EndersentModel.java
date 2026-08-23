package dev.wux.survivaldreams.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import dev.wux.survivaldreams.entity.Endersent;
import net.minecraft.util.Mth;

public class EndersentModel extends EntityModel<EndersentRenderState> {

    private static final float WALK_FREQUENCY = 0.35F;
    private static final float WALK_LEG_AMPLITUDE = 0.90F;
    private static final float IDLE_SPEED = 0.045F;

    private final ModelPart bone;
    private final ModelPart piernas;
    private final ModelPart pierna1;
    private final ModelPart pierna2;
    private final ModelPart cuerpo;
    private final ModelPart cara;
    private final ModelPart brazoDerecho;
    private final ModelPart brazo1;
    private final ModelPart anteBrazo1;
    private final ModelPart mano1;
    private final ModelPart brazoIzquierdo;
    private final ModelPart brazo2;
    private final ModelPart anteBrazo2;
    private final ModelPart mano2;

    public EndersentModel(ModelPart root) {
        super(root);
        this.bone = root.getChild("bone");
        this.piernas = this.bone.getChild("Piernas");
        this.pierna1 = this.piernas.getChild("Pierna1");
        this.pierna2 = this.piernas.getChild("Pierna2");
        this.cuerpo = this.bone.getChild("Cuerpo");
        this.cara = this.bone.getChild("Cara");
        this.brazoDerecho = this.bone.getChild("BrazoDerecho");
        this.brazo1 = this.brazoDerecho.getChild("Brazo1");
        this.anteBrazo1 = this.brazo1.getChild("AnteBrazo1");
        this.mano1 = this.anteBrazo1.getChild("Mano1");
        this.brazoIzquierdo = this.bone.getChild("BrazoIzquierdo");
        this.brazo2 = this.brazoIzquierdo.getChild("Brazo2");
        this.anteBrazo2 = this.brazo2.getChild("AnteBrazo2");
        this.mano2 = this.anteBrazo2.getChild("Mano2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone",
                CubeListBuilder.create(), PartPose.offset(-7.0F, 21.0F, -1.0F));

        PartDefinition piernas = bone.addOrReplaceChild("Piernas",
                CubeListBuilder.create(), PartPose.offset(5.0F, 3.0F, 0.0F));

        piernas.addOrReplaceChild("Pierna1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 60.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offset(-1.0F, -59.0F, 1.0F));

        piernas.addOrReplaceChild("Pierna2",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 60.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offset(4.0F, -59.0F, 1.0F));

        bone.addOrReplaceChild("Cuerpo",
                CubeListBuilder.create().texOffs(82, 30).addBox(-14.0F, -26.0F, -1.0F, 15.0F, 26.0F, 8.0F, CubeDeformation.NONE),
                PartPose.offset(13.0F, -56.0F, -2.0F));

        bone.addOrReplaceChild("Cara",
                CubeListBuilder.create().texOffs(43, 0).addBox(-8.0F, -14.0F, -1.0F, 9.0F, 14.0F, 6.0F, CubeDeformation.NONE),
                PartPose.offset(10.0F, -76.0F, -7.0F));

        PartDefinition brazoDerecho = bone.addOrReplaceChild("BrazoDerecho",
                CubeListBuilder.create(), PartPose.offset(14.0F, -81.0F, 1.0F));

        PartDefinition brazo1 = brazoDerecho.addOrReplaceChild("Brazo1",
                CubeListBuilder.create().texOffs(32, 32).addBox(-1.0F, -1.0F, -1.0F, 3.0F, 28.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offset(0.0F, 0.0F, -1.0F));

        PartDefinition anteBrazo1 = brazo1.addOrReplaceChild("AnteBrazo1",
                CubeListBuilder.create().texOffs(16, 18).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 42.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offset(1.0F, 29.0F, 1.0F));

        anteBrazo1.addOrReplaceChild("Mano1",
                CubeListBuilder.create().texOffs(76, 0).addBox(-4.0F, -4.0F, -2.0F, 8.0F, 12.0F, 18.0F, CubeDeformation.NONE),
                PartPose.offset(0.0F, 42.0F, 0.0F));

        PartDefinition brazoIzquierdo = bone.addOrReplaceChild("BrazoIzquierdo",
                CubeListBuilder.create(), PartPose.offset(-1.0F, -80.0F, 1.0F));

        PartDefinition brazo2 = brazoIzquierdo.addOrReplaceChild("Brazo2",
                CubeListBuilder.create().texOffs(32, 32).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 28.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition anteBrazo2 = brazo2.addOrReplaceChild("AnteBrazo2",
                CubeListBuilder.create().texOffs(16, 18).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 42.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offset(-1.0F, 28.0F, 0.0F));

        anteBrazo2.addOrReplaceChild("Mano2",
                CubeListBuilder.create().texOffs(76, 0).addBox(-3.0F, -4.0F, -2.0F, 8.0F, 12.0F, 18.0F, CubeDeformation.NONE),
                PartPose.offset(-1.0F, 42.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 64);
    }

    @Override
    public void setupAnim(EndersentRenderState state) {
        super.setupAnim(state);

        this.root().getAllParts().forEach(ModelPart::resetPose);

        cara.yRot = Mth.clamp(state.yRot * Mth.DEG_TO_RAD, -0.75F, 0.75F);
        cara.xRot = Mth.clamp(state.xRot * Mth.DEG_TO_RAD, -0.45F, 0.45F);

        cuerpo.yRot = cara.yRot * 0.18F;

        float walkPos = state.walkAnimationPos;
        float walkSpeed = Math.min(state.walkAnimationSpeed, 1.0F);

        float walk = walkPos * WALK_FREQUENCY;

        float idle = state.ageInTicks * IDLE_SPEED;

        pierna1.xRot = Mth.cos(walk) * WALK_LEG_AMPLITUDE * walkSpeed;
        pierna2.xRot = Mth.cos(walk + Mth.PI) * WALK_LEG_AMPLITUDE * walkSpeed;

        pierna1.zRot = Mth.sin(walk) * 0.05F * walkSpeed;
        pierna2.zRot = -Mth.sin(walk) * 0.05F * walkSpeed;

        float bob = Mth.abs(Mth.sin(walk)) * 1.4F * walkSpeed;
        bone.y += bob;

        cuerpo.y += Mth.sin(idle) * 0.45F;
        cara.y += Mth.sin(idle) * 0.20F;

        cuerpo.xRot += Mth.sin(idle) * 0.025F;
        cuerpo.zRot += Mth.sin(idle * 0.6F) * 0.015F;
        cara.zRot += Mth.sin(idle * 0.8F) * 0.020F;

        brazo1.xRot += Mth.cos(walk + Mth.PI) * 0.12F * walkSpeed;
        brazo2.xRot += Mth.cos(walk) * 0.12F * walkSpeed;

        brazoDerecho.zRot += Mth.sin(walk) * 0.035F * walkSpeed;
        brazoIzquierdo.zRot -= Mth.sin(walk) * 0.035F * walkSpeed;

        anteBrazo1.xRot += 0.05F * walkSpeed;
        anteBrazo2.xRot += 0.05F * walkSpeed;

        mano1.xRot += anteBrazo1.xRot * 0.15F;
        mano2.xRot += anteBrazo2.xRot * 0.15F;

        cuerpo.zRot += Mth.sin(walk) * 0.04F * walkSpeed;
        cara.zRot += Mth.sin(walk) * 0.02F * walkSpeed;


        float t = Mth.clamp(state.attackTime, 0.0F, 1.0F);

        switch (state.attackType) {

            case Endersent.ATTACK_BASIC -> {

                if (t < 0.25F) {
                    float prep = t / 0.25F;
                    cuerpo.xRot -= 0.18F * prep;
                    brazo1.xRot += 0.45F * prep;
                    anteBrazo1.xRot += 0.20F * prep;

                } else if (t < 0.75F) {
                    float swing = Mth.sin(((t - 0.25F) / 0.50F) * Mth.PI);
                    cuerpo.xRot += 0.30F * swing;
                    brazo1.xRot -= 2.05F * swing;
                    anteBrazo1.xRot -= 1.40F * swing;
                    mano1.xRot -= 0.55F * swing;
                    cuerpo.y += swing * 0.8F;
                    cara.xRot += swing * 0.18F;

                } else {
                    float recover = (t - 0.75F) / 0.25F;
                    brazo1.xRot *= (1F - recover);
                    anteBrazo1.xRot *= (1F - recover);
                    mano1.xRot *= (1F - recover);
                }
            }

            case Endersent.ATTACK_BASIC_VARIANT -> {

                if (t < 0.30F) {
                    float prep = t / 0.30F;
                    cuerpo.yRot += 0.25F * prep;
                    brazo1.zRot += 0.55F * prep;

                } else {
                    float swing = Mth.sin(((t - 0.30F) / 0.70F) * Mth.PI);
                    cuerpo.yRot -= 0.45F * swing;
                    cuerpo.xRot += 0.12F * swing;
                    brazo1.zRot -= 2.15F * swing;
                    brazo1.xRot -= 0.95F * swing;
                    anteBrazo1.zRot -= 0.90F * swing;
                    anteBrazo1.xRot -= 0.30F * swing;
                    mano1.zRot -= 0.40F * swing;
                }
            }

            case Endersent.ATTACK_DEADLY_ESCAPE -> {

                if (t < 0.35F) {
                    float prep = t / 0.35F;
                    cuerpo.xRot -= 0.30F * prep;
                    brazo1.xRot += 0.85F * prep;
                    brazo2.xRot += 0.85F * prep;

                } else {
                    float swing = Mth.sin(((t - 0.35F) / 0.65F) * Mth.PI);
                    cuerpo.xRot += 0.45F * swing;
                    cuerpo.y += swing;
                    brazo1.xRot -= 2.35F * swing;
                    brazo2.xRot -= 2.35F * swing;
                    anteBrazo1.xRot -= 1.50F * swing;
                    anteBrazo2.xRot -= 1.50F * swing;
                    mano1.xRot -= 0.55F * swing;
                    mano2.xRot -= 0.55F * swing;
                    brazoDerecho.zRot += 0.12F * swing;
                    brazoIzquierdo.zRot -= 0.12F * swing;
                }
            }

            case Endersent.ATTACK_TELEPORT_SMASH -> {

                if (t < 0.45F) {
                    float raise = t / 0.45F;
                    cuerpo.xRot -= 0.22F * raise;
                    brazo1.xRot -= 1.75F * raise;
                    anteBrazo1.xRot -= 0.65F * raise;

                } else {
                    float swing = Mth.sin(((t - 0.45F) / 0.55F) * Mth.PI);
                    cuerpo.xRot += 0.42F * swing;
                    cuerpo.y += swing * 1.1F;
                    brazo1.xRot -= 2.20F * swing;
                    brazo2.xRot -= 2.20F * swing;
                    anteBrazo1.xRot -= 1.25F * swing;
                    anteBrazo2.xRot -= 1.25F * swing;
                    mano1.xRot -= 0.55F * swing;
                    mano2.xRot -= 0.55F * swing;
                    brazoDerecho.zRot += 0.15F * swing;
                    brazoIzquierdo.zRot -= 0.15F * swing;
                }
            }

            default -> {
            }
        }

        float attackBlend = state.attackType == Endersent.ATTACK_NONE ? 0.0F : t;

        cuerpo.zRot += Mth.sin(idle * 0.8F) * 0.01F * (1.0F - attackBlend);
        bone.zRot += Mth.sin(walk * 0.5F) * 0.025F * walkSpeed;

        brazoDerecho.yRot += Mth.sin(idle * 0.7F) * 0.03F * (1.0F - attackBlend);
        brazoIzquierdo.yRot -= Mth.sin(idle * 0.7F) * 0.03F * (1.0F - attackBlend);

        anteBrazo1.zRot += Mth.sin(idle * 1.1F) * 0.015F;
        anteBrazo2.zRot -= Mth.sin(idle * 1.1F) * 0.015F;

        mano1.zRot += Mth.sin(idle * 1.8F) * 0.02F;
        mano2.zRot -= Mth.sin(idle * 1.8F) * 0.02F;

        cara.zRot += Mth.sin(idle * 0.9F) * 0.015F;

        cara.yRot += cuerpo.yRot * 0.15F;

        cara.xRot -= Mth.abs(Mth.sin(walk)) * 0.025F * walkSpeed;

        cuerpo.yRot += Mth.sin(walk) * 0.05F * walkSpeed;

        brazoDerecho.xRot += cuerpo.xRot * 0.15F;
        brazoIzquierdo.xRot += cuerpo.xRot * 0.15F;

        mano1.xRot += anteBrazo1.xRot * 0.18F;
        mano2.xRot += anteBrazo2.xRot * 0.18F;

        cara.xRot += cuerpo.xRot * 0.08F;

        cara.yRot += Mth.sin(walk * 0.5F) * 0.03F * walkSpeed;

        cuerpo.yRot += Mth.sin(idle * 0.55F) * 0.015F;

        if (state.attackType == Endersent.ATTACK_NONE) {
            brazo1.xRot += 0.08F;
            brazo2.xRot += 0.08F;
            anteBrazo1.xRot += 0.05F;
            anteBrazo2.xRot += 0.05F;
        }
        bone.y += Mth.sin(walk * 2.0F) * 0.15F * walkSpeed;

        bone.xRot += Mth.sin(idle * 0.35F) * 0.01F;
    }
}