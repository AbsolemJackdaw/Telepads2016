package subaraki.telepads.capability;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import subaraki.telepads.handler.WorldDataHandler;
import subaraki.telepads.network.NetworkHandler;
import subaraki.telepads.network.PacketSyncTelepadEntries;
import subaraki.telepads.utility.TelepadEntry;

public class TelepadData {

	private EntityPlayer player;
	/**A list of entries that this player has acces to. */
	private List<TelepadEntry> entries;

	private boolean isInTeleportGui;
	private static final int MAX_TIME = 3 * 20;
	public int counter = MAX_TIME;

	public TelepadData(){
	}

	public EntityPlayer getPlayer() { 
		return player; 
	}

	public void setPlayer(EntityPlayer newPlayer){
		entries = new ArrayList<TelepadEntry>();
		this.player = newPlayer;
	}

	public NBTBase writeData(){
		NBTTagCompound tag = new NBTTagCompound();

		NBTTagList entryList = new NBTTagList();
		for (TelepadEntry entry : this.entries)
			entryList.appendTag(entry.writeToNBT(new NBTTagCompound()));

		tag.setTag("entries", entryList);
		return tag;
	}

	public void readData(NBTBase nbt){

		NBTTagCompound tag = (NBTTagCompound)nbt;

		List<TelepadEntry> entryList = new ArrayList<TelepadEntry>();
		NBTTagList entryTagList = tag.getTagList("entries", 10);

		for (int tagPos = 0; tagPos < entryTagList.tagCount(); tagPos++)
			entryList.add(new TelepadEntry(entryTagList.getCompoundTagAt(tagPos)));

		this.entries = entryList;

	}

	public List<TelepadEntry> getEntries () {

		if (this.entries == null)
			this.entries = new ArrayList<TelepadEntry>();

		return this.entries;
	}

	public void addEntry (TelepadEntry entry) {

		this.getEntries().add(entry);
		this.sync();
	}

	public void removeEntry (TelepadEntry entry) {

		for (TelepadEntry tpe : getEntries())
			if (tpe.position.equals(entry.position))
				if (tpe.dimensionID == entry.dimensionID) {
					// if(tpe.entryName.equals(entry.entryName)){
					this.getEntries().remove(tpe);
					break;
				}
		this.sync();
	}

	public void removeEventualQueuedForRemovalEntries(){
		for (TelepadEntry tpe : getEntries())
			if (tpe.entryName.equals("QUEUEDFORREMOVAL")){
				this.getEntries().remove(tpe);
				break; //repeat untill none are left
			}
		sync();
	}

	/**sends packet to sync entries with the client*/
	public void sync () {
		if (player instanceof EntityPlayerMP)
			NetworkHandler.NETWORK.sendTo(new PacketSyncTelepadEntries(player.getUniqueID(), this.entries), (EntityPlayerMP) player);
	}

	public void syncPoweredWithWorldData(WorldDataHandler wdh){
		for(TelepadEntry entry : wdh.getEntries())
			for(TelepadEntry tpe : getEntries()){
				if(entry.position.equals(tpe.position))
					if(entry.dimensionID == tpe.dimensionID)
						tpe.setPowered(entry.isPowered);
			}
		sync();
	}

	public boolean isInTeleportGui() {
		return isInTeleportGui;
	}

	public void setInTeleportGui(boolean isInTeleportGui) {
		this.isInTeleportGui = isInTeleportGui;
	}

	public void overrideEntries(List<TelepadEntry> entries){
		this.entries = entries;
		sync();
	}

	public int getCounter() {
		return counter;
	}

	public void countDown(){
		counter--;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public static int getMaxTime() {
		return MAX_TIME;
	}
}
