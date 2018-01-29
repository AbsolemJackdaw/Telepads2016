package subaraki.telepads.capability;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import subaraki.telepads.handler.ConfigurationHandler;
import subaraki.telepads.handler.WorldDataHandler;
import subaraki.telepads.network.NetworkHandler;
import subaraki.telepads.network.PacketSyncTelepadData;
import subaraki.telepads.utility.TelepadEntry;

public class TelepadData {

	private EntityPlayer player ;
	/**A list of entries that this player has acces to. */
	//initialize to prevent null crashes caused by external sources
	private List<TelepadEntry> entries = new ArrayList<TelepadEntry>();

	/**A list of uuid's a player can whitelist, to share coordinates of a placed pad.*/
	private List<String> whitelist = new ArrayList<String>();

	private boolean isInTeleportGui;
	private static final int MAX_TIME = ConfigurationHandler.instance.teleport_seconds * 20;
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

		NBTTagList friends = new NBTTagList();
		for(String S : whitelist)
		{
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("friend", S);
			friends.appendTag(nbt);
		}
		tag.setTag("list", friends);

		return tag;
	}

	public void readData(NBTBase nbt){

		NBTTagCompound tag = (NBTTagCompound)nbt;

		List<TelepadEntry> entryList = new ArrayList<TelepadEntry>();
		NBTTagList entryTagList = tag.getTagList("entries", 10);

		for (int tagPos = 0; tagPos < entryTagList.tagCount(); tagPos++)
			entryList.add(new TelepadEntry(entryTagList.getCompoundTagAt(tagPos)));

		this.entries = entryList;

		NBTTagList friendList = tag.getTagList("list", 10);
		for(int i = 0; i < friendList.tagCount(); i++)
		{
			NBTTagCompound compound = friendList.getCompoundTagAt(i);
			String s = compound.getString("friend");
			whitelist.add(s);
		}

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
			NetworkHandler.NETWORK.sendTo(new PacketSyncTelepadData(player.getUniqueID(), this.entries, this.whitelist), (EntityPlayerMP) player);
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

	public void syncPublicPadsToPlayer(WorldDataHandler wdh){
		ArrayList<TelepadEntry> remove = new ArrayList<>(); //battling ConcurrentModificationException here

		for(TelepadEntry owned : getEntries())
		{
			//if the public pad got set private
			if(owned.isPublic && wdh.contains(owned) && !wdh.getEntryForLocation(owned.position, owned.dimensionID).isPublic)
			{
				remove.add(owned);
			}

			//if the public pad doesn't exist anymore
			if(owned.isPublic && !wdh.contains(owned))
			{
				remove.add(owned);
			}
		}

		if(!remove.isEmpty())
		{
			for(TelepadEntry entry : remove)
				removeEntry(entry);
		}

		for(TelepadEntry entry : wdh.getEntries())
		{
			if(entry.isPublic && !getEntries().contains(entry))
				addEntry((TelepadEntry)entry.clone());
		}
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

	public boolean addToWiteList(String player){
		if(!isWhiteListFull())
			if(!whitelist.contains(player))
				whitelist.add(player);
			else
				return false;

		return true;
	}

	public void clearList(){
		whitelist.clear();
	}

	public void commandWhitelist(String s) throws Exception{
		if(s.startsWith("/"))
		{
			s = s.substring(1);
			String[] text = s.split(" ");

			if(text.length == 1)
			{
				if(text[0].toLowerCase().equals("clear"))
					clearList();
				else
					throw new Exception("no such command " + text[0]);
			}

			else if(text.length == 2)
				if(text[0].toLowerCase().equals("remove"))
					if(whitelist.contains(text[1]))
					{
						whitelist.remove(text[1]);
					}
					else;
				else if(text[0].toLowerCase().equals("add"))
				{
					if(player.world.getPlayerEntityByName(text[1]) != null)
						addToWiteList(text[1]);
				}
				else
					throw new Exception("no such command " + text[0]);
			else
				throw new Exception("too much arguments ! ");

			sync();
		}
	}

	public List<String> getWhitelist() {
		return whitelist;
	}

	public boolean isWhiteListFull(){
		return whitelist.size() >= 9;
	}
}
