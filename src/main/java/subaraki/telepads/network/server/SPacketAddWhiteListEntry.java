package subaraki.telepads.network.server;

import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import subaraki.telepads.capability.player.TelepadData;
import subaraki.telepads.network.IPacketBase;
import subaraki.telepads.network.NetworkHandler;

public class SPacketAddWhiteListEntry implements IPacketBase {

    private String command;

    public SPacketAddWhiteListEntry() {

    }

    public SPacketAddWhiteListEntry(String command) {

        this.command = command;
    }

    public SPacketAddWhiteListEntry(PacketBuffer buf) {

        this.decode(buf);
    }

    @Override
    public void encode(PacketBuffer buf)
    {

        buf.writeUtf(command, 23);
    }

    @Override
    public void decode(PacketBuffer buf)
    {

        this.command = buf.readUtf(23);
    }

    @Override
    public void handle(Supplier<Context> context)
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
