package subaraki.telepads.tileentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

public class ModelTelepad extends Model {

    ModelPart frame;
    ModelPart arrows;
    ModelPart legWestRight;
    ModelPart legWestLeft;
    ModelPart legEastRight;
    ModelPart legEastLeft;
    ModelPart antenna;
    ModelPart antennaPad;

    public ModelTelepad(ModelPart part) {
        super(RenderType::entityCutoutNoCull);
        frame = part.getChild("frame");
        arrows = part.getChild("arrows");
        legWestLeft = part.getChild("leg_west_left");
        legWestRight = part.getChild("leg_west_right");
        legEastLeft = part.getChild("leg_east_left");
        legEastRight = part.getChild("leg_east_right");
        antenna = part.getChild("antenna");
        antennaPad = part.getChild("antenna_pad");
    }

    public static LayerDefinition createTelepadMesh() {
        MeshDefinition meshDef = new MeshDefinition();
        PartDefinition partDefinition = meshDef.getRoot();

        partDefinition.addOrReplaceChild("antenna", CubeListBuilder.create().texOffs(0, 27).addBox(2.0f, -4.0f, -0.5f, 1.0f, 4.0f, 1.0f),
                PartPose.offsetAndRotation(-5.0f, 21.0f, 4.0f, 0.0f, -0.80f, 0.0f));
        partDefinition.addOrReplaceChild("antenna_pad", CubeListBuilder.create().texOffs(4, 28).addBox(-3.0f, 0.0f, -2.0f, 6.0f, 1.0f, 3.0f),
                PartPose.offsetAndRotation(-5.0f, 21.0f, 4.0f, 0.0f, -0.80f, 0.0f));
        partDefinition.addOrReplaceChild("frame", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0f, 0.0f, -5.0f, 10.0f, 1.0f, 10.0f),
                PartPose.offset(0.0f, 22.0f, 0.0f));
        partDefinition.addOrReplaceChild("arrows", CubeListBuilder.create().texOffs(0, 11).addBox(-5.0f, -0.5f, -5.0f, 10.0f, 1.0f, 10.0f),
                PartPose.offsetAndRotation(0.0f, 23.0f, 0.0f, 0.0f, 0.785f, 0.0f));

        CubeListBuilder cubeListBuilder = CubeListBuilder.create().texOffs(0, 0).addBox(-3.0f, 0.0f, 0.0f, 3.0f, 1.0f, 2.0f);
        partDefinition.addOrReplaceChild("leg_west_right", cubeListBuilder, PartPose.offsetAndRotation(-5.0f, 22.0f, 0.0f, -0.2f, 0.0f, -0.4f));
        partDefinition.addOrReplaceChild("leg_west_left", cubeListBuilder, PartPose.offsetAndRotation(-5.0f, 22.0f, -2.0f, 0.2f, 0.0f, -0.4f));

        cubeListBuilder = CubeListBuilder.create().texOffs(0, 0).addBox(0.0f, 0.0f, 0.0f, 3.0f, 1.0f, 2.0f);
        partDefinition.addOrReplaceChild("leg_east_left", cubeListBuilder, PartPose.offsetAndRotation(5.0f, 22.0f, 0.0f, -0.2f, 0.0f, 0.4f));
        partDefinition.addOrReplaceChild("leg_east_right", cubeListBuilder, PartPose.offsetAndRotation(5.0f, 22.0f, -2.0f, 0.2f, 0.0f, 0.4f));

        return LayerDefinition.create(meshDef, 64, 32);
    }

    public void renderLegs(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float r, float g, float b) {

        legWestRight.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, r, g, b, 1.0F);
        legWestLeft.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, r, g, b, 1.0F);
        legEastRight.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, r, g, b, 1.0F);
        legEastLeft.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, r, g, b, 1.0F);
    }

    public void renderUpgrade(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn) {

        antenna.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
        antennaPad.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
    }

    public void renderFrame(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float r, float g, float b) {

        frame.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, r, g, b, 1.0F);
    }

    public void renderArrows(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float r, float g, float b) {

        arrows.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, r, g, b, 1.0F);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
    }
}