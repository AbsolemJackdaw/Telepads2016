package subaraki.telepads.gui.client;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import subaraki.telepads.capability.TelePadDataCapability;
import subaraki.telepads.network.NetworkHandler;
import subaraki.telepads.network.PacketAddTelepadEntry;
import subaraki.telepads.tileentity.TileEntityTelepad;
import subaraki.telepads.utility.TelepadEntry;

public class GuiNameTelepad extends GuiScreen {

	private GuiTextField padNameField;

	public TileEntityTelepad te;
	public EntityPlayer player;

	public boolean share = false;

	public GuiNameTelepad(EntityPlayer player, TileEntityTelepad te) {
		this.te = te;
		this.player = player;
	}

	@Override
	public void actionPerformed (GuiButton button) {

		if(button.id == 0)
			share = !share;
	}

	@Override
	public boolean doesGuiPauseGame () {
		return false;
	}

	@Override
	public void drawScreen (int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
		try {
			int posX = (this.width) / 2;
			int posY = (this.height) / 2;

			String enter = I18n.format("enter.to.confirm");
			String nameYourPad = I18n.format("name.your.telepad")+ " : " + padNameField.getText();
			String sharing = share ? "Yes" : "No";

			fontRenderer.drawStringWithShadow(enter, posX - fontRenderer.getStringWidth(enter)/2, posY, 0xffffff);
			fontRenderer.drawStringWithShadow(nameYourPad , posX - fontRenderer.getStringWidth(nameYourPad)/2, posY - 20, 0xff0000);

			if(!this.player.getCapability(TelePadDataCapability.CAPABILITY, null).getWhitelist().isEmpty())
				fontRenderer.drawStringWithShadow(sharing , posX - fontRenderer.getStringWidth(sharing)/2 + 25, posY+27, 0xafafaf);

		}
		finally {
			if (padNameField != null)
				padNameField.drawTextBox();
		}
	}

	@Override
	public void initGui () {

		int posX = (this.width) / 2;
		int posY = (this.height) / 2;
		this.buttonList.clear();

		if(!this.player.getCapability(TelePadDataCapability.CAPABILITY, null).getWhitelist().isEmpty())
			this.buttonList.add(new GuiButton(0, posX - (45), posY + 20, 45,20, "Share"));

		padNameField = new GuiTextField(0, fontRenderer, posX - (150 / 2), posY - 50, 150, 20);
		padNameField.setFocused(true);

		String padName = te.getWorld().getBiome(te.getPos()).getBiomeName();

		if(padName.length() > 16)
		{
			String[] names = padName.split(" ");
			String newName = "";

			for(String entry : names)
				newName += entry.substring(0, Math.min(3, entry.length()))+" ";

			padName = newName;
		}

		if (padNameField != null) {
			padNameField.setText(padName);
			padNameField.setMaxStringLength(16);
		}
	}

	@Override
	protected void keyTyped (char c, int i) throws IOException {
		super.keyTyped(c, i);

		if (i == Keyboard.KEY_RETURN || i == Keyboard.KEY_ESCAPE)
			sendPacket(padNameField.getText());

		if (padNameField != null)
			padNameField.textboxKeyTyped(c, i);

	}
	@Override
	protected void mouseClicked (int i, int j, int k) throws IOException {
		super.mouseClicked(i, j, k);
		if (padNameField != null)
			padNameField.mouseClicked(i, j, k);
	}

	public void sendPacket (String padName) {
		NetworkHandler.NETWORK.sendToServer(
				new PacketAddTelepadEntry(
						mc.player.getUniqueID(), 
						new TelepadEntry(padNameField.getText(), mc.world.provider.getDimension(), te.getPos(), false, false, false),
						share));

		this.mc.player.closeScreen();
	}
}
