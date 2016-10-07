package subaraki.telepads.gui.server;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerTelepad extends Container {

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

}
