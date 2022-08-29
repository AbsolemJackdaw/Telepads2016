package subaraki.telepads.registry.forge_bus;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.PacketDistributor;
import subaraki.telepads.capability.player.TelepadData;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.network.NetworkHandler;
import subaraki.telepads.network.client.CPacketEditWhiteListEntry;
import subaraki.telepads.tileentity.TileEntityTelepad;

@EventBusSubscriber(bus = Bus.FORGE, modid = Telepads.MODID)
public class PlayerTracker {

    @SubscribeEvent
    public static void updateEntity(LivingEvent.LivingTickEvent event) {

        if (event.getEntity() instanceof Player player)

            TelepadData.get(player).ifPresent(data -> {

                if (!(event.getEntity().level.getBlockEntity(event.getEntity().blockPosition()) instanceof TileEntityTelepad te)) {
                    if (data.getCounter() != TelepadData.getMaxTime())
                        data.setCounter(TelepadData.getMaxTime());
                    if (data.isInTeleportGui())
                        data.setInTeleportGui(false);
                }
            });

    }

    @SubscribeEvent
    public static void login(PlayerEvent.PlayerLoggedInEvent event) {
        syncFriendList(event);
    }

    @SubscribeEvent
    public static void dimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        syncFriendList(event);
    }

    private static void syncFriendList(PlayerEvent event) {
        if (event.getEntity() instanceof ServerPlayer player)
            TelepadData.get(player).ifPresent(data -> {
                data.getWhitelist().forEach((name, uuid) -> {
                    NetworkHandler.NETWORK.send(PacketDistributor.PLAYER.with(() -> player), new CPacketEditWhiteListEntry(name, uuid, true));
                });
            });
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {

        TelepadData.get(event.getOriginal()).ifPresent(data -> {
            TelepadData.get(event.getEntity()).ifPresent(newdata -> {
                newdata.overrideEntries(data.getEntries());
            });
        });

    }
}
