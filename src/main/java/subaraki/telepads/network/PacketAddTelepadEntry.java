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
import subaraki.telepads.handler.WorldDataHandler;
import subaraki.telepads.utility.TelepadEntry;

public class PacketAddTelepadEntry implements IMessage {

	/**
	 * The UUID of the player to add the new TelepadEntry to.
	 */
	private UUID playerUUID;

	/**
	 * The entry to be added to the player's list of locations.
	 */
	private TelepadEntry entry;

	/**wether or not to share the telepadentry to whitelisted friends*/
	private boolean share;

	/**
	 * A packet to add a new TelepadEntry to a player's list of locations. This packet is used
	 * to send data from the client to the server, and should not be sent from a server thread.
	 * When this packet is handled on the server side, a sync packet with automatically be sent
	 * back to the client to ensure everything is consistent.
	 * 
	 * @param playerUUID : The UUID of the player to add the new TelepadEntry to.
	 * @param entry : The TelepadEntry to be added to the player's list of locations.
	 */
	public PacketAddTelepadEntry(UUID playerUUID, TelepadEntry entry, boolean share) {

		this.playerUUID = playerUUID;
		this.entry = entry;
		this.share = share;
	}

	@Override
	public void fromBytes (ByteBuf buf) {

		this.playerUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
		this.entry = new TelepadEntry(buf);
		this.share = buf.readBoolean();
	}

	@Override
	public void toBytes (ByteBuf buf) {

		ByteBufUtils.writeUTF8String(buf, this.playerUUID.toString());
		this.entry.writeToByteBuf(buf);
		buf.writeBoolean(share);
	}

	public PacketAddTelepadEntry() {

	}

	public static class PacketAddTelepadEntryHandler implements IMessageHandler<PacketAddTelepadEntry, IMessage> {

		@Override
		public IMessage onMessage (PacketAddTelepadEntry packet, MessageContext ctx) {

			((WorldServer)ctx.getServerHandler().player.world).addScheduledTask(() -> {
				EntityPlayer player = ctx.getServerHandler().player.world.getPlayerEntityByUUID(packet.playerUUID);

				TelepadData td = player.getCapability(TelePadDataCapability.CAPABILITY, null);
				WorldDataHandler wdh = WorldDataHandler.get(player.world);

				addSafeEntry(td, wdh, packet);

				if(packet.share)
				{
					for(String name : td.getWhitelist())
					{
						EntityPlayer friend = player.world.getPlayerEntityByName(name);
						if(friend != null)
						{
							TelepadData data = friend.getCapability(TelePadDataCapability.CAPABILITY, null);
							addSafeEntry(data, wdh, packet);
						}
					}
				}
			});
			return null;
		}
		
		private void addSafeEntry(TelepadData td, WorldDataHandler wdh, PacketAddTelepadEntry packet){
			if (td.getEntries().isEmpty()){
				td.addEntry(packet.entry);
				wdh.addEntry(packet.entry);
				wdh.markDirty();
			}else{
				TelepadEntry hasEntry = null;	
				for (TelepadEntry tpe : td.getEntries())
					if (tpe.position.equals(packet.entry.position))
						if(tpe.dimensionID == packet.entry.dimensionID){
							hasEntry = tpe;
							break;
						}
				if(hasEntry == null){
					td.addEntry(packet.entry);
					wdh.addEntry(packet.entry);
					wdh.markDirty();
				}else{
					//dont know why i add this... should never be reached
					//remove old entry
					td.removeEntry(hasEntry);
					wdh.removeEntry(hasEntry);
					//replace with given entry
					td.addEntry(packet.entry);
					wdh.addEntry(packet.entry);
					wdh.markDirty();
				}
			}
		}
	}
}
