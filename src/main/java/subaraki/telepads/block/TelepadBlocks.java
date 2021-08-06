//package subaraki.telepads.block;
//
//import net.minecraft.network.chat.Component;
//import net.minecraft.network.chat.TextComponent;
//import net.minecraft.network.chat.TranslatableComponent;
//import net.minecraft.world.item.*;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.Block;
//import subaraki.telepads.mod.Telepads.ObjectHolders;
//import subaraki.telepads.utility.PropertiesWrapper;
//
//import java.util.List;
//
//public class TelepadBlocks {
//
//
//    public static Block[] register()
//    {
//
//        Block telepad = new BlockTelepad();
//
//        return new Block[] { telepad };
//    }
//
//    public static BlockItem[] registerBlockItems()
//    {
//
//        BlockItem telepadItem = new BlockItem(ObjectHolders.TELEPAD_BLOCK, PropertiesWrapper.getItemProperties().tab(CreativeModeTab.TAB_TRANSPORTATION)) {
//
//            @Override
//            public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
//            {
//
//                super.appendHoverText(stack, worldIn, tooltip, flagIn);
//
//                if (stack.hasTag())
//                {
//                    String[] tags = new String[] { "colorFrame", "colorBase" };
//                    String[] translate_missing_dye = new String[] { "feet.color", "arrow.color" };
//                    String[] translate_part_names = new String[] { "feet.name", "arrow.name" };
//
//                    int iteration = 0;
//
//                    for (String tag : tags)
//                    {
//                        if (stack.getTag().contains(tag))
//                        {
//                            int saved_color = stack.getTag().getInt(tag);
//
//                            String color_name = translate_missing_dye[iteration];
//                            for (DyeColor dye : DyeColor.values())
//                            {
//                                if (dye.getTextureDiffuseColors()[0] == (float) ((saved_color & 16711680) >> 16) / 255f
//                                        && dye.getTextureDiffuseColors()[1] == (float) ((saved_color & 65280) >> 8) / 255f
//                                        && dye.getTextureDiffuseColors()[2] == (float) ((saved_color & 255) >> 0) / 255f)
//                                {
//                                    color_name = "item.minecraft.firework_star." + dye.getName();
//                                    break;
//                                }
//                            }
//
//                            TranslatableComponent part_name = new TranslatableComponent(translate_part_names[iteration]);
//                            TranslatableComponent dye_name = new TranslatableComponent(color_name);
//                            TextComponent text = new TextComponent(part_name.getString() + dye_name.getString());
//                            tooltip.add(text);
//                        }
//
//                        iteration++;
//                    }
//                }
//            }
//        };
//
//        telepadItem.setRegistryName(ObjectHolders.TELEPAD_BLOCK.getRegistryName());
//
//        return new BlockItem[] { telepadItem };
//    }
//}
