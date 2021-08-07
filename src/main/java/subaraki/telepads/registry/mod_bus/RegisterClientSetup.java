package subaraki.telepads.registry.mod_bus;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.tileentity.render.ModelTelepad;
import subaraki.telepads.tileentity.render.TileEntityTelepadRenderer;

@EventBusSubscriber(modid = Telepads.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public class RegisterClientSetup {

    public static ModelLayerLocation TELEPAD_BLOCK_MODEL_LAYER = new ModelLayerLocation(new ResourceLocation("minecraft:player"), "telepad_layer");

    @SubscribeEvent
    public static void register(final FMLClientSetupEvent event) {
        KeyRegistry.registerKey();
    }

    @SubscribeEvent
    public static void registerRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(Telepads.TILE_ENTITY_TELEPAD.get(), TileEntityTelepadRenderer::new);
    }

    @SubscribeEvent
    public static void registerBlockLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(TELEPAD_BLOCK_MODEL_LAYER, ModelTelepad::createTelepadMesh);
    }
}
