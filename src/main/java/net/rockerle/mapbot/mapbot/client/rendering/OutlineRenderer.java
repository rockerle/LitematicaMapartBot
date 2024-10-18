package net.rockerle.mapbot.mapbot.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.debug.BlockOutlineDebugRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class OutlineRenderer extends BlockOutlineDebugRenderer {
    public record PathColor(float r, float g, float b, float a) {
    }

    private Map<List<BlockPos>, PathColor> pathsToDraw = new HashMap<>();

    public boolean visCurrent = true;

    public OutlineRenderer(MinecraftClient client) {
        super(client);
        WorldRenderEvents.LAST.register(context ->{
            pathsToDraw.forEach((p, c) -> renderOutlines(context, (LinkedList) p, c.r(), c.g(), c.b(), c.a()));
        });
    }

    public void render(Matrix4f posMatrix, Vec3d cameraPos, LinkedList<BlockPos> blocksToOutline, float r, float g, float b, float a) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(2.0F);
        RenderSystem.depthMask(true);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.disableDepthTest();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

        for (BlockPos pos : blocksToOutline) {
            float x = (float) (pos.getX() - cameraPos.getX());
            float y = (float) (pos.getY() - cameraPos.getY());
            float z = (float) (pos.getZ() - cameraPos.getZ());

            // down
//            bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(posMatrix, x, y, z).color(r, g, b, a);
            bufferBuilder.vertex(posMatrix, x, y, z + 1.0f).color(r, g, b, a);
            bufferBuilder.vertex(posMatrix, x + 1.0f, y, z + 1.0f).color(r, g, b, a);
            bufferBuilder.vertex(posMatrix, x + 1.0f, y, z).color(r, g, b, a);
//            tessellator.draw();
            // west
//            bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(posMatrix, x, y, z).color(r, g, b, a);
            bufferBuilder.vertex(posMatrix, x + 1.0f, y, z).color(r, g, b, a);
            bufferBuilder.vertex(posMatrix, x + 1.0f, y + 1.0f, z).color(r, g, b, a);
            bufferBuilder.vertex(posMatrix, x, y + 1.0f, z).color(r, g, b, a);
//            tessellator.draw();
            // south
//            bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(posMatrix, x, y, z).color(r, g, b, a);
            bufferBuilder.vertex(posMatrix, x, y + 1.0f, z).color(r, g, b, a);
            bufferBuilder.vertex(posMatrix, x, y + 1.0f, z + 1.0f).color(r, g, b, a);
            bufferBuilder.vertex(posMatrix, x, y, z + 1.0f).color(r, g, b, a);
//            tessellator.draw();
            // east
//            bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(posMatrix, x, y + 1.0f, z + 1.0f).color(r, g, b, a);
            bufferBuilder.vertex(posMatrix, x + 1.0f, y + 1.0f, z + 1.0f).color(r, g, b, a);
            bufferBuilder.vertex(posMatrix, x + 1.0f, y, z + 1.0f).color(r, g, b, a);
//            tessellator.draw();
            // north
//            bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(posMatrix, x + 1.0f, y + 1.0f, z).color(r, g, b, a);
            bufferBuilder.vertex(posMatrix, x + 1.0f, y + 1.0f, z + 1.0f).color(r, g, b, a);
//            tessellator.draw();
        }
//
//        RenderSystem.enableDepthTest();
//        RenderSystem.depthMask(true);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
    }

    public void addPath(List path, PathColor p) {
        this.pathsToDraw.put(path, p);
    }

    public void visualizePath(LinkedList<BlockPos> p) {
        this.addPath(p, new PathColor(0.980f, 0.239f, 0.0f, 1.0f));
    }

    private void renderOutlines(WorldRenderContext wrc, LinkedList<BlockPos> path, float r, float g, float b, float a) {
        this.render(wrc.matrixStack().peek().getPositionMatrix(), wrc.camera().getPos(), path, r, g, b, a);
    }

    public void visualizeCurrentPath(LinkedList<BlockPos> blockPos){
        this.clearRenderedPath();
        this.addPath(blockPos, new PathColor(0.0f, 1.0f, 0.0f, 1.0f));
    }

    public void clearRenderedPath() {
        this.pathsToDraw.clear();
    }
}