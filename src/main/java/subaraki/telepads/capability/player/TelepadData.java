package subaraki.telepads.capability.player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;
import subaraki.telepads.handler.ConfigData;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.network.NetworkHandler;
import subaraki.telepads.network.client.CPacketEditWhiteListEntry;
import subaraki.telepads.utility.TelepadEntry;

public class TelepadData {

    private PlayerEntity player;
    /** A list of entries that this player has acces to. */
    // initialize to prevent null crashes caused by external sources
    private List<TelepadEntry> entries = new ArrayList<TelepadEntry>();

    /**
     * A list of uuid's a player can whitelist, to share coordinates of a placed
     * pad.
     */
    // private List<UUID> whitelist = Lists.newArrayList();
    private LinkedHashMap<String, UUID> whitelist = new LinkedHashMap<>();

    private boolean isInTeleportGui;
    private static final int MAX_TIME = ConfigData.teleport_seconds * 20;
    public int counter = MAX_TIME;

    private boolean request_teleport = false;

    public void setPlayer(PlayerEntity newPlayer)
    {

        this.player = newPlayer;
    }

    public static LazyOptional<TelepadData> get(PlayerEntity player)
    {

        return player.getCapability(TelePadDataCapability.CAPABILITY, null);
    }

    public INBT writeData()
    {

        CompoundNBT tag = new CompoundNBT();

        ListNBT entryList = new ListNBT();
        if (entries != null && !entries.isEmpty())
            for (TelepadEntry entry : this.entries)
                if (entry != null)
                    entryList.add(entry.writeToNBT(new CompoundNBT()));

        tag.put("entries", entryList);

        ListNBT friends = new ListNBT();

        whitelist.keySet().stream().forEach(name -> {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putUUID(name, whitelist.get(name));
            friends.add(nbt);
        });
        tag.put("list", friends);

        return tag;
    }

    public void readData(INBT nbt)
    {

        CompoundNBT tag = (CompoundNBT) nbt;

        List<TelepadEntry> entryList = new ArrayList<TelepadEntry>();
        ListNBT entryTagList = tag.getList("entries", 10);

        for (int tagPos = 0; tagPos < entryTagList.size(); tagPos++)
            entryList.add(new TelepadEntry(entryTagList.getCompound(tagPos)));

        this.entries = entryList;

        ListNBT friendList = tag.getList("list", 10);
        friendList.stream().forEach(entry -> ((CompoundNBT) entry).getAllKeys().forEach(key -> whitelist.put(key, ((CompoundNBT) entry).getUUID(key))));

    }

    public List<TelepadEntry> getEntries()
    {

        if (this.entries == null)
            this.entries = new ArrayList<TelepadEntry>();

        return this.entries;
    }

    public void addEntry(TelepadEntry entry)
    {

        if (!this.getEntries().contains(entry))
            this.getEntries().add(entry);
    }

    public void removeEntry(TelepadEntry entry)
    {

        for (TelepadEntry tpe : getEntries())
            if (tpe.position.equals(entry.position))
                if (tpe.dimensionID == entry.dimensionID)
                {
                    // if(tpe.entryName.equals(entry.entryName)){
                    this.getEntries().remove(tpe);
                    break;
                }
    }

    public boolean isInTeleportGui()
    {

        return isInTeleportGui;
    }

    public void setInTeleportGui(boolean isInTeleportGui)
    {

        this.isInTeleportGui = isInTeleportGui;
    }

    public void overrideEntries(List<TelepadEntry> entries)
    {

        this.entries = entries;
    }

    public int getCounter()
    {

        return counter;
    }

    public void countDown()
    {

        counter--;
    }

    public void setCounter(int counter)
    {

        this.counter = counter;
    }

    public static int getMaxTime()
    {

        return MAX_TIME;
    }

    public void removeWhiteListEntryClient(String name)
    {

        System.out.println(name);
        if (whitelist.containsKey(name))
            whitelist.remove(name);
    }

    public void removeWhiteListEntryServer(String name)
    {

        System.out.println(whitelist);
        System.out.println(name);
        System.out.println(whitelist.containsKey(name));
        if (whitelist.containsKey(name))
        {
            System.out.println(name);
            whitelist.remove(name);
            NetworkHandler.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player),
                    new CPacketEditWhiteListEntry(name, UUID.randomUUID(), false));
        }
    }

    public void addWhiteListEntryServer(ServerPlayerEntity player)
    {

        if (!isWhiteListFull())
        {
            GameProfile profile = player.getGameProfile();
            if (!whitelist.containsKey(profile.getName()))
            {
                whitelist.put(player.getGameProfile().getName(), player.getGameProfile().getId());
                NetworkHandler.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)this.player),
                        new CPacketEditWhiteListEntry(profile.getName(), profile.getId(), true));
            }
        }
    }

    public void addWhiteListEntryClient(String name, UUID id)
    {

        if (!isWhiteListFull())
            if (!whitelist.containsKey(name))
                whitelist.put(name, id);
    }

    // screen id to update data in current gui screen
    public void commandWhitelist(String command)
    {

        if (player.level.isClientSide)
        {
            Telepads.log.error("Tried handling commands on the client side.");
            return;
        }

        MinecraftServer serverWorld = player.level.getServer();

        // player names are minimum 3 characters
        if (!command.isEmpty() && command.length() > 2)
        {
            //command = command.toLowerCase();
            // didnt add clear command because I was to lazy to add another client packet to
            // sync up the data
//            if (command.equals("clear"))
//            {
//                this.whitelist.clear();
//
//                return;
//            }

            String[] command_split = command.split(" ");

            if (command_split.length == 2)
            {
                String playername = command_split[1];

                switch (command_split[0])
                {
                case "add":
                    ServerPlayerEntity player = serverWorld.getPlayerList().getPlayerByName(playername);
                    if (player == null)
                        return;
                    addWhiteListEntryServer(player);
                    break;
                case "remove":
                    System.out.println(playername);
                    removeWhiteListEntryServer(playername);
                    break;
                }
            }
        }
    }

    public LinkedHashMap<String, UUID> getWhitelist()
    {

        return whitelist;
    }

    public boolean isWhiteListFull()
    {

        return whitelist.size() >= 9;
    }

    public void setRequestTeleportScreen(boolean flag)
    {

        this.request_teleport = flag;
    }

    public boolean getRequestTeleportScreen()
    {

        return request_teleport;
    }
}
