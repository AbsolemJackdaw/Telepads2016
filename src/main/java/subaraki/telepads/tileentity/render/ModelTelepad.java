package subaraki.telepads.tileentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class ModelTelepad extends Model {

    ModelPart frame;
    ModelPart arrows;
    ModelPart legWestRight;
    ModelPart legWestLeft;
    ModelPart legEastRight;
    ModelPart legEastLeft;
    ModelPart legSouthRight;
    ModelPart legSouthLeft;
    ModelPart legNorthRight;
    ModelPart legNorthLeft;

    ModelPart antenna;
    ModelPart antennaPad;

    public ModelTelepad(Function<ResourceLocation, RenderType> renderTypeIn) {

        super(renderTypeIn);

        texWidth = 64;
        texHeight = 32;

        antenna = new ModelPart(this, 0, 27);
        antenna.addBox(2f, -4f, -0.5f, 1, 4, 1);
        antenna.setPos(-5F, 21F, 4F);
        antenna.setTexSize(64, 32);
        setRotation(antenna, 0, -0.80f, 0);

        antennaPad = new ModelPart(this, 4, 28);
        antennaPad.addBox(-3f, 0f, -2f, 6, 1, 3);
        antennaPad.setPos(-5F, 21F, 4F);
        antennaPad.setTexSize(64, 32);
        setRotation(antennaPad, 0, -0.80f, 0);

        frame = new ModelPart(this, 0, 0);
        frame.addBox(-5F, 0F, -5F, 10, 1, 10);
        frame.setPos(0F, 22F, 0F);
        frame.setTexSize(64, 32);
        frame.mirror = true;
        setRotation(frame, 0F, 0F, 0F);

        arrows = new ModelPart(this, 0, 11);
        arrows.addBox(-5F, -0.5F, -5F, 10, 1, 10);
        arrows.setPos(0F, 23F, 0F);
        arrows.setTexSize(64, 32);
        arrows.mirror = true;
        setRotation(arrows, 0F, 0.785F, 0F);

        legWestRight = new ModelPart(this, 0, 0);
        legWestRight.addBox(-3F, 0F, 0F, 3, 1, 2);
        legWestRight.setPos(-5F, 22F, 0F);
        legWestRight.setTexSize(64, 32);
        legWestRight.mirror = true;

        legWestLeft = new ModelPart(this, 0, 0);
        legWestLeft.addBox(-3F, 0F, -2F, 3, 1, 2);
        legWestLeft.setPos(-5F, 22F, 0F);
        legWestLeft.setTexSize(64, 32);
        legWestLeft.mirror = true;

        legEastLeft = new ModelPart(this, 0, 0);
        legEastLeft.addBox(0F, 0F, 0F, 3, 1, 2);
        legEastLeft.setPos(5F, 22F, 0F);
        legEastLeft.setTexSize(64, 32);
        legEastLeft.mirror = true;

        legEastRight = new ModelPart(this, 0, 0);
        legEastRight.addBox(0F, 0F, -2F, 3, 1, 2);
        legEastRight.setPos(5F, 22F, 0F);
        legEastRight.setTexSize(64, 32);
        legEastRight.mirror = true;

        setRotation(legWestLeft, 0.2F, 0F, -0.4F);
        setRotation(legWestRight, -0.2F, 0F, -0.4F);
        setRotation(legEastLeft, -0.2F, 0F, 0.4F);
        setRotation(legEastRight, 0.2F, 0F, 0.4F);

    }

    public void renderLegs(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float r, float g , float b)
    {

        legWestRight.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, r, g, b, 1.0F);
        legWestLeft.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, r, g, b, 1.0F);
        legEastRight.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, r, g, b, 1.0F);
        legEastLeft.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, r, g, b, 1.0F);
    }

    public void renderUpgrade(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn)
    {

        antenna.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        antennaPad.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
    }

    public void renderFrame(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float r, float g , float b)
    {

        frame.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, r, g, b, 1.0F);
    }

    public void renderArrows(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float r, float g , float b)
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
    private void setRotation(ModelPart model, float x, float y, float z)
    {

        model.xRot = x;
        model.yRot = y;
        model.zRot = z;
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
    {

    }
}