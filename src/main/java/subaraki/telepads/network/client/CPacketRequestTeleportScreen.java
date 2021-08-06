package subaraki.telepads.network.client;

import com.google.common.collect.Lists;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import subaraki.telepads.network.IPacketBase;
import subaraki.telepads.network.NetworkHandler;
import subaraki.telepads.utility.ClientReferences;
import subaraki.telepads.utility.TelepadEntry;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class CPacketRequestTeleportScreen implements IPacketBase {

    /**
     * The list of entries to sync to the player.
     */
    public List<TelepadEntry> entries;

    /**
     * The list of friends that will have coordinates added.
     */
    public Collection<UUID> whiteList;

    /** wether the pad we came from has a transmitter or not */
    public boolean has_transmitter;

    /**
     * A packet to sync entries to a player, on the client side. This packet must
     * only be sent from a server thread. The entries on the client side will be
     * overridden with the provided list of entries.
     * 
     * @param playerUUID
     *            : The unique identifier of the player sync this data to.
     * @param entries
     *            : The list of entries to sync to the player.
     */
    public CPacketRequestTeleportScreen(List<TelepadEntry> entries, Collection<UUID> whiteList, boolean has_transmitter) {

        this.entries = entries;
        this.whiteList = whiteList;
        this.has_transmitter = has_transmitter;
    }

    public CPacketRequestTeleportScreen(FriendlyByteBuf buf) {

        decode(buf);
    }

    public CPacketRequestTeleportScreen() {

    }

    @Override
    public void encode(FriendlyByteBuf buf)
    {

        buf.writeInt(this.entries.size());

        for (TelepadEntry entry : this.entries)
            entry.writeToBuffer(buf);

        buf.writeInt(this.whiteList.size());
        for (UUID s : whiteList)
            buf.writeUUID(s);

        buf.writeBoolean(has_transmitter);

    }

    @Override
    public void decode(FriendlyByteBuf buf)
    {

        int size = buf.readInt();
        if (!(size > 0))
            return;
        List<TelepadEntry> entryList = Lists.newArrayList();
        for (int index = 0; index < size; index++)
            entryList.add(new TelepadEntry(buf));
        this.entries = entryList;

        size = buf.readInt();
        List<UUID> whiteList = Lists.newArrayList();
        for (int i = 0; i < size; i++)
            whiteList.add(buf.readUUID());
        this.whiteList = whiteList;

        has_transmitter = buf.readBoolean();

    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context)
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

        NetworkHandler.NETWORK.registerMessage(id, CPacketRequestTeleportScreen.class, CPacketRequestTeleportScreen::encode, CPacketRequestTeleportScreen::new,
                CPacketRequestTeleportScreen::handle);
    }
}
