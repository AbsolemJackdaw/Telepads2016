package subaraki.telepads.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import subaraki.telepads.capability.TelePadDataCapability;
import subaraki.telepads.capability.TelepadData;
import subaraki.telepads.gui.GuiHandler;
import subaraki.telepads.handler.WorldDataHandler;
import subaraki.telepads.item.TelepadItems;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.tileentity.TileEntityTelepad;
import subaraki.telepads.utility.TelepadEntry;

public class BlockTelepad extends Block{

	protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D);

	public BlockTelepad() {
		super(Material.GLASS);
		setLightLevel(0.2f);
		setHardness(5f);
		setSoundType(SoundType.GLASS);
		setCreativeTab(CreativeTabs.TRANSPORTATION);
		setHarvestLevel("pickaxe", 1);
		setUnlocalizedName(Telepads.MODID+".telepad");
		setRegistryName("telepad");
	}

	/////////////////rendering//////////////
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	///////////////TE Stuff//////////////////////
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityTelepad();
	}

	//////////Interaction///////

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

		ItemStack heldItem = player.getHeldItem(hand);

		if(!heldItem.isEmpty()){
			Item item = heldItem.getItem();
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof TileEntityTelepad){
				TileEntityTelepad tet = (TileEntityTelepad)te;
				if(item.equals(TelepadItems.transmitter)){
					//check for server only. syncs automatically with client. if doing both sides, client setter will make it look jumpy
					if(!tet.hasDimensionUpgrade() && !world.isRemote){ 
						tet.addDimensionUpgrade(true);
						world.notifyBlockUpdate(pos, world.getBlockState(pos), getDefaultState(), 3);
						TelepadEntry entry = WorldDataHandler.get(world).getEntryForLocation(pos, world.provider.getDimension());
						entry.hasTransmitter = true;
						WorldDataHandler.get(world).updateEntry(entry);
					}
				}
				//check for server only. syncs automatically with client. if doing both sides, client setter will make it look jumpy
				if(item.equals(TelepadItems.toggler)&& !world.isRemote){
					if(!tet.hasRedstoneUpgrade()){
						tet.addRedstoneUpgrade();
						world.notifyBlockUpdate(pos, world.getBlockState(pos), getDefaultState(), 3);
						this.neighborChanged(state, world, pos, state.getBlock(), null);
					}
				}

				if(item.equals(Items.DYE)){
					int color = EnumDyeColor.byDyeDamage(heldItem.getItemDamage()).getColorValue();
					if(tet.getColorFeet() == tet.COLOR_FEET_BASE)
						tet.setFeetColor(color);
					else if(tet.getColorArrow() == tet.COLOR_ARROW_BASE)
						tet.setArrowColor(color);
					tet.markDirty();
					world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
					if(!player.isCreative())
						heldItem.shrink(1);
				}

				if(item.equals(Items.WATER_BUCKET)){
					boolean hasWashed = false;
					if(tet.getColorFeet() != tet.COLOR_FEET_BASE){
						wash(tet.getColorFeet(), world, pos);
						tet.setFeetColor(tet.COLOR_FEET_BASE);
						hasWashed = true;
					}
					if(tet.getColorArrow() != tet.COLOR_ARROW_BASE){
						wash(tet.getColorArrow(), world, pos);
						tet.setArrowColor(tet.COLOR_ARROW_BASE);
						hasWashed=true;
					}
					if(!player.isCreative() && hasWashed){
						world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1, 1, true);
						player.setHeldItem(hand, new ItemStack(Items.WATER_BUCKET.getContainerItem(), heldItem.getCount(), heldItem.getMetadata()));
					}
					return true;
				}
			}
		}else{
			if(player.isSneaking()){
				if(world.getTileEntity(pos) instanceof TileEntityTelepad){
					//check if the player has this pad
					TelepadData td = player.getCapability(TelePadDataCapability.CAPABILITY, null);
					TileEntityTelepad tet = (TileEntityTelepad)world.getTileEntity(pos);
					TelepadEntry lookingForEntry = null;
					for(TelepadEntry tpe: td.getEntries()){
						if(tpe.position.equals(pos)){
							lookingForEntry = tpe;
						}
					}

					//if he doesn't have the pad, look it up in the world save
					if(lookingForEntry == null){
						TelepadEntry entry = WorldDataHandler.get(world).getEntryForLocation(pos, world.provider.getDimension());
						if(entry != null){
							td.addEntry(entry);
							if (!world.isRemote)
								player.sendMessage(new TextComponentString(TextFormatting.GREEN+"Added " + entry.entryName));
						}else
							player.sendMessage(new TextComponentString(TextFormatting.RED+ "The Telepad you try to register does not exist in the world save. Cannot add Telepad to your registry."));
					}else{
						if (!world.isRemote)
							player.sendMessage(new TextComponentString(TextFormatting.RED+lookingForEntry.entryName+" has already been registered"));
					}
				}
			}
		}

		return true;
	}

	private void wash(int color, World world, BlockPos pos){
		EnumDyeColor edc = EnumDyeColor.WHITE;
		for(EnumDyeColor dye : EnumDyeColor.values())
			if(dye.getColorValue() == color)
				edc = dye;
		ItemStack stack = new ItemStack(Items.DYE, 1, edc.getDyeDamage());
		if(!world.isRemote)
			world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), stack));
	}

	/////////////////////////////REDSTONE////////////////////////////////////////
	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {

		TileEntity te = world.getTileEntity(pos);
		TileEntityTelepad tet = null;
		if(te == null || !(te instanceof TileEntityTelepad))
			return;

		tet = (TileEntityTelepad)te;

		if(!tet.hasRedstoneUpgrade())
			return;

		EnumFacing facesThatCanPower[] = new EnumFacing[]{
				EnumFacing.NORTH,
				EnumFacing.SOUTH,
				EnumFacing.EAST,
				EnumFacing.WEST,
				EnumFacing.DOWN
		};

		boolean isPowered = false;
		for(EnumFacing face : facesThatCanPower){
			if(!world.getBlockState(pos.offset(face)).canProvidePower())
				continue;
			int power = world.getBlockState(pos.offset(face)).getStrongPower(world, pos, face);
			int weakPower = world.getBlockState(pos.offset(face)).getWeakPower(world, pos, face);
			if(power > 0 || weakPower > 0){
				isPowered = true;
				break;
			}
		}

		tet.setPowered(isPowered);
		tet.markDirty();
		world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);

		WorldDataHandler wdh = WorldDataHandler.get(world);
		TelepadEntry entry = wdh.getEntryForLocation(pos, world.provider.getDimension());
		entry.isPowered = isPowered;
		wdh.updateEntry(entry);
		wdh.markDirty();
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		super.onNeighborChange(world, pos, neighbor);
	}

	////////////////Block Placed///////////////////////////////////

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		TileEntity te = world.getTileEntity(pos);
		TileEntityTelepad tet = null;
		if(te == null || !(te instanceof TileEntityTelepad))
			return;

		tet = (TileEntityTelepad)te;	

		if(placer instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer)placer;
			tet.setDimension(world.provider.getDimension());
			if (stack.hasTagCompound()) {
				if (stack.getTagCompound().hasKey("colorFrame"))
					tet.setFeetColor(stack.getTagCompound().getInteger("colorFrame"));
				if (stack.getTagCompound().hasKey("colorBase"))
					tet.setArrowColor(stack.getTagCompound().getInteger("colorBase"));
			}
			te.markDirty();
			world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
			world.setTileEntity(pos, te);

			FMLNetworkHandler.openGui(player, Telepads.instance, GuiHandler.NAME_TELEPAD, world, pos.getX(), pos.getY(), pos.getZ());
		}
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		TileEntity te = world.getTileEntity(pos);
		TileEntityTelepad tet = null;
		if(te == null || !(te instanceof TileEntityTelepad))
			return false;

		tet = (TileEntityTelepad)te;

		WorldDataHandler wdh = WorldDataHandler.get(world);
		TelepadEntry entry = wdh.getEntryForLocation(pos, world.provider.getDimension());
		if(entry != null && wdh.contains(entry) && !world.isRemote){
			wdh.removeEntry(entry);
			dropPad(world, tet, pos);
			if (tet.hasDimensionUpgrade())
				world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(TelepadItems.transmitter, 1)));
			if (tet.hasRedstoneUpgrade())
				world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(TelepadItems.toggler, 1)));

			return world.setBlockToAir(pos);
		}

		return super.removedByPlayer(state, world, pos, player, willHarvest);
	}

	private void dropPad (World world, TileEntityTelepad telepad, BlockPos pos) {

		EntityItem ei = new EntityItem(world);
		ei.setPosition(pos.getX(), pos.getY(), pos.getZ());

		ItemStack stack = new ItemStack(TelepadBlocks.blockTelepad);
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("colorBase", telepad.getColorArrow());
		nbt.setInteger("colorFrame", telepad.getColorFeet());
		stack.setTagCompound(nbt);

		ei.setItem(stack);
		world.spawnEntity(ei);
	}

	///////////////inherited methods////////////////////
	@Override
	public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
		if (entity instanceof EntityPlayer)
			return true;
		return false;
	}
	@Override
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
		//keep empty
	}
	@Override
	public int quantityDropped(IBlockState state, int fortune, Random random) {
		return 0;
	}
	@Override
	public int quantityDropped(Random random) {
		return 0;
	}
	@Override
	public int quantityDroppedWithBonus(int fortune, Random random) {
		return 0;
	}
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}
	@Override
	public float getExplosionResistance(Entity exploder) {
		return Float.MAX_VALUE;
	}
	@Override
	public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
		return Float.MAX_VALUE;
	}
	
	@Override
	public void randomDisplayTick(IBlockState stateIn, World world, BlockPos pos, Random rand) {
	
		TileEntity te = world.getTileEntity(pos);

		if(te == null || !(te instanceof TileEntityTelepad))
			return;
		
		int maxParticleCount = (((TileEntityTelepad)te).isStandingOnPlatform()) ? 15 : 1;
		
		for (int particleCount = 0; particleCount < maxParticleCount; ++particleCount) {

			double posX = pos.getX() + 0.5f;
			double posY = pos.getY() + (rand.nextFloat() * 1.5f);
			double posZ = pos.getZ() + 0.5f;
			double velocityX = 0.0D;
			double volocityY = 0.0D;
			double velocityZ = 0.0D;
			int velocityXOffset = (rand.nextInt(2) * 2) - 1;
			int velocityZOffset = (rand.nextInt(2) * 2) - 1;

			velocityX = (rand.nextFloat() - 0.5D) * 0.125D;
			volocityY = (rand.nextFloat() - 0.5D) * 0.125D;
			velocityZ = (rand.nextFloat() - 0.5D) * 0.125D;
			velocityX = rand.nextFloat() * 1.0F * velocityXOffset;
			velocityZ = rand.nextFloat() * 1.0F * velocityZOffset;
			world.spawnParticle(EnumParticleTypes.PORTAL, posX, posY, posZ, velocityX, volocityY, velocityZ);
		}
	}
}
