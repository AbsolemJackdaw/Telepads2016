package subaraki.telepads.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.TextureTracker;
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

        BlockItem telepadItem = new BlockItem(ObjectHolders.TELEPAD_BLOCK, PropertiesWrapper.getItemProperties().group(ItemGroup.TRANSPORTATION)) {

            @Override
            public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
            {

                super.addInformation(stack, worldIn, tooltip, flagIn);

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
                                    color_name = "item.minecraft.firework_star." + dye.getTranslationKey();
                                    break;
                                }
                            }

                            TranslationTextComponent part_name = new TranslationTextComponent(translate_part_names[iteration]);
                            TranslationTextComponent dye_name = new TranslationTextComponent(color_name);
                            StringTextComponent text = new StringTextComponent(part_name.getFormattedText() + dye_name.getFormattedText());
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
