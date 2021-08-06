package subaraki.telepads.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.network.client.CPacketEditWhiteListEntry;
import subaraki.telepads.network.client.CPacketRequestNamingScreen;
import subaraki.telepads.network.client.CPacketRequestTeleportScreen;
import subaraki.telepads.network.server.SPacketAddTelepadToWorld;
import subaraki.telepads.network.server.SPacketAddWhiteListEntry;
import subaraki.telepads.network.server.SPacketRemoveEntry;
import subaraki.telepads.network.server.SPacketTeleport;

public class NetworkHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(new ResourceLocation(Telepads.MODID, "telepadchannel"), () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public NetworkHandler() {

        int id = 0;

        // client side handling
        new CPacketRequestTeleportScreen().register(id++);
        new CPacketRequestNamingScreen().register(id++);
        new CPacketEditWhiteListEntry().register(id++);

        // server side handling
        new SPacketAddTelepadToWorld().register(id++);
        new SPacketTeleport().register(id++);
        new SPacketRemoveEntry().register(id++);
        new SPacketAddWhiteListEntry().register(id++);
    }
}
