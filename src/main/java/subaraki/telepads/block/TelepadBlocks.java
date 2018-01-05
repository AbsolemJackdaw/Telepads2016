package subaraki.telepads.block;

import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TelepadBlocks {

	public TelepadBlocks() {
		loadBlocks();
		MinecraftForge.EVENT_BUS.register(this);
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
}
