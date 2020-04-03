package subaraki.telepads.network.server;

import java.util.function.Supplier;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import subaraki.telepads.capability.player.TelepadData;
import subaraki.telepads.handler.WorldDataHandler;
import subaraki.telepads.network.IPacketBase;
import subaraki.telepads.network.NetworkHandler;
import subaraki.telepads.utility.TelepadEntry;

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
     * @param playerUUID
     *            : The UUID of the player to add the new TelepadEntry to.
     * @param entry
     *            : The TelepadEntry to be added to the player's list of locations.
     */
    public SPacketAddTelepadToWorld(TelepadEntry entry) {

        this.entry = entry;
    }

    public SPacketAddTelepadToWorld(PacketBuffer buf) {

        decode(buf);
    }

    public SPacketAddTelepadToWorld() {

    }

    @Override
    public void encode(PacketBuffer buf)
    {

        this.entry.writeToBuffer(buf);
    }

    @Override
    public void decode(PacketBuffer buf)
    {

        this.entry = new TelepadEntry(buf);
    }

    @Override
    public void handle(Supplier<Context> context)
    {

        context.get().enqueueWork(() -> {
            PlayerEntity player = context.get().getSender();

            TelepadData.get(player).ifPresent(data -> {
                WorldDataHandler wdh = WorldDataHandler.get(player.world);

                TelepadEntry old_entry = wdh.getEntryForLocation(entry.position, entry.dimensionID);

                // if there is no matching entry already in the world save
                if (old_entry == null)
                {
                    // add to world save
                    wdh.addEntry(entry);
                }

                // if the entry existed before and had it's tag set to 'missing', replace that
                // entry
                else
                    if (old_entry != null && old_entry.isMissingFromLocation)
                    {
                        old_entry.isMissingFromLocation = false;
                        old_entry.entryName = entry.entryName;
                    }

            });
        });

        context.get().setPacketHandled(true);
    }

    @Override
    public void register(int id)
    {

        NetworkHandler.NETWORK.registerMessage(id, SPacketAddTelepadToWorld.class, SPacketAddTelepadToWorld::encode, SPacketAddTelepadToWorld::new,
                SPacketAddTelepadToWorld::handle);
    }
}
