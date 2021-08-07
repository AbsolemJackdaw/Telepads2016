package subaraki.telepads.tileentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.data.EmptyModelData;
import subaraki.telepads.registry.mod_bus.RegisterClientSetup;
import subaraki.telepads.tileentity.TileEntityTelepad;

import java.awt.*;

public class TileEntityTelepadRenderer implements BlockEntityRenderer<TileEntityTelepad> {

    private static final String resourcePath = "telepads:textures/entity/tile/";
    private static final ResourceLocation base = new ResourceLocation(resourcePath + "telepad_base.png");
    private static final ResourceLocation pads = new ResourceLocation(resourcePath + "telepad_pads.png");
    private static final ResourceLocation frame = new ResourceLocation(resourcePath + "telepad_frame.png");
    private static final ResourceLocation frame_upgrade = new ResourceLocation(resourcePath + "telepad_dimension_upgrade.png");
    private static final ResourceLocation frame_upgrade_2 = new ResourceLocation(resourcePath + "telepad_dimension_upgrade_2.png");
    private static final ResourceLocation frame_upgrade_3 = new ResourceLocation(resourcePath + "telepad_dimension_upgrade_3.png");
    private static final ResourceLocation frame_upgrade_4 = new ResourceLocation(resourcePath + "telepad_dimension_upgrade_4.png");
    private static ModelTelepad modeltelepad;
    private static int animation_counter;

    public TileEntityTelepadRenderer(BlockEntityRendererProvider.Context c) {
        modeltelepad = new ModelTelepad(Minecraft.getInstance().getEntityModels().bakeLayer(RegisterClientSetup.TELEPAD_BLOCK_MODEL_LAYER));
    }

    @Override
    public void render(TileEntityTelepad te, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

        animation_counter++;

        if (!te.hasRedstoneUpgrade() || te.hasRedstoneUpgrade() && !te.isPowered()) {
            stack.pushPose();
            this.renderCube(stack.last().pose(), bufferIn.getBuffer(RenderType.endPortal()));
            stack.popPose();
        }

        stack.pushPose();
        // set normal
        stack.translate(0.5F, 2.25F, 0.5F);
        stack.scale(1.0F, -1F, 1F);

        Color colorBase = new Color(te.getColorArrow());
        Color colorFrame = new Color(te.getColorFeet());

        renderPad(stack, colorFrame, colorBase, bufferIn, combinedLightIn, combinedOverlayIn);

        if (te.hasDimensionUpgrade()) {
            stack.pushPose();

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
            stack.scale(0.75f, 0.75f, 0.75f);
            stack.translate(-0.1f, 0.45f, 0.1f);

            switch (te.getUpgradeRotation()) {
                case 0 -> {
                    stack.mulPose(new Quaternion(0, 0, 0, true));
                    stack.translate(0f, 0, 0f);
                }
                case 1 -> {
                    stack.mulPose(new Quaternion(0, -90, 0, true));
                    stack.translate(-0.1f, 0, 0f);
                }
                case 2 -> {
                    stack.mulPose(new Quaternion(0, 180, 0, true));
                    stack.translate(-0.2f, 0, 0.2f);
                }
                default -> {// also case 3
                    stack.mulPose(new Quaternion(0, 90, 0, true));
                    stack.translate(0f, 0, 0.2f);
                }
            }

            modeltelepad.renderUpgrade(stack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(resLocAnimation)), combinedLightIn,
                    combinedOverlayIn);
            stack.popPose();
        }
        stack.popPose();

        if (te.hasRedstoneUpgrade()) {
            renderTorch(te, stack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(InventoryMenu.BLOCK_ATLAS)), combinedOverlayIn,
                    -(0.0625 * 7), -(0.0625 * 4), (0.0625 * 7));
            renderTorch(te, stack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(InventoryMenu.BLOCK_ATLAS)), combinedOverlayIn,
                    -(0.0625 * 7), -(0.0625 * 4), -(0.0625 * 7));
            renderTorch(te, stack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(InventoryMenu.BLOCK_ATLAS)), combinedOverlayIn,
                    (0.0625 * 7), -(0.0625 * 4), (0.0625 * 7));
            renderTorch(te, stack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(InventoryMenu.BLOCK_ATLAS)), combinedOverlayIn,
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
                ((float) colorBase.getRed() / 255.0f), ((float) colorBase.getGreen() / 255.0f), ((float) colorBase.getBlue() / 255.0f));
        stack.popPose();

        stack.pushPose();
        modeltelepad.renderLegs(stack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(pads)), packedLightIn, packedOverlayIn,
                ((float) colorFrame.getRed() / 255.0f), ((float) colorFrame.getGreen() / 255.0f), ((float) colorFrame.getBlue() / 255.0f));
        // current fix : south and north legs are acting funky when rotated, so
        // duplicate west and est and turn them around.
        stack.mulPose(new Quaternion(0, 90, 0, true));
        modeltelepad.renderLegs(stack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(pads)), packedLightIn, packedOverlayIn,
                ((float) colorFrame.getRed() / 255.0f), ((float) colorFrame.getGreen() / 255.0f), ((float) colorFrame.getBlue() / 255.0f));
        // Reset normal
        stack.mulPose(new Quaternion(0, -90, 0, true));
        stack.popPose();

        stack.pushPose();
        modeltelepad.renderFrame(stack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(frame)), packedLightIn, packedOverlayIn, 1F, 1F, 1F);
        stack.popPose();

    }

    private void renderCube(Matrix4f stack, VertexConsumer vertexConsumer) {
        this.renderFace(stack, vertexConsumer, 0.0F, 1.0F, 0.75f, 0.75f, 1.0F, 1.0F, 0.0F, 0.0F);

    }

    private void renderFace(Matrix4f matrix4f, VertexConsumer vertexConsumer, float x0, float x1, float y0, float y1, float normalA, float normalB, float normalC, float normalD) {
        vertexConsumer.vertex(matrix4f, x0, y0, normalA).endVertex();
        vertexConsumer.vertex(matrix4f, x1, y0, normalB).endVertex();
        vertexConsumer.vertex(matrix4f, x1, y1, normalC).endVertex();
        vertexConsumer.vertex(matrix4f, x0, y1, normalD).endVertex();
    }

}
