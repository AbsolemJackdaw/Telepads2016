package subaraki.telepads.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import subaraki.telepads.handler.WorldDataHandler;
import subaraki.telepads.network.IPacketBase;
import subaraki.telepads.network.NetworkHandler;
import subaraki.telepads.utility.TelepadEntry;

import java.util.function.Supplier;

public class SPacketAddTelepadToWorld implements IPacketBase {

    /**
     * The entry to be added to the player's list of locations.
     */
    private TelepadEntry entry;

    /**
     * A packet to add a new TelepadEntry to a player's list of locations. This
     * packet is used to send data from the client to the server, and should not be
     * sent from a server thread. When this packet is handled on the server side, a
     * sync packet with automatically be sent back to the client to ensure
     * everything is consistent.
     *
     * @param entry : The TelepadEntry to be added to the player's list of locations.
     */
    public SPacketAddTelepadToWorld(TelepadEntry entry) {

        this.entry = entry;
    }

    public SPacketAddTelepadToWorld(FriendlyByteBuf buf) {

        decode(buf);
    }

    public SPacketAddTelepadToWorld() {

    }

    @Override
    public void encode(FriendlyByteBuf buf) {

        this.entry.writeToBuffer(buf);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {

        this.entry = new TelepadEntry(buf);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {

        context.get().enqueueWork(() -> {
            Player player = context.get().getSender();
            if (player != null) {

                WorldDataHandler wdh = WorldDataHandler.get(player.level);

                TelepadEntry old_entry = wdh.getEntryForLocation(entry.position, entry.dimensionID);

                // if there is no matching entry already in the world save
                if (old_entry == null) {
                    // add to world save
                    wdh.addEntry(entry);
                }

                // if the entry existed before and had it's tag set to 'missing', replace that
                // entry
                else if (old_entry.isMissingFromLocation) {
                    wdh.updateEntry(old_entry, entry);
                }

                wdh.setDirty(true);
            }
        });

        context.get().setPacketHandled(true);
    }

    @Override
    public void register(int id) {

        NetworkHandler.NETWORK.registerMessage(id, SPacketAddTelepadToWorld.class, SPacketAddTelepadToWorld::encode, SPacketAddTelepadToWorld::new,
                SPacketAddTelepadToWorld::handle);
    }
}
