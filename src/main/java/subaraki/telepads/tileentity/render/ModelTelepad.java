package subaraki.telepads.tileentity.render;

import java.util.function.Function;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

public class ModelTelepad extends Model {

    ModelRenderer frame;
    ModelRenderer arrows;
    ModelRenderer legWestRight;
    ModelRenderer legWestLeft;
    ModelRenderer legEastRight;
    ModelRenderer legEastLeft;
    ModelRenderer legSouthRight;
    ModelRenderer legSouthLeft;
    ModelRenderer legNorthRight;
    ModelRenderer legNorthLeft;

    ModelRenderer antenna;
    ModelRenderer antennaPad;

    public ModelTelepad(Function<ResourceLocation, RenderType> renderTypeIn) {

        super(renderTypeIn);

        texWidth = 64;
        texHeight = 32;

        antenna = new ModelRenderer(this, 0, 27);
        antenna.addBox(2f, -4f, -0.5f, 1, 4, 1);
        antenna.setPos(-5F, 21F, 4F);
        antenna.setTexSize(64, 32);
        setRotation(antenna, 0, -0.80f, 0);

        antennaPad = new ModelRenderer(this, 4, 28);
        antennaPad.addBox(-3f, 0f, -2f, 6, 1, 3);
        antennaPad.setPos(-5F, 21F, 4F);
        antennaPad.setTexSize(64, 32);
        setRotation(antennaPad, 0, -0.80f, 0);

        frame = new ModelRenderer(this, 0, 0);
        frame.addBox(-5F, 0F, -5F, 10, 1, 10);
        frame.setPos(0F, 22F, 0F);
        frame.setTexSize(64, 32);
        frame.mirror = true;
        setRotation(frame, 0F, 0F, 0F);

        arrows = new ModelRenderer(this, 0, 11);
        arrows.addBox(-5F, -0.5F, -5F, 10, 1, 10);
        arrows.setPos(0F, 23F, 0F);
        arrows.setTexSize(64, 32);
        arrows.mirror = true;
        setRotation(arrows, 0F, 0.785F, 0F);

        legWestRight = new ModelRenderer(this, 0, 0);
        legWestRight.addBox(-3F, 0F, 0F, 3, 1, 2);
        legWestRight.setPos(-5F, 22F, 0F);
        legWestRight.setTexSize(64, 32);
        legWestRight.mirror = true;

        legWestLeft = new ModelRenderer(this, 0, 0);
        legWestLeft.addBox(-3F, 0F, -2F, 3, 1, 2);
        legWestLeft.setPos(-5F, 22F, 0F);
        legWestLeft.setTexSize(64, 32);
        legWestLeft.mirror = true;

        legEastLeft = new ModelRenderer(this, 0, 0);
        legEastLeft.addBox(0F, 0F, 0F, 3, 1, 2);
        legEastLeft.setPos(5F, 22F, 0F);
        legEastLeft.setTexSize(64, 32);
        legEastLeft.mirror = true;

        legEastRight = new ModelRenderer(this, 0, 0);
        legEastRight.addBox(0F, 0F, -2F, 3, 1, 2);
        legEastRight.setPos(5F, 22F, 0F);
        legEastRight.setTexSize(64, 32);
        legEastRight.mirror = true;

        setRotation(legWestLeft, 0.2F, 0F, -0.4F);
        setRotation(legWestRight, -0.2F, 0F, -0.4F);
        setRotation(legEastLeft, -0.2F, 0F, 0.4F);
        setRotation(legEastRight, 0.2F, 0F, 0.4F);

    }

    public void renderLegs(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float r, float g , float b)
    {

        legWestRight.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, r, g, b, 1.0F);
        legWestLeft.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, r, g, b, 1.0F);
        legEastRight.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, r, g, b, 1.0F);
        legEastLeft.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, r, g, b, 1.0F);
    }

    public void renderUpgrade(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn)
    {

        antenna.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        antennaPad.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
    }

    public void renderFrame(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float r, float g , float b)
    {

        frame.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, r, g, b, 1.0F);
    }

    public void renderArrows(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float r, float g , float b)
    {

        arrows.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, r, g, b, 1.0F);
    }

    /**
     * Sets the rotations for a model piece.
     * 
     * @param model
     *            : The model to rotate.
     * @param x
     *            : The rotation for the X axis.
     * @param y
     *            : The rotation for the Y axis.
     * @param z
     *            : The rotation for the Z axis.
     */
    private void setRotation(ModelRenderer model, float x, float y, float z)
    {

        model.xRot = x;
        model.yRot = y;
        model.zRot = z;
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
    {

    }
}