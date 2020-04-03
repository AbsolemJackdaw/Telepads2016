package subaraki.telepads.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.PacketDistributor;
import subaraki.telepads.handler.ConfigData;
import subaraki.telepads.handler.CoordinateHandler;
import subaraki.telepads.handler.WorldDataHandler;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.mod.Telepads.ObjectHolders;
import subaraki.telepads.network.NetworkHandler;
import subaraki.telepads.network.client.CPacketRequestNamingScreen;
import subaraki.telepads.tileentity.TileEntityTelepad;
import subaraki.telepads.utility.TelepadEntry;

public class BlockTelepad extends Block implements IWaterLoggable {

    protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.20D, 1.0D);
    protected static final VoxelShape VOX = VoxelShapes.create(AABB);

    private static final VoxelShape shape = VoxelShapes.create(new AxisAlignedBB(0.0D, 0D, 0.0D, 1.0D, 0.20D, 1.0D));
    private static final VoxelShape shape_n = VoxelShapes.create(new AxisAlignedBB(0.32D, 0.0D, -.22D, .68D, 0.15D, 0D));
    private static final VoxelShape shape_s = VoxelShapes.create(new AxisAlignedBB(0.32D, 0.0D, 1.00D, .68D, 0.15D, 1.22D));
    private static final VoxelShape shape_e = VoxelShapes.create(new AxisAlignedBB(0D, 0.0D, 0.32D, 1.22D, 0.15D, .68D));
    private static final VoxelShape shape_w = VoxelShapes.create(new AxisAlignedBB(0D, 0.0D, 0.32D, -.22D, 0.15D, .68D));
    private static final VoxelShape SHAPE_VOX = VoxelShapes.or(shape_w, shape_e, shape_s, shape_n, shape);

    private static Properties block_properties = Properties.create(Material.GLASS).hardnessAndResistance(5F, Float.MAX_VALUE).sound(SoundType.GLASS)
            .harvestTool(ToolType.PICKAXE).harvestLevel(1);

    private String text_public_rod;
    private String text_public_rod_private;
    private String text_public_rod_public;

    private String text__cycle_rod_normal;
    private String text__cycle_rod;

    private String cycle_add_success;
    private String cycle_add_remove;
    private String cycle_add_fail;

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public BlockTelepad() {

        super(block_properties);
        setRegistryName(Telepads.MODID, "telepad");

        text_public_rod = new TranslationTextComponent("block.info.rod").getFormattedText();
        text_public_rod_private = new TranslationTextComponent("block.info.rod.private").getFormattedText();
        text_public_rod_public = new TranslationTextComponent("lock.info.rod.public").getFormattedText();
        text__cycle_rod_normal = new TranslationTextComponent("block.info.cycle.normal").getFormattedText();
        text__cycle_rod = new TranslationTextComponent("block.info.cycle").getFormattedText();
        cycle_add_success = new TranslationTextComponent("block.info.add.succes").getFormattedText();
        cycle_add_remove = new TranslationTextComponent("block.info.add.fail").getFormattedText();
        cycle_add_fail = new TranslationTextComponent("block.info.add.remove").getFormattedText();

        this.setDefaultState(getDefaultState().with(WATERLOGGED, Boolean.valueOf(false)));

    }

    ///////////////// waterlogged//////////////

    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {

        if (stateIn.get(WATERLOGGED))
        {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }

        return stateIn;
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {

        builder.add(WATERLOGGED);
    }

    @Override
    public boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, IFluidState fluidStateIn)
    {
    
        return IWaterLoggable.super.receiveFluid(worldIn, pos, state, fluidStateIn);
    }

    public IFluidState getFluidState(BlockState state)
    {

        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    ///////////////// rendering//////////////

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {

        return SHAPE_VOX;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {

        return VOX;
    }

    @Override
    public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos)
    {

        return SHAPE_VOX;
    }

    @Override
    public boolean isSolid(BlockState state)
    {

        return false;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state)
    {

        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    /////////////// TE Stuff//////////////////////
    @Override
    public boolean hasTileEntity(BlockState state)
    {

        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {

        return new TileEntityTelepad();
    }

    ////////// Interaction///////

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {

        ItemStack heldItem = player.getHeldItem(hand);

        if (world.isRemote())
            return false;

        if (!heldItem.isEmpty())
        {
            Item item = heldItem.getItem();
            TileEntity tile_entity = world.getTileEntity(pos);

            if (tile_entity instanceof TileEntityTelepad)
            {
                TileEntityTelepad telepad_tile_entity = (TileEntityTelepad) tile_entity;

                TelepadEntry entry = WorldDataHandler.get(world).getEntryForLocation(pos, world.dimension.getType().getId());

                if (item.equals(ObjectHolders.TRANSMITTER))
                {
                    // check for server only. syncs automatically with client. if doing both sides,
                    // client setter will make it look jumpy
                    if (!entry.hasTransmitter)
                    {
                        telepad_tile_entity.addDimensionUpgrade(true);
                        world.notifyBlockUpdate(pos, world.getBlockState(pos), getDefaultState(), 3);
                        entry.hasTransmitter = true;
                        heldItem.shrink(player.isCreative() ? 0 : 1);
                        WorldDataHandler.get(world).updateEntry(entry);
                    }
                }
                // check for server only. syncs automatically with client. if doing both sides,
                // client setter will make it look jumpy
                if (item.equals(ObjectHolders.TOGGLER))
                {
                    if (!telepad_tile_entity.hasRedstoneUpgrade())
                    {
                        telepad_tile_entity.addRedstoneUpgrade();
                        world.notifyBlockUpdate(pos, world.getBlockState(pos), getDefaultState(), 3);
                        this.neighborChanged(state, world, pos, state.getBlock(), null, false);
                        heldItem.shrink(player.isCreative() ? 0 : 1);
                    }
                }

                if (item.equals(ObjectHolders.CREATIVE_ROD_PUBLIC))
                {
                    telepad_tile_entity.toggleAcces();
                    world.notifyBlockUpdate(pos, world.getBlockState(pos), getDefaultState(), 3);
                    entry.setPublic(telepad_tile_entity.isPublic());
                    player.sendMessage(
                            new StringTextComponent(text_public_rod + (telepad_tile_entity.isPublic() ? text_public_rod_public : text_public_rod_private)));
                    WorldDataHandler.get(world).updateEntry(entry);

                }
                // check server sideo nly, so server side config is read
                if (item.equals(ObjectHolders.CREATIVE_ROD))
                {
                    telepad_tile_entity.rotateCoordinateHandlerIndex();

                    int index = telepad_tile_entity.getCoordinateHandlerIndex();
                    if (index > -1)
                    {
                        String[] tpl = ConfigData.tp_locations;
                        CoordinateHandler ch = new CoordinateHandler(tpl[index]);
                        String name = ch.getName();

                        player.sendMessage(new StringTextComponent(text__cycle_rod + name));

                        world.notifyBlockUpdate(pos, world.getBlockState(pos), getDefaultState(), 3);
                        this.neighborChanged(state, world, pos, state.getBlock(), null, false);
                    }
                    else
                        player.sendMessage(new StringTextComponent(text__cycle_rod_normal));

                }

                if (item instanceof DyeItem)
                {
                    DyeColor edc = DyeColor.getColor(heldItem);

                    float red = edc.getColorComponentValues()[0];
                    float green = edc.getColorComponentValues()[1];
                    float blue = edc.getColorComponentValues()[2];

                    int color = (int) (red * 255f);
                    color = (color << 8) + (int) (green * 255f);
                    color = (color << 8) + (int) (blue * 255f);

                    if (telepad_tile_entity.getColorFeet() == TileEntityTelepad.COLOR_FEET_BASE)
                        telepad_tile_entity.setFeetColor(color);
                    else
                        if (telepad_tile_entity.getColorArrow() == TileEntityTelepad.COLOR_ARROW_BASE)
                            telepad_tile_entity.setArrowColor(color);
                    telepad_tile_entity.markDirty();
                    world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);

                    if (!player.isCreative())
                        heldItem.shrink(1);
                }

                if (item.equals(Items.WATER_BUCKET))
                {
                    boolean hasWashed = false;
                    if (telepad_tile_entity.getColorFeet() != TileEntityTelepad.COLOR_FEET_BASE)
                    {
                        wash(telepad_tile_entity.getColorFeet(), world, pos);
                        telepad_tile_entity.setFeetColor(TileEntityTelepad.COLOR_FEET_BASE);
                        hasWashed = true;
                    }
                    if (telepad_tile_entity.getColorArrow() != TileEntityTelepad.COLOR_ARROW_BASE)
                    {
                        wash(telepad_tile_entity.getColorArrow(), world, pos);
                        telepad_tile_entity.setArrowColor(TileEntityTelepad.COLOR_ARROW_BASE);
                        hasWashed = true;
                    }
                    if (!player.isCreative() && hasWashed)
                    {
                        world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1, 1, true);
                        player.setHeldItem(hand, heldItem.getContainerItem());
                    }

                    return true;
                }
            }
        }
        else
        {
            if (player.isSneaking())
            {
                if (world.getTileEntity(pos) instanceof TileEntityTelepad)
                {
                    // check if the player has this pad
                    TelepadEntry entry = WorldDataHandler.get(world).getEntryForLocation(pos, world.dimension.getType().getId());

                    if (entry != null && Hand.MAIN_HAND.equals(hand))
                    {
                        // if the player cannot use this pad (is not registered to it), then add it
                        if (!entry.canUse(player.getUniqueID()))
                        {
                            Style style = new Style().setColor(TextFormatting.GREEN);
                            ITextComponent text = new TranslationTextComponent(cycle_add_success).setStyle(style);
                            ITextComponent name = new StringTextComponent(entry.entryName).setStyle(style);

                            player.sendMessage(new StringTextComponent(text.getFormattedText() + name.getFormattedText()));

                            entry.addUser(player.getUniqueID());
                            return true;
                        }

                        // if the player is not registered to it and the pad is not public
                        else
                            if (!entry.isPublic)
                            {
                                Style style = new Style().setColor(TextFormatting.GOLD);
                                ITextComponent text = new TranslationTextComponent(cycle_add_remove).setStyle(style);
                                ITextComponent name = new StringTextComponent(entry.entryName).setStyle(style);

                                player.sendMessage(new StringTextComponent(text.getFormattedText() + name.getFormattedText()));
                                entry.removeUser(player.getUniqueID());
                                return true;
                            }
                            else
                            {
                                player.sendMessage(new TranslationTextComponent(cycle_add_fail).setStyle(new Style().setColor(TextFormatting.RED)));
                                return true;

                            }
                    }
                }
            }
        }

        return true;

    }

    private void wash(int color, World world, BlockPos pos)
    {

        DyeColor edc = DyeColor.WHITE;
        for (DyeColor dye : DyeColor.values())
            if (dye.getColorComponentValues()[0] == (float) ((color & 16711680) >> 16) / 255f
                    && dye.getColorComponentValues()[1] == (float) ((color & 65280) >> 8) / 255f
                    && dye.getColorComponentValues()[2] == (float) ((color & 255) >> 0) / 255f)
                edc = dye;

        ItemStack stack = new ItemStack(DyeItem.getItem(edc), 1);

        if (!world.isRemote)
            world.addEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack));
    }

    ///////////////////////////// REDSTONE////////////////////////////////////////
    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving)
    {

        TileEntity te = world.getTileEntity(pos);
        TileEntityTelepad tet = null;
        if (te == null || !(te instanceof TileEntityTelepad))
            return;

        tet = (TileEntityTelepad) te;

        if (!tet.hasRedstoneUpgrade())
            return;

        Direction facesThatCanPower[] = new Direction[] { Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.DOWN };

        boolean isPowered = false;
        for (Direction face : facesThatCanPower)
        {
            if (!world.getBlockState(pos.offset(face)).canProvidePower())
                continue;
            int power = world.getBlockState(pos.offset(face)).getStrongPower(world, pos, face);
            int weakPower = world.getBlockState(pos.offset(face)).getWeakPower(world, pos, face);
            if (power > 0 || weakPower > 0)
            {
                isPowered = true;
                break;
            }
        }

        tet.setPowered(isPowered);
        tet.markDirty();
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);

        WorldDataHandler wdh = WorldDataHandler.get(world);
        TelepadEntry entry = wdh.getEntryForLocation(pos, world.dimension.getType().getId());
        entry.isPowered = isPowered;
        wdh.updateEntry(entry);
        wdh.markDirty();
    }

    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor)
    {

        super.onNeighborChange(state, world, pos, neighbor);
    }

    //////////////// Block Placed///////////////////////////////////

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {

        if (world.isRemote)
            return;

        TileEntity tile_entity = world.getTileEntity(pos);
        TileEntityTelepad tile_entity_telepad = null;
        if (tile_entity == null || !(tile_entity instanceof TileEntityTelepad))
            return;

        tile_entity_telepad = (TileEntityTelepad) tile_entity;

        if (placer instanceof ServerPlayerEntity)
        {
            tile_entity_telepad.setDimension(world.dimension.getType().getId());
            if (stack.hasTag())
            {
                if (stack.getTag().contains("colorFrame"))
                    tile_entity_telepad.setFeetColor(stack.getTag().getInt("colorFrame"));
                if (stack.getTag().contains("colorBase"))
                    tile_entity_telepad.setArrowColor(stack.getTag().getInt("colorBase"));
            }
            tile_entity.markDirty();
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            world.setTileEntity(pos, tile_entity);

            NetworkHandler.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) placer), new CPacketRequestNamingScreen(pos));
        }
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid)
    {

        TileEntity tile_entity = world.getTileEntity(pos);
        TileEntityTelepad tile_entity_telepad = null;
        if (tile_entity == null || !(tile_entity instanceof TileEntityTelepad))
            return false;

        tile_entity_telepad = (TileEntityTelepad) tile_entity;

        WorldDataHandler wdh = WorldDataHandler.get(world);
        TelepadEntry entry = wdh.getEntryForLocation(pos, world.dimension.getType().getId());
        if (entry != null && !world.isRemote)
        {
            entry.isMissingFromLocation = true;
            dropPad(world, tile_entity_telepad, pos);
            if (tile_entity_telepad.hasDimensionUpgrade())
                world.addEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ObjectHolders.TRANSMITTER, 1)));
            if (tile_entity_telepad.hasRedstoneUpgrade())
                world.addEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ObjectHolders.TOGGLER, 1)));

            return world.removeBlock(pos, false);
        }

        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    private void dropPad(World world, TileEntityTelepad telepad, BlockPos pos)
    {

        ItemEntity item_entity = new ItemEntity(EntityType.ITEM, world);
        item_entity.setPosition(pos.getX(), pos.getY(), pos.getZ());

        ItemStack stack = new ItemStack(ObjectHolders.TELEPAD_BLOCK);
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("colorBase", telepad.getColorArrow());
        nbt.putInt("colorFrame", telepad.getColorFeet());
        stack.setTag(nbt);

        item_entity.setItem(stack);
        world.addEntity(item_entity);
    }

    /////////////// inherited methods////////////////////

    @Override
    public boolean canEntityDestroy(BlockState state, IBlockReader world, BlockPos pos, Entity entity)
    {

        if (entity instanceof PlayerEntity)
            return true;
        return false;
    }

    @Override
    public float getExplosionResistance(BlockState state, IWorldReader world, BlockPos pos, Entity exploder, Explosion explosion)
    {

        return Float.MAX_VALUE;
    }

    @Override
    public boolean ticksRandomly(BlockState state)
    {

        return true;
    }

    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random random)
    {

        TileEntity te = world.getTileEntity(pos);

        if (te == null || !(te instanceof TileEntityTelepad))
            return;

        if (((TileEntityTelepad) te).isPowered())
            return;

        int maxParticleCount = (((TileEntityTelepad) te).isStandingOnPlatform()) ? 15 : 1;

        for (int particleCount = 0; particleCount < maxParticleCount; ++particleCount)
        {

            if (((TileEntityTelepad) te).getCoordinateHandlerIndex() > -1)
            {
                for (int i = -2; i <= 2; ++i)
                {
                    for (int j = -2; j <= 2; ++j)
                    {
                        if (i > -2 && i < 2 && j == -1)
                        {
                            j = 2;
                        }

                        if (random.nextInt(4) == 0)
                        {
                            for (int k = 0; k <= 2; ++k)
                            {
                                world.addParticle(ParticleTypes.ENCHANT, (double) pos.getX() + 0.5D, (double) pos.getY() + 1.0D, (double) pos.getZ() + 0.5D,
                                        (double) ((float) i + random.nextFloat()) - 0.5D, (double) ((float) k - random.nextFloat() - .0F),
                                        (double) ((float) j + random.nextFloat()) - 0.5D);
                            }
                        }
                    }
                }
            }

            else
            {
                double posX = pos.getX() + 0.5f;
                double posY = pos.getY() + (random.nextFloat() * 1.5f);
                double posZ = pos.getZ() + 0.5f;
                double velocityX = 0.0D;
                double velocityY = 0.0D;
                double velocityZ = 0.0D;
                int velocityXOffset = (random.nextInt(2) * 2) - 1;
                int velocityZOffset = (random.nextInt(2) * 2) - 1;

                velocityX = (random.nextFloat() - 0.5D) * 0.125D;
                velocityY = (random.nextFloat() - 0.5D) * 0.125D;
                velocityZ = (random.nextFloat() - 0.5D) * 0.125D;
                velocityX = random.nextFloat() * 1.0F * velocityXOffset;
                velocityZ = random.nextFloat() * 1.0F * velocityZOffset;
                world.addParticle(ParticleTypes.PORTAL, posX, posY, posZ, velocityX, velocityY, velocityZ);
            }
        }
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player)
    {

        super.onBlockHarvested(worldIn, pos, state, player);

        if (!worldIn.isRemote())
        {
            WorldDataHandler wdh = WorldDataHandler.get(worldIn);
            TelepadEntry entry = wdh.getEntryForLocation(pos, worldIn.dimension.getType().getId());
            if (entry != null)
                entry.isMissingFromLocation = true;
        }
    }

    @Override
    public void onBlockExploded(BlockState state, World world, BlockPos pos, Explosion explosion)
    {

        super.onBlockExploded(state, world, pos, explosion);

        if (!world.isRemote())
        {
            WorldDataHandler wdh = WorldDataHandler.get(world);
            TelepadEntry entry = wdh.getEntryForLocation(pos, world.dimension.getType().getId());
            if (entry != null)
                entry.isMissingFromLocation = true;
        }
    }
}
