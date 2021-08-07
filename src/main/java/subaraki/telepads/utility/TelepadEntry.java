package subaraki.telepads.utility;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.UUID;

public class TelepadEntry {

    /**
     * The user defined name for the TelePad entry.
     */
    public String entryName;

    /**
     * The dimension that the TelePad entry is located in.
     */
    public ResourceKey<Level> dimensionID;

    /**
     * The coordinates of the TelePad entry.
     */
    public BlockPos position;

    /***/
    public boolean isPowered;
    public boolean hasTransmitter;
    public boolean isPublic;
    public boolean isMissingFromLocation = false;

    private final ArrayList<UUID> users = Lists.newArrayList();

    /**
     * Creates a TelepadEntry from a PacketBuffer. This is useful for reading from
     * networking.
     *
     * @param buf : A ByteBuf containing the data needed to create a TelepadEntry.
     */
    public TelepadEntry(FriendlyByteBuf buf) {

        this(buf.readUtf(256), ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(buf.readUtf())), new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()));
        addDetails(buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean());

        int size = buf.readInt();
        if (size > 0)
            for (int i = 0; i < size; i++)
                addUser(buf.readUUID());

    }

    /**
     * Creates a TelepadEntry from a CompoundNBT. This is used for reading from
     * CompoundNBT.
     *
     * @param tag : An NBTTagCompound to read the required data from.
     */
    public TelepadEntry(CompoundTag tag) {


        this(tag.getString("entryName"), ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(tag.getString("dimensionID"))), new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z")));
        addDetails(tag.getBoolean("power"), tag.getBoolean("transmitter"), tag.getBoolean("public"), tag.getBoolean("missing"));

        int size = tag.getInt("size");
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                users.add(UUID.fromString(tag.getString("id_" + i)));
            }
        }
    }

    /**
     * Creates a new TelepadEntry. This is used to represent an entry that a player
     * can teleport to.
     *
     * @param name      : A display name to use for the entry.
     * @param dimension : The id of the dimension that this entry is within.
     * @param pos       : The BlockPos of this TelepadEntry.
     */
    public TelepadEntry(String name, ResourceKey<Level> dimension, BlockPos pos) {

        this.entryName = name;
        this.dimensionID = dimension;
        this.position = pos;
    }

    // mainly used for reconstructing telepads
    private TelepadEntry addDetails(boolean isPowered, boolean hasTransmitter, boolean isPublic, boolean isMissing) {

        this.isPowered = isPowered;
        this.hasTransmitter = hasTransmitter;
        this.isPublic = isPublic;
        this.isMissingFromLocation = isMissing;
        return this;
    }

    /**
     * Writes the TelepadEntry to a CompoundNBT.
     *
     * @param tag : The tag to write the TelepadEntry to.
     * @return CompoundNBT: An CompoundNBT containing all of the TelepadEntry data.
     */
    public CompoundTag writeToNBT(CompoundTag tag) {

        tag.putString("entryName", this.entryName);
        tag.putString("dimensionID", this.dimensionID.location().toString());
        tag.putBoolean("power", isPowered);
        tag.putBoolean("transmitter", hasTransmitter);
        tag.putBoolean("public", isPublic);

        tag.putInt("x", position.getX());
        tag.putInt("y", position.getY());
        tag.putInt("z", position.getZ());

        tag.putBoolean("missing", isMissingFromLocation);

        tag.putInt("size", users.size());
        if (users.size() > 0) {
            for (int i = 0; i < users.size(); i++)
                tag.putString("id_" + i, users.get(i).toString());
        }
        return tag;
    }

    /**
     * Write the TelepadEntry to a ByteBuf.
     *
     * @param buf : The ByteBuf to write the TelepadEntry to.
     */
    public void writeToBuffer(FriendlyByteBuf buf) {

        buf.writeUtf(this.entryName);
        buf.writeUtf(this.dimensionID.location().toString());
        buf.writeInt(position.getX());
        buf.writeInt(position.getY());
        buf.writeInt(position.getZ());
        buf.writeBoolean(isPowered);
        buf.writeBoolean(hasTransmitter);
        buf.writeBoolean(isPublic);
        buf.writeBoolean(isMissingFromLocation);

        buf.writeInt(users.size());
        if (!users.isEmpty())
            users.stream().forEach(entry -> buf.writeUUID(entry));
    }

    @Override
    public String toString() {

        return "Entry Name: " + this.entryName + " DimensionID: " + this.dimensionID + " " + this.position.toString() + " Public Pad:" + this.isPublic
                + " transmitter: " + this.hasTransmitter + " is powered: " + this.isPowered + " is missing: " + this.isMissingFromLocation + " Users : " + this.users;
    }

    @Override
    public boolean equals(Object compared) {

        if (!(compared instanceof TelepadEntry))
            return false;

        TelepadEntry entry = (TelepadEntry) compared;
        return this.entryName.equals(entry.entryName) && this.dimensionID == entry.dimensionID && this.position.equals(entry.position);
    }

    public void setPowered(boolean flag) {

        isPowered = flag;
    }

    public void setTransmitter(boolean flag) {

        hasTransmitter = flag;
    }

    public void setPublic(boolean isPublic) {

        this.isPublic = isPublic;
    }

    public boolean canUse(UUID player_id) {

        return isPublic || users.contains(player_id);
    }

    public TelepadEntry addUser(UUID player_id) {

        users.add(player_id);
        return this;
    }

    public void removeUser(UUID player_id) {

        users.remove(player_id);
    }

}
