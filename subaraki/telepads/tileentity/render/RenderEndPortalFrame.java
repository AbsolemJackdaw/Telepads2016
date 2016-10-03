package subaraki.telepads.tileentity.render;

import java.nio.FloatBuffer;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class RenderEndPortalFrame {

	private static final ResourceLocation enderPortalEndSkyTextures = new ResourceLocation("textures/environment/end_sky.png");
	private static final ResourceLocation endPortalTextures = new ResourceLocation("textures/entity/end_portal.png");
	private static final Random teleporterRandom = new Random(31100L);

	private FloatBuffer floatBuffer = GLAllocation.createDirectFloatBuffer(16);


	private FloatBuffer getBuffer (float par1, float par2, float par3, float par4) {
		this.floatBuffer.clear();
		this.floatBuffer.put(par1).put(par2).put(par3).put(par4);
		this.floatBuffer.flip();
		return this.floatBuffer;
	}

	public void renderEndPortalSurface (double x, double y, double z, TileEntityRendererDispatcher rendererDispatcher) {
		float f = (float)rendererDispatcher.entityX;
		float f1 = (float)rendererDispatcher.entityY;
		float f2 = (float)rendererDispatcher.entityZ;
		GlStateManager.disableLighting();
		teleporterRandom.setSeed(31100L);
		float f3 = 0.75F;

		for (int i = 0; i < 16; ++i)
		{
			GlStateManager.pushMatrix();
			float f4 = (float)(16 - i);
			float f5 = 0.0625F;
			float f6 = 1.0F / (f4 + 1.0F);

			if (i == 0)
			{
				rendererDispatcher.renderEngine.bindTexture(enderPortalEndSkyTextures);
				f6 = 0.1F;
				f4 = 65.0F;
				f5 = 0.125F;
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			}

			if (i >= 1)
			{
				rendererDispatcher.renderEngine.bindTexture(endPortalTextures);
			}

			if (i == 1)
			{
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
				f5 = 0.5F;
			}

			float f7 = (float)(-(y + 0.75D));
			float f8 = f7 + (float)ActiveRenderInfo.getPosition().yCoord;
			float f9 = f7 + f4 + (float)ActiveRenderInfo.getPosition().yCoord;
			float f10 = f8 / f9;
			f10 = (float)(y + 0.75D) + f10;
			GlStateManager.translate(f, f10, f2);
			GlStateManager.texGen(GlStateManager.TexGen.S, 9217);
			GlStateManager.texGen(GlStateManager.TexGen.T, 9217);
			GlStateManager.texGen(GlStateManager.TexGen.R, 9217);
			GlStateManager.texGen(GlStateManager.TexGen.Q, 9216);
			GlStateManager.texGen(GlStateManager.TexGen.S, 9473, this.getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
			GlStateManager.texGen(GlStateManager.TexGen.T, 9473, this.getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
			GlStateManager.texGen(GlStateManager.TexGen.R, 9473, this.getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
			GlStateManager.texGen(GlStateManager.TexGen.Q, 9474, this.getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
			GlStateManager.enableTexGenCoord(GlStateManager.TexGen.S);
			GlStateManager.enableTexGenCoord(GlStateManager.TexGen.T);
			GlStateManager.enableTexGenCoord(GlStateManager.TexGen.R);
			GlStateManager.enableTexGenCoord(GlStateManager.TexGen.Q);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.loadIdentity();
			GlStateManager.translate(0.0F, (float)(Minecraft.getSystemTime() % 700000L) / 700000.0F, 0.0F);
			GlStateManager.scale(f5, f5, f5);
			GlStateManager.translate(0.5F, 0.5F, 0.0F);
			GlStateManager.rotate((float)(i * i * 4321 + i * 9) * 2.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translate(-0.5F, -0.5F, 0.0F);
			GlStateManager.translate(-f, -f2, -f1);
			f8 = f7 + (float)ActiveRenderInfo.getPosition().yCoord;
			GlStateManager.translate((float)ActiveRenderInfo.getPosition().xCoord * f4 / f8, (float)ActiveRenderInfo.getPosition().zCoord * f4 / f8, -f1);
			Tessellator tessellator = Tessellator.getInstance();
			VertexBuffer vertexbuffer = tessellator.getBuffer();
			vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
			float f11 = (teleporterRandom.nextFloat() * 0.5F + 0.1F) * f6;
			float f12 = (teleporterRandom.nextFloat() * 0.5F + 0.4F) * f6;
			float f13 = (teleporterRandom.nextFloat() * 0.5F + 0.5F) * f6;
			vertexbuffer.pos(x + 0.05D, y + 0.75D, z + 0.05D).color(f11, f12, f13, 1.0F).endVertex();
			vertexbuffer.pos(x + 0.05D, y + 0.75D, z + 0.95D).color(f11, f12, f13, 1.0F).endVertex();
			vertexbuffer.pos(x + 0.95D, y + 0.75D, z + 0.95D).color(f11, f12, f13, 1.0F).endVertex();
			vertexbuffer.pos(x + 0.95D, y + 0.75D, z + 0.05D).color(f11, f12, f13, 1.0F).endVertex();
			tessellator.draw();
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
			rendererDispatcher.renderEngine.bindTexture(enderPortalEndSkyTextures);
		}

		GlStateManager.disableBlend();
		GlStateManager.disableTexGenCoord(GlStateManager.TexGen.S);
		GlStateManager.disableTexGenCoord(GlStateManager.TexGen.T);
		GlStateManager.disableTexGenCoord(GlStateManager.TexGen.R);
		GlStateManager.disableTexGenCoord(GlStateManager.TexGen.Q);
		GlStateManager.enableLighting();
	}
}
