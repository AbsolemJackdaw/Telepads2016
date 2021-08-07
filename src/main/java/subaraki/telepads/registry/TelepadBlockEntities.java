package subaraki.telepads.registry;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.tileentity.TileEntityTelepad;

public class TelepadBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> TILEENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Telepads.MODID);
    public static final RegistryObject<BlockEntityType<TileEntityTelepad>> TILE_ENTITY_TELEPAD = TILEENTITIES.register("telepadtileentity", () ->
            BlockEntityType.Builder.of(TileEntityTelepad::new, TelepadBlocks.TELEPAD_BLOCK.get()).build(null));
}
