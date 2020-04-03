package subaraki.telepads.capability.player;

import java.util.concurrent.Callable;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class TelePadDataCapability {

    /*
     * This field will contain the forge-allocated Capability class. This instance
     * will be initialized internally by Forge, upon calling register.
     */
    @CapabilityInject(TelepadData.class)
    public static Capability<TelepadData> CAPABILITY;

    /*
     * This registers our capability to the manager
     */
    public void register() {

        CapabilityManager.INSTANCE.register(

                // This is the class the capability works with
                TelepadData.class,

                // This is a helper for users to save and load
                new StorageHelper(),

                // This is a factory for default instances
                new DefaultInstanceFactory());
    }

    /*
     * This class handles saving and loading the data.
     */
    public static class StorageHelper implements Capability.IStorage<TelepadData> {

        @Override
        public INBT writeNBT(Capability<TelepadData> capability, TelepadData instance, Direction side) {

            return instance.writeData();
        }

        @Override
        public void readNBT(Capability<TelepadData> capability, TelepadData instance, Direction side, INBT nbt) {

            instance.readData(nbt);
        }
    }

    /*
     * This class handles constructing new instances for this capability
     */
    public static class DefaultInstanceFactory implements Callable<TelepadData> {

        @Override
        public TelepadData call() throws Exception {

            return new TelepadData();
        }
    }
}
