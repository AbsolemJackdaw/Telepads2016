package subaraki.telepads.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import subaraki.telepads.capability.TelePadDataCapability;
import subaraki.telepads.capability.TelepadData;
import subaraki.telepads.gui.GuiHandler;
import subaraki.telepads.handler.WorldDataHandler;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.utility.TelepadEntry;
import subaraki.telepads.utility.masa.Teleport;

public class PacketTeleport implements IMessage {

	/**
	 * The position to send the player to.
	 */
	public TelepadEntry goTo;

	/**
	 * The position the player comes from.
	 */
	public BlockPos oldPos;

	/**
	 * flag to force teleport and bypass checking if a tile entity exists to teleport too
	 */
	public boolean force;

	/**
	 * A packet to teleport the player to a given position from the client side. This packet
	 * must be sent from a client thread.
	 * 
	 * @param goTo : The position/entry to send the player to.
	 * @param playerFrom : The position the player comes from.
	 * @param forceTeleport : Flag to force teleport and bypass checking if a telepad entry
	 *            exists to teleport too
	 */
	public PacketTeleport(BlockPos playerFrom, TelepadEntry goTo, boolean forceTeleport) {

		this.oldPos = playerFrom;
		this.goTo = goTo;
		this.force = forceTeleport;
	}

	@Override
	public void fromBytes (ByteBuf buf) {

		oldPos = BlockPos.fromLong(buf.readLong());
		goTo = new TelepadEntry(buf);
		force = buf.readBoolean();
	}

	@Override
	public void toBytes (ByteBuf buf) {
		buf.writeLong(oldPos.toLong());
		goTo.writeToByteBuf(buf);
		buf.writeBoolean(force);
	}

	public PacketTeleport() {

	}

	public static class PacketTeleportHandler implements IMessageHandler<PacketTeleport, IMessage> {

		@Override
		public IMessage onMessage (PacketTeleport packet, MessageContext ctx) {
			((WorldServer)ctx.getServerHandler().player.world).addScheduledTask(() -> {

				
				EntityPlayer player = ctx.getServerHandler().player;
				WorldDataHandler wdh = WorldDataHandler.get(player.world);

				TelepadData td = player.getCapability(TelePadDataCapability.CAPABILITY, null);
				td.setInTeleportGui(false);

				BlockPos goTo = packet.goTo.position.up();
				int goToDimensionid = packet.goTo.dimensionID;

				if (packet.goTo.dimensionID == player.dimension) {
					if (packet.force) {
						Teleport.teleportEntityInsideSameDimension(player, goTo);
						return;
					}

					if(wdh.contains(packet.goTo)){
						if (!packet.goTo.isPowered) {
							if (goToDimensionid == player.dimension)
								Teleport.teleportEntityInsideSameDimension(player, goTo);
						}
						else
							player.sendMessage(new TextComponentString(TextFormatting.ITALIC+""+TextFormatting.DARK_RED+"This pad was powered off"));
					}
					else{
						td.setInTeleportGui(true); //set to true so when changing gui, it doesnt try to open the teleport gui.
						removePad(player, packet.goTo);
					}
				}
				else {
					if (packet.force) {
						Teleport.teleportEntityToDimension(player, goTo, goToDimensionid);
						return;
					}
					if(wdh.contains(packet.goTo))
						if (!packet.goTo.isPowered) 
							Teleport.teleportEntityToDimension(player, goTo, goToDimensionid);
						else
							player.sendMessage(new TextComponentString(TextFormatting.ITALIC+""+TextFormatting.DARK_RED+"This pad was powered off"));
					else
						removePad(player, packet.goTo);
				}
			});
			return null;
		}

		private static void removePad (EntityPlayer player,  TelepadEntry entry) {
			TelepadData td = player.getCapability(TelePadDataCapability.CAPABILITY, null);
			td.addEntry(new TelepadEntry("QUEUEDFORREMOVAL", entry.dimensionID, entry.position, false, false));
			td.sync();
			td.setInTeleportGui(true);
			player.openGui(Telepads.instance, GuiHandler.REMOVE_TELEPAD, player.world, 0,0,0);
		}
	}
}