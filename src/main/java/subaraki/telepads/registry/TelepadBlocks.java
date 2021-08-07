package subaraki.telepads.registry;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import subaraki.telepads.block.BlockTelepad;
import subaraki.telepads.mod.Telepads;

public class TelepadBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Telepads.MODID);
    public static final RegistryObject<Block> TELEPAD_BLOCK = BLOCKS.register("telepad", BlockTelepad::new);


}
