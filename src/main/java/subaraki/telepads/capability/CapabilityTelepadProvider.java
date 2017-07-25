package subaraki.telepads.capability;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import subaraki.telepads.mod.Telepads;

public class CapabilityTelepadProvider implements ICapabilitySerializable<NBTTagCompound>
{
    /**
     * Unique key to identify the attached provider from others
     */
    public static final ResourceLocation KEY = new ResourceLocation(Telepads.MODID, "telepad_data");

    /**
     * The instance that we are providing
     */
    final TelepadData data = new TelepadData();

    /**gets called before world is initiated. player.worldObj will return null here !*/
    public CapabilityTelepadProvider(EntityPlayer player){
        data.setPlayer(player);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        if (capability == TelePadDataCapability.CAPABILITY)
            return true;
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
        if (capability == TelePadDataCapability.CAPABILITY)
            return (T)data;
        return null;
    }

    @Override
    public NBTTagCompound serializeNBT(){
        return (NBTTagCompound) TelePadDataCapability.CAPABILITY.writeNBT(data, null);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt){
    	TelePadDataCapability.CAPABILITY.readNBT(data, null, nbt);
    }
}
