package subaraki.telepads.handler;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.utility.TelepadEntry;

public class WorldDataHandler extends SavedData {

    private static final String TELEPADS_WORLD_SAVE_DATA = "telepads_world_save_data";

    private List<TelepadEntry> allTelepads = new ArrayList<TelepadEntry>();

    public WorldDataHandler() {

        super(TELEPADS_WORLD_SAVE_DATA);
    }

    @Override
    public void load(CompoundTag nbt)
    {

        List<TelepadEntry> entryList = new ArrayList<TelepadEntry>();
        ListTag taglist = nbt.getList("entries", 10);

        for (int entryTag = 0; entryTag < taglist.size(); entryTag++)
            entryList.add(new TelepadEntry(taglist.getCompound(entryTag)));

        this.allTelepads = entryList;
    }

    @Override
    public CompoundTag save(CompoundTag nbt)
    {

        ListTag taglist = new ListTag();

        if (!allTelepads.isEmpty())
            for (TelepadEntry entry : allTelepads)
            {
                taglist.add(entry.writeToNBT(new CompoundTag()));
            }
        nbt.put("entries", taglist);

        return nbt;
    }

    public static WorldDataHandler get(LevelAccessor world)
    {

        ServerLevel overworld = ((ServerLevel) world).getServer().getLevel(Level.OVERWORLD);
        return overworld.getDataStorage().computeIfAbsent(WorldDataHandler::new, TELEPADS_WORLD_SAVE_DATA);
    }

    /////////////////////////////////////////////////////////////////////////////
    public void addEntry(TelepadEntry entry)
    {

        if (!contains(entry))
            allTelepads.add(entry);
    }

    /**
     * checks for position and dimension. you can never place two pads at the same
     * spot.
     */
    public boolean contains(TelepadEntry entry)
    {

        if (allTelepads.isEmpty())
            return false;
        for (TelepadEntry pad : allTelepads)
            if (entry.equals(pad))
                return true;
        return false;
    }

    public void removeEntry(TelepadEntry entry)
    {

        if (contains(entry))
            allTelepads.remove(entry);
    }

    /** Compares block position and dimension id, ignoring the name of the entry */
    public TelepadEntry getEntryForLocation(BlockPos pos, ResourceKey<Level> dimID)
    {

        if (allTelepads.isEmpty())
            return null;

        for (TelepadEntry entry : allTelepads)
            if (entry.position.equals(pos))
                if (entry.dimensionID.equals(dimID))
                    return entry;

        return null;
    }

    /**
     * replaces the entry that has the same position as the given entry, to update
     * eventual upgrades that can be set
     */
    public void updateEntry(TelepadEntry entry)
    {

        if (contains(entry))
            if (getEntryForLocation(entry.position, entry.dimensionID) != null)
            {
                allTelepads.remove(getEntryForLocation(entry.position, entry.dimensionID));
                allTelepads.add(entry);
            }
    }

    public boolean isEntryPowered(TelepadEntry entry)
    {

        if (contains(entry))
        {
            if (getEntryForLocation(entry.position, entry.dimensionID).isPowered)
                return true;
        }
        return false;
    }

    public List<TelepadEntry> getEntries()
    {

        return allTelepads;
    }

    /** used only to sync server's data to the client */
    public void copyOverEntries(List<TelepadEntry> allTelepads)
    {

        this.allTelepads = allTelepads;
    }

    @EventBusSubscriber(bus = Bus.FORGE, modid = Telepads.MODID)
    public static class WorldDataHandlerSaveEvent {

        @SubscribeEvent
        public static void onWorldSave(WorldEvent.Save event)
        {

            if (event.getWorld() instanceof ServerLevel)
            {
                ServerLevel server = (ServerLevel) event.getWorld();
                ServerLevel overworld = server.getServer().getLevel(Level.OVERWORLD);
                WorldDataHandler.get(overworld).setDirty();

            }
        }

        @SubscribeEvent
        public static void onWorldLoad(WorldEvent.Load event)
        {

            if (event.getWorld() instanceof ServerLevel)
            {
                ServerLevel server = (ServerLevel) event.getWorld();
                ServerLevel overworld = server.getServer().getLevel(Level.OVERWORLD);
                // simply calling an instance will load it's data
                WorldDataHandler.get(overworld);

            }
        }
    }
}
