package subaraki.telepads.registry;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.tileentity.TileEntityTelepad;

public class TelepadBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Telepads.MODID);
    public static final RegistryObject<BlockEntityType<TileEntityTelepad>> TELEPAD = BLOCK_ENTITIES.register("telepadtileentity", () ->
            BlockEntityType.Builder.of(TileEntityTelepad::new, TelepadBlocks.TELEPAD.get()).build(null));
}
