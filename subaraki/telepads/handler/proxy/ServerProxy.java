package subaraki.telepads.handler.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.GameRegistry;
import subaraki.telepads.tileentity.TileEntityTelepad;

public class ServerProxy {

	public void registerRenders(){};
	
	public EntityPlayer getClientPlayer(){return null;};
	
	public void registerTileEntityAndRender(){
		GameRegistry.registerTileEntity(TileEntityTelepad.class, "TileEntityTelepad");
	}
}
