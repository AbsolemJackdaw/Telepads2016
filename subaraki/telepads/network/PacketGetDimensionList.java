package subaraki.telepads.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketGetDimensionList implements IMessage{

	public PacketGetDimensionList() {
	}
	
	public DimensionList dl;
	public PacketGetDimensionList(DimensionList dl) {
		this.dl=dl;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		
	}
	
	public static class PacketGetDimensionListHandler implements IMessageHandler<PacketGetDimensionList, IMessage>{

		@Override
		public IMessage onMessage(PacketGetDimensionList message, MessageContext ctx) {

			((WorldServer)ctx.getServerHandler().playerEntity.world).addScheduledTask(() -> {
				int dimension = ctx.getServerHandler().playerEntity.world.provider.getDimension();
				
				message.dl.dim_id = dimension;
				message.dl.dimension_name = DimensionType.getById(dimension).getName();
				NetworkHandler.NETWORK.sendTo(new PacketSendDimensionList(), ctx.getServerHandler().playerEntity);
			});
			return null;
		}
	}
}
