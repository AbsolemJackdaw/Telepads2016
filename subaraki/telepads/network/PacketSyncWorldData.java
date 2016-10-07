package subaraki.telepads.network;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import subaraki.telepads.handler.WorldDataHandler;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.utility.TelepadEntry;

public class PacketSyncWorldData implements IMessage{

	public PacketSyncWorldData() {
	}
	
	public List<TelepadEntry> listOfEntries = new ArrayList<TelepadEntry>();

	public PacketSyncWorldData(List<TelepadEntry> listOfEntries) {
		this.listOfEntries = listOfEntries;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		listOfEntries = new ArrayList<TelepadEntry>();
		int size = buf.readInt();
		for(int i = 0; i < size; i++){
			TelepadEntry entry = new TelepadEntry(buf);
			listOfEntries.add(entry);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(listOfEntries.size());
		for(TelepadEntry entry : listOfEntries)
			entry.writeToByteBuf(buf);
	}

	public static class PacketSyncWorldDataHandler implements IMessageHandler<PacketSyncWorldData, IMessage>{

		@Override
		public IMessage onMessage(PacketSyncWorldData message, MessageContext ctx) {

			Minecraft.getMinecraft().addScheduledTask(() -> {
				WorldDataHandler wdh = WorldDataHandler.get(Telepads.proxy.getClientPlayer().worldObj);
				wdh.copyOverEntries(message.listOfEntries);
				wdh.markDirty();
			});
			
			return null;
		}

	}
}
