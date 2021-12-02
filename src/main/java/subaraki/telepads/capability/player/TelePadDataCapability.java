package subaraki.telepads.capability.player;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import java.util.concurrent.Callable;

public class TelePadDataCapability {

    /*
     * This field will contain the forge-allocated Capability class. This instance
     * will be initialized internally by Forge, upon calling register.
     */
    public static Capability<TelepadData> CAPABILITY = CapabilityManager.get(new CapabilityToken<TelepadData>() {
    });

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
