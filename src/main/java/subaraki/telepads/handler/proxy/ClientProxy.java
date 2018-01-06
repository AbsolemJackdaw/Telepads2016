package subaraki.telepads.handler.proxy;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import subaraki.telepads.tileentity.TileEntityTelepad;
import subaraki.telepads.tileentity.render.TileEntityTelepadSpecialRenderer;

public class ClientProxy extends ServerProxy {

	@Override
	public EntityPlayer getClientPlayer(){
		return Minecraft.getMinecraft().player;
	}

	@Override
	public void registerTileEntityAndRender() {
		GameRegistry.registerTileEntity(TileEntityTelepad.class, "TileEntityTelepad");
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTelepad.class, new TileEntityTelepadSpecialRenderer());
	}

	private Random rand = new Random();

}
