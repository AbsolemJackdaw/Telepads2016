package subaraki.telepads.handler.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.registry.GameRegistry;
import subaraki.telepads.tileentity.TileEntityTelepad;

public class ServerProxy {

	public EntityPlayer getClientPlayer(){return null;};
	
	public void registerTileEntityAndRender(){
		GameRegistry.registerTileEntity(TileEntityTelepad.class, "TileEntityTelepad");
	}
}
