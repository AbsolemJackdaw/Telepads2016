package subaraki.telepads.block;

import static lib.block.BlockRegistry.registerBlock;

import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TelepadBlocks {

	public TelepadBlocks() {
		MinecraftForge.EVENT_BUS.register(this);
		loadBlocks();
	}

	@SubscribeEvent
	public void register(RegistryEvent.Register<Block> event)
	{
		event.getRegistry().register(blockTelepad);
	}
	
	public Block blockTelepad;
	
	public void loadBlocks(){
		blockTelepad = new BlockTelepad();
	}
	
	public static void registerRenders(){
	}
}
