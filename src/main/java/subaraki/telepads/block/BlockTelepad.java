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
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
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
import net.minecraft.util.ActionResultType;
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
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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

    private static Properties block_properties = Properties.of(Material.GLASS).strength(5F, Float.MAX_VALUE).sound(SoundType.GLASS)
            .harvestTool(ToolType.PICKAXE).harvestLevel(1).noOcclusion();

    private TranslationTextComponent text_public_rod;
    private TranslationTextComponent text_public_rod_private;
    private TranslationTextComponent text_public_rod_public;

    private TranslationTextComponent text__cycle_rod_normal;
    private TranslationTextComponent text__cycle_rod;

    private TranslationTextComponent cycle_add_success;
    private TranslationTextComponent cycle_add_remove;
    private TranslationTextComponent cycle_add_fail;

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public BlockTelepad() {

        super(block_properties);
        setRegistryName(Telepads.MODID, "telepad");

        text_public_rod = new TranslationTextComponent("block.info.rod");
        text_public_rod_private = new TranslationTextComponent("block.info.rod.private");
        text_public_rod_public = new TranslationTextComponent("block.info.rod.public");
        text__cycle_rod_normal = new TranslationTextComponent("block.info.cycle.normal");
        text__cycle_rod = new TranslationTextComponent("block.info.cycle");
        cycle_add_success = new TranslationTextComponent("block.info.add.succes");
        cycle_add_remove = new TranslationTextComponent("block.info.add.fail");
        cycle_add_fail = new TranslationTextComponent("block.info.add.remove");

        this.registerDefaultState(defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(false)));
    }

    ///////////////// waterlogged//////////////

    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {

        if (stateIn.getValue(WATERLOGGED))
        {
            worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        }

        return stateIn;
    }

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {

        builder.add(WATERLOGGED);
    }

    @Override
    public boolean placeLiquid(IWorld worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn)
    {

        return IWaterLoggable.super.placeLiquid(worldIn, pos, state, fluidStateIn);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {

        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
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
    public VoxelShape getOcclusionShape(BlockState state, IBlockReader worldIn, BlockPos pos)
    {

        return SHAPE_VOX;
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state)
    {

        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public int getLightBlock(BlockState state, IBlockReader worldIn, BlockPos pos)
    {

        return 1;
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
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {

        ItemStack heldItem = player.getItemInHand(hand);

        if (world.isClientSide())
            return ActionResultType.FAIL;

        if (!heldItem.isEmpty())
        {
            Item item = heldItem.getItem();
            TileEntity tile_entity = world.getBlockEntity(pos);

            if (tile_entity instanceof TileEntityTelepad)
            {
                TileEntityTelepad telepad_tile_entity = (TileEntityTelepad) tile_entity;

                TelepadEntry entry = WorldDataHandler.get(world).getEntryForLocation(pos, world.dimension());

                if (item.equals(ObjectHolders.TRANSMITTER))
                {
                    // check for server only. syncs automatically with client. if doing both sides,
                    // client setter will make it look jumpy
                    if (!entry.hasTransmitter)
                    {
                        telepad_tile_entity.addDimensionUpgrade(true);
                        world.sendBlockUpdated(pos, world.getBlockState(pos), defaultBlockState(), 3);
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
                        world.sendBlockUpdated(pos, world.getBlockState(pos), defaultBlockState(), 3);
                        this.neighborChanged(state, world, pos, state.getBlock(), null, false);
                        heldItem.shrink(player.isCreative() ? 0 : 1);
                    }
                }

                if (item.equals(ObjectHolders.CREATIVE_ROD_PUBLIC))
                {
                    telepad_tile_entity.toggleAcces();
                    world.sendBlockUpdated(pos, world.getBlockState(pos), defaultBlockState(), 3);
                    entry.setPublic(telepad_tile_entity.isPublic());

                    ITextComponent private_rod = text_public_rod.copy().append(" ").append(text_public_rod_private);
                    ITextComponent public_rod = text_public_rod.copy().append(" ").append(text_public_rod_public);

                    ITextComponent text = telepad_tile_entity.isPublic() ? public_rod : private_rod;

                    player.sendMessage(text, player.getUUID());
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
                        CoordinateHandler ch = new CoordinateHandler((ServerWorld) world, tpl[index]);
                        String name = ch.getName();

                        ITextComponent msg = text__cycle_rod.copy().append(name);
                        player.sendMessage(msg, player.getUUID());

                        world.sendBlockUpdated(pos, world.getBlockState(pos), defaultBlockState(), 3);
                        this.neighborChanged(state, world, pos, state.getBlock(), null, false);
                    }
                    else
                        player.sendMessage(text__cycle_rod_normal, player.getUUID());

                }

                if (item instanceof DyeItem)
                {
                    DyeColor edc = DyeColor.getColor(heldItem);

                    float red = edc.getTextureDiffuseColors()[0];
                    float green = edc.getTextureDiffuseColors()[1];
                    float blue = edc.getTextureDiffuseColors()[2];

                    int color = (int) (red * 255f);
                    color = (color << 8) + (int) (green * 255f);
                    color = (color << 8) + (int) (blue * 255f);

                    if (telepad_tile_entity.getColorFeet() == TileEntityTelepad.COLOR_FEET_BASE)
                        telepad_tile_entity.setFeetColor(color);
                    else
                        if (telepad_tile_entity.getColorArrow() == TileEntityTelepad.COLOR_ARROW_BASE)
                            telepad_tile_entity.setArrowColor(color);
                    telepad_tile_entity.setChanged();
                    world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 3);

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
                        world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BUCKET_EMPTY, SoundCategory.BLOCKS, 1, 1, true);
                        player.setItemInHand(hand, heldItem.getContainerItem());
                    }

                    return ActionResultType.SUCCESS;
                }
            }
        }
        else
        {
            if (player.isShiftKeyDown())
            {
                if (world.getBlockEntity(pos) instanceof TileEntityTelepad)
                {
                    // check if the player has this pad
                    TelepadEntry entry = WorldDataHandler.get(world).getEntryForLocation(pos, world.dimension());

                    if (entry != null && Hand.MAIN_HAND.equals(hand))
                    {
                        // if the player cannot use this pad (is not registered to it), then add it
                        if (!entry.canUse(player.getUUID()))
                        {
                            Style style = Style.EMPTY.withColor(Color.fromLegacyFormat(TextFormatting.GREEN));

                            IFormattableTextComponent msg = cycle_add_success.copy().append(entry.entryName);
                            player.sendMessage(msg.setStyle(style), player.getUUID());

                            entry.addUser(player.getUUID());
                            return ActionResultType.SUCCESS;
                        }

                        // if the player is not registered to it and the pad is not public
                        else
                            if (!entry.isPublic)
                            {
                                Style style = Style.EMPTY.withColor(Color.fromLegacyFormat(TextFormatting.GOLD));

                                IFormattableTextComponent msg = cycle_add_remove.copy().append(entry.entryName).setStyle(style);
                                player.sendMessage(msg, player.getUUID());
                                entry.removeUser(player.getUUID());
                                return ActionResultType.SUCCESS;
                            }
                            else
                            {
                                IFormattableTextComponent msg = cycle_add_fail.copy()
                                        .setStyle(Style.EMPTY.withColor(Color.fromLegacyFormat(TextFormatting.RED)));
                                player.sendMessage(msg, player.getUUID());
                                return ActionResultType.SUCCESS;

                            }
                    }
                }
            }
        }

        return ActionResultType.SUCCESS;

    }

    private void wash(int color, World world, BlockPos pos)
    {

        DyeColor edc = DyeColor.WHITE;
        for (DyeColor dye : DyeColor.values())
            if (dye.getTextureDiffuseColors()[0] == (float) ((color & 16711680) >> 16) / 255f
                    && dye.getTextureDiffuseColors()[1] == (float) ((color & 65280) >> 8) / 255f
                    && dye.getTextureDiffuseColors()[2] == (float) ((color & 255) >> 0) / 255f)
                edc = dye;

        ItemStack stack = new ItemStack(DyeItem.byColor(edc), 1);

        if (!world.isClientSide)
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack));
    }

    ///////////////////////////// REDSTONE////////////////////////////////////////
    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving)
    {

        TileEntity te = world.getBlockEntity(pos);
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
            if (!world.getBlockState(pos.relative(face)).isSignalSource())
                continue;
            int power = world.getBlockState(pos.relative(face)).getDirectSignal(world, pos, face);
            int weakPower = world.getBlockState(pos.relative(face)).getSignal(world, pos, face);
            if (power > 0 || weakPower > 0)
            {
                isPowered = true;
                break;
            }
        }

        tet.setPowered(isPowered);
        tet.setChanged();
        world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 3);

        WorldDataHandler wdh = WorldDataHandler.get(world);
        TelepadEntry entry = wdh.getEntryForLocation(pos, world.dimension());
        entry.isPowered = isPowered;
        wdh.updateEntry(entry);
        wdh.setDirty();
    }

    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor)
    {

        super.onNeighborChange(state, world, pos, neighbor);
    }

    //////////////// Block Placed///////////////////////////////////

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {

        if (world.isClientSide)
            return;

        TileEntity tile_entity = world.getBlockEntity(pos);
        TileEntityTelepad tile_entity_telepad = null;
        if (tile_entity == null || !(tile_entity instanceof TileEntityTelepad))
            return;

        tile_entity_telepad = (TileEntityTelepad) tile_entity;

        if (placer instanceof ServerPlayerEntity)
        {
            tile_entity_telepad.setDimension(world.dimension());
            if (stack.hasTag())
            {
                if (stack.getTag().contains("colorFrame"))
                    tile_entity_telepad.setFeetColor(stack.getTag().getInt("colorFrame"));
                if (stack.getTag().contains("colorBase"))
                    tile_entity_telepad.setArrowColor(stack.getTag().getInt("colorBase"));
            }
            tile_entity.setChanged();
            world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            world.setBlockEntity(pos, tile_entity);

            NetworkHandler.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) placer), new CPacketRequestNamingScreen(pos));
        }
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid)
    {

        TileEntity tile_entity = world.getBlockEntity(pos);
        TileEntityTelepad tile_entity_telepad = null;
        if (tile_entity == null || !(tile_entity instanceof TileEntityTelepad))
            return false;

        tile_entity_telepad = (TileEntityTelepad) tile_entity;

        if (world.isClientSide)
            return false;

        WorldDataHandler wdh = WorldDataHandler.get(world);
        TelepadEntry entry = wdh.getEntryForLocation(pos, world.dimension());
        if (entry != null)
        {
            entry.isMissingFromLocation = true;
            dropPad(world, tile_entity_telepad, pos);
            if (tile_entity_telepad.hasDimensionUpgrade())
                world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ObjectHolders.TRANSMITTER, 1)));
            if (tile_entity_telepad.hasRedstoneUpgrade())
                world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ObjectHolders.TOGGLER, 1)));

            return world.removeBlock(pos, false);
        }

        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    private void dropPad(World world, TileEntityTelepad telepad, BlockPos pos)
    {

        ItemEntity item_entity = new ItemEntity(EntityType.ITEM, world);
        item_entity.setPos(pos.getX(), pos.getY(), pos.getZ());

        ItemStack stack = new ItemStack(ObjectHolders.TELEPAD_BLOCK);
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("colorBase", telepad.getColorArrow());
        nbt.putInt("colorFrame", telepad.getColorFeet());
        stack.setTag(nbt);

        item_entity.setItem(stack);
        world.addFreshEntity(item_entity);
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
    public float getExplosionResistance(BlockState state, IBlockReader world, BlockPos pos, Explosion explosion)
    {

        return Float.MAX_VALUE;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state)
    {

        return true;
    }

    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random random)
    {

        TileEntity te = world.getBlockEntity(pos);

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
    public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player)
    {

        super.playerWillDestroy(worldIn, pos, state, player);

        if (!worldIn.isClientSide())
        {
            WorldDataHandler wdh = WorldDataHandler.get(worldIn);
            TelepadEntry entry = wdh.getEntryForLocation(pos, worldIn.dimension());
            if (entry != null)
                entry.isMissingFromLocation = true;
        }
    }

    @Override
    public void onBlockExploded(BlockState state, World world, BlockPos pos, Explosion explosion)
    {

        super.onBlockExploded(state, world, pos, explosion);

        if (!world.isClientSide())
        {
            WorldDataHandler wdh = WorldDataHandler.get(world);
            TelepadEntry entry = wdh.getEntryForLocation(pos, world.dimension());
            if (entry != null)
                entry.isMissingFromLocation = true;
        }
    }
}
