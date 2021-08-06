package subaraki.telepads.block;

import java.util.List;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import subaraki.telepads.mod.Telepads.ObjectHolders;
import subaraki.telepads.utility.PropertiesWrapper;

public class TelepadBlocks {

    public static Block[] register()
    {

        Block telepad = new BlockTelepad();

        return new Block[] { telepad };
    }

    public static BlockItem[] registerBlockItems()
    {

        BlockItem telepadItem = new BlockItem(ObjectHolders.TELEPAD_BLOCK, PropertiesWrapper.getItemProperties().tab(CreativeModeTab.TAB_TRANSPORTATION)) {

            @Override
            public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
            {

                super.appendHoverText(stack, worldIn, tooltip, flagIn);

                if (stack.hasTag())
                {
                    String[] tags = new String[] { "colorFrame", "colorBase" };
                    String[] translate_missing_dye = new String[] { "feet.color", "arrow.color" };
                    String[] translate_part_names = new String[] { "feet.name", "arrow.name" };

                    int iteration = 0;

                    for (String tag : tags)
                    {
                        if (stack.getTag().contains(tag))
                        {
                            int saved_color = stack.getTag().getInt(tag);

                            String color_name = translate_missing_dye[iteration];
                            for (DyeColor dye : DyeColor.values())
                            {
                                if (dye.getColorValue() == saved_color)
                                {
                                    color_name = "item.minecraft.firework_star." + dye.getName();
                                    break;
                                }
                            }

                            TranslatableComponent part_name = new TranslatableComponent(translate_part_names[iteration]);
                            TranslatableComponent dye_name = new TranslatableComponent(color_name);
                            TextComponent text = new TextComponent(part_name.getString() + dye_name.getString());
                            tooltip.add(text);
                        }

                        iteration++;
                    }
                }
            }
        };

        telepadItem.setRegistryName(ObjectHolders.TELEPAD_BLOCK.getRegistryName());

        return new BlockItem[] { telepadItem };
    }
}
