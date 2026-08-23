package dev.wux.survivaldreams.client;

import dev.wux.survivaldreams.block.EndersentSpawnerBlockEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import org.joml.Matrix4f;

public class EndersentSpawnerHighlightRenderer {

    public static void register() {
        WorldRenderEvents.BEFORE_TRANSLUCENT.register(EndersentSpawnerHighlightRenderer::render);
    }

    private static void render(WorldRenderContext context) {
        var player = Minecraft.getInstance().player;
        if (player == null || !player.getAbilities().instabuild) return;

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;

        PoseStack matrices = context.matrices();
        MultiBufferSource consumers = context.consumers();
        if (matrices == null || consumers == null) return;

        Vec3 camera = context.worldState().cameraRenderState.pos;

        matrices.pushPose();
        matrices.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumer buffer = consumers.getBuffer(RenderTypes.lines());
        Matrix4f pose = matrices.last().pose();

        int radius = 32;
        BlockPos playerPos = player.blockPosition();

        for (BlockPos pos : BlockPos.betweenClosed(
                playerPos.offset(-radius, -radius, -radius),
                playerPos.offset(radius, radius, radius))) {

            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof EndersentSpawnerBlockEntity) {
                addBoxLines(pose, buffer, pos, 1.0f, 0.0f, 1.0f, 1.0f);
            }
        }

        matrices.popPose();
    }

    private static void addBoxLines(Matrix4f pose, VertexConsumer buffer, BlockPos pos,
                                    float r, float g, float b, float a) {
        float x0 = pos.getX() - 0.002f, y0 = pos.getY() - 0.002f, z0 = pos.getZ() - 0.002f;
        float x1 = pos.getX() + 1.002f, y1 = pos.getY() + 1.002f, z1 = pos.getZ() + 1.002f;

        line(buffer, pose, x0, y0, z0, x0, y1, z0, r, g, b, a);
        line(buffer, pose, x1, y0, z0, x1, y1, z0, r, g, b, a);
        line(buffer, pose, x0, y0, z1, x0, y1, z1, r, g, b, a);
        line(buffer, pose, x1, y0, z1, x1, y1, z1, r, g, b, a);

        line(buffer, pose, x0, y0, z0, x1, y0, z0, r, g, b, a);
        line(buffer, pose, x0, y0, z1, x1, y0, z1, r, g, b, a);
        line(buffer, pose, x0, y0, z0, x0, y0, z1, r, g, b, a);
        line(buffer, pose, x1, y0, z0, x1, y0, z1, r, g, b, a);

        line(buffer, pose, x0, y1, z0, x1, y1, z0, r, g, b, a);
        line(buffer, pose, x0, y1, z1, x1, y1, z1, r, g, b, a);
        line(buffer, pose, x0, y1, z0, x0, y1, z1, r, g, b, a);
        line(buffer, pose, x1, y1, z0, x1, y1, z1, r, g, b, a);
    }

    private static void line(VertexConsumer buffer, Matrix4f pose,
                             float x0, float y0, float z0, float x1, float y1, float z1,
                             float r, float g, float b, float a) {
        float nx = x1 - x0, ny = y1 - y0, nz = z1 - z0;
        buffer.addVertex(pose, x0, y0, z0).setColor(r, g, b, a).setNormal(nx, ny, nz).setLineWidth(1.0f);
        buffer.addVertex(pose, x1, y1, z1).setColor(r, g, b, a).setNormal(nx, ny, nz).setLineWidth(1.0f);
    }
}