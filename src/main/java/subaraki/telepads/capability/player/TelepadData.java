package subaraki.telepads.capability.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;
import subaraki.telepads.handler.ConfigData;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.network.NetworkHandler;
import subaraki.telepads.network.client.CPacketEditWhiteListEntry;
import subaraki.telepads.utility.TelepadEntry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class TelepadData {

    private static final int MAX_TIME = ConfigData.teleport_seconds * 20;
    /**
     * A list of uuid's a player can whitelist, to share coordinates of a placed
     * pad.
     */
    // private List<UUID> whitelist = Lists.newArrayList();
    private final LinkedHashMap<String, UUID> whitelist = new LinkedHashMap<>();
    public int counter = MAX_TIME;
    private Player player;
    /**
     * A list of entries that this player has acces to.
     */
    // initialize to prevent null crashes caused by external sources
    private List<TelepadEntry> entries = new ArrayList<TelepadEntry>();
    private boolean isInTeleportGui;
    private boolean request_teleport = false;

    public static LazyOptional<TelepadData> get(Player player) {

        return player.getCapability(TelePadDataCapability.CAPABILITY, null);
    }

    public static int getMaxTime() {

        return MAX_TIME;
    }

    public void setPlayer(Player newPlayer) {

        this.player = newPlayer;
    }

    public Tag writeData() {

        CompoundTag tag = new CompoundTag();

        ListTag entryList = new ListTag();
        if (entries != null && !entries.isEmpty())
            for (TelepadEntry entry : this.entries)
                if (entry != null)
                    entryList.add(entry.writeToNBT(new CompoundTag()));

        tag.put("entries", entryList);

        ListTag friends = new ListTag();

        whitelist.keySet().forEach(name -> {
            CompoundTag nbt = new CompoundTag();
            nbt.putUUID(name, whitelist.get(name));
            friends.add(nbt);
        });
        tag.put("list", friends);

        return tag;
    }

    public void readData(Tag nbt) {

        CompoundTag tag = (CompoundTag) nbt;

        List<TelepadEntry> entryList = new ArrayList<TelepadEntry>();
        ListTag entryTagList = tag.getList("entries", 10);

        for (int tagPos = 0; tagPos < entryTagList.size(); tagPos++)
            entryList.add(new TelepadEntry(entryTagList.getCompound(tagPos)));

        this.entries = entryList;

        ListTag friendList = tag.getList("list", 10);
        friendList.forEach(entry -> ((CompoundTag) entry).getAllKeys().forEach(key -> whitelist.put(key, ((CompoundTag) entry).getUUID(key))));

    }

    public List<TelepadEntry> getEntries() {

        if (this.entries == null)
            this.entries = new ArrayList<TelepadEntry>();

        return this.entries;
    }

    public void addEntry(TelepadEntry entry) {

        if (!this.getEntries().contains(entry))
            this.getEntries().add(entry);
    }

    public void removeEntry(TelepadEntry entry) {

        for (TelepadEntry tpe : getEntries())
            if (tpe.position.equals(entry.position))
                if (tpe.dimensionID == entry.dimensionID) {
                    // if(tpe.entryName.equals(entry.entryName)){
                    this.getEntries().remove(tpe);
                    break;
                }
    }

    public boolean isInTeleportGui() {

        return isInTeleportGui;
    }

    public void setInTeleportGui(boolean isInTeleportGui) {

        this.isInTeleportGui = isInTeleportGui;
    }

    public void overrideEntries(List<TelepadEntry> entries) {

        this.entries = entries;
    }

    public int getCounter() {

        return counter;
    }

    public void setCounter(int counter) {

        this.counter = counter;
    }

    public void countDown() {

        counter--;
    }

    public void removeWhiteListEntryClient(String name) {

        whitelist.remove(name);
    }

    public void removeWhiteListEntryServer(String name) {

        if (whitelist.containsKey(name)) {
            whitelist.remove(name);
            NetworkHandler.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                    new CPacketEditWhiteListEntry(name, UUID.randomUUID(), false));
        }
    }

    public void addWhiteListEntryServer(ServerPlayer player) {

        if (isWhiteListNotFull()) {
            GameProfile profile = player.getGameProfile();
            if (!whitelist.containsKey(profile.getName())) {
                whitelist.put(player.getGameProfile().getName(), player.getGameProfile().getId());
                NetworkHandler.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) this.player),
                        new CPacketEditWhiteListEntry(profile.getName(), profile.getId(), true));
            }
        }
    }

    public void addWhiteListEntryClient(String name, UUID id) {

        if (isWhiteListNotFull())
            if (!whitelist.containsKey(name))
                whitelist.put(name, id);
    }

    // screen id to update data in current gui screen
    public void commandWhitelist(String command) {

        if (player.level.isClientSide) {
            Telepads.log.error("Tried handling commands on the client side.");
            return;
        }

        MinecraftServer serverWorld = player.level.getServer();

        // player names are minimum 3 characters
        if (command.length() > 2) {
            //TODO
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

            if (command_split.length == 2) {
                String playername = command_split[1];

                switch (command_split[0]) {
                    case "add" -> {
                        ServerPlayer player = serverWorld.getPlayerList().getPlayerByName(playername);
                        if (player != null)
                            addWhiteListEntryServer(player);
                    }
                    case "remove" -> {
                        removeWhiteListEntryServer(playername);
                    }
                }
            }
        }
    }

    public LinkedHashMap<String, UUID> getWhitelist() {

        return whitelist;
    }

    public boolean isWhiteListNotFull() {

        return whitelist.size() < 9;
    }

    public boolean getRequestTeleportScreen() {

        return request_teleport;
    }

    public void setRequestTeleportScreen(boolean flag) {

        this.request_teleport = flag;
    }
}
