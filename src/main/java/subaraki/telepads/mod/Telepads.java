package subaraki.telepads.mod;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import subaraki.telepads.block.BlockTelepad;
import subaraki.telepads.handler.ConfigData;
import subaraki.telepads.item.ItemEnderBead;
import subaraki.telepads.item.ItemEnderNecklace;
import subaraki.telepads.tileentity.TileEntityTelepad;
import subaraki.telepads.utility.PropertiesWrapper;

import java.util.List;

@Mod(value = Telepads.MODID)
@EventBusSubscriber(bus = Bus.MOD, modid = Telepads.MODID)
public class Telepads {

    public static final String MODID = "telepads";
    public static final String NAME = "Telepads";
    public static final String VERSION = "$version";
    public static final String DEPENDENCY = "required-after:subcommonlib";
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final RegistryObject<Block> TELEPAD_BLOCK = BLOCKS.register("telepad", BlockTelepad::new);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> TELEPAD_ITEM = ITEMS.register("telepad", () ->
            new BlockItem(TELEPAD_BLOCK.get(), PropertiesWrapper.getItemProperties().tab(CreativeModeTab.TAB_TRANSPORTATION)) {

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
    public static final RegistryObject<Item> TOGGLER = ITEMS.register("toggler", () -> new Item(PropertiesWrapper.getItemProperties().tab(CreativeModeTab.TAB_REDSTONE)));
    public static final RegistryObject<Item> TRANSMITTER = ITEMS.register("transmitter", () -> new Item(PropertiesWrapper.getItemProperties().tab(CreativeModeTab.TAB_REDSTONE)));
    public static final RegistryObject<Item> CREATIVE_ROD = ITEMS.register("creative_rod", () -> new Item(PropertiesWrapper.getItemProperties().tab(CreativeModeTab.TAB_REDSTONE)) {

        @Override
        public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {

            tooltip.add(
                    new TextComponent("can be used by people with creative access to enable telepads to teleport to a location defined in config"));
        }
    });
    public static final RegistryObject<Item> CREATIVE_ROD_PUBLIC = ITEMS.register("creative_rod_public", () -> new Item(PropertiesWrapper.getItemProperties().tab(CreativeModeTab.TAB_REDSTONE)) {
        @Override
        public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {

            tooltip.add(new TextComponent("can be used by people with creative access to toggle public acces to a telepad"));
        }
    });
    private static final DeferredRegister<BlockEntityType<?>> TILEENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MODID);
    public static Logger log = LogManager.getLogger(MODID);

    public Telepads() {

        ModLoadingContext modLoadingContext = ModLoadingContext.get();

        modLoadingContext.registerConfig(ModConfig.Type.SERVER, ConfigData.SERVER_SPEC);
        modLoadingContext.registerConfig(ModConfig.Type.CLIENT, ConfigData.CLIENT_SPEC);

        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILEENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }    public static final RegistryObject<BlockEntityType<TileEntityTelepad>> TILE_ENTITY_TELEPAD = TILEENTITIES.register("telepadtileentity", () ->
            BlockEntityType.Builder.of(TileEntityTelepad::new, TELEPAD_BLOCK.get()).build(null));


}
