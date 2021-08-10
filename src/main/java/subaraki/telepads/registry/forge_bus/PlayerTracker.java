package subaraki.telepads.registry.forge_bus;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import subaraki.telepads.capability.player.TelepadData;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.tileentity.TileEntityTelepad;

@EventBusSubscriber(bus = Bus.FORGE, modid = Telepads.MODID)
public class PlayerTracker {

    @SubscribeEvent
    public static void updateEntity(LivingUpdateEvent event) {

        if (event.getEntityLiving() instanceof Player player)

            TelepadData.get(player).ifPresent(data -> {

                BlockEntity te = event.getEntityLiving().level.getBlockEntity(event.getEntityLiving().blockPosition());
                if (!(te instanceof TileEntityTelepad)) {
                    if (data.getCounter() != TelepadData.getMaxTime())
                        data.setCounter(TelepadData.getMaxTime());
                    if (data.isInTeleportGui())
                        data.setInTeleportGui(false);
                }
            });

    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {

        TelepadData.get(event.getOriginal()).ifPresent(data -> {
            TelepadData.get(event.getPlayer()).ifPresent(newdata -> {
                newdata.overrideEntries(data.getEntries());
            });
        });

    }
}
