package subaraki.telepads.registry.mod_bus;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.tileentity.render.TileEntityTelepadRenderer;

@EventBusSubscriber(modid = Telepads.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public class RegisterClientSetup {

    @SubscribeEvent
    public static void register(final FMLClientSetupEvent event)
    {
        ClientRegistry.bindTileEntityRenderer(Telepads.ObjectHolders.TILE_ENTITY_TELEPAD, TileEntityTelepadRenderer::new);
        KeyRegistry.registerKey();
    }
}
