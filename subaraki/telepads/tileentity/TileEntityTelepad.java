package subaraki.telepads.tileentity;

import java.awt.Color;
import java.util.List;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
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
		return compound;
	}


	@Override
	public void update() {
		if(isPowered)
			return;

		Telepads.proxy.createTelepadParticleEffect(getPos(), isStandingOnPlatform);

		//if(aabb == null)
		aabb = new AxisAlignedBB(getPos()).expand(0,-0.5,0);

		List<EntityPlayer> playersInRange = worldObj.getEntitiesWithinAABB(EntityPlayer.class, aabb);

		if(playersInRange.isEmpty() && isStandingOnPlatform){
			resetTE();
			return;
		}

		for(EntityPlayer playerInAabb : playersInRange){
			isStandingOnPlatform = true;
			TelepadData td = playerInAabb.getCapability(TelePadDataCapability.CAPABILITY, null);
			if(td.getCounter() > 0 && !td.isInTeleportGui()){
				td.counter--;
			}else{

				if(worldObj.provider.getDimension() == 1 && ConfigurationHandler.instance.allowDragonBlocking){
					for (Object o : worldObj.loadedEntityList)
						if (o instanceof EntityDragon){
							td.setCounter(td.getMaxTime());
							if(!worldObj.isRemote)
								playerInAabb.addChatComponentMessage(new TextComponentString(TextFormatting.DARK_PURPLE+""+TextFormatting.ITALIC+ I18n.format("dragon.obstructs")));
							break;
						}
					if(td.getCounter() <= 0){ //timer gets reset when the dragon is found
						td.setCounter(td.getMaxTime());
						activateTelepadGui(td);
						td.setInTeleportGui(true);
					}
				}else{
					td.setCounter(td.getMaxTime());
					activateTelepadGui(td);
					td.setInTeleportGui(true);
				}
			}
		}
	}

	/**
	 * Resets the count down of the pad, sets that there is no player on the pad, and no player
	 * using the gui. And causes a block update.
	 */
	public void resetTE () {

		isStandingOnPlatform = false;
		markDirty();
		worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(getPos()), worldObj.getBlockState(getPos()), 3);

	}

	private void activateTelepadGui (TelepadData td){
		if (!td.isInTeleportGui() && !(td.getPlayer().openContainer instanceof ContainerTelepad)) {
			if(!worldObj.isRemote){
				WorldDataHandler.get(worldObj).syncClient();
				td.removeEventualQueuedForRemovalEntries();
				td.syncPoweredWithWorldData(WorldDataHandler.get(worldObj));
				td.sync();
				FMLNetworkHandler.openGui(td.getPlayer(), Telepads.instance, GuiHandler.TELEPORT, worldObj, getPos().getX(), getPos().getY(), getPos().getZ());
			}
			markDirty();
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
}
