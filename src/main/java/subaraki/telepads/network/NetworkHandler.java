package subaraki.telepads.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import subaraki.telepads.network.PacketAddTelepadEntry.PacketAddTelepadEntryHandler;
import subaraki.telepads.network.PacketAddWhitelistEntry.PacketAddWhitelistEntryHandler;
import subaraki.telepads.network.PacketOpenWhiteList.PacketOpenWhiteListHandler;
import subaraki.telepads.network.PacketRemoveTelepadEntry.PacketRemoveTelepadEntryHandler;
import subaraki.telepads.network.PacketSyncPlayerAfterTeleport.PacketSyncPlayerAfterTeleportHandler;
import subaraki.telepads.network.PacketSyncTelepadData.PacketSyncTelepadEntriesHandler;
import subaraki.telepads.network.PacketSyncWorldData.PacketSyncWorldDataHandler;
import subaraki.telepads.network.PacketTeleport.PacketTeleportHandler;

public class NetworkHandler {

	public static final String CHANNEL = "telepad_channel";
	public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(CHANNEL);

	public NetworkHandler() {

		NETWORK.registerMessage(PacketAddTelepadEntryHandler.class, PacketAddTelepadEntry.class, 0, Side.SERVER);
		NETWORK.registerMessage(PacketRemoveTelepadEntryHandler.class, PacketRemoveTelepadEntry.class, 1, Side.SERVER);
		NETWORK.registerMessage(PacketTeleportHandler.class, PacketTeleport.class, 2, Side.SERVER);

		NETWORK.registerMessage(PacketSyncTelepadEntriesHandler.class, PacketSyncTelepadData.class, 3, Side.CLIENT);
		NETWORK.registerMessage(PacketSyncWorldDataHandler.class, PacketSyncWorldData.class, 4, Side.CLIENT);
		
		NETWORK.registerMessage(PacketSyncPlayerAfterTeleportHandler.class, PacketSyncPlayerAfterTeleport.class, 5, Side.CLIENT);

		NETWORK.registerMessage(PacketAddWhitelistEntryHandler.class, PacketAddWhitelistEntry.class, 6, Side.SERVER);

		NETWORK.registerMessage(PacketOpenWhiteListHandler.class, PacketOpenWhiteList.class, 7, Side.SERVER);

	}
}
