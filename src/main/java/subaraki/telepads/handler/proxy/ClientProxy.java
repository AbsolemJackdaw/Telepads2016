package subaraki.telepads.handler.proxy;

import java.util.Random;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import subaraki.telepads.tileentity.TileEntityTelepad;
import subaraki.telepads.tileentity.render.TileEntityTelepadSpecialRenderer;

public class ClientProxy extends ServerProxy {

	public static KeyBinding keyWhiteList;

	@Override
	public void registerKey() {
		keyWhiteList = new KeyBinding("Friend Whitelist", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_PERIOD, "Telepad Friend List");
		ClientRegistry.registerKeyBinding(keyWhiteList);
	}
	
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
