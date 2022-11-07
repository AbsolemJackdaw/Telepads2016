package subaraki.telepads.handler;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class ConfigData {

    public static final ServerConfig SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;
    public static final ClientConfig CLIENT;
    public static final ForgeConfigSpec CLIENT_SPEC;
    // SERVER
    public static boolean allowDragonBlocking = true;
    public static boolean allowAnvilPearls = true;
    public static boolean allowBeadsUsage = true;
    public static boolean allowNecklaceUsage = true;
    public static String[] teleportLocations = new String[]{};
    public static int teleprtDelay = 3; //seconds
    public static int expConsume;
    public static int lvlConsume;
    // CLIENT
    public static boolean allowParticles = true;

    static {
        final Pair<ServerConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_SPEC = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    static {
        final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT_SPEC = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    public static void refreshServer() {

        allowDragonBlocking = SERVER.allowDragonBlocking.get();
        allowAnvilPearls = SERVER.allowAnvilPearls.get();
        allowBeadsUsage = SERVER.allowBeadsUsage.get();
        allowNecklaceUsage = SERVER.allowNecklaceUsage.get();
        expConsume = SERVER.exp.get();
        lvlConsume = SERVER.lvl.get();
        teleprtDelay = SERVER.teleportDelay.get();


        teleportLocations = new String[SERVER.val.get().size()];
        for (int i = 0; i < SERVER.val.get().size(); i++) {
            teleportLocations[i] = SERVER.val.get().get(i);
        }
    }

    public static void refreshClient() {
        allowParticles = CLIENT.allowParticles.get();
    }

    public static class ServerConfig {

        public final ForgeConfigSpec.BooleanValue allowDragonBlocking;
        public final ForgeConfigSpec.BooleanValue allowAnvilPearls;
        public final ForgeConfigSpec.BooleanValue allowBeadsUsage;
        public final ForgeConfigSpec.BooleanValue allowNecklaceUsage;

        public final ForgeConfigSpec.IntValue exp;
        public final ForgeConfigSpec.IntValue lvl;
        public final ForgeConfigSpec.IntValue teleportDelay;

        public final ConfigValue<List<? extends String>> val;

        ServerConfig(ForgeConfigSpec.Builder builder) {

            builder.push("Teleporting from and to the End");
            allowDragonBlocking = builder.comment("Wether to allow the presence of the Ender Dragon to prevent teleporting away from the End")
                    .translation("config.block.dragon").define("Dragon Teleport Block", true);
            builder.pop();

            builder.push("Teleport Items");
            allowAnvilPearls = builder.comment("Disable creation of Ender Beads here").translation("config.anvil.allow").define("Allow anvil pearl crafting",
                    true);
            allowBeadsUsage = builder.comment("Disable usage of the Ender Beads here").translation("config.beads.allow").define("Allow Ender Bead usage",
                    true);
            allowNecklaceUsage = builder.comment("Dsiabe usage of the Ender Necklace here").translation("config.necklace.allow")
                    .define("Allow Ender Necklace usage", true);
            builder.pop();

            builder.push("Teleportation Details");
            exp = builder.comment(
                            "Penalty cost for teleportation, if set to 0, there will be no exp loss. Set level to 0 if you only want to consume an amount of exp.")
                    .translation("config.consume.exp").defineInRange("Experience Consumation", 0, 0, 10000);
            lvl = builder.comment("Penalty cost for teleportation, if set to 0, there will be no exp loss. set experience to 0 if you want to consume levels.")
                    .translation("config.consume.level").defineInRange("Level Consumation", 0, 0, 32);
            teleportDelay = builder.comment("Delay, in seconds, on how long a player must wait on the telepad block before the gui opens")
                    .translation("config.tele.gui.open").defineInRange("Teleport GUI Delay", 3, 1, 60);
            builder.pop();

            val = builder.comment("""
                            [x,y,z,dimension,locationName]
                            -locations can be defined in multiple ways:
                            -exact coordinates : 100/64/100/overworld/Your Own TelepadIdentifier name
                            -with margin : -500#1000/64#128/0#500/the_nether/Some Location
                            -random : random/random/random/random/My Random Teleporter
                            -values may be mixed : -100#5000/random/100/the_end/locationNameHere
                            """)
                    .defineList("reflection_whitelisted_blocks", Lists.newArrayList(), obj -> obj instanceof String);
        }
    }

    public static class ClientConfig {

        public final ForgeConfigSpec.BooleanValue allowParticles;

        ClientConfig(ForgeConfigSpec.Builder builder) {

            builder.push("Rendering");
            allowParticles = builder.comment("Some people find them annoying. Feel free to disable them here if needed")
                    .translation("config.particles.telepad.allow").define("Allow Particle Spawning", true);
            builder.pop();

        }
    }
}
