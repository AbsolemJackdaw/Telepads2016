package subaraki.telepads.block;

import static lib.block.BlockRegistry.registerBlock;

import net.minecraft.block.Block;

public class TelepadBlocks {

	public static Block blockTelepad;
	public static void loadBlocks(){
		blockTelepad = new BlockTelepad();
		
		register();
	}
	
	private static void register(){
		registerBlock(blockTelepad);
	}
	
	public static void registerRenders(){
	}
}
