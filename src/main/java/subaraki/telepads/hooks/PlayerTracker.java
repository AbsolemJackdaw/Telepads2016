package subaraki.telepads.hooks;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import subaraki.telepads.capability.TelePadDataCapability;
import subaraki.telepads.capability.TelepadData;
import subaraki.telepads.tileentity.TileEntityTelepad;
import subaraki.telepads.utility.TelepadEntry;

public class PlayerTracker {

	public PlayerTracker() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void updateEntity(LivingUpdateEvent event){
		if(!(event.getEntityLiving() instanceof EntityPlayer))
			return;

		TelepadData td = event.getEntityLiving().getCapability(TelePadDataCapability.CAPABILITY, null);
		TileEntity te = event.getEntityLiving().world.getTileEntity(event.getEntityLiving().getPosition());
		if(te == null || !(te instanceof TileEntityTelepad)){
			if(td.getCounter() != td.getMaxTime())
				td.setCounter(td.getMaxTime());
			if(td.isInTeleportGui())
				td.setInTeleportGui(false);
		}
	}

	@SubscribeEvent
	public void onPlayerClone (PlayerEvent.Clone event) {

		List<TelepadEntry> list = event.getOriginal().getCapability(TelePadDataCapability.CAPABILITY, null).getEntries();
		event.getEntityPlayer().getCapability(TelePadDataCapability.CAPABILITY, null).overrideEntries(list);

	}

	@SubscribeEvent
	public void onEntityJoinWorld (PlayerLoggedInEvent event) {
		
		if (event.player instanceof EntityPlayer && !event.player.world.isRemote){
			TelepadData data = event.player.getCapability(TelePadDataCapability.CAPABILITY, null);
			if(data != null)
				data.sync();
		}
	}
	
	@SubscribeEvent
	public void onDimensionChange(PlayerChangedDimensionEvent event){
		if (event.player instanceof EntityPlayer && !event.player.world.isRemote){
			TelepadData data = event.player.getCapability(TelePadDataCapability.CAPABILITY, null);
			if(data != null)
				data.sync();
		}
	}

}
