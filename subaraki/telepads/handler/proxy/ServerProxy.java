package subaraki.telepads.handler.proxy;

import net.minecraftforge.fml.common.registry.GameRegistry;
import subaraki.telepads.tileentity.TileEntityTelepad;

public class ServerProxy {

	public void registerRenders(){};

	public void registerTileEntityAndRender(){
		GameRegistry.registerTileEntity(TileEntityTelepad.class, "TileEntityTelepad");
	}
}
