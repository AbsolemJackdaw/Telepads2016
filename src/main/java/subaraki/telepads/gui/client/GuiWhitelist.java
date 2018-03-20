package subaraki.telepads.gui.client;

import java.awt.Color;
import java.io.IOException;

import org.lwjgl.input.Keyboard;

import lib.Lib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import subaraki.telepads.capability.TelePadDataCapability;
import subaraki.telepads.capability.TelepadData;
import subaraki.telepads.gui.server.ContainerTelepad;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.network.NetworkHandler;
import subaraki.telepads.network.PacketAddWhitelistEntry;

public class GuiWhitelist extends GuiContainer {

	private TextureManager renderEngine = Minecraft.getMinecraft().renderEngine;
	private static final ResourceLocation BACKGROUND = new ResourceLocation(Telepads.MODID,"textures/gui/whitelist.png");

	private GuiTextField textField;

	private TelepadData data;

	private int errorCount = 0;
	private String error = "";
	private static final String eT = "no such player";

	private boolean aid;

	public GuiWhitelist() {
		super(new ContainerTelepad());
		data = Lib.proxy.clientPlayer().getCapability(TelePadDataCapability.CAPABILITY, null);
		this.xSize = 142;
		this.ySize = 166;
	}


	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		renderEngine.bindTexture(BACKGROUND);
		drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, xSize, ySize);

		if(!error.isEmpty())
		{
			fontRenderer.drawStringWithShadow(error, this.guiLeft + (xSize / 2 - fontRenderer.getStringWidth(error)/2),  this.guiTop+ySize-15, 0xc00009);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if(button.id == 0)
		{
			aid = !aid;
		}
	}

	@Override
	public void drawScreen (int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
		GlStateManager.disableLighting();
		//this.drawDefaultBackground();
		try {
			int posX = (this.width) / 2;
			int posY = (this.height) / 2;

			int i = 0;
			if(!data.getWhitelist().isEmpty())
				for(String name : data.getWhitelist())
				{
					fontRenderer.drawStringWithShadow(name, posX - fontRenderer.getStringWidth(name)/2, this.guiTop + 35 + i, 0xffffff);
					i+=13;
				}

			fontRenderer.drawStringWithShadow("Friends Whitelist", this.guiLeft+ 5 , this.guiTop + 3, Color.white.getRGB());

			if(aid)
			{
				fontRenderer.drawSplitString(
						"Clear list: /clear         "+
								"Clear name: /remove 'name' ", this.guiLeft + xSize + 2, this.guiTop + 25, 80, 0xdddddd);
			}
		}
		finally {
			if (textField != null)
				textField.drawTextBox();
		}
		GlStateManager.enableLighting();
	}

	@Override
	public void initGui () {

		super.initGui();

		int posX = (this.width) / 2;
		int posY = (this.height) / 2;
		this.buttonList.clear();

		this.buttonList.add(new GuiButton(0, guiLeft+this.xSize, guiTop, 20, 20, "?"));

		textField = new GuiTextField(0, fontRenderer, posX - (132 / 2)  , posY - 70, 132, 11);
		textField.setFocused(true);

		if (textField != null) {
			textField.setText("");
			textField.setMaxStringLength(17); //16 is max allowed. 17 for error margin. you never know
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		if(!error.isEmpty())
			errorCount++;
		if(errorCount > 300)
		{
			errorCount =0;
			error = "";
		}
	}

	@Override
	protected void keyTyped (char c, int i) throws IOException {
		//super.keyTyped(c, i);

		if (i == Keyboard.KEY_RETURN || i == Keyboard.KEY_ESCAPE)
		{
			String name = textField.getText();
			error = "";

			if(name == null || name != null && name.isEmpty())
			{
				this.mc.player.closeScreen();
				return;
			}

			if(name.startsWith("/"))
			{
				String command = name;
				sendPacket(command);
				if(i == Keyboard.KEY_ESCAPE)
					mc.player.closeScreen();
			}
			else
			{

				EntityPlayer player = Lib.proxy.getMC().world.getPlayerEntityByName(name);

				if(player != null && !data.isWhiteListFull())
				{
					error = "added "+name;
					sendPacket(name);
				}
				else
				{
					error = data.isWhiteListFull() ? "List Full." : eT;
					textField.setText("");
				}

				if(i == Keyboard.KEY_ESCAPE)
					mc.player.closeScreen();
			}
		}

		if (textField != null)
			textField.textboxKeyTyped(c, i);

	}
	@Override
	protected void mouseClicked (int i, int j, int k) throws IOException {
		super.mouseClicked(i, j, k);
		if (textField != null)
			textField.mouseClicked(i, j, k);
	}

	public void sendPacket (String padName) {
		NetworkHandler.NETWORK.sendToServer(new PacketAddWhitelistEntry(textField.getText()));
		if(!textField.getText().startsWith("/"))
			data.addToWiteList(textField.getText());
		else
			try {
				data.commandWhitelist(textField.getText());
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		textField.setText("");
	}
}
