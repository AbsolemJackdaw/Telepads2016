package subaraki.telepads.tileentity.render;

import java.nio.FloatBuffer;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class RenderEndPortalFrame {

	private static final ResourceLocation enderPortalEndSkyTextures = new ResourceLocation("textures/environment/end_sky.png");
	private static final ResourceLocation endPortalTextures = new ResourceLocation("textures/entity/end_portal.png");
	private static final Random teleporterRandom = new Random(31100L);
	private static final FloatBuffer MODELVIEW = GLAllocation.createDirectFloatBuffer(16);
	private static final FloatBuffer PROJECTION = GLAllocation.createDirectFloatBuffer(16);

	private FloatBuffer floatBuffer = GLAllocation.createDirectFloatBuffer(16);

	public void renderEndPortalSurface (double x, double y, double z, TileEntityRendererDispatcher rendererDispatcher) {
		GlStateManager.disableLighting();
		teleporterRandom.setSeed(31100L);
		GlStateManager.getFloat(2982, MODELVIEW);
		GlStateManager.getFloat(2983, PROJECTION);
		double d0 = x * x + y * y + z * z;
		int i = this.getPasses(d0);
		float f = this.getOffset();

		for (int j = 0; j < i; ++j)
		{
			GlStateManager.pushMatrix();
			float f1 = 2.0F / (float)(18 - j);

			if (j == 0)
			{
				rendererDispatcher.renderEngine.bindTexture(enderPortalEndSkyTextures);
				f1 = 0.15F;
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			}

			if (j >= 1)
			{
				rendererDispatcher.renderEngine.bindTexture(endPortalTextures);
			}

			if (j == 1)
			{
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
			}

			GlStateManager.texGen(GlStateManager.TexGen.S, 9216);
			GlStateManager.texGen(GlStateManager.TexGen.T, 9216);
			GlStateManager.texGen(GlStateManager.TexGen.R, 9216);
			GlStateManager.texGen(GlStateManager.TexGen.S, 9474, this.getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
			GlStateManager.texGen(GlStateManager.TexGen.T, 9474, this.getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
			GlStateManager.texGen(GlStateManager.TexGen.R, 9474, this.getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
			GlStateManager.enableTexGenCoord(GlStateManager.TexGen.S);
			GlStateManager.enableTexGenCoord(GlStateManager.TexGen.T);
			GlStateManager.enableTexGenCoord(GlStateManager.TexGen.R);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.loadIdentity();
			GlStateManager.translate(0.5F, 0.5F, 0.0F);
			GlStateManager.scale(0.5F, 0.5F, 1.0F);
			float f2 = (float)(j + 1);
			GlStateManager.translate(17.0F / f2, (2.0F + f2 / 1.5F) * ((float)Minecraft.getSystemTime() % 800000.0F / 800000.0F), 0.0F);
			GlStateManager.rotate((f2 * f2 * 4321.0F + f2 * 9.0F) * 2.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.scale(4.5F - f2 / 4.0F, 4.5F - f2 / 4.0F, 1.0F);
			GlStateManager.multMatrix(PROJECTION);
			GlStateManager.multMatrix(MODELVIEW);
			Tessellator tessellator = Tessellator.getInstance();
			VertexBuffer vertexbuffer = tessellator.getBuffer();
			vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
			float f3 = (teleporterRandom.nextFloat() * 0.5F + 0.1F) * f1;
			float f4 = (teleporterRandom.nextFloat() * 0.5F + 0.4F) * f1;
			float f5 = (teleporterRandom.nextFloat() * 0.5F + 0.5F) * f1;

			vertexbuffer.pos(x, y + (double)f, z + 1.0D).color(f3, f4, f5, 1.0F).endVertex();
			vertexbuffer.pos(x + 1.0D, y + (double)f, z + 1.0D).color(f3, f4, f5, 1.0F).endVertex();
			vertexbuffer.pos(x + 1.0D, y + (double)f, z).color(f3, f4, f5, 1.0F).endVertex();
			vertexbuffer.pos(x, y + (double)f, z).color(f3, f4, f5, 1.0F).endVertex();

			tessellator.draw();
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
			rendererDispatcher.renderEngine.bindTexture(enderPortalEndSkyTextures);
		}

		GlStateManager.disableBlend();
		GlStateManager.disableTexGenCoord(GlStateManager.TexGen.S);
		GlStateManager.disableTexGenCoord(GlStateManager.TexGen.T);
		GlStateManager.disableTexGenCoord(GlStateManager.TexGen.R);
		GlStateManager.enableLighting();
	}
	
	  protected int getPasses(double p_191286_1_)
	    {
	        int i;

	        if (p_191286_1_ > 36864.0D)
	        {
	            i = 1;
	        }
	        else if (p_191286_1_ > 25600.0D)
	        {
	            i = 3;
	        }
	        else if (p_191286_1_ > 16384.0D)
	        {
	            i = 5;
	        }
	        else if (p_191286_1_ > 9216.0D)
	        {
	            i = 7;
	        }
	        else if (p_191286_1_ > 4096.0D)
	        {
	            i = 9;
	        }
	        else if (p_191286_1_ > 1024.0D)
	        {
	            i = 11;
	        }
	        else if (p_191286_1_ > 576.0D)
	        {
	            i = 13;
	        }
	        else if (p_191286_1_ > 256.0D)
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
