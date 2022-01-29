package it.hurts.sskirillss.relics.client.particles.circle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;

import javax.annotation.Nonnull;
import java.awt.*;

public class CircleTintParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    float resizeSpeed;

    public CircleTintParticle(ClientLevel world, double x, double y, double z, double velocityX,
                              double velocityY, double velocityZ, Color spark, float diameter,
                              int lifeTime, float resizeSpeed, boolean shouldCollide, SpriteSet sprites) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.sprites = sprites;
        this.resizeSpeed = resizeSpeed;
        setColor(spark.getRed() / 255.0F, spark.getGreen() / 255.0F, spark.getBlue() / 255.0F);
        setSize(diameter, diameter);

        lifetime = lifeTime;

        quadSize = diameter;
        this.alpha = 1.0F;

        xd = velocityX;
        yd = velocityY;
        zd = velocityZ;

        this.hasPhysics = shouldCollide;
    }

    @Override
    public float getQuadSize(float scaleFactor) {
        return this.quadSize;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return LightTexture.pack(15, 15);
    }

    @Nonnull
    @Override
    public ParticleRenderType getRenderType() {
        return RENDERER;
    }

    @Override
    public void tick() {
        this.quadSize *= this.resizeSpeed;

        xo = x;
        yo = y;
        zo = z;

        move(xd, yd, zd);

        if (onGround) {
            this.remove();
        }

        if (yo == y && yd > 0) {
            this.remove();
        }

        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    private static final ParticleRenderType RENDERER = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
            RenderSystem.setShader(GameRenderer::getParticleShader);

            RenderSystem.depthMask(true);

            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);

            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator tessellator) {
            tessellator.end();

            RenderSystem.enableDepthTest();

            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
        }

        @Override
        public String toString() {
            return Reference.MODID + ":" + "circle_tint";
        }
    };
}