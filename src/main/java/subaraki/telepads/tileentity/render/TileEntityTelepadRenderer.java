package subaraki.telepads.tileentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.data.EmptyModelData;
import org.lwjgl.opengl.GL11;
import subaraki.telepads.tileentity.TileEntityTelepad;

import java.awt.*;

public class TileEntityTelepadRenderer implements BlockEntityRenderer<TileEntityTelepad> {

    private static ModelTelepad modeltelepad;

    private static final String resourcePath = "telepads:textures/entity/tile/";
    private static final ResourceLocation base = new ResourceLocation(resourcePath + "telepad_base.png");
    private static final ResourceLocation pads = new ResourceLocation(resourcePath + "telepad_pads.png");
    private static final ResourceLocation frame = new ResourceLocation(resourcePath + "telepad_frame.png");
    private static final ResourceLocation frame_upgrade = new ResourceLocation(resourcePath + "telepad_dimension_upgrade.png");
    private static final ResourceLocation frame_upgrade_2 = new ResourceLocation(resourcePath + "telepad_dimension_upgrade_2.png");
    private static final ResourceLocation frame_upgrade_3 = new ResourceLocation(resourcePath + "telepad_dimension_upgrade_3.png");
    private static final ResourceLocation frame_upgrade_4 = new ResourceLocation(resourcePath + "telepad_dimension_upgrade_4.png");

    private static int animation_counter;
    private final RenderEndPortalFrame endPortalFrame;

    public TileEntityTelepadRenderer() {
        modeltelepad = new ModelTelepad(RenderType::entityCutoutNoCull);
        endPortalFrame = new RenderEndPortalFrame();
    }

    @Override
    public void render(TileEntityTelepad te, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

        animation_counter++;

        if (!te.hasRedstoneUpgrade() || te.hasRedstoneUpgrade() && !te.isPowered()) {
            matrixStackIn.pushPose();
            endPortalFrame.render(te, Minecraft.getInstance().getBlockEntityRenderDispatcher(), partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
            matrixStackIn.popPose();
        }

        matrixStackIn.pushPose();
        // set normal
        matrixStackIn.translate(0.5F, 2.25F, 0.5F);
        matrixStackIn.scale(1.0F, -1F, 1F);

        Color colorBase = new Color(te.getColorArrow());
        Color colorFrame = new Color(te.getColorFeet());

        renderPad(matrixStackIn, colorFrame, colorBase, bufferIn, combinedLightIn, combinedOverlayIn);

        if (te.hasDimensionUpgrade()) {
            matrixStackIn.pushPose();

            ResourceLocation resLocAnimation;
            if (animation_counter < 25)
                resLocAnimation = frame_upgrade;
            else if (animation_counter < 50)
                resLocAnimation = frame_upgrade_2;
            else if (animation_counter < 75)
                resLocAnimation = frame_upgrade_3;
            else if (animation_counter < 99) {// next tick is >=99, so resets and stays tex.4
                resLocAnimation = frame_upgrade_4;
            } else {
                animation_counter = 0;
                resLocAnimation = frame_upgrade_4;
            }
            GL11.glColor3f(1, 1, 1);
            matrixStackIn.scale(0.75f, 0.75f, 0.75f);
            matrixStackIn.translate(-0.1f, 0.45f, 0.1f);

            switch (te.getUpgradeRotation()) {
                case 0 -> {
                    matrixStackIn.mulPose(new Quaternion(0, 0, 0, true));
                    matrixStackIn.translate(0f, 0, 0f);
                }
                case 1 -> {
                    matrixStackIn.mulPose(new Quaternion(0, -90, 0, true));
                    matrixStackIn.translate(-0.1f, 0, 0f);
                }
                case 2 -> {
                    matrixStackIn.mulPose(new Quaternion(0, 180, 0, true));
                    matrixStackIn.translate(-0.2f, 0, 0.2f);
                }
                default -> {// also case 3
                    matrixStackIn.mulPose(new Quaternion(0, 90, 0, true));
                    matrixStackIn.translate(0f, 0, 0.2f);
                }
            }

            modeltelepad.renderUpgrade(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(resLocAnimation)), combinedLightIn,
                    combinedOverlayIn);
            matrixStackIn.popPose();
        }
        matrixStackIn.popPose();

        if (te.hasRedstoneUpgrade()) {
            renderTorch(te, matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(InventoryMenu.BLOCK_ATLAS)), combinedOverlayIn,
                    -(0.0625 * 7), -(0.0625 * 4), (0.0625 * 7));
            renderTorch(te, matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(InventoryMenu.BLOCK_ATLAS)), combinedOverlayIn,
                    -(0.0625 * 7), -(0.0625 * 4), -(0.0625 * 7));
            renderTorch(te, matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(InventoryMenu.BLOCK_ATLAS)), combinedOverlayIn,
                    (0.0625 * 7), -(0.0625 * 4), (0.0625 * 7));
            renderTorch(te, matrixStackIn, bufferIn.getBuffer(RenderType.entityCutoutNoCull(InventoryMenu.BLOCK_ATLAS)), combinedOverlayIn,
                    (0.0625 * 7), -(0.0625 * 4), -(0.0625 * 7));
        }
    }

    private void renderTorch(TileEntityTelepad te, PoseStack stack, VertexConsumer builder, int combinedOverlay, double offsetX, double offsetY, double offsetZ) {

        stack.pushPose();
        BlockRenderDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();
        BlockState state = Blocks.REDSTONE_TORCH.defaultBlockState().setValue(BlockStateProperties.LIT, te.isPowered());
        stack.translate(offsetX, offsetY, offsetZ);
        BakedModel model = blockrendererdispatcher.getBlockModel(state);
        blockrendererdispatcher.getModelRenderer().renderModel(stack.last(), builder, te.getBlockState(), model, 0, 0, 0, OverlayTexture.NO_OVERLAY, combinedOverlay, EmptyModelData.INSTANCE);
        stack.popPose();
    }

    /**
     * Renders a telepad model. this is rendered without norm and without end-portal
     * sky texture
     */
    public void renderPad(PoseStack stack, Color colorFrame, Color colorBase, MultiBufferSource bufferIn, int packedLightIn, int packedOverlayIn) {

        float f2 = 1.5f;
        stack.scale(f2, f2, f2);
        stack.pushPose();
        modeltelepad.renderArrows(stack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(base)), packedLightIn, packedOverlayIn,
                ((float)colorBase.getRed() / 255.0f), ((float)colorBase.getGreen() / 255.0f), ((float)colorBase.getBlue() / 255.0f));
        stack.popPose();

        stack.pushPose();
        modeltelepad.renderLegs(stack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(pads)), packedLightIn, packedOverlayIn,
                ((float)colorFrame.getRed() / 255.0f), ((float)colorFrame.getGreen() / 255.0f), ((float)colorFrame.getBlue() / 255.0f));
        // current fix : south and north legs are acting funky when rotated, so
        // duplicate west and est and turn them around.
        stack.mulPose(new Quaternion(0, 90, 0, true));
        modeltelepad.renderLegs(stack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(pads)), packedLightIn, packedOverlayIn,
                ((float)colorFrame.getRed() / 255.0f), ((float)colorFrame.getGreen() / 255.0f), ((float)colorFrame.getBlue() / 255.0f));
        // Reset normal
        stack.mulPose(new Quaternion(0, -90, 0, true));
        stack.popPose();

        stack.pushPose();
        modeltelepad.renderFrame(stack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(frame)), packedLightIn, packedOverlayIn, 1F, 1F, 1F);
        stack.popPose();

    }

}
