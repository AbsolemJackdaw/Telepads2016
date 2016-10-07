package subaraki.telepads.hooks;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import subaraki.telepads.capability.CapabilityTelepadProvider;

public class AttachCapability {

	public AttachCapability() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void onAttachEvent(AttachCapabilitiesEvent.Entity event){
		final Entity entity = event.getEntity();

		if (entity instanceof EntityPlayer)
			event.addCapability(CapabilityTelepadProvider.KEY, new CapabilityTelepadProvider((EntityPlayer)entity));
	}
}
