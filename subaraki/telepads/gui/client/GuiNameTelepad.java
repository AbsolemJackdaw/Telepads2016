package subaraki.telepads.gui.client;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import subaraki.telepads.handler.WorldDataHandler;
import subaraki.telepads.network.NetworkHandler;
import subaraki.telepads.network.PacketAddTelepadEntry;
import subaraki.telepads.tileentity.TileEntityTelepad;
import subaraki.telepads.utility.TelepadEntry;

public class GuiNameTelepad extends GuiScreen {

	private GuiTextField padNameField;

	public TileEntityTelepad te;

	public GuiNameTelepad(EntityPlayer player, TileEntityTelepad te) {
		this.te = te;
	}

	@Override
	public void actionPerformed (GuiButton button) {
	}

	@Override
	public boolean doesGuiPauseGame () {
		return false;
	}

	@Override
	public void drawScreen (int par1, int par2, float par3) {
		try {
			int posX = (this.width) / 2;
			int posY = (this.height) / 2;

			String enter = I18n.format("enter.to.confirm");
			String nameYourPad = I18n.format("name.your.telepad")+ " : " + padNameField.getText();

			fontRendererObj.drawStringWithShadow(enter, posX - fontRendererObj.getStringWidth(enter)/2, posY, 0xffffff);
			fontRendererObj.drawStringWithShadow(nameYourPad , posX - fontRendererObj.getStringWidth(nameYourPad)/2, posY - 20, 0xff0000);
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

		padNameField = new GuiTextField(0, fontRendererObj, posX - (150 / 2), posY - 50, 150, 20);
		padNameField.setFocused(true);

		String padName = te.getWorld().getBiomeGenForCoords(te.getPos()).getBiomeName();

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
		NetworkHandler.NETWORK.sendToServer(new PacketAddTelepadEntry(mc.thePlayer.getUniqueID(), new TelepadEntry(padNameField.getText(), mc.thePlayer.worldObj.provider.getDimension(), te.getPos(), false, false)));
		this.mc.thePlayer.closeScreen();
	}
}
