package subaraki.telepads.registry.mod_bus;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.network.NetworkHandler;

@EventBusSubscriber(modid = Telepads.MODID, bus = Bus.MOD)
public class RegisterCommonSetup {

    @SubscribeEvent
    public static void register(final FMLCommonSetupEvent event) {
        NetworkHandler.register();
    }
}
