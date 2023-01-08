package subaraki.telepads.registry.mod_bus;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.registry.TelepadItems;

@Mod.EventBusSubscriber(modid=Telepads.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class CreativeTabsEvent {

	@SubscribeEvent
	public static void setCreativeTabs(CreativeModeTabEvent.BuildContents event) {
		
		if (event.getTab() == CreativeModeTabs.TOOLS_AND_UTILITIES)
			event.accept(TelepadItems.TELEPAD);
		
		if (event.getTab() == CreativeModeTabs.REDSTONE_BLOCKS) {
			event.accept(TelepadItems.TOGGLER);
			event.accept(TelepadItems.TRANSMITTER);
			event.accept(TelepadItems.CYCLE_ROD);
			event.accept(TelepadItems.PUBLIC_TOGGLE_ROD);
		}
		
		if (event.getTab() == CreativeModeTabs.INGREDIENTS) {
			event.accept(TelepadItems.BEAD);
			event.accept(TelepadItems.NECKLACE);
		}
	}
}
