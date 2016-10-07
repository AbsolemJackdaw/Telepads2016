package subaraki.telepads.utility;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class TelepadEntry {
    
    /**
     * The user defined name for the TelePad entry.
     */
    public String entryName;
    
    /**
     * The dimension that the TelePad entry is located in.
     */
    public int dimensionID;
    
    /**
     * The coordinates of the TelePad entry.
     */
    public BlockPos position;
    
    /***/
    public boolean isPowered;
    public boolean hasTransmitter;
    
    /**
     * Creates a TelepadEntry from a ByteBuf. This is useful for reading from networking.
     * 
     * @param buf : A ByteBuf containing the data needed to create a TelepadEntry.
     */
    public TelepadEntry(ByteBuf buf) {
        
        this(ByteBufUtils.readUTF8String(buf), buf.readInt(), new BlockPos(buf.readInt(),buf.readInt(),buf.readInt()), buf.readBoolean(), buf.readBoolean());
    }
    
    /**
     * Creates a TelepadEntry from a NBTTagCompound. This is used for reading from
     * NBTTagCompound.
     * 
     * @param tag : An NBTTagCompound to read the required data from.
     */
    public TelepadEntry(NBTTagCompound tag) {
        
        this(tag.getString("entryName"), tag.getInteger("dimensionID"), new BlockPos(tag.getInteger("x"),tag.getInteger("y"),tag.getInteger("z")), tag.getBoolean("power"), tag.getBoolean("transmitter"));
    }
    
    /**
     * Creates a new TelepadEntry. This is used to represent an entry that a player can
     * teleport to.
     * 
     * @param name : A display name to use for the entry.
     * @param dimension : The id of the dimension that this entry is within.
     * @param pos : The BlockPos of this TelepadEntry.
     * @param isPowered defaults to false. wether this entry's tile entity is redstone
     *            powered or not
     * @param hasTransmitter defaults to false. wether this entry's tile entity has a
     *            transmitter upgrade
     */
    public TelepadEntry(String name, int dimension, BlockPos pos, boolean isPowered, boolean hasTransmitter) {
        
        this.entryName = name;
        this.dimensionID = dimension;
        this.position = pos;
        this.isPowered = isPowered;
        this.hasTransmitter = hasTransmitter;
    }
    
    /**
     * Writes the TelepadEntry to a NBTTagCompound.
     * 
     * @param tag : The tag to write the TelepadEntry to.
     * @return NBTTagCompound: An NBTTagCompound containing all of the TelepadEntry data.
     */
    public NBTTagCompound writeToNBT (NBTTagCompound tag) {
        
        tag.setString("entryName", this.entryName);
        tag.setInteger("dimensionID", this.dimensionID);
        tag.setBoolean("power", isPowered);
        tag.setBoolean("transmitter", hasTransmitter);

        tag.setInteger("x", position.getX());
        tag.setInteger("y", position.getY());
        tag.setInteger("z", position.getZ());

        return tag;
    }
    
    /**
     * Write the TelepadEntry to a ByteBuf.
     * 
     * @param buf : The ByteBuf to write the TelepadEntry to.
     */
    public void writeToByteBuf (ByteBuf buf) {
        
        ByteBufUtils.writeUTF8String(buf, this.entryName);
        buf.writeInt(this.dimensionID);
        buf.writeInt(position.getX());
        buf.writeInt(position.getY());
        buf.writeInt(position.getZ());
        buf.writeBoolean(isPowered);
        buf.writeBoolean(hasTransmitter);
    }
    
    @Override
    public String toString () {
        
        return "Entry Name: " + this.entryName + " DimensionID: " + this.dimensionID + " " + this.position.toString();
    }
    
    @Override
    public Object clone () {
        
        return new TelepadEntry(this.entryName, this.dimensionID, this.position, this.isPowered, this.hasTransmitter);
    }
    
    @Override
    public boolean equals (Object compared) {
        
        if (!(compared instanceof TelepadEntry))
            return false;
            
        TelepadEntry entry = (TelepadEntry) compared;
        return this.entryName.equals(entry.entryName) && this.dimensionID == entry.dimensionID && this.position.equals(entry.position);
    }
    
    public void setPowered (boolean flag) {
        
        isPowered = flag;
    }
    
    public void setTransmitter (boolean flag) {
        
        hasTransmitter = flag;
    }
}
