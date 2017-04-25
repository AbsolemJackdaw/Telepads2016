package subaraki.telepads.handler;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import subaraki.telepads.network.NetworkHandler;
import subaraki.telepads.network.PacketSyncWorldData;
import subaraki.telepads.utility.TelepadEntry;

public class WorldDataHandler extends WorldSavedData {

	private static final String TELEPADS_WORLD_SAVE_DATA = "telepads_world_save_data";

	private List<TelepadEntry> allTelepads = new ArrayList<TelepadEntry>();

	public WorldDataHandler() {
		super(TELEPADS_WORLD_SAVE_DATA);
	}

	public WorldDataHandler(String s) {
		super(s);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		List<TelepadEntry> entryList = new ArrayList<TelepadEntry>();
		NBTTagList taglist = nbt.getTagList("entries", 10);

		for (int entryTag = 0; entryTag < taglist.tagCount(); entryTag++)
			entryList.add(new TelepadEntry(taglist.getCompoundTagAt(entryTag)));

		this.allTelepads = entryList;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		NBTTagList taglist = new NBTTagList();

		if(!allTelepads.isEmpty())
			for(TelepadEntry entry : allTelepads){
				taglist.appendTag(entry.writeToNBT(new NBTTagCompound()));
			}
		nbt.setTag("entries", taglist);

		return nbt;
	}

	public static WorldDataHandler get(World world){
		MapStorage storage = world.getMapStorage(); // world.getperworldstorage for dimension precise saving
		WorldSavedData instance = storage.getOrLoadData(WorldDataHandler.class, TELEPADS_WORLD_SAVE_DATA);

		if(instance == null){
			instance = new WorldDataHandler();
			storage.setData(TELEPADS_WORLD_SAVE_DATA, instance);
		}
		return (WorldDataHandler) instance;
	}

	/////////////////////////////////////////////////////////////////////////////
	public void addEntry(TelepadEntry entry){
		if(!contains(entry))
			allTelepads.add(entry);
		syncClient();
	}

	/**checks for position and dimension. you can never place two pads at the same spot.*/
	public boolean contains(TelepadEntry entry){
		if(allTelepads.isEmpty())
			return false;
		for(TelepadEntry pad : allTelepads)
			if(entry.position.equals(pad.position))
				if(entry.dimensionID == pad.dimensionID)
					return true;
		return false;
	}

	public void removeEntry(TelepadEntry entry) {
		if(contains(entry))
			allTelepads.remove(getEntryForLocation(entry.position, entry.dimensionID));

		syncClient();
	}

	public TelepadEntry getEntryForLocation(BlockPos pos, int dimensionId){
		if(allTelepads.isEmpty())
			return null;

		for(TelepadEntry entry : allTelepads)
			if(entry.position.equals(pos))
				if(entry.dimensionID==dimensionId)
					return entry;

		return null;
	}

	public void syncClient(){
		NetworkHandler.NETWORK.sendToAll(new PacketSyncWorldData(allTelepads));
	}

	/**replaces the entry that has the same position as the given entry, to update eventual upgrades that can be set*/
	public void updateEntry(TelepadEntry entry){
		if(contains(entry))
			if(getEntryForLocation(entry.position, entry.dimensionID) != null){
				allTelepads.remove(getEntryForLocation(entry.position, entry.dimensionID));
				allTelepads.add(entry);
			}
		syncClient();
	}

	public boolean isEntryPowered(TelepadEntry entry){
		if(contains(entry)){
			if(getEntryForLocation(entry.position, entry.dimensionID).isPowered)
				return true;
		}
		return false;
	}
	
	public List<TelepadEntry> getEntries(){
		return allTelepads;
	}

	/**used only to sync server's data to the client*/
	public void copyOverEntries(List<TelepadEntry> allTelepads ){
		this.allTelepads = allTelepads;
	}
	
	public static class WorldDataHandlerSaveEvent{
		
		public WorldDataHandlerSaveEvent() {
			MinecraftForge.EVENT_BUS.register(this);
		}
		
		@SubscribeEvent
		public void onWorldSave(WorldEvent.Save event){
			WorldDataHandler.get(event.getWorld()).markDirty();
		}
		@SubscribeEvent
		public void onWorldLoad(WorldEvent.Load event){
			//simply calling an instance will load it's data
			WorldDataHandler.get(event.getWorld());
		}
	}
}
