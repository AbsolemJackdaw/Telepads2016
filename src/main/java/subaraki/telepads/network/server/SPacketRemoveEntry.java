package subaraki.telepads.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import subaraki.telepads.handler.WorldDataHandler;
import subaraki.telepads.network.IPacketBase;
import subaraki.telepads.network.NetworkHandler;
import subaraki.telepads.utility.TelepadEntry;

import java.util.function.Supplier;

public class SPacketRemoveEntry implements IPacketBase {

    private TelepadEntry entry;

    public SPacketRemoveEntry() {

    }

    public SPacketRemoveEntry(TelepadEntry entry) {

        this.entry = entry;
    }

    public SPacketRemoveEntry(FriendlyByteBuf buf) {

        this.decode(buf);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {

        entry.writeToBuffer(buf);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {

        this.entry = new TelepadEntry(buf);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {

        context.get().enqueueWork(() -> {

            WorldDataHandler wdh = WorldDataHandler.get(context.get().getSender().level);

            if (wdh.contains(entry)) {
                wdh.removeEntry(entry);
            }
        });

        context.get().setPacketHandled(true);
    }

    @Override
    public void register(int id) {

        NetworkHandler.NETWORK.registerMessage(id, SPacketRemoveEntry.class, SPacketRemoveEntry::encode, SPacketRemoveEntry::new, SPacketRemoveEntry::handle);

    }

}
