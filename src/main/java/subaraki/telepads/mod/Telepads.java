package subaraki.telepads.mod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import subaraki.telepads.block.BlockTelepad;
import subaraki.telepads.block.TelepadBlocks;
import subaraki.telepads.handler.ConfigData;
import subaraki.telepads.item.TelepadItems;
import subaraki.telepads.tileentity.TileEntityTelepad;

@Mod(value = Telepads.MODID)
@EventBusSubscriber(bus = Bus.MOD, modid = Telepads.MODID)
public class Telepads {

    public static final String MODID = "telepads";
    public static final String NAME = "Telepads";
    public static final String VERSION = "$version";
    public static final String DEPENDENCY = "required-after:subcommonlib";

    public static Logger log = LogManager.getLogger(MODID);

    public static TelepadItems items;
    public static TelepadBlocks blocks;

//    private static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MODID);
//
//    private static RegistryObject<TileEntityType<TileEntityTelepad>> HEART_TREE_TILE_ENTITY = TILE_ENTITIES.register("heart_tree_core",
//            () -> TileEntityType.Builder.<TileEntityTelepad>of(TileEntityTelepad::new, Telepads.ObjectHolders.TELEPAD_BLOCK).build(null));

    public Telepads() {

        ModLoadingContext modLoadingContext = ModLoadingContext.get();

        modLoadingContext.registerConfig(ModConfig.Type.SERVER, ConfigData.SERVER_SPEC);
        modLoadingContext.registerConfig(ModConfig.Type.CLIENT, ConfigData.CLIENT_SPEC);
    }

    @ObjectHolder(MODID)
    public static class ObjectHolders {

        @ObjectHolder(value = "telepad")
        public static final BlockTelepad TELEPAD_BLOCK = null;

        @ObjectHolder(value = "ender_bead")
        public static final Item BEADS = null;

        @ObjectHolder(value = "ender_bead_necklace")
        public static final Item NECKLACE = null;

        @ObjectHolder(value = "toggler")
        public static final Item TOGGLER = null;

        @ObjectHolder(value = "transmitter")
        public static final Item TRANSMITTER = null;

        @ObjectHolder(value = "upgrade")
        public static final Item UPGRADE = null;

        @ObjectHolder(value = "tp_upgrade")
        public static final Item CREATIVE_ROD = null;

        @ObjectHolder(value = "tp_upgrade_public")
        public static final Item CREATIVE_ROD_PUBLIC = null;

        @ObjectHolder(value = "telepadtileentity")
        public static final TileEntityType<TileEntityTelepad> TILE_ENTITY_TELEPAD = null;

    }
}
