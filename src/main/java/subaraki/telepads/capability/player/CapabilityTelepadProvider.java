package subaraki.telepads.capability.player;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import subaraki.telepads.mod.Telepads;

public class CapabilityTelepadProvider implements ICapabilitySerializable<CompoundTag> {

    /**
     * Unique key to identify the attached provider from others
     */
    public static final ResourceLocation KEY = new ResourceLocation(Telepads.MODID, "telepad_data");

    /**
     * The instance that we are providing
     */
    final TelepadData data = new TelepadData();

    /**
     * gets called before world is initiated. player.worldObj will return null here
     * !
     */
    public CapabilityTelepadProvider(Player player) {

        data.setPlayer(player);
    }

    @Override
    public CompoundTag serializeNBT() {

        return (CompoundTag) data.writeData();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

        data.readData(nbt);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {

        if (cap == TelePadDataCapability.CAPABILITY)
            return (LazyOptional<T>) LazyOptional.of(this::getImpl);

        return LazyOptional.empty();
    }
    
    private TelepadData getImpl() {

        if (data != null) {
            return data;
        }
        return new TelepadData();
    }

}
