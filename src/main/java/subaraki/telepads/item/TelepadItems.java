package subaraki.telepads.item;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.utility.PropertiesWrapper;

public class TelepadItems {

    public static Item[] register()
    {

        Item bead = new ItemEnderBead();
        Item necklace = new ItemEnderNecklace();

        Item toggler = new Item(PropertiesWrapper.getItemProperties().tab(ItemGroup.TAB_REDSTONE)).setRegistryName(Telepads.MODID, "toggler");
        Item transmitter = new Item(PropertiesWrapper.getItemProperties().tab(ItemGroup.TAB_REDSTONE)).setRegistryName(Telepads.MODID, "transmitter");

        Item creative_rod = new Item(PropertiesWrapper.getItemProperties().tab(ItemGroup.TAB_REDSTONE)) {

            @Override
            public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
            {

                tooltip.add(
                        new StringTextComponent("can be used by people with creative acces to enable telepads to teleport to a location defined in config"));
            }
        }.setRegistryName(Telepads.MODID, "tp_upgrade");

        Item creative_rod_2 = new Item(PropertiesWrapper.getItemProperties().tab(ItemGroup.TAB_REDSTONE)) {

            @Override
            public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
            {

                tooltip.add(new StringTextComponent("can be used by people with creative acces to toggle public acces to a telepad"));
            }
        }.setRegistryName(Telepads.MODID, "tp_upgrade_public");

        return new Item[] { bead, necklace, toggler, transmitter, creative_rod, creative_rod_2 };
    }
}
