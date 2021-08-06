package subaraki.telepads.network.client;

import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import subaraki.telepads.network.IPacketBase;
import subaraki.telepads.network.NetworkHandler;
import subaraki.telepads.utility.ClientReferences;

public class CPacketEditWhiteListEntry implements IPacketBase {

    public String name;
    public UUID id;
    public boolean add;

    public CPacketEditWhiteListEntry() {

    }

    public CPacketEditWhiteListEntry(String name, UUID id, boolean add) {

        this.name = name;
        this.id = id;
        this.add = add;
    }

    public CPacketEditWhiteListEntry(FriendlyByteBuf buf) {

        this.decode(buf);
    }

    @Override
    public void encode(FriendlyByteBuf buf)
    {

        buf.writeUtf(name, 16);
        buf.writeBoolean(add);
        buf.writeUUID(id);

    }

    @Override
    public void decode(FriendlyByteBuf buf)
    {

        this.name = buf.readUtf();
        this.add = buf.readBoolean();
        this.id = buf.readUUID();
    }

    @Override
    public void handle(Supplier<Context> context)
    {

        context.get().enqueueWork(() -> {
            if (FMLEnvironment.dist == Dist.CLIENT)
                ClientReferences.handlePacket(this);

        });
        context.get().setPacketHandled(true);
    }

    @Override
    public void register(int id)
    {

        NetworkHandler.NETWORK.registerMessage(id, CPacketEditWhiteListEntry.class, CPacketEditWhiteListEntry::encode, CPacketEditWhiteListEntry::new,
                CPacketEditWhiteListEntry::handle);

    }

}
