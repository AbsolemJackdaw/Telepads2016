package subaraki.telepads.registry.mod_bus;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.tileentity.TileEntityTelepad;

@EventBusSubscriber(modid = Telepads.MODID, bus = Bus.MOD)
public class RegisterTileEntity {

    @SubscribeEvent
    public static void registerTE(RegistryEvent.Register<TileEntityType<?>> evt)
    {

        TileEntityType<?> type = TileEntityType.Builder.of(TileEntityTelepad::new, Telepads.ObjectHolders.TELEPAD_BLOCK).build(null);
        type.setRegistryName(Telepads.MODID, "telepadtileentity");
        evt.getRegistry().register(type);
    }
}
