package subaraki.telepads.handler;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import subaraki.telepads.mod.Telepads;

public class EventHandler {

	public EventHandler() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void anvil(AnvilUpdateEvent event)
	{
		ItemStack input = event.getLeft();
		ItemStack ingredient = event.getRight();

		if(ConfigurationHandler.instance.allowAnvilPearls)
			if(input.getItem().equals(Items.ENDER_PEARL) && input.getItem().equals(ingredient.getItem()))
			{
				int amount = input.getCount() + ingredient.getCount();
				if(amount > 8)
					return;
				event.setOutput(new ItemStack(Telepads.items.ender_bead, 2*amount));
				event.setCost(3);
			}
	}
}
