package subaraki.telepads.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import subaraki.telepads.network.PacketAddTelepadEntry.PacketAddTelepadEntryHandler;
import subaraki.telepads.network.PacketRemoveTelepadEntry.PacketRemoveTelepadEntryHandler;
import subaraki.telepads.network.PacketSyncTelepadEntries.PacketSyncTelepadEntriesHandler;
import subaraki.telepads.network.PacketSyncWorldData.PacketSyncWorldDataHandler;
import subaraki.telepads.network.PacketTeleport.PacketTeleportHandler;

public class NetworkHandler {

	public static final String CHANNEL = "telepad_channel";
	public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(CHANNEL);

	public NetworkHandler() {

		NETWORK.registerMessage(PacketAddTelepadEntryHandler.class, PacketAddTelepadEntry.class, 0, Side.SERVER);
		NETWORK.registerMessage(PacketRemoveTelepadEntryHandler.class, PacketRemoveTelepadEntry.class, 1, Side.SERVER);
		NETWORK.registerMessage(PacketTeleportHandler.class, PacketTeleport.class, 2, Side.SERVER);

		NETWORK.registerMessage(PacketSyncTelepadEntriesHandler.class, PacketSyncTelepadEntries.class, 3, Side.CLIENT);
		NETWORK.registerMessage(PacketSyncWorldDataHandler.class, PacketSyncWorldData.class, 4, Side.CLIENT);

	}
}
