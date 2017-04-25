package subaraki.telepads.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import subaraki.telepads.capability.TelePadDataCapability;
import subaraki.telepads.capability.TelepadData;
import subaraki.telepads.handler.WorldDataHandler;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.utility.TelepadEntry;
import subaraki.telepads.utility.TeleportUtility;

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
			((WorldServer)ctx.getServerHandler().playerEntity.world).addScheduledTask(() -> {

				EntityPlayer player = ctx.getServerHandler().playerEntity;
				WorldDataHandler wdh = WorldDataHandler.get(player.world);

				TelepadData td = player.getCapability(TelePadDataCapability.CAPABILITY, null);
				td.setInTeleportGui(false);

				BlockPos goTo = packet.goTo.position.up();
				int goToDimensionid = packet.goTo.dimensionID;
				
				if (packet.goTo.dimensionID == player.dimension) {
					if (packet.force) {
						TeleportUtility.teleportEntityTo(player, goTo, player.rotationYaw, player.rotationPitch);
						return;
					}

					if(wdh.contains(packet.goTo)){
						if (!packet.goTo.isPowered) {
							if (goToDimensionid == player.dimension)
								TeleportUtility.teleportEntityTo(player, goTo, player.rotationYaw, player.rotationPitch);
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
						TeleportUtility.changeToDimension(player, goTo, goToDimensionid, FMLCommonHandler.instance().getMinecraftServerInstance());
						return;
					}
					if(wdh.contains(packet.goTo))
						if (!packet.goTo.isPowered) 
							TeleportUtility.changeToDimension(player, goTo, goToDimensionid, FMLCommonHandler.instance().getMinecraftServerInstance());
						else
							player.sendMessage(new TextComponentString(TextFormatting.ITALIC+""+TextFormatting.DARK_RED+"This pad was powered off"));
					else
						removePad(player, packet.goTo);
				}
				
				syncUpClientWithServerHotFix((EntityPlayerMP) player);
			});
			return null;
		}

		private void syncUpClientWithServerHotFix(EntityPlayerMP player) {
			NBTTagCompound tag_basic = new NBTTagCompound();
			player.writeToNBT(tag_basic);
			NBTTagCompound tag_entity = new NBTTagCompound();
			player.writeToNBT(tag_entity);
			NetworkHandler.NETWORK.sendTo(new PacketSyncPlayerAfterTeleport(tag_basic, tag_entity), player);			
		}

		private static void removePad (EntityPlayer player,  TelepadEntry entry) {
			TelepadData td = player.getCapability(TelePadDataCapability.CAPABILITY, null);
			td.addEntry(new TelepadEntry("QUEUEDFORREMOVAL", entry.dimensionID, entry.position, false, false));
			td.sync();
			td.setInTeleportGui(true);
			Telepads.proxy.openRemovalGui(player);
		}
	}
}