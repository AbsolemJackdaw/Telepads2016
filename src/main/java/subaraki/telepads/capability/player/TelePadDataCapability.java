package subaraki.telepads.capability.player;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import java.util.concurrent.Callable;

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
                TelepadData.class);

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
