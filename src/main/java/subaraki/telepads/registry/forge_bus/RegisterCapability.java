package subaraki.telepads.registry.forge_bus;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import subaraki.telepads.capability.player.TelepadCapProvider;
import subaraki.telepads.mod.Telepads;

@EventBusSubscriber(bus = Bus.FORGE, modid = Telepads.MODID)
public class RegisterCapability {

    @SubscribeEvent
    public static void onAttachEventEntity(AttachCapabilitiesEvent<Entity> event) {

        if (event.getObject() instanceof Player player)
            event.addCapability(TelepadCapProvider.KEY, new TelepadCapProvider(player));
    }
}
