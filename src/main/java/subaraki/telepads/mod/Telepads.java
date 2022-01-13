package subaraki.telepads.mod;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import subaraki.telepads.handler.ConfigData;
import subaraki.telepads.registry.TelepadBlockEntities;
import subaraki.telepads.registry.TelepadBlocks;
import subaraki.telepads.registry.TelepadItems;

@Mod(value = Telepads.MODID)
@EventBusSubscriber(bus = Bus.MOD, modid = Telepads.MODID)
public class Telepads {

    public static final String MODID = "telepads";
    public static final String NAME = "Telepads";
    public static final String VERSION = "$version";

    public static Logger log = LogManager.getLogger(MODID);

    public Telepads() {

        ModLoadingContext modLoadingContext = ModLoadingContext.get();

        modLoadingContext.registerConfig(ModConfig.Type.SERVER, ConfigData.SERVER_SPEC);
        modLoadingContext.registerConfig(ModConfig.Type.CLIENT, ConfigData.CLIENT_SPEC);

        TelepadBlocks.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TelepadItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TelepadBlockEntities.BLOCK_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::modConfig);
    }

    public void modConfig(ModConfigEvent event) {

        ModConfig config = event.getConfig();
        if (config.getSpec() == ConfigData.CLIENT_SPEC)
            ConfigData.refreshClient();
        else if (config.getSpec() == ConfigData.SERVER_SPEC)
            ConfigData.refreshServer();
    }
}
