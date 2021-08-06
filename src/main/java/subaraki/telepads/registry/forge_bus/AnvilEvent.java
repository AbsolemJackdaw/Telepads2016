package subaraki.telepads.registry.forge_bus;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import subaraki.telepads.handler.ConfigData;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.mod.Telepads.ObjectHolders;

@EventBusSubscriber(bus = Bus.FORGE, modid = Telepads.MODID)
public class AnvilEvent {

    @SubscribeEvent
    public static void anvil(AnvilUpdateEvent event)
    {

        ItemStack input = event.getLeft();
        ItemStack ingredient = event.getRight();

        if (ConfigData.allowAnvilPearls)
            if (input.getItem().equals(Items.ENDER_PEARL) && input.getItem().equals(ingredient.getItem()))
            {
                int amount = input.getCount() + ingredient.getCount();
                if (amount > 8)
                    return;
                event.setOutput(new ItemStack(ObjectHolders.BEADS, 2 * amount));
                event.setCost(amount);
            }
    }
}
