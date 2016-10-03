package subaraki.telepads.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import subaraki.telepads.item.TelepadItems;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.tileentity.TileEntityTelepad;

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
		setRegistryName(Telepads.MODID+".telepad");
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
			EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {

		if(heldItem != null){
			Item item = heldItem.getItem();
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof TileEntityTelepad){
				TileEntityTelepad tet = (TileEntityTelepad)te;
				if(item != null){
					if(item.equals(TelepadItems.transmitter)){
						//check for server only. syncs automatically with client. if doing both sides, client setter will make it look jumpy
						if(!tet.hasDimensionUpgrade() && !world.isRemote){ 
							tet.addDimensionUpgrade(true);
							tet.markDirty();
							world.notifyBlockUpdate(pos, getDefaultState(), getDefaultState(), 3);
						}
					}
					//check for server only. syncs automatically with client. if doing both sides, client setter will make it look jumpy
					if(item.equals(TelepadItems.toggler)&& !world.isRemote){
						if(!tet.hasRedstoneUpgrade()){
							tet.addRedstoneUpgrade();
							tet.markDirty();
							world.notifyBlockUpdate(pos, getDefaultState(), getDefaultState(), 3);
						}
					}
				}
			}
		}else{
			if(player.isSneaking()){
				//TODO add pad to player data list
			}
		}

		return super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block) {
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
		world.notifyBlockUpdate(pos, getDefaultState(), getDefaultState(), 3);

		//TODO set player locations data for this pad to false
	}


	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {

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
	public boolean isVisuallyOpaque() {
		return false;
	}
	@Override
	public boolean isOpaqueCube(IBlockState state) {
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
}
