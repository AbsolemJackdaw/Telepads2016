package subaraki.telepads.tileentity.render;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.OverlayRenderer;
import net.minecraft.client.renderer.RenderState.OverlayState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.EmptyModelData;
import subaraki.telepads.tileentity.TileEntityTelepad;

public class TileEntityTelepadRenderer extends TileEntityRenderer<TileEntityTelepad> {

    private static ModelTelepad modeltelepad;

    private static String resourcePath = "telepads:textures/entity/tile/";
    private static ResourceLocation base = new ResourceLocation(resourcePath + "telepad_base.png");
    private static ResourceLocation pads = new ResourceLocation(resourcePath + "telepad_pads.png");
    private static ResourceLocation frame = new ResourceLocation(resourcePath + "telepad_frame.png");
    private static ResourceLocation frame_upgrade = new ResourceLocation(resourcePath + "telepad_dimension_upgrade.png");
    private static ResourceLocation frame_upgrade_2 = new ResourceLocation(resourcePath + "telepad_dimension_upgrade_2.png");
    private static ResourceLocation frame_upgrade_3 = new ResourceLocation(resourcePath + "telepad_dimension_upgrade_3.png");
    private static ResourceLocation frame_upgrade_4 = new ResourceLocation(resourcePath + "telepad_dimension_upgrade_4.png");

    private static int animation_counter;
    private RenderEndPortalFrame endPortalFrame;

    public TileEntityTelepadRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {

        super(rendererDispatcherIn);
        modeltelepad = new ModelTelepad(RenderType::entityCutoutNoCull);
        endPortalFrame = new RenderEndPortalFrame();
    }

    @Override
    public void render(TileEntityTelepad te, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
    {

        animation_counter++;

        if (te == null)
            return;
        TileEntityTelepad tet = null;
        if (te instanceof TileEntityTelepad)
            tet = (TileEntityTelepad) te;
        if (tet == null)
            return;

        if (!tet.hasRedstoneUpgrade() || tet.hasRedstoneUpgrade() && !tet.isPowered())
        {
            matrixStackIn.pushPose();
            endPortalFrame.render(tet, renderer, partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
            matrixStackIn.popPose();
        }

        matrixStackIn.pushPose();
        // set normal
        matrixStackIn.translate(0.5F, 2.25F, 0.5F);
        matrixStackIn.scale(1.0F, -1F, 1F);

        Color colorBase = new Color(tet.getColorArrow());
        Color colorFrame = new Color(tet.getColorFeet());

        renderPad(matrixStackIn, colorFrame, colorBase, bufferIn, combinedLightIn, combinedOverlayIn);

        if (tet.hasDimensionUpgrade())
        {
            matrixStackIn.pushPose();

            ResourceLocation resLocAnimation = frame_upgrade;
            if (animation_counter < 25)
                resLocAnimation = frame_upgrade;
            else
                if (animation_counter < 50)
                    resLocAnimation = frame_upgrade_2;
                else
                    if (animation_counter < 75)
                        resLocAnimation = frame_upgrade_3;
                    else
                        if (animation_counter < 99)
                        {// next tick is >=99, so resets and stays tex.4
                            resLocAnimation = frame_upgrade_4;
                        }
                        else
                        {
                            animation_counter = 0;
                            resLocAnimation = frame_upgrade_4;
                        }
            GL11.glColor3f(1, 1, 1);
            matrixStackIn.scale(0.75f, 0.75f, 0.75f);
            matrixStackIn.translate(-0.1f, 0.45f, 0.1f);

            switch (tet.getUpgradeRotation())
            {
            case 0:
                matrixStackIn.mulPose(new Quaternion(0, 0, 0, true));
                matrixStackIn.translate(0f, 0, 0f);
                break;
            case 1:
                matrixStackIn.mulPose(new Quaternion(0, -90, 0, true));
                matrixStackIn.translate(-0.1f, 0, 0f);
                break;
            case 2:
                matrixStackIn.mulPose(new Quaternion(0, 180, 0, true));
                matrixStackIn.translate(-0.2f, 0, 0.2f);
                break;
            case 3:
                matrixStackIn.mulPose(new Quaternion(0, 90, 0, true));
                matrixStackIn.translate(0f, 0, 0.2f);
                break;
            default:
                matrixStackIn.mulPose(new Quaternion(0, 90, 0, true));
                matrixStackIn.translate(0f, 0, 0.2f);
                break;
            }

            modeltelepad.renderUpgrade(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(resLocAnimation)), combinedLightIn,
                    combinedOverlayIn);
            matrixStackIn.popPose();
        }
        matrixStackIn.popPose();

        if (tet.hasRedstoneUpgrade())
        {
            renderTorch(tet, matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(PlayerContainer.BLOCK_ATLAS)), combinedOverlayIn,
                    -(0.0625 * 7), -(0.0625 * 4), (0.0625 * 7));
            renderTorch(tet, matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(PlayerContainer.BLOCK_ATLAS)), combinedOverlayIn,
                    -(0.0625 * 7), -(0.0625 * 4), -(0.0625 * 7));
            renderTorch(tet, matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(PlayerContainer.BLOCK_ATLAS)), combinedOverlayIn,
                    (0.0625 * 7), -(0.0625 * 4), (0.0625 * 7));
            renderTorch(tet, matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(PlayerContainer.BLOCK_ATLAS)), combinedOverlayIn,
                    (0.0625 * 7), -(0.0625 * 4), -(0.0625 * 7));
        }
    }

    private void renderTorch(TileEntityTelepad te, MatrixStack stack, IVertexBuilder builder, int combinedOverlay, double offsetX, double offsetY, double offsetZ)
    {

        stack.pushPose();
        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();
        World world = te.getLevel();
        BlockState state = Blocks.REDSTONE_TORCH.defaultBlockState().setValue(BlockStateProperties.LIT, te.isPowered());
        stack.translate(offsetX, offsetY, offsetZ);
        IBakedModel model = blockrendererdispatcher.getBlockModel(state);
        blockrendererdispatcher.getModelRenderer().renderModel(world, model, state, te.getBlockPos(), stack, builder, false, world.random, OverlayTexture.NO_OVERLAY, combinedOverlay,
                EmptyModelData.INSTANCE);
        stack.popPose();
    }

    /**
     * Renders a telepad model. this is rendered without norm and without end-portal
     * sky texture
     */
    public void renderPad(MatrixStack stack, Color colorFrame, Color colorBase, IRenderTypeBuffer bufferIn, int packedLightIn, int packedOverlayIn)
    {

        float f2 = 1.5f;
        stack.scale(f2, f2, f2);
        stack.pushPose();
        modeltelepad.renderArrows(stack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(base)), packedLightIn, packedOverlayIn,
                (float) (colorBase.getRed() / 255.0f), (float) (colorBase.getGreen() / 255.0f), (float) (colorBase.getBlue() / 255.0f));
        stack.popPose();

        stack.pushPose();
        modeltelepad.renderLegs(stack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(pads)), packedLightIn, packedOverlayIn,
                (float) (colorFrame.getRed() / 255.0f), (float) (colorFrame.getGreen() / 255.0f), (float) (colorFrame.getBlue() / 255.0f));
        // current fix : south and north legs are acting funky when rotated, so
        // duplicate west and est and turn them around.
        stack.mulPose(new Quaternion(0, 90, 0, true));
        modeltelepad.renderLegs(stack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(pads)), packedLightIn, packedOverlayIn,
                (float) (colorFrame.getRed() / 255.0f), (float) (colorFrame.getGreen() / 255.0f), (float) (colorFrame.getBlue() / 255.0f));
        // Reset normal
        stack.mulPose(new Quaternion(0, -90, 0, true));
        stack.popPose();

        stack.pushPose();
        modeltelepad.renderFrame(stack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(frame)), packedLightIn, packedOverlayIn, 1F, 1F, 1F);
        stack.popPose();

    }

}
