package subaraki.telepads.tileentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.data.ModelData;
import subaraki.telepads.registry.mod_bus.RegisterClientSetup;
import subaraki.telepads.tileentity.TileEntityTelepad;

import java.awt.*;

import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class TileEntityTelepadRenderer implements BlockEntityRenderer<TileEntityTelepad> {

    private static final String resourcePath = "telepads:textures/entity/tile/";
    private static final ResourceLocation base = new ResourceLocation(resourcePath + "telepad_base.png");
    private static final ResourceLocation pads = new ResourceLocation(resourcePath + "telepad_pads.png");
    private static final ResourceLocation frame = new ResourceLocation(resourcePath + "telepad_frame.png");
    private static final ResourceLocation transmitterFrameA = new ResourceLocation(resourcePath + "telepad_dimension_upgrade.png");
    private static final ResourceLocation transmitterFrameB = new ResourceLocation(resourcePath + "telepad_dimension_upgrade_2.png");
    private static final ResourceLocation transmitterFrameC = new ResourceLocation(resourcePath + "telepad_dimension_upgrade_3.png");
    private static final ResourceLocation transmitterFrameD = new ResourceLocation(resourcePath + "telepad_dimension_upgrade_4.png");
    private static ModelTelepad modeltelepad;
    private static int transmitterFrameCounter;

    public TileEntityTelepadRenderer(BlockEntityRendererProvider.Context c) {
        modeltelepad = new ModelTelepad(Minecraft.getInstance().getEntityModels().bakeLayer(RegisterClientSetup.TELEPAD));
    }

    @Override
    public void render(TileEntityTelepad te, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

        transmitterFrameCounter++;

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
            if (transmitterFrameCounter < 25)
                resLocAnimation = transmitterFrameA;
            else if (transmitterFrameCounter < 50)
                resLocAnimation = transmitterFrameB;
            else if (transmitterFrameCounter < 75)
                resLocAnimation = transmitterFrameC;
            else if (transmitterFrameCounter < 99) {// next tick is >=99, so resets and stays tex.4
                resLocAnimation = transmitterFrameD;
            } else {
                transmitterFrameCounter = 0;
                resLocAnimation = transmitterFrameD;
            }
            stack.scale(0.75f, 0.75f, 0.75f);
            stack.translate(-0.1f, 0.45f, 0.1f);

            switch (te.getUpgradeRotation()) {
                case 0 -> {
                    stack.mulPose(new Quaternionf(0, 0, 0, 1f));
                    stack.translate(0f, 0, 0f);
                }
                case 1 -> {
                    stack.mulPose(new Quaternionf(0, -90, 0, 1f));
                    stack.translate(-0.1f, 0, 0f);
                }
                case 2 -> {
                    stack.mulPose(new Quaternionf(0, 180, 0, 1f));
                    stack.translate(-0.2f, 0, 0.2f);
                }
                default -> {// also case 3
                    stack.mulPose(new Quaternionf(0, 90, 0, 1f));
                    stack.translate(0f, 0, 0.2f);
                }
            }

            modeltelepad.renderUpgrade(stack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(resLocAnimation)), combinedLightIn,
                    combinedOverlayIn);
            stack.popPose();
        }
        stack.popPose();

        if (te.hasRedstoneUpgrade()) {
            renderTorch(te, stack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(InventoryMenu.BLOCK_ATLAS)), combinedLightIn, combinedOverlayIn,
                    -(0.0625 * 7), -(0.0625 * 4), (0.0625 * 7));
            renderTorch(te, stack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(InventoryMenu.BLOCK_ATLAS)), combinedLightIn, combinedOverlayIn,
                    -(0.0625 * 7), -(0.0625 * 4), -(0.0625 * 7));
            renderTorch(te, stack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(InventoryMenu.BLOCK_ATLAS)), combinedLightIn, combinedOverlayIn,
                    (0.0625 * 7), -(0.0625 * 4), (0.0625 * 7));
            renderTorch(te, stack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(InventoryMenu.BLOCK_ATLAS)), combinedLightIn, combinedOverlayIn,
                    (0.0625 * 7), -(0.0625 * 4), -(0.0625 * 7));
        }
    }

    private void renderTorch(TileEntityTelepad te, PoseStack stack, VertexConsumer builder, int combinedLight, int combinedOverlay, double offsetX, double offsetY, double offsetZ) {

        stack.pushPose();
        BlockRenderDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();
        BlockState state = Blocks.REDSTONE_TORCH.defaultBlockState().setValue(BlockStateProperties.LIT, te.isPowered());
        stack.translate(offsetX, offsetY, offsetZ);
        BakedModel model = blockrendererdispatcher.getBlockModel(state);
        blockrendererdispatcher.getModelRenderer().renderModel(stack.last(), builder, te.getBlockState(), model, 0, 0, 0, combinedLight, combinedOverlay, ModelData.EMPTY, RenderType.solid());
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
        stack.popPose();

        stack.pushPose();
        modeltelepad.renderFrame(stack, bufferIn.getBuffer(RenderType.entityCutoutNoCull(frame)), packedLightIn, packedOverlayIn, 1F, 1F, 1F);
        stack.popPose();

    }

    private void renderCube(Matrix4f stack, VertexConsumer vertexConsumer) {
        this.renderFace(stack, vertexConsumer, 0.03F, 0.97F, 0.19f, 0.19f, 0.97F, 0.97F, 0.03F, 0.03F);

    }

    private void renderFace(Matrix4f matrix4f, VertexConsumer vertexConsumer, float x0, float x1, float y0, float y1, float normalA, float normalB, float normalC, float normalD) {
        vertexConsumer.vertex(matrix4f, x0, y0, normalA).endVertex();
        vertexConsumer.vertex(matrix4f, x1, y0, normalB).endVertex();
        vertexConsumer.vertex(matrix4f, x1, y1, normalC).endVertex();
        vertexConsumer.vertex(matrix4f, x0, y1, normalD).endVertex();
    }

}
