package subaraki.telepads.handler.proxy;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import subaraki.telepads.block.TelepadBlocks;
import subaraki.telepads.item.TelepadItems;
import subaraki.telepads.tileentity.TileEntityTelepad;
import subaraki.telepads.tileentity.render.TileEntityTelepadSpecialRenderer;

public class ClientProxy extends ServerProxy {

	@Override
	public void registerRenders() {
		TelepadItems.registerRenders();
		TelepadBlocks.registerRenders();
	}
	
	@Override
	public void registerTileEntityAndRender() {
		GameRegistry.registerTileEntity(TileEntityTelepad.class, "TileEntityTelepad");
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTelepad.class, new TileEntityTelepadSpecialRenderer());
	}
}
