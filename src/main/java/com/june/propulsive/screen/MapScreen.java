package com.june.propulsive.screen;

import com.june.propulsive.Propulsive;
import com.june.propulsive.types.Planet;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

// TODO
public class MapScreen extends Screen {

    protected MapScreen(Text title) {
        super(title);
    }

    public MapScreen() {
        super(Text.of("Map Screen"));
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        double zoom = 1.0; // TODO : Make configurable (Scroll wheel?)

        double[] p = worldToMapCoords(this.client.player.getX(), this.client.player.getZ(), 1);
        Matrix4f positionMatrix = context.getMatrices().peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
        buffer.vertex(positionMatrix, (float) (p[0] - 10.0), (float) (p[1] - 10.0), 0).color(1f, 1f, 1f, 1f).texture(0f, 0f).next();
        buffer.vertex(positionMatrix, (float) (p[0] - 10.0), (float) (p[1] + 10.0), 0).color(1f, 0f, 0f, 1f).texture(0f, 1f).next();
        buffer.vertex(positionMatrix, (float) (p[0] + 10.0), (float) (p[1] + 10.0), 0).color(0f, 1f, 0f, 1f).texture(1f, 1f).next();
        buffer.vertex(positionMatrix, (float) (p[0] + 10.0), (float) (p[1] - 10.0), 0).color(0f, 0f, 1f, 1f).texture(1f, 0f).next();
        RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
        RenderSystem.setShaderTexture(0, new Identifier("propulsive:textures/gui/player.png"));
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        tessellator.draw();


        for (Planet planet : Propulsive.TickablePlanets) {

                double[] pos = worldToMapCoords(planet.planetPos.x, planet.planetPos.z, zoom);
                double x = pos[0];
                double z = pos[1];
                double planetScale = planet.planetSize / zoom;
                Matrix4f pm = context.getMatrices().peek().getPositionMatrix();
                Tessellator t = Tessellator.getInstance();
                BufferBuilder b = t.getBuffer();
                b.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
                b.vertex(pm, (float) (x - planetScale), (float) (z - planetScale), 0).color(1f, 1f, 1f, 1f).texture(0f, 0f).next();
                b.vertex(pm, (float) (x - planetScale), (float) (z + planetScale), 0).color(1f, 0f, 0f, 1f).texture(0f, 1f).next();
                b.vertex(pm, (float) (x + planetScale), (float) (z + planetScale), 0).color(0f, 1f, 0f, 1f).texture(1f, 1f).next();
                b.vertex(pm, (float) (x + planetScale), (float) (z - planetScale), 0).color(0f, 0f, 1f, 1f).texture(1f, 0f).next();
                RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
                RenderSystem.setShaderTexture(0, planet.texture2d);
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                t.draw();
            }
    }

    public double[] worldToMapCoords(double x, double y, double zoom) {
        double screenX = ((x + (this.client.currentScreen.width / 2)) / zoom) - this.client.player.getX();
        double screenZ = ((y + (this.client.currentScreen.height / 2)) / zoom) - this.client.player.getZ();
        return new double[] { screenX, screenZ };
    }

}