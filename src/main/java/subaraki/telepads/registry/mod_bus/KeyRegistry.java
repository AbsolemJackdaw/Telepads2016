package subaraki.telepads.registry.mod_bus;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.screen.WhiteListScreen;

@EventBusSubscriber(bus = Bus.FORGE, modid = Telepads.MODID, value = Dist.CLIENT)
public class KeyRegistry {

    public static KeyBinding keyWhiteList;

    public static void registerKey()
    {

        keyWhiteList = new KeyBinding("Friend Whitelist", GLFW.GLFW_KEY_PERIOD, "Telepad Friend List");
        ClientRegistry.registerKeyBinding(keyWhiteList);
    }

    @SubscribeEvent
    public static void keys(KeyInputEvent evt)
    {

        if (keyWhiteList.consumeClick())
        {
            Minecraft.getInstance().setScreen(new WhiteListScreen());
        }
    }
}
