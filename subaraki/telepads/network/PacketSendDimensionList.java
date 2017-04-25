package subaraki.telepads.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSendDimensionList implements IMessage{

	public PacketSendDimensionList() {
	}
	
	public DimensionList dl;
	public PacketSendDimensionList(DimensionList dl) {
		this.dl = dl;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		dl.dim_id = buf.readInt();
		dl.dimension_name = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(dl.dim_id);
		ByteBufUtils.writeUTF8String(buf, dl.dimension_name);
	}

	public static class PacketSendDimensionListHandler implements IMessageHandler<PacketSendDimensionList, IMessage>{
		@Override
		public IMessage onMessage(PacketSendDimensionList message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				message.dl.hasInitializedVariables = true;
			});
			return null;
		}
	}
}
