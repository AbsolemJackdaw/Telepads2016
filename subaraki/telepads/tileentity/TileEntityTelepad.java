package subaraki.telepads.tileentity;

import java.awt.Color;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TileEntityTelepad extends TileEntity implements ITickable{

	private String telepadname = "TelePad";
	private int dimension;
	private int colorFrame = new Color(26, 246, 172).getRGB();
	private int colorBase = new Color(243, 89, 233).getRGB();

	/**
	 * rotation set when inter-dimension upgrade is applied. NR from 0 to 3 to determines the
	 * position of the transmitter
	 */
	private int upgradeRotation = 0;

	private boolean hasDimensionUpgrade = false;
	private boolean hasRedstoneUpgrade = false;
	private boolean isPowered = false;

	private static final int MAX_TIME = 3 * 20;
	public int counter = MAX_TIME;

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
	public void readFromNBT (NBTTagCompound compound) {

		telepadname = (compound.getString("name"));
		dimension = compound.getInteger("dimension");
		hasDimensionUpgrade = compound.getBoolean("upgrade_dimension");
		hasRedstoneUpgrade = compound.getBoolean("upgrade_redstone");
		isPowered = compound.getBoolean("is_powered");
		this.colorBase = compound.getInteger("colorBase");
		this.colorFrame = compound.getInteger("colorFrame");
		this.upgradeRotation = compound.getInteger("upgradeRotation");

		super.readFromNBT(compound);
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setString("name", telepadname);
		compound.setInteger("dimension", dimension);
		compound.setBoolean("upgrade_dimension", hasDimensionUpgrade);
		compound.setBoolean("upgrade_redstone", hasRedstoneUpgrade);
		compound.setBoolean("is_powered", isPowered);
		compound.setInteger("colorBase", this.colorBase);
		compound.setInteger("colorFrame", this.colorFrame);
		compound.setInteger("upgradeRotation", upgradeRotation);
		return super.writeToNBT(compound);
	}

	@Override
	public void update() {
		if(isPowered)
			return;
	}

	
	
	
	
	
	
	
	/////////////////Setters and Getters/////////////////////////
	public String getTelePadName () {
		return telepadname;
	}

	public void setTelePadName (String name) {
		telepadname = name;
	}

	public int getDimension () {
		return dimension;
	}

	public void setDimension (int dimensionID) {
		dimension = dimensionID;
	}

	public void setFrameColor (int rgb) {
		colorFrame = rgb;
	}

	public void setBaseColor (int rgb) {

		colorBase = rgb;
	}

	public int getColorFrame () {
		return colorFrame;
	}

	public int getColorBase () {
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
}
