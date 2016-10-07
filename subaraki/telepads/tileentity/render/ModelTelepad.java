package subaraki.telepads.tileentity.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelTelepad extends ModelBase {
    
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
    
    public ModelTelepad() {
        
        textureWidth = 64;
        textureHeight = 32;
        
        antenna = new ModelRenderer(this, 0, 27);
        antenna.addBox(2f, -4f, -0.5f, 1, 4, 1);
        antenna.setRotationPoint(-5F, 21F, 4F);
        antenna.setTextureSize(64, 32);
        setRotation(antenna, 0, -0.80f, 0);
        
        antennaPad = new ModelRenderer(this, 4, 28);
        antennaPad.addBox(-3f, 0f, -2f, 6, 1, 3);
        antennaPad.setRotationPoint(-5F, 21F, 4F);
        antennaPad.setTextureSize(64, 32);
        setRotation(antennaPad, 0, -0.80f, 0);
        
        frame = new ModelRenderer(this, 0, 0);
        frame.addBox(-5F, 0F, -5F, 10, 1, 10);
        frame.setRotationPoint(0F, 22F, 0F);
        frame.setTextureSize(64, 32);
        frame.mirror = true;
        setRotation(frame, 0F, 0F, 0F);
        
        arrows = new ModelRenderer(this, 0, 11);
        arrows.addBox(-5F, -0.5F, -5F, 10, 1, 10);
        arrows.setRotationPoint(0F, 23F, 0F);
        arrows.setTextureSize(64, 32);
        arrows.mirror = true;
        setRotation(arrows, 0F, 0.785F, 0F);
        
        
        legWestRight = new ModelRenderer(this, 0, 0);
        legWestRight.addBox(-3F, 0F, 0F, 3, 1, 2);
        legWestRight.setRotationPoint(-5F, 22F, 0F);
        legWestRight.setTextureSize(64, 32);
        legWestRight.mirror = true;
        
        legWestLeft = new ModelRenderer(this, 0, 0);
        legWestLeft.addBox(-3F, 0F, -2F, 3, 1, 2);
        legWestLeft.setRotationPoint(-5F, 22F, 0F);
        legWestLeft.setTextureSize(64, 32);
        legWestLeft.mirror = true;
        
        legEastLeft = new ModelRenderer(this, 0, 0);
        legEastLeft.addBox(0F, 0F, 0F, 3, 1, 2);
        legEastLeft.setRotationPoint(5F, 22F, 0F);
        legEastLeft.setTextureSize(64, 32);
        legEastLeft.mirror = true;
        
        legEastRight = new ModelRenderer(this, 0, 0);
        legEastRight.addBox(0F, 0F, -2F, 3, 1, 2);
        legEastRight.setRotationPoint(5F, 22F, 0F);
        legEastRight.setTextureSize(64, 32);
        legEastRight.mirror = true;
        
//        legSouthRight = new ModelRenderer(this, 0, 0);
//        legSouthRight.addBox(-2F, 0F, -3F, 2, 1, 3);
//        legSouthRight.setRotationPoint(0F, 22, -5);
//        legSouthRight.setTextureSize(64, 32);
//        legSouthRight.mirror = true;
//        setRotation(legSouthRight, 0.4F, 0F, -0.1F);
//        
//        legSouthLeft = new ModelRenderer(this, 0, 0);
//        legSouthLeft.addBox(0F, 0F, -3F, 2, 1, 3);
//        legSouthLeft.setRotationPoint(0F, 22F, -5F);
//        legSouthLeft.setTextureSize(64, 32);
//        legSouthLeft.mirror = true;
//        setRotation(legSouthLeft, 0.4F, 0F, 0.1F);

//        legNorthRight = new ModelRenderer(this, 0, 0);
//        legNorthRight.addBox(0F, -0F, 0F, 2, 1, 3);
//        legNorthRight.setRotationPoint(0F, 22F, 5F);
//        legNorthRight.setTextureSize(64, 32);
//        legNorthRight.mirror = true;
//        
//        legNorthLeft = new ModelRenderer(this, 0, 0);
//        legNorthLeft.addBox(-2F, -0F, 0F, 2, 1, 3);
//        legNorthLeft.setRotationPoint(0F, 22F, 5F);
//        legNorthLeft.setTextureSize(64, 32);
//        legNorthLeft.mirror = true;
        
//        setRotation(legNorthLeft, -0.4F, 0.0F, -0.2F);
//        setRotation(legNorthRight, -0.4F, 0F, 0.2F);
        
        setRotation(legWestLeft, 0.2F, 0F, -0.4F);
        setRotation(legWestRight,-0.2F, 0F, -0.4F);
        setRotation(legEastLeft, -0.2F, 0F, 0.4F);
        setRotation(legEastRight, 0.2F, 0F, 0.4F);
       
    }
    
    public void renderLegs (float f5) {
        
        legWestRight.render(f5);
        legWestLeft.render(f5);
        legEastRight.render(f5);
        legEastLeft.render(f5);
//        legSouthRight.render(f5);
//        legSouthLeft.render(f5);
//        legNorthRight.render(f5);
//        legNorthLeft.render(f5);
    }
    
    public void renderUpgrade (float f5) {
        
        antenna.render(f5);
        antennaPad.render(f5);
    }
    
    public void renderFrame (float f5) {
        
        frame.render(f5);
    }
    
    public void renderArrows (float f5) {
        arrows.render(f5);
    }
    
    /**
     * Sets the rotations for a model piece.
     * 
     * @param model : The model to rotate.
     * @param x : The rotation for the X axis.
     * @param y : The rotation for the Y axis.
     * @param z : The rotation for the Z axis.
     */
    private void setRotation (ModelRenderer model, float x, float y, float z) {
        
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}