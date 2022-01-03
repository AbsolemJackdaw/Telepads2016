package subaraki.telepads.capability.player;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import subaraki.telepads.mod.Telepads;

public class TelepadCapProvider implements ICapabilitySerializable<CompoundTag> {
    /**
     * Unique key to identify the attached provider from others
     */
    public static final ResourceLocation KEY = new ResourceLocation(Telepads.MODID, "telepad_data");
    public static Capability<TelepadData> CAPABILITY = CapabilityManager.get(new CapabilityToken<TelepadData>() {
    });
    /**
     * The instance that we are providing
     */
    final TelepadData data = new TelepadData();

    /**
     * gets called before world is initiated. player.worldObj will return null here
     * !
     */
    public TelepadCapProvider(Player player) {

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

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return getCapability(cap);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if (cap == CAPABILITY)
            return (LazyOptional<T>) LazyOptional.of(() -> data);
        return LazyOptional.empty();
    }
}
