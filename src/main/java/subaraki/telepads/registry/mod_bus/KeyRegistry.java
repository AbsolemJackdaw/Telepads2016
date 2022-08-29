package subaraki.telepads.registry.mod_bus;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.lwjgl.glfw.GLFW;
import subaraki.telepads.mod.Telepads;

@EventBusSubscriber(bus = Bus.MOD, modid = Telepads.MODID, value = Dist.CLIENT)
public class KeyRegistry {

    public static KeyMapping keyWhiteList;

    @SubscribeEvent
    public static void keys(RegisterKeyMappingsEvent evt) {
        keyWhiteList = new KeyMapping("Friend Whitelist", GLFW.GLFW_KEY_PERIOD, "Telepad Friend List");
        evt.register(keyWhiteList);
    }
}
