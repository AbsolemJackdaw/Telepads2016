package subaraki.telepads.gui.server;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import subaraki.telepads.tileentity.TileEntityTelepad;

public class ContainerTelepad extends Container {

	private TileEntityTelepad pad;
	public ContainerTelepad(TileEntityTelepad pad) {
		this.pad = pad;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return pad.isUsableByPlayer(playerIn);
	}
}
