package subaraki.telepads.tileentity.render;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.Direction;
import com.mojang.math.Matrix4f;
import subaraki.telepads.tileentity.TileEntityTelepad;

public class RenderEndPortalFrame {

    private static final Random RANDOM = new Random(31100L);
    private static final List<RenderType> RENDER_TYPES = IntStream.range(0, 16).mapToObj((itteration) -> {
        return RenderType.endPortal(itteration + 1);
    }).collect(ImmutableList.toImmutableList());

    public void render(TileEntityTelepad tileEntityIn, BlockEntityRenderDispatcher renderDispatcher, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn)
    {

        RANDOM.setSeed(31100L);
        double d0 = tileEntityIn.getBlockPos().distSqr(renderDispatcher.camera.getPosition(), true);
        int i = this.getPasses(d0);
        float f = this.getOffset();
        Matrix4f matrix4f = matrixStackIn.last().pose();

        matrixStackIn.translate(0, -0.55, 0);
        this.renderCube(f, 0.15F, matrix4f, bufferIn.getBuffer(RENDER_TYPES.get(0)));

        for (int j = 1; j < i; ++j)
        {
            this.renderCube(f, 2.0F / (float) (18 - j), matrix4f, bufferIn.getBuffer(RENDER_TYPES.get(j)));
        }

    }

    private void renderCube(float offset, float pass, Matrix4f stack, VertexConsumer vertexBuilder)
    {

        float f = (RANDOM.nextFloat() * 0.5F + 0.1F) * pass;
        float f1 = (RANDOM.nextFloat() * 0.5F + 0.4F) * pass;
        float f2 = (RANDOM.nextFloat() * 0.5F + 0.5F) * pass;
        // this.renderFace(stack, vertexBuilder, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F,
        // 1.0F, 1.0F, f, f1, f2, Direction.SOUTH);
        // this.renderFace(stack, vertexBuilder, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F,
        // 0.0F, 0.0F, f, f1, f2, Direction.NORTH);
        // this.renderFace(stack, vertexBuilder, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F,
        // 1.0F, 0.0F, f, f1, f2, Direction.EAST);
        // this.renderFace(stack, vertexBuilder, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F,
        // 1.0F, 0.0F, f, f1, f2, Direction.WEST);
        // this.renderFace(stack, vertexBuilder, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F,
        // 1.0F, 1.0F, f, f1, f2, Direction.DOWN);
        this.renderFace(stack, vertexBuilder, 0.0F, 1.0F, offset, offset, 1.0F, 1.0F, 0.0F, 0.0F, f, f1, f2, Direction.UP);
    }

    private void renderFaceGUI(float offset, float pass, Matrix4f stack, VertexConsumer vertexBuilder, int mouseX, int mouseY)
    {

        float f = (RANDOM.nextFloat() * 0.5F + 0.1F) * pass;
        float f1 = (RANDOM.nextFloat() * 0.5F + 0.4F) * pass;
        float f2 = (RANDOM.nextFloat() * 0.5F + 0.5F) * pass;
        float width = (float) Minecraft.getInstance().getWindow().getGuiScaledWidth();
        float height = (float) Minecraft.getInstance().getWindow().getGuiScaledHeight();
        float max = width > height ? width : height;
        this.renderFace(stack, vertexBuilder, 0.0F, max, max, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, f, f1, f2, Direction.NORTH);
    }

    private void renderFace(Matrix4f stack, VertexConsumer vertexBuilder, float f1, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float red, float green, float blue, Direction direction)
    {

        vertexBuilder.vertex(stack, f1, f3, f5).color(red, green, blue, 1.0F).endVertex();
        vertexBuilder.vertex(stack, f2, f3, f6).color(red, green, blue, 1.0F).endVertex();
        vertexBuilder.vertex(stack, f2, f4, f7).color(red, green, blue, 1.0F).endVertex();
        vertexBuilder.vertex(stack, f1, f4, f8).color(red, green, blue, 1.0F).endVertex();
    }

    protected int getPasses(double passes)
    {

        if (passes > 36864.0D)
        {
            return 1;
        }
        else
            if (passes > 25600.0D)
            {
                return 3;
            }
            else
                if (passes > 16384.0D)
                {
                    return 5;
                }
                else
                    if (passes > 9216.0D)
                    {
                        return 7;
                    }
                    else
                        if (passes > 4096.0D)
                        {
                            return 9;
                        }
                        else
                            if (passes > 1024.0D)
                            {
                                return 11;
                            }
                            else
                                if (passes > 576.0D)
                                {
                                    return 13;
                                }
                                else
                                {
                                    return passes > 256.0D ? 14 : 15;
                                }
    }

    protected float getOffset()
    {

        return 0.75F;
    }

    public void renderEndPortalSurfaceGUI(PoseStack stack, MultiBufferSource buffer, int mouseX, int mouseY)
    {

        RANDOM.setSeed(31100L);
        int i = this.getPasses(15);
        float f = this.getOffset();
        Matrix4f matrix4f = stack.last().pose();

        stack.translate(0, 0, 0);
        this.renderFaceGUI(f, 0.15F, matrix4f, buffer.getBuffer(RENDER_TYPES.get(0)), mouseX, mouseY);

        for (int j = 1; j < i; ++j)
        {
            this.renderFaceGUI(f, 2.0F / (float) (18 - j), matrix4f, buffer.getBuffer(RENDER_TYPES.get(j)), mouseX, mouseY);
        }
    }

}
