package subaraki.telepads.registry;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import subaraki.telepads.item.ItemEnderBead;
import subaraki.telepads.item.ItemEnderNecklace;
import subaraki.telepads.mod.Telepads;

import java.util.List;

public class TelepadItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Telepads.MODID);

    public static final RegistryObject<Item> TELEPAD = ITEMS.register("telepad", () ->
            new BlockItem(TelepadBlocks.TELEPAD.get(), new Item.Properties().tab(CreativeModeTab.TAB_TRANSPORTATION)) {

                @Override
                public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {

                    super.appendHoverText(stack, worldIn, tooltip, flagIn);

                    if (stack.hasTag()) {
                        String[] tags = new String[]{"colorFrame", "colorBase"};
                        String[] translate_missing_dye = new String[]{"feet.color", "arrow.color"};
                        String[] translate_part_names = new String[]{"feet.name", "arrow.name"};

                        int iteration = 0;

                        for (String tag : tags) {
                            if (stack.hasTag() && stack.getTag() != null && stack.getTag().contains(tag)) {
                                int saved_color = stack.getTag().getInt(tag);

                                String color_name = translate_missing_dye[iteration];
                                for (DyeColor dye : DyeColor.values()) {
                                    if (dye.getTextureDiffuseColors()[0] == (float) ((saved_color & 16711680) >> 16) / 255f
                                            && dye.getTextureDiffuseColors()[1] == (float) ((saved_color & 65280) >> 8) / 255f
                                            && dye.getTextureDiffuseColors()[2] == (float) ((saved_color & 255)) / 255f) {
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
            });
    public static final RegistryObject<Item> BEAD = ITEMS.register("ender_bead", ItemEnderBead::new);
    public static final RegistryObject<Item> NECKLACE = ITEMS.register("ender_bead_necklace", ItemEnderNecklace::new);
    public static final RegistryObject<Item> TOGGLER = ITEMS.register("toggler", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE)));
    public static final RegistryObject<Item> TRANSMITTER = ITEMS.register("transmitter", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE)));
    public static final RegistryObject<Item> CYCLE_ROD = ITEMS.register("cycle_rod", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE)) {
        @Override
        public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
            tooltip.add(new TextComponent("can be used by people with creative access to enable telepads to teleport to a location defined in config"));
        }
    });
    public static final RegistryObject<Item> PUBLIC_TOGGLE_ROD = ITEMS.register("public_toggle_rod", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE)) {
        @Override
        public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
            tooltip.add(new TextComponent("can be used by people with creative access to toggle public acces to a telepad"));
        }
    });

}
