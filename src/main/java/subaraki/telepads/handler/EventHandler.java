package subaraki.telepads.handler;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import subaraki.telepads.item.TelepadItems;

public class EventHandler {

	public EventHandler() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void anvil(AnvilUpdateEvent event)
	{
		ItemStack input = event.getLeft();
		ItemStack ingredient = event.getRight();
		
		if(input.getItem().equals(Items.ENDER_PEARL) && input.getItem().equals(ingredient.getItem()))
		{
			event.setOutput(new ItemStack(TelepadItems.ender_bead,3));
			event.setCost(3);
		}
	}
}
