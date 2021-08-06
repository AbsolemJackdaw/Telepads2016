package subaraki.telepads.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import subaraki.telepads.capability.player.TelepadData;
import subaraki.telepads.network.IPacketBase;
import subaraki.telepads.network.NetworkHandler;

import java.util.function.Supplier;

public class SPacketAddWhiteListEntry implements IPacketBase {

    private String command;

    public SPacketAddWhiteListEntry() {

    }

    public SPacketAddWhiteListEntry(String command) {

        this.command = command;
    }

    public SPacketAddWhiteListEntry(FriendlyByteBuf buf) {

        this.decode(buf);
    }

    @Override
    public void encode(FriendlyByteBuf buf)
    {

        buf.writeUtf(command, 23);
    }

    @Override
    public void decode(FriendlyByteBuf buf)
    {

        this.command = buf.readUtf(23);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context)
    {

        context.get().enqueueWork(() -> {
            TelepadData.get(context.get().getSender()).ifPresent(data -> {
                data.commandWhitelist(command);
            });
        });
        context.get().setPacketHandled(true);
    }

    @Override
    public void register(int id)
    {

        NetworkHandler.NETWORK.registerMessage(id, SPacketAddWhiteListEntry.class, SPacketAddWhiteListEntry::encode, SPacketAddWhiteListEntry::new,
                SPacketAddWhiteListEntry::handle);

    }

}
