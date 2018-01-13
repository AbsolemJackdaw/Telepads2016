package subaraki.telepads.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import subaraki.telepads.capability.TelePadDataCapability;
import subaraki.telepads.capability.TelepadData;

public class PacketAddWhitelistEntry implements IMessage {

	public PacketAddWhitelistEntry() {
	}

	public String name;

	public PacketAddWhitelistEntry(String name) {
		this.name = name;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		name = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, name);
	}

	public static class PacketAddWhitelistEntryHandler implements IMessageHandler<PacketAddWhitelistEntry, IMessage>{

		@Override
		public IMessage onMessage(PacketAddWhitelistEntry message, MessageContext ctx) {

			WorldServer server = (WorldServer)ctx.getServerHandler().player.world;
			EntityPlayer player = ctx.getServerHandler().player;

			server.addScheduledTask(() -> {

				TelepadData data = player.getCapability(TelePadDataCapability.CAPABILITY, null);
				EntityPlayer friend = server.getPlayerEntityByName(message.name);
				if(friend != null)
				{
					data.addToWiteList(message.name);
				}
				else if(message.name.startsWith("/"))
				{
					try {
						data.commandWhitelist(message.name);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			return null;
		}
	}
}
