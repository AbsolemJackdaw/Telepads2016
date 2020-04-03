package subaraki.telepads.registry.mod_bus;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import subaraki.telepads.block.TelepadBlocks;
import subaraki.telepads.item.TelepadItems;
import subaraki.telepads.mod.Telepads;

@EventBusSubscriber(modid = Telepads.MODID, bus = Bus.MOD)
public class RegisterItems {

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Item> event)
    {

        event.getRegistry().registerAll(TelepadItems.register());
        event.getRegistry().registerAll(TelepadBlocks.registerBlockItems());

    }
}
