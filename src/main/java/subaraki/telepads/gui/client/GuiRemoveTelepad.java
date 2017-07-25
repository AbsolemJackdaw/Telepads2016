package subaraki.telepads.gui.client;

import static net.minecraft.client.renderer.GlStateManager.color;
import static net.minecraft.client.renderer.GlStateManager.disableBlend;
import static net.minecraft.client.renderer.GlStateManager.enableBlend;
import static net.minecraft.client.renderer.GlStateManager.popMatrix;
import static net.minecraft.client.renderer.GlStateManager.pushMatrix;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import subaraki.telepads.capability.TelePadDataCapability;
import subaraki.telepads.gui.server.ContainerTelepad;
import subaraki.telepads.network.NetworkHandler;
import subaraki.telepads.network.PacketRemoveTelepadEntry;
import subaraki.telepads.network.PacketTeleport;
import subaraki.telepads.utility.TelepadEntry;

public class GuiRemoveTelepad extends GuiContainer {

	private EntityPlayer player;
	private TelepadEntry entryToRemove;

	public GuiRemoveTelepad(EntityPlayer player) {
		super(new ContainerTelepad());
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

		int lenght = this.fontRenderer.getStringWidth(s);

		fontRenderer.drawStringWithShadow(s, posX - lenght/2, posY - 30, 0xffffff);
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

	private float backgroundScroll = 0;
	private float backgroundSideScroll = 0;
	private TextureManager renderEngine = Minecraft.getMinecraft().renderEngine;
	private static final ResourceLocation enderPortalEndSkyTextures = new ResourceLocation("textures/environment/end_sky.png");
	private static final ResourceLocation endPortalTextures = new ResourceLocation("textures/entity/end_portal.png");

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		backgroundScroll += 1f;
		backgroundSideScroll += 0.01f;
		float scrollSpeed = backgroundScroll + 2;

		pushMatrix();

		enableBlend();

		color(0.2f, 0.6f, 1f, 0.6f);
		renderEngine.bindTexture(enderPortalEndSkyTextures);
		drawTexturedModalRect(0, 0, -(int) scrollSpeed * 2, (int) backgroundScroll * 2, width, height);
		color(1, 1, 1, 1);

		color(0.2f, 0.6f, 1f, 0.75f);
		renderEngine.bindTexture(endPortalTextures);
		drawTexturedModalRect(0, 0, (int) scrollSpeed * 2, (int) backgroundScroll*2, width, height);
		color(1, 1, 1, 1);

		disableBlend();

		popMatrix();
	}
}
