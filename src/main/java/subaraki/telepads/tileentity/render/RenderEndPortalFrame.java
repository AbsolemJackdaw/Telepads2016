package subaraki.telepads.tileentity.render;

import java.nio.FloatBuffer;
import java.util.Random;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import subaraki.telepads.screen.TeleportScreen;

public class RenderEndPortalFrame {

    private static final ResourceLocation enderPortalEndSkyTextures = new ResourceLocation("textures/environment/end_sky.png");
    private static final ResourceLocation endPortalTextures = new ResourceLocation("textures/entity/end_portal.png");
    private static final Random teleporterRandom = new Random(31100L);
    private static final FloatBuffer MODELVIEW = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer PROJECTION = GLAllocation.createDirectFloatBuffer(16);

    private FloatBuffer floatBuffer = GLAllocation.createDirectFloatBuffer(16);

    public void renderEndPortalSurface(double x, double y, double z, TileEntityRendererDispatcher rendererDispatcher)
    {

        GlStateManager.disableLighting();
        teleporterRandom.setSeed(31100L);
        GlStateManager.getMatrix(2982, MODELVIEW);
        GlStateManager.getMatrix(2983, PROJECTION);
        double d0 = x * x + y * y + z * z;
        int i = this.getPasses(d0);
        float f = this.getOffset();

        for (int j = 0; j < i; ++j)
        {
            GlStateManager.pushMatrix();
            float f1 = 2.0F / (float) (18 - j);

            if (j == 0)
            {
                rendererDispatcher.textureManager.bindTexture(enderPortalEndSkyTextures);
                f1 = 0.15F;
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            }

            if (j >= 1)
            {
                rendererDispatcher.textureManager.bindTexture(endPortalTextures);
            }

            if (j == 1)
            {
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
            }

            GlStateManager.texGenMode(GlStateManager.TexGen.S, 9216);
            GlStateManager.texGenMode(GlStateManager.TexGen.T, 9216);
            GlStateManager.texGenMode(GlStateManager.TexGen.R, 9216);
            GlStateManager.texGenParam(GlStateManager.TexGen.S, 9474, this.getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.texGenParam(GlStateManager.TexGen.T, 9474, this.getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
            GlStateManager.texGenParam(GlStateManager.TexGen.R, 9474, this.getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
            GlStateManager.enableTexGen(GlStateManager.TexGen.S);
            GlStateManager.enableTexGen(GlStateManager.TexGen.T);
            GlStateManager.enableTexGen(GlStateManager.TexGen.R);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            GlStateManager.translatef(0.5F, 0.5F, 0.0F);
            GlStateManager.scalef(0.5F, 0.5F, 1.0F);
            float f2 = (float) (j + 1);
            GlStateManager.translatef(17.0F / f2, (2.0F + f2 / 1.5F) * ((float) Util.milliTime() % 800000.0F / 800000.0F), 0.0F);
            GlStateManager.rotatef((f2 * f2 * 4321.0F + f2 * 9.0F) * 2.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.scalef(4.5F - f2 / 4.0F, 4.5F - f2 / 4.0F, 1.0F);
            GlStateManager.multMatrix(PROJECTION);
            GlStateManager.multMatrix(MODELVIEW);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
            float f3 = (teleporterRandom.nextFloat() * 0.5F + 0.1F) * f1;
            float f4 = (teleporterRandom.nextFloat() * 0.5F + 0.4F) * f1;
            float f5 = (teleporterRandom.nextFloat() * 0.5F + 0.5F) * f1;

            bufferbuilder.pos(x, y + (double) f, z + 1.0D).color(f3, f4, f5, 1.0F).endVertex();
            bufferbuilder.pos(x + 1.0D, y + (double) f, z + 1.0D).color(f3, f4, f5, 1.0F).endVertex();
            bufferbuilder.pos(x + 1.0D, y + (double) f, z).color(f3, f4, f5, 1.0F).endVertex();
            bufferbuilder.pos(x, y + (double) f, z).color(f3, f4, f5, 1.0F).endVertex();

            tessellator.draw();
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
            rendererDispatcher.textureManager.bindTexture(enderPortalEndSkyTextures);
        }

        GlStateManager.disableBlend();
        GlStateManager.disableTexGen(GlStateManager.TexGen.S);
        GlStateManager.disableTexGen(GlStateManager.TexGen.T);
        GlStateManager.disableTexGen(GlStateManager.TexGen.R);
        GlStateManager.enableLighting();
    }

    public void renderEndPortalSurfaceGUI(double x, double y, double z, Minecraft mc, TeleportScreen gui)
    {

        GlStateManager.disableLighting();
        teleporterRandom.setSeed(31100L);
        GlStateManager.getMatrix(2982, MODELVIEW);
        GlStateManager.getMatrix(2983, PROJECTION);
        double d0 = x * x + y * y + z * z;
        int i = this.getPasses(d0);

        for (int j = 0; j < i; ++j)
        {
            GlStateManager.pushMatrix();
            float f1 = 2.0F / (float) (18 - j);

            if (j == 0)
            {
                mc.textureManager.bindTexture(enderPortalEndSkyTextures);
                f1 = 0.15F;
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            }

            if (j >= 1)
            {
                mc.textureManager.bindTexture(endPortalTextures);
            }

            if (j == 1)
            {
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
            }

            GlStateManager.texGenMode(GlStateManager.TexGen.S, 9216);
            GlStateManager.texGenMode(GlStateManager.TexGen.T, 9216);
            GlStateManager.texGenMode(GlStateManager.TexGen.R, 9216);
            GlStateManager.texGenParam(GlStateManager.TexGen.S, 9474, this.getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.texGenParam(GlStateManager.TexGen.T, 9474, this.getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
            GlStateManager.texGenParam(GlStateManager.TexGen.R, 9474, this.getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
            GlStateManager.enableTexGen(GlStateManager.TexGen.S);
            GlStateManager.enableTexGen(GlStateManager.TexGen.T);
            GlStateManager.enableTexGen(GlStateManager.TexGen.R);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            GlStateManager.translatef(0.5F, 0.5F, 0.0F);
            GlStateManager.scalef(0.5F, 0.5F, 1.0F);
            float f2 = (float) (j + 1);

            GlStateManager.translatef(17.0F / f2, (2.0F + f2 / 1.5F) * ((float) Util.milliTime() % 800000.0F / 800000.0F), 0.0F);
            GlStateManager.rotatef((f2 * f2 * 4321.0F + f2 * 9.0F) * 2.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.scalef(4.5F - f2 / 4.0F, 4.5F - f2 / 4.0F, 1.0F);
            GlStateManager.multMatrix(PROJECTION);
            GlStateManager.multMatrix(MODELVIEW);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();

            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
            float f3 = (teleporterRandom.nextFloat() * 0.5F + 0.1F) * f1;
            float f4 = (teleporterRandom.nextFloat() * 0.5F + 0.4F) * f1;
            float f5 = (teleporterRandom.nextFloat() * 0.5F + 0.5F) * f1;

            int sizex = mc.mainWindow.getScaledWidth();// mc.mainWindow.getWidth() + 2;
            int sizey = mc.mainWindow.getScaledHeight(); // mc.mainWindow.getHeight() + 2;
            bufferbuilder.pos(0, sizey, 0).color(f3, f4, f5, 1.0F).endVertex();
            bufferbuilder.pos(sizex, sizey, 0).color(f3, f4, f5, 1.0F).endVertex();
            bufferbuilder.pos(sizex, 0, 0).color(f3, f4, f5, 1.0F).endVertex();
            bufferbuilder.pos(0, 0, 0).color(f3, f4, f5, 1.0F).endVertex();

            tessellator.draw();
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
            mc.textureManager.bindTexture(enderPortalEndSkyTextures);
        }

        GlStateManager.disableBlend();
        GlStateManager.disableTexGen(GlStateManager.TexGen.S);
        GlStateManager.disableTexGen(GlStateManager.TexGen.T);
        GlStateManager.disableTexGen(GlStateManager.TexGen.R);
        GlStateManager.enableLighting();
    }

    protected int getPasses(double pass)
    {

        int i;
        if (pass > 36864.0D)
        {
            i = 1;
        }
        else
            if (pass > 25600.0D)
            {
                i = 3;
            }
            else
                if (pass > 16384.0D)
                {
                    i = 5;
                }
                else
                    if (pass > 9216.0D)
                    {
                        i = 7;
                    }
                    else
                        if (pass > 4096.0D)
                        {
                            i = 9;
                        }
                        else
                            if (pass > 1024.0D)
                            {
                                i = 11;
                            }
                            else
                                if (pass > 576.0D)
                                {
                                    i = 13;
                                }
                                else
                                    if (pass > 256.0D)
                                    {
                                        i = 14;
                                    }
                                    else
                                    {
                                        i = 15;
                                    }

        return i;
    }

    protected float getOffset()
    {

        return 0.75F;
    }

    private FloatBuffer getBuffer(float u, float v, float x, float y)
    {

        this.floatBuffer.clear();
        this.floatBuffer.put(u).put(v).put(x).put(y);
        this.floatBuffer.flip();
        return this.floatBuffer;
    }
}
