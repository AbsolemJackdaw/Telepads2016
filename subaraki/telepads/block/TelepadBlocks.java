package subaraki.telepads.block;

import static lib.block.BlockRegistry.*;

import lib.item.ItemRegistry;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;
import subaraki.telepads.mod.Telepads;

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
