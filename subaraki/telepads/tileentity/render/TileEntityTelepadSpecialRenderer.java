package subaraki.telepads.tileentity.render;

import java.awt.Color;

import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import static net.minecraft.client.renderer.GlStateManager.*;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import subaraki.telepads.tileentity.TileEntityTelepad;

public class TileEntityTelepadSpecialRenderer extends TileEntitySpecialRenderer {

	private static final ModelTelepad modeltelepad = new ModelTelepad();

	private static String resourcePath = "telepads:textures/entity/tile/";
	private static ResourceLocation base = new ResourceLocation(resourcePath + "telepad_base.png");
	private static ResourceLocation pads = new ResourceLocation(resourcePath + "telepad_pads.png");
	private static ResourceLocation frame = new ResourceLocation(resourcePath + "telepad_frame.png");
	private static ResourceLocation frame_empty = new ResourceLocation(resourcePath + "telepad_frame_interDimension.png");
	private static ResourceLocation frame_upgrade = new ResourceLocation(resourcePath + "telepad_dimensionUpgrade.png");
	private static ResourceLocation frame_upgrade_2 = new ResourceLocation(resourcePath + "telepad_dimensionUpgrade_2.png");
	private static ResourceLocation frame_upgrade_3 = new ResourceLocation(resourcePath + "telepad_dimensionUpgrade_3.png");
	private static ResourceLocation frame_upgrade_4 = new ResourceLocation(resourcePath + "telepad_dimensionUpgrade_4.png");
	private static ResourceLocation frame_powered_off = new ResourceLocation("minecraft:textures/blocks/red_sand.png");
	private static ResourceLocation frame_powered_on = new ResourceLocation("minecraft:textures/blocks/redstone_block.png");

	private static int animation_counter;
	private RenderEndPortalFrame endPortalFrame;

	//RenderBlocks renderBlocks;

	public TileEntityTelepadSpecialRenderer() {
		endPortalFrame = new RenderEndPortalFrame();
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {
		animation_counter++;

		if(te == null)return;
		TileEntityTelepad tet = null;
		if(te instanceof TileEntityTelepad)
			tet = (TileEntityTelepad)te;
		if(tet == null)return;

		if (!tet.hasRedstoneUpgrade() || tet.hasRedstoneUpgrade() && !tet.isPowered()) {
			pushMatrix();
			endPortalFrame.renderEndPortalSurface(x, y-0.56f, z, this.rendererDispatcher);
			popMatrix();
		}

		pushMatrix();
		// set normal
		translate((float) x + 0.5F, (float) y + 2.25F, (float) z + 0.5F);
		scale(1.0F, -1F, -1F);

		Color colorBase = new Color(tet.getColorArrow());
		Color colorFrame = new Color(tet.getColorFeet());

		renderPad(te, colorFrame, colorBase, x, y, z, partialTicks);

		if (tet.hasDimensionUpgrade()) {
			pushMatrix();

			if (animation_counter < 25)
				bindTexture(frame_upgrade);
			else if (animation_counter < 50)
				bindTexture(frame_upgrade_2);
			else if (animation_counter < 75)
				bindTexture(frame_upgrade_3);
			else if (animation_counter < 99){//next tick is >=99, so resets and stays tex.4
				bindTexture(frame_upgrade_4);
			}else{
				animation_counter = 0;
				bindTexture(frame_upgrade_4);
			}
			color(1, 1, 1);
			scale(0.75f, 0.75f, 0.75f);
			translate(-0.1f, 0.45f, 0.1f);

			switch (tet.getUpgradeRotation()) {
			case 0:
				rotate(0f, 0, 1, 0);
				translate(0f, 0, 0f);
				break;
			case 1:
				rotate(-90f, 0, 1, 0);
				translate(-0.1f, 0, 0f);
				break;
			case 2:
				rotate(180f, 0, 1, 0);
				translate(-0.2f, 0, 0.2f);
				break;
			case 3:
				rotate(90f, 0, 1, 0);
				translate(0f, 0, 0.2f);
				break;
			default:
				rotate(90f, 0, 1, 0);
				translate(0f, 0, 0.2f);
				break;
			}

			modeltelepad.renderUpgrade(0.0625f);
			popMatrix();
		}
		popMatrix();

		if (tet.hasRedstoneUpgrade()) {
			renderTorch(tet, -0.5, 0.4, -0.5, x, y, z);
			renderTorch(tet, 0.5, 0.4, -0.5, x, y, z);
			renderTorch(tet, -0.5, 0.4, 0.5, x, y, z);
			renderTorch(tet, 0.5, 0.4, 0.5, x, y, z);
		}
	}

	private void renderTorch (TileEntityTelepad te, double offsetX, double offsetY, double offsetZ, double x, double y, double z) {
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();

		BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
		this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		vertexbuffer.begin(7, DefaultVertexFormats.BLOCK);
		vertexbuffer.setTranslation(x - te.getPos().getX()- offsetX, y - te.getPos().getY() - offsetY, z - te.getPos().getZ() - offsetZ);
		World world = this.getWorld();

		IBlockState state = null;

		if(te.isPowered())
			state = Blocks.REDSTONE_TORCH.getDefaultState();
		else
			state = Blocks.UNLIT_REDSTONE_TORCH.getDefaultState();

		blockrendererdispatcher.getBlockModelRenderer().renderModel(world, blockrendererdispatcher.getModelForState(state), state, te.getPos(), vertexbuffer, false);

		vertexbuffer.setTranslation(0.0D, 0.0D, 0.0D);
		tessellator.draw();

	}

	/**
	 * Renders a telepad model. this is rendered without norm and without end-portal sky
	 * texture
	 */
	public void renderPad (TileEntity tileentity, Color colorFrame, Color colorBase, double x, double y, double z, float f) {

		TileEntityTelepad te = null;

		if (tileentity instanceof TileEntityTelepad)
			te = (TileEntityTelepad) tileentity;

		float f2 = 1.5f;
		scale(f2, f2, f2);

		pushMatrix();
		bindTexture(base);
		color((float) (colorBase.getRed() / 255.0f), (float) (colorBase.getGreen() / 255.0f), (float) (colorBase.getBlue() / 255.0f));
		modeltelepad.renderArrows(0.0625f);
		popMatrix();
		
		pushMatrix();
		bindTexture(pads);
		color((float) (colorFrame.getRed() / 255.0f), (float) (colorFrame.getGreen() / 255.0f), (float) (colorFrame.getBlue() / 255.0f));
		modeltelepad.renderLegs(0.0625f);
		//current fix : south and north legs are acting funky when rotated, so duplicate west and est and turn them around.
		rotate(90, 0, 1, 0);
		modeltelepad.renderLegs(0.0625f);
		//Reset normal
		rotate(-90, 0, 1, 0);
		popMatrix();
		
		pushMatrix();
		bindTexture(frame);
		color(1f, 1f, 1f);
		modeltelepad.renderFrame(0.0625f);
		popMatrix();

	}
}
