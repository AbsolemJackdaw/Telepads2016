package subaraki.telepads.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import subaraki.telepads.capability.TelePadDataCapability;
import subaraki.telepads.capability.TelepadData;
import subaraki.telepads.gui.GuiHandler;
import subaraki.telepads.handler.ConfigurationHandler;
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

	/**Necessary empty constructor*/
	public PacketTeleport() {}


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
				int penalty = ConfigurationHandler.instance.expConsume;
				
				if(penalty > 0 && (player.experienceLevel == 0 && player.experience * player.xpBarCap() <= penalty))
				{
					player.sendStatusMessage(new TextComponentTranslation("no.exp").setStyle(new Style().setItalic(true).setColor(TextFormatting.DARK_RED)), true);
					return;
				}
				
				if (packet.goTo.dimensionID == player.dimension) 
				{
					if (packet.force) 
					{
						Teleport.teleportEntityInsideSameDimension(player, goTo);
						teleportPenalty(player);
						return;
					}

					if(wdh.contains(packet.goTo))
					{
						if (!packet.goTo.isPowered) 
						{
							if (goToDimensionid == player.dimension)
							{
								Teleport.teleportEntityInsideSameDimension(player, goTo);
								teleportPenalty(player);
							}
						}
						else
						{
							player.sendStatusMessage(new TextComponentTranslation("no.power").setStyle(new Style().setItalic(true).setColor(TextFormatting.DARK_RED)), true);
						}
					}
					else
					{
						td.setInTeleportGui(true); //set to true so when changing gui, it doesnt try to open the teleport gui.
						removePad(player, packet.goTo);
					}
				}
				else 
				{
					if (packet.force)
					{
						Teleport.teleportEntityToDimension(player, goTo, goToDimensionid);
						teleportPenalty(player);
						return;
					}
					if(wdh.contains(packet.goTo))
					{
						if (!packet.goTo.isPowered) 
						{
							Teleport.teleportEntityToDimension(player, goTo, goToDimensionid);
							teleportPenalty(player);
						}
						else
						{
							player.sendStatusMessage(new TextComponentTranslation("no.power").setStyle(new Style().setItalic(true).setColor(TextFormatting.DARK_RED)), true);
						}
					}
					else
					{
						removePad(player, packet.goTo);
					}
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

		/**Teleport Penalty is removed here if any is given in the config file*/
		private static void teleportPenalty(EntityPlayer player)
		{
			//lessons learned from fiddling with experience stuff : 
			//1 : don't use the experienceTotal to calculate whatever. its inconsistent and can be messed up 
			//by manually adding levels
			//2 : don't set levels manually, use addExperienceLevels, it does all the needed stuff for you
			//3 : experience is only the representing of the bar, and is calculated from 0.0 to 1.0 to draw the green bar,
			//it is some amount of experience devided by the level cap 
			int expConsuming = ConfigurationHandler.instance.expConsume;

			float actualExpInBar = player.experience * (float)player.xpBarCap();

			if(actualExpInBar < expConsuming)//less exp then penalty
			{
				expConsuming-=actualExpInBar; //remove resting exp from penalty
				player.addExperienceLevel(-1); //down a level
				actualExpInBar = (float)player.xpBarCap(); //exp bar is concidered full here when going down a level
				float total = actualExpInBar - expConsuming; //the total refund is one level of exp - the penalty left
				player.experience = 0.0f; //reset the 'exp bar' to 0
				if(total < 0 )
					total = 0;
				player.addExperience((int)total); //give exp
			}
			else
			{
				float total = actualExpInBar - (float)expConsuming;
				player.experience = 0.0f;
				player.addExperience((int)total);
			}
		}
	}
}