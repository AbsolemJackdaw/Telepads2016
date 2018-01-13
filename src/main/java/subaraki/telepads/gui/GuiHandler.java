package subaraki.telepads.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import subaraki.telepads.gui.client.GuiNameTelepad;
import subaraki.telepads.gui.client.GuiRemoveTelepad;
import subaraki.telepads.gui.client.GuiTeleport;
import subaraki.telepads.gui.client.GuiWhitelist;
import subaraki.telepads.gui.server.ContainerTelepad;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.tileentity.TileEntityTelepad;

public class GuiHandler implements IGuiHandler {

	public static final int NAME_TELEPAD = 1;
	public static final int REMOVE_TELEPAD = 2;
	public static final int WHITELIST= 3;
	public static final int TELEPORT = 0;

	public GuiHandler() {
		NetworkRegistry.INSTANCE.registerGuiHandler(Telepads.instance, this);
	}

	@Override
	public Object getServerGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z) {
		//PacketTeleport sends a packet with xyz 0 for opening the remove gui
		if(x == 0 && y == 0 && z == 0)
		{
			return new ContainerTelepad();
		}

		if(ID == WHITELIST)
			return new ContainerTelepad();
		
		TileEntity te = world.getTileEntity(new BlockPos(x, y, z));

		if(te instanceof TileEntityTelepad)
		{
			switch (ID) {
			case TELEPORT:
				return new ContainerTelepad((TileEntityTelepad)te);
			default : 
				return null;
			}
		}
		return null;
	}

	@Override
	public Object getClientGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z) {
		//PacketTeleport sends a packet with xyz 0 for opening the remove gui
		if( x == 0 && y == 0 && z == 0)
		{
			return new GuiRemoveTelepad(player);
		}

		if(ID == WHITELIST)
			return new GuiWhitelist();

		TileEntity te = world.getTileEntity(new BlockPos(x, y, z));

		if(te instanceof TileEntityTelepad)
		{
			switch (ID) {
			case TELEPORT:
				return new GuiTeleport(player, (TileEntityTelepad) te);
			case NAME_TELEPAD:
				return new GuiNameTelepad(player, (TileEntityTelepad) te);
			default : 
				return null;
			}
		}
		return null;
	}
}