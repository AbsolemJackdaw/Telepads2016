package subaraki.telepads.handler.client;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import subaraki.telepads.handler.proxy.ClientProxy;
import subaraki.telepads.network.NetworkHandler;
import subaraki.telepads.network.PacketOpenWhiteList;

public class KeyHandler {

	public KeyHandler() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void keys(KeyInputEvent evt) {
		if(ClientProxy.keyWhiteList.isPressed())
			NetworkHandler.NETWORK.sendToServer(new PacketOpenWhiteList());
	}
}
