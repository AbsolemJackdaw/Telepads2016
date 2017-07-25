package subaraki.telepads.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSyncPlayerAfterTeleport implements IMessage {

	public PacketSyncPlayerAfterTeleport() {
	}
	
	NBTTagCompound tag_basic;
	NBTTagCompound tag_entity;
	public PacketSyncPlayerAfterTeleport(NBTTagCompound tag_basic, NBTTagCompound tag_entity) {
		this.tag_basic = tag_basic;
		this.tag_entity = tag_entity;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		tag_basic = ByteBufUtils.readTag(buf);
		tag_entity = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, tag_basic);
		ByteBufUtils.writeTag(buf, tag_entity);
	}

	public static class PacketSyncPlayerAfterTeleportHandler implements IMessageHandler<PacketSyncPlayerAfterTeleport, IMessage>{

		@Override
		public IMessage onMessage(PacketSyncPlayerAfterTeleport message, MessageContext ctx){
			Minecraft.getMinecraft().addScheduledTask(()->{
				Minecraft.getMinecraft().player.readFromNBT(message.tag_basic);
				Minecraft.getMinecraft().player.readEntityFromNBT(message.tag_entity);
			});
			return null;
		}
		
	}
}
