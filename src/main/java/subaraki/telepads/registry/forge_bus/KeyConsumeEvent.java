package subaraki.telepads.registry.forge_bus;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.registry.mod_bus.KeyRegistry;
import subaraki.telepads.screen.WhiteListScreen;

@Mod.EventBusSubscriber(modid = Telepads.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)

public class KeyConsumeEvent {

    @SubscribeEvent
    public static void keyPressed(InputEvent.Key event) {
        if (KeyRegistry.keyWhiteList.consumeClick() && Minecraft.getInstance().player != null) {
            Minecraft.getInstance().setScreen(new WhiteListScreen());
        }
    }
}
