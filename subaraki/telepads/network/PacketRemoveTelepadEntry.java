package subaraki.telepads.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import subaraki.telepads.capability.TelePadDataCapability;
import subaraki.telepads.capability.TelepadData;
import subaraki.telepads.utility.TelepadEntry;

public class PacketRemoveTelepadEntry implements IMessage {

	/**
	 * The UUID of the player to remove the entry from.
	 */
	private UUID playerUUID;

	/**
	 * The entry to be removed from the player's list of locations.
	 */
	private TelepadEntry entry;

	/**
	 * A packet to remove a TelepadEntry from a player's list of locations. This packet is used
	 * to send data from the client to the server, and should not be sent from a server thread.
	 * When this packet is handled on the server side, a sync packet will automatically be send
	 * back to the client to ensure that everthing is consistent.
	 * 
	 * @param playerUUID : The UUID of the player to remove the entry from.
	 * @param entry : The entry to be removed from the player's list of locations.
	 */
	public PacketRemoveTelepadEntry(UUID playerUUID, TelepadEntry entry) {

		this.playerUUID = playerUUID;
		this.entry = entry;
	}

	@Override
	public void fromBytes (ByteBuf buf) {

		this.playerUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
		this.entry = new TelepadEntry(buf);
	}

	@Override
	public void toBytes (ByteBuf buf) {

		ByteBufUtils.writeUTF8String(buf, this.playerUUID.toString());
		this.entry.writeToByteBuf(buf);
	}

	public PacketRemoveTelepadEntry() {

	}

	public static class PacketRemoveTelepadEntryHandler implements IMessageHandler<PacketRemoveTelepadEntry, IMessage> {

		@Override
		public IMessage onMessage (PacketRemoveTelepadEntry packet, MessageContext ctx) {
			((WorldServer)ctx.getServerHandler().player.world).addScheduledTask(() -> {
				EntityPlayer player = ctx.getServerHandler().player.world.getPlayerEntityByUUID(packet.playerUUID);
				TelepadData td = player.getCapability(TelePadDataCapability.CAPABILITY, null);
				td.removeEntry(packet.entry);
				td.removeEventualQueuedForRemovalEntries();
				td.sync();
			});
			return null;
		}
	}
}
