package subaraki.telepads.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import subaraki.telepads.gui.GuiHandler;
import subaraki.telepads.mod.Telepads;

public class PacketOpenWhiteList implements IMessage{

	public PacketOpenWhiteList() {
	}

	@Override
	public void fromBytes(ByteBuf buf) {

	}

	@Override
	public void toBytes(ByteBuf buf) {

	}

	public static class PacketOpenWhiteListHandler implements IMessageHandler<PacketOpenWhiteList, IMessage>{

		@Override
		public IMessage onMessage(PacketOpenWhiteList message, MessageContext ctx) {

			((WorldServer)ctx.getServerHandler().player.world).addScheduledTask(() -> {
				EntityPlayerMP player_mp = ctx.getServerHandler().player;
				World world = player_mp.world;

				FMLNetworkHandler.openGui(
						player_mp, 
						Telepads.instance, 
						GuiHandler.WHITELIST,
						world, 
						(int)player_mp.posX, (int)player_mp.posY, (int)player_mp.posZ);
			});

			return null;
		}
	}
}