package subaraki.telepads.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.utility.PropertiesWrapper;

import java.util.List;

public class TelepadItems {

    public static Item[] register()
    {

        Item bead = new ItemEnderBead();
        Item necklace = new ItemEnderNecklace();

        Item toggler = new Item(PropertiesWrapper.getItemProperties().tab(CreativeModeTab.TAB_REDSTONE)).setRegistryName(Telepads.MODID, "toggler");
        Item transmitter = new Item(PropertiesWrapper.getItemProperties().tab(CreativeModeTab.TAB_REDSTONE)).setRegistryName(Telepads.MODID, "transmitter");

        Item creative_rod = new Item(PropertiesWrapper.getItemProperties().tab(CreativeModeTab.TAB_REDSTONE)) {

            @Override
            public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
            {

                tooltip.add(
                        new TextComponent("can be used by people with creative acces to enable telepads to teleport to a location defined in config"));
            }
        }.setRegistryName(Telepads.MODID, "tp_upgrade");

        Item creative_rod_2 = new Item(PropertiesWrapper.getItemProperties().tab(CreativeModeTab.TAB_REDSTONE)) {

            @Override
            public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
            {

                tooltip.add(new TextComponent("can be used by people with creative acces to toggle public acces to a telepad"));
            }
        }.setRegistryName(Telepads.MODID, "tp_upgrade_public");

        return new Item[] { bead, necklace, toggler, transmitter, creative_rod, creative_rod_2 };
    }
}
