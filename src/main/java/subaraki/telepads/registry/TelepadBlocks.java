package subaraki.telepads.registry;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import subaraki.telepads.block.BlockTelepad;
import subaraki.telepads.mod.Telepads;

public class TelepadBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Telepads.MODID);
    public static final RegistryObject<Block> TELEPAD = BLOCKS.register("telepad", BlockTelepad::new);


}
