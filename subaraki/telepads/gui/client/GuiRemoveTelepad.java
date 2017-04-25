package subaraki.telepads.gui.client;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import subaraki.telepads.capability.TelePadDataCapability;
import subaraki.telepads.network.NetworkHandler;
import subaraki.telepads.network.PacketRemoveTelepadEntry;
import subaraki.telepads.network.PacketTeleport;
import subaraki.telepads.utility.TelepadEntry;

public class GuiRemoveTelepad extends GuiScreen {

	private EntityPlayer player;
	private TelepadEntry entryToRemove;

	public GuiRemoveTelepad(EntityPlayer player) {
		super();
		this.player = player;

		entryToRemove = null;
		Integer dim = null;
		BlockPos pos = null;

		for(TelepadEntry entry : player.getCapability(TelePadDataCapability.CAPABILITY, null).getEntries())
			if(entry.entryName.equals("QUEUEDFORREMOVAL")){
				dim = entry.dimensionID;
				pos = entry.position;
			}

		if(pos != null && dim != null)
			for(TelepadEntry entry : player.getCapability(TelePadDataCapability.CAPABILITY, null).getEntries())
				if(!entry.entryName.equals("QUEUEDFORREMOVAL"))
					if(dim == entry.dimensionID && pos.equals(entry.position)){
						entryToRemove = entry;
						break;
					}
	}

	@Override
	public void actionPerformed (GuiButton button) {

		if (button.id == 0 && entryToRemove != null) {
			NetworkHandler.NETWORK.sendToServer(new PacketRemoveTelepadEntry(player.getPersistentID(), entryToRemove));
			this.mc.player.closeScreen();
		}

		if (button.id == 1 && entryToRemove != null) {
			NetworkHandler.NETWORK.sendToServer(new PacketTeleport(player.getPosition(), entryToRemove, true));
			this.mc.player.closeScreen();
		}
	}

	@Override
	public boolean doesGuiPauseGame () {

		return false;
	}

	@Override
	public void drawScreen (int par1, int par2, float par3) {

		super.drawScreen(par1, par2, par3);

		int posX = (this.width) / 2;
		int posY = (this.height) / 2;

		String s = I18n.format("cannot.find.remove");

		int lenght = this.fontRendererObj.getStringWidth(s);

		fontRendererObj.drawStringWithShadow(s, posX - lenght/2, posY - 30, 0xffffff);
	}

	@Override
	public void initGui () {

		super.initGui();

		int posX = (this.width) / 2;
		int posY = (this.height) / 2;

		this.buttonList.clear();

		this.buttonList.add(new GuiButton(0, posX - 50, posY - 12, 100, 20, ChatFormatting.RED + I18n.format("button.forget")));
		this.buttonList.add(new GuiButton(1, posX - 50, posY + 12, 100, 20, I18n.format("button.teleport")));
	}
}
