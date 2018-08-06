package subaraki.telepads.network;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import subaraki.telepads.capability.TelePadDataCapability;
import subaraki.telepads.capability.TelepadData;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.utility.TelepadEntry;

public class PacketSyncTelepadData implements IMessage {

	/**
	 * The unique identifier of the player sync this data to.
	 */
	private UUID playerUUID;

	/**
	 * The list of entries to sync to the player.
	 */
	private List<TelepadEntry> entries;

	/**
	 * The list of friends that will have coordinates added.
	 */
	private List<String> whiteList;

	/**
	 * A packet to sync entries to a player, on the client side. This packet must only be sent
	 * from a server thread. The entries on the client side will be overridden with the
	 * provided list of entries.
	 * 
	 * @param playerUUID : The unique identifier of the player sync this data to.
	 * @param entries : The list of entries to sync to the player.
	 */
	public PacketSyncTelepadData(UUID playerUUID, List<TelepadEntry> entries, List<String> whiteList) {

		this.playerUUID = playerUUID;
		this.entries = entries;
		this.whiteList = whiteList;
	}

	@Override
	public void fromBytes (ByteBuf buf) {

		List<TelepadEntry> entryList = new ArrayList<TelepadEntry>();
		this.playerUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
		int size = buf.readInt();

		if (!(size > 0))
			return;

		for (int index = 0; index < size; index++)
			entryList.add(new TelepadEntry(buf));

		this.entries = entryList;

		size = buf.readInt();
		List<String> whiteList = new ArrayList<String>();

		for(int i =0; i <size; i++)
			whiteList.add(ByteBufUtils.readUTF8String(buf));

		this.whiteList = whiteList;
	}

	@Override
	public void toBytes (ByteBuf buf) {

		ByteBufUtils.writeUTF8String(buf, playerUUID.toString());
		buf.writeInt(this.entries.size());

		for (TelepadEntry entry : this.entries)
			entry.writeToByteBuf(buf);

		buf.writeInt(this.whiteList.size());
		for(String s : whiteList)
			ByteBufUtils.writeUTF8String(buf, s);
	}

	public PacketSyncTelepadData() {

	}

	public static class PacketSyncTelepadEntriesHandler implements IMessageHandler<PacketSyncTelepadData, IMessage> {

		@Override
		public IMessage onMessage (PacketSyncTelepadData packet, MessageContext ctx) {

			Minecraft.getMinecraft().addScheduledTask(()->{
				EntityPlayer player = Telepads.proxy.getClientPlayer();
				TelepadData td = player.getCapability(TelePadDataCapability.CAPABILITY, null);
				td.overrideEntries(packet.entries);

				if(packet.whiteList != null && !packet.whiteList.isEmpty())
					for(String s : packet.whiteList)
						td.addToWiteList(s);

				//reset states
				td.setCounter(td.getMaxTime());
				td.setInTeleportGui(false);
			});
			return null;
		}
	}
}
