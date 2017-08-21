package subaraki.telepads.tileentity;

import java.awt.Color;
import java.util.List;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import subaraki.telepads.block.TelepadBlocks;
import subaraki.telepads.capability.TelePadDataCapability;
import subaraki.telepads.capability.TelepadData;
import subaraki.telepads.gui.GuiHandler;
import subaraki.telepads.gui.server.ContainerTelepad;
import subaraki.telepads.handler.ConfigurationHandler;
import subaraki.telepads.handler.WorldDataHandler;
import subaraki.telepads.mod.Telepads;

public class TileEntityTelepad extends TileEntity implements ITickable{

	private int dimension;
	public static final int COLOR_FEET_BASE = new Color(26, 246, 172).getRGB();
	public static final int COLOR_ARROW_BASE = new Color(243, 89, 233).getRGB();
	private int colorFrame = COLOR_FEET_BASE;
	private int colorBase = COLOR_ARROW_BASE;

	/**
	 * rotation set when inter-dimension upgrade is applied. NR from 0 to 3 to determines the
	 * position of the transmitter
	 */
	private int upgradeRotation = 0;

	private boolean hasDimensionUpgrade = false;
	private boolean hasRedstoneUpgrade = false;
	private boolean isPowered = false;

	private boolean isStandingOnPlatform;

	private AxisAlignedBB aabb ;

	public TileEntityTelepad(){
		aabb = new AxisAlignedBB(getPos()).expand(1, 1, 1);
	}

	/////////////////3 METHODS ABSOLUTELY NEEDED FOR CLIENT/SERVER SYNCING/////////////////////
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);

		return new SPacketUpdateTileEntity(getPos(), 0, nbt);

	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt =  super.getUpdateTag();
		writeToNBT(nbt);
		return nbt;
	}

	//calls readFromNbt by default. no need to add anything in here
	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
	}
	////////////////////////////////////////////////////////////////////

	@Override
	public void readFromNBT (NBTTagCompound compound) {
		super.readFromNBT(compound);
		dimension = compound.getInteger("dimension");
		hasDimensionUpgrade = compound.getBoolean("upgrade_dimension");
		hasRedstoneUpgrade = compound.getBoolean("upgrade_redstone");
		isPowered = compound.getBoolean("is_powered");
		this.colorBase = compound.getInteger("colorBase");
		this.colorFrame = compound.getInteger("colorFrame");
		this.upgradeRotation = compound.getInteger("upgradeRotation");
		isStandingOnPlatform = compound.getBoolean("standingon");

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("dimension", dimension);
		compound.setBoolean("upgrade_dimension", hasDimensionUpgrade);
		compound.setBoolean("upgrade_redstone", hasRedstoneUpgrade);
		compound.setBoolean("is_powered", isPowered);
		compound.setInteger("colorBase", this.colorBase);
		compound.setInteger("colorFrame", this.colorFrame);
		compound.setInteger("upgradeRotation", upgradeRotation);
		compound.setBoolean("standingon", isStandingOnPlatform);
		return compound;
	}


	@Override
	public void update() {

		if(isPowered)
			return;

		if(!world.isRemote)
		{

			AxisAlignedBB aabb = new AxisAlignedBB(getPos());
			List<EntityPlayer> list = world.getEntitiesWithinAABB(EntityPlayer.class, aabb);

			if(!list.isEmpty())
			{

				setPlatform(true);

				for(EntityPlayer standing : list)
				{
					TelepadData playersave = standing.getCapability(TelePadDataCapability.CAPABILITY, null);

					if(playersave.getCounter() > 0 && !playersave.isInTeleportGui())
					{
						playersave.counter--;
					}

					else if (playersave.getCounter() == 0 && !playersave.isInTeleportGui())
					{
						if(world.provider.getDimension() == 1 && ConfigurationHandler.instance.allowDragonBlocking)
						{
							for (Object o : world.loadedEntityList)
								if (o instanceof EntityDragon){
									playersave.setCounter(playersave.getMaxTime());
									
									standing.sendMessage(
											new TextComponentTranslation("dragon.obstructs")
											.setStyle(new Style().setColor(TextFormatting.DARK_PURPLE).setItalic(true)));
									return;
								}
						}
						//if no dragon is found, or dimension != the end, you end up here
						playersave.setInTeleportGui(true);
						playersave.setCounter(playersave.getMaxTime());
						activateTelepadGui(playersave);
					}
				}
			}

			else
			{
				setPlatform(false);
			}
		}
	}

	/**
	 * Resets the count down of the pad, sets that there is no player on the pad, and no player
	 * using the gui. And causes a block update.
	 */
	public void setPlatform (boolean onPlatform) {
		isStandingOnPlatform = onPlatform;
		world.notifyBlockUpdate(pos, world.getBlockState(getPos()), TelepadBlocks.blockTelepad.getDefaultState(), 3);
	}

	private void activateTelepadGui (TelepadData td){
		if (!(td.getPlayer().openContainer instanceof ContainerTelepad)) 
		{
			WorldDataHandler.get(world).syncClient();
			td.removeEventualQueuedForRemovalEntries();
			td.syncPoweredWithWorldData(WorldDataHandler.get(world));
			td.sync();
			td.getPlayer().openGui(Telepads.instance, GuiHandler.TELEPORT, world, getPos().getX(), getPos().getY(), getPos().getZ());
		}
	}

	/////////////////Setters and Getters/////////////////////////

	public int getDimension () {
		return dimension;
	}

	public void setDimension (int dimensionID) {
		dimension = dimensionID;
	}

	public void setFeetColor (int rgb) {
		colorFrame = rgb;
	}

	public void setArrowColor (int rgb) {
		colorBase = rgb;
	}

	public int getColorFeet () {
		return colorFrame;
	}

	public int getColorArrow () {
		return colorBase;
	}

	public boolean hasDimensionUpgrade () {
		return hasDimensionUpgrade;
	}

	public void addDimensionUpgrade (boolean allowed) {
		this.upgradeRotation = allowed ? new Random().nextInt(4) : 0;
		hasDimensionUpgrade = true;
	}

	public int getUpgradeRotation () {
		return upgradeRotation;
	}

	public boolean hasRedstoneUpgrade () {
		return hasRedstoneUpgrade;
	}

	public void addRedstoneUpgrade () {
		hasRedstoneUpgrade = true;
	}

	public void setPowered (boolean flag) {
		isPowered = flag;
	}

	public boolean isPowered () {
		return isPowered;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
	{
		return oldState.getBlock() != newSate.getBlock();
	}

	public boolean isUsableByPlayer(EntityPlayer player){
		return this.world.getTileEntity(this.pos) != this ? false : player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
	}

	public boolean isStandingOnPlatform() {
		return isStandingOnPlatform;
	}
}
