package subaraki.telepads.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import subaraki.telepads.capability.TelePadDataCapability;
import subaraki.telepads.capability.TelepadData;
import subaraki.telepads.handler.WorldDataHandler;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.tileentity.TileEntityTelepad;
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

	/**
	 * A packet to add a new TelepadEntry to a player's list of locations. This packet is used
	 * to send data from the client to the server, and should not be sent from a server thread.
	 * When this packet is handled on the server side, a sync packet with automatically be sent
	 * back to the client to ensure everything is consistent.
	 * 
	 * @param playerUUID : The UUID of the player to add the new TelepadEntry to.
	 * @param entry : The TelepadEntry to be added to the player's list of locations.
	 */
	public PacketAddTelepadEntry(UUID playerUUID, TelepadEntry entry) {

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

	public PacketAddTelepadEntry() {

	}

	public static class PacketAddTelepadEntryHandler implements IMessageHandler<PacketAddTelepadEntry, IMessage> {

		@Override
		public IMessage onMessage (PacketAddTelepadEntry packet, MessageContext ctx) {

			((WorldServer)ctx.getServerHandler().playerEntity.worldObj).addScheduledTask(() -> {
				EntityPlayer player = ctx.getServerHandler().playerEntity.worldObj.getPlayerEntityByUUID(packet.playerUUID);

				TelepadData td = player.getCapability(TelePadDataCapability.CAPABILITY, null);
				WorldDataHandler wdh = WorldDataHandler.get(player.worldObj);

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
			});
			return null;
		}
	}
}
