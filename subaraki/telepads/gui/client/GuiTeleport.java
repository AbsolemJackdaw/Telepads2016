package subaraki.telepads.gui.client;

import static net.minecraft.client.renderer.GlStateManager.color;
import static net.minecraft.client.renderer.GlStateManager.popMatrix;
import static net.minecraft.client.renderer.GlStateManager.pushMatrix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.DimensionManager;
import subaraki.telepads.capability.TelePadDataCapability;
import subaraki.telepads.capability.TelepadData;
import subaraki.telepads.gui.server.ContainerTelepad;
import subaraki.telepads.handler.WorldDataHandler;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.network.NetworkHandler;
import subaraki.telepads.network.PacketTeleport;
import subaraki.telepads.tileentity.TileEntityTelepad;
import subaraki.telepads.utility.TelepadEntry;

public class GuiTeleport extends GuiContainer {

	public EntityPlayer player;
	public TileEntityTelepad te;

	public static final int EXIT_BUTTON = 4000;
	public static final int AREA_LEFT = 3999;
	public static final int AREA_RIGHT = 3998;
	public static final int SCROLL = 3997;

	private int tuner_counter;
	private int dimension_ID;
	private int scroll_index = 0;

	private TextureManager renderEngine = Minecraft.getMinecraft().renderEngine;
	private static final ResourceLocation enderPortalEndSkyTextures = new ResourceLocation("textures/environment/end_sky.png");
	private static final ResourceLocation endPortalTextures = new ResourceLocation("textures/entity/end_portal.png");

	/**
	 * int : buttonID to keep track of entry to look for in player locations, TelepadEntry
	 */
	private HashMap<TelepadEntry, Integer> pageEntries = new HashMap<TelepadEntry, Integer>();

	private List<Integer> dimensionsVisited = new ArrayList<Integer>();

	private int xPosition = width/2;
	private int yPosition = height/2;

	public GuiTeleport(EntityPlayer player, TileEntityTelepad te) {
		super(new ContainerTelepad(te));
		this.te = te;
		this.player = player;
		dimension_ID = player.world.provider.getDimension();

		TelepadData td = player.getCapability(TelePadDataCapability.CAPABILITY, null);

		// this 'add' is performed so that the id of the current world
		// is always set first ! this prevents wrong dimensions from displaying
		// the default
		dimensionsVisited.add(dimension_ID);

		for (TelepadEntry tpe : td.getEntries()) {
			if (!dimensionsVisited.contains(tpe.dimensionID))
				dimensionsVisited.add(tpe.dimensionID);
		}
	}

	@Override
	public void actionPerformed (GuiButton button) {

		if (player != null ){
			int id = button.id;

			if (id == SCROLL) {
				if(pageEntries != null && !pageEntries.isEmpty()){
					scroll_index++;
					if(scroll_index > pageEntries.size()/15)
						scroll_index = 0;
				}

				drawButtonsOnScreen(scroll_index);
			}

			else if (id == EXIT_BUTTON)
				this.mc.player.closeScreen(); // closes the screen

			else if (id == AREA_LEFT) {
				if(dimensionsVisited.size() > 1){
					tuner_counter--;
					drawButtonsOnScreen(0);
				}
			}

			else if (id == AREA_RIGHT) {
				if(dimensionsVisited.size() > 1){
					tuner_counter++;
					drawButtonsOnScreen(0);
				}
			}

			else {
				sendPacket(id);
				te.resetTE();
			}
		}
	}

	@Override
	public boolean doesGuiPauseGame () {
		return false;
	}

	private float backgroundScroll = 0;
	private float backgroundSideScroll = 0;

	@Override
	public void drawBackground (int par1) {

	}

	@Override
	public void drawScreen (int par1, int par2, float par3) {

		this.drawBackground(par1);
		super.drawScreen(par1, par2, par3);

		int offset = 60;
		int x = xPosition - 135/2;
		int y = yPosition - 86;

		//page index indicator
		if(pageEntries != null){
			String page_indicator = "Page " +(this.scroll_index+1) + "/" + ((pageEntries.size()/15)+1);
			int stringX = xPosition - 110;
			fontRenderer.drawStringWithShadow(page_indicator, stringX, y+2, 0xffffff);
		}

		//dimension name field
		drawRect(x - 1 + offset, y - 1, x + 135 + offset + 1, y + 12 + 1, -6250336);
		drawRect(x + offset, y, x + 135 + offset, y + 12, -16777216);

		String dimension_name = DimensionManager.getProviderType(dimension_ID).getName();//DimensionType.getById(dimension_ID).getName();
		int stringX = xPosition - fontRenderer.getStringWidth(dimension_name)/2;

		if (!te.hasDimensionUpgrade())
			fontRenderer.drawStringWithShadow(dimension_name, stringX + offset , y+2, 0xffffff);
		else {
			if (dimension_name != null && dimension_name.length() > 0) 
				fontRenderer.drawStringWithShadow(dimension_name, stringX + offset, y+2, 0xffffff);
			else 
				fontRenderer.drawStringWithShadow("No%Dim- Error : Hz " + dimension_ID, stringX + offset, y+3, 0xffffff);
		}
	}

	public void drawButtonsOnScreen (int scroll_page) {
		//helper to reset pages if the channel is changed
		this.scroll_index = scroll_page;

		this.buttonList.clear();
		pageEntries.clear();

		if (tuner_counter >= dimensionsVisited.size())
			tuner_counter = 0;
		if (tuner_counter < 0)
			tuner_counter = dimensionsVisited.size() - 1;

		dimension_ID = dimensionsVisited.get(tuner_counter);

		if (te.hasDimensionUpgrade()) {
			this.buttonList.add(new GuiButton(AREA_LEFT, xPosition - 33, yPosition - 90, 20, 20, "<"));
			this.buttonList.add(new GuiButton(AREA_RIGHT,xPosition + 133, yPosition - 90, 20, 20, ">"));
		}

		this.buttonList.add(new GuiButton(EXIT_BUTTON, xPosition - 157, yPosition - 90, 20, 20, "x"));

		fillEntries();

		if(pageEntries != null && !pageEntries.isEmpty() && pageEntries.size() > 15)
			this.buttonList.add(new GuiButton(SCROLL, xPosition - 135, yPosition - 90, 20, 20, "v"));

		makePage(scroll_page);
	}

	@Override
	public void initGui () {

		if(xPosition != width/2)
			xPosition = width/2;
		if(yPosition != height/2)
			yPosition = height/2;

		drawButtonsOnScreen(0);
	}

	@Override
	protected void keyTyped (char c, int i) throws IOException {

		super.keyTyped(c, i);

		if (i == Keyboard.KEY_ESCAPE) {
			te.resetTE();
			mc.player.closeScreen();
		}
	}

	public void sendPacket (int id) {

		if (player == null)
			return;

		this.mc.player.closeScreen();
		TelepadData td = player.getCapability(TelePadDataCapability.CAPABILITY, null);
		NetworkHandler.NETWORK.sendToServer(new PacketTeleport(player.getPosition(), td.getEntries().get(id), false));
	}

	private void fillEntries(){
		TelepadData td = player.getCapability(TelePadDataCapability.CAPABILITY, null);

		int classificationID = 0;

		for (TelepadEntry tpe : td.getEntries()) {

			if (tpe.dimensionID == dimension_ID)
				pageEntries.put(tpe, classificationID);
			classificationID++;
		}
	}

	private void makePage (int scroll_page) {

		int entry = 0;

		for (TelepadEntry tpe : pageEntries.keySet()) {

			//the world data has all info needed. player data only has location correctly stored
			TelepadEntry worldEntry = WorldDataHandler.get(te.getWorld()).getEntryForLocation(tpe.position, tpe.dimensionID);

			String telepadName = "";

			if(worldEntry != null){
				boolean isPowered = worldEntry.isPowered;
				boolean isTransmitter = worldEntry.hasTransmitter;
				telepadName = isPowered ? ChatFormatting.DARK_RED + tpe.entryName : isTransmitter ? ChatFormatting.DARK_GREEN + tpe.entryName : tpe.entryName;
			}else{
				telepadName = ChatFormatting.DARK_GRAY+tpe.entryName;
			}

			if(entry < 15*(scroll_page+1) && entry >= 15*scroll_page){
				int index = entry - 15*scroll_page; //index = [0;14]

				int theX = (index/5) * 105;
				int theY = (index%5) * 25;
				this.buttonList.add(new GuiButton(pageEntries.get(tpe), 

						/* x */
						xPosition + theX - (int)(105*1.5f),
						/* y */
						yPosition + theY - (int)(25*2.5f), 

						/* size */100, 20,
						telepadName));
			}
			entry++;
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float arg0, int arg1, int arg2) {
		
		backgroundScroll += 1f;
		backgroundSideScroll += 0.01f;
		float scrollSpeed = backgroundScroll + 2;

		pushMatrix();
		GlStateManager.enableBlend();
		color(0.2f, 0.6f, 1f, backgroundSideScroll < 0.6f ? backgroundSideScroll : 0.6f);
		renderEngine.bindTexture(enderPortalEndSkyTextures);
		drawTexturedModalRect(0, 0, -(int) scrollSpeed * 2, (int) backgroundScroll * 2, width, height);
		color(1, 1, 1, 1);
		
		color(0.2f, 0.6f, 1f, backgroundSideScroll < 0.75f ? backgroundSideScroll : 0.75f);
		renderEngine.bindTexture(endPortalTextures);
		drawTexturedModalRect(0, 0, (int) scrollSpeed * 2, (int) backgroundScroll*2, width, height);
		color(1, 1, 1, 1);
		GlStateManager.disableBlend();
		popMatrix();

		
	}
}
