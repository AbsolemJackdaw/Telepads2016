package subaraki.telepads.registry.mod_bus;

import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import subaraki.telepads.capability.player.TelepadData;
import subaraki.telepads.mod.Telepads;

@Mod.EventBusSubscriber(modid = Telepads.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonSetupEvent {

    @SubscribeEvent
    public static void capRegistry(RegisterCapabilitiesEvent event) {
        event.register(TelepadData.class);
    }
}
