package subaraki.telepads.block;

import java.util.Random;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
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

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class BlockTelepad extends Block implements SimpleWaterloggedBlock {

    protected static final AABB AABB = new AABB(0.0D, 0.0D, 0.0D, 1.0D, 0.20D, 1.0D);
    protected static final VoxelShape VOX = Shapes.create(AABB);

    private static final VoxelShape shape = Shapes.create(new AABB(0.0D, 0D, 0.0D, 1.0D, 0.20D, 1.0D));
    private static final VoxelShape shape_n = Shapes.create(new AABB(0.32D, 0.0D, -.22D, .68D, 0.15D, 0D));
    private static final VoxelShape shape_s = Shapes.create(new AABB(0.32D, 0.0D, 1.00D, .68D, 0.15D, 1.22D));
    private static final VoxelShape shape_e = Shapes.create(new AABB(0D, 0.0D, 0.32D, 1.22D, 0.15D, .68D));
    private static final VoxelShape shape_w = Shapes.create(new AABB(0D, 0.0D, 0.32D, -.22D, 0.15D, .68D));
    private static final VoxelShape SHAPE_VOX = Shapes.or(shape_w, shape_e, shape_s, shape_n, shape);

    private static Properties block_properties = Properties.of(Material.GLASS).strength(5F, Float.MAX_VALUE).sound(SoundType.GLASS)
            .harvestTool(ToolType.PICKAXE).harvestLevel(1).noOcclusion();

    private TranslatableComponent text_public_rod;
    private TranslatableComponent text_public_rod_private;
    private TranslatableComponent text_public_rod_public;

    private TranslatableComponent text__cycle_rod_normal;
    private TranslatableComponent text__cycle_rod;

    private TranslatableComponent cycle_add_success;
    private TranslatableComponent cycle_add_remove;
    private TranslatableComponent cycle_add_fail;

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public BlockTelepad() {

        super(block_properties);
        setRegistryName(Telepads.MODID, "telepad");

        text_public_rod = new TranslatableComponent("block.info.rod");
        text_public_rod_private = new TranslatableComponent("block.info.rod.private");
        text_public_rod_public = new TranslatableComponent("block.info.rod.public");
        text__cycle_rod_normal = new TranslatableComponent("block.info.cycle.normal");
        text__cycle_rod = new TranslatableComponent("block.info.cycle");
        cycle_add_success = new TranslatableComponent("block.info.add.succes");
        cycle_add_remove = new TranslatableComponent("block.info.add.fail");
        cycle_add_fail = new TranslatableComponent("block.info.add.remove");

        this.registerDefaultState(defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(false)));
    }

    @Override
    public int getLightValue(BlockState state, BlockGetter world, BlockPos pos)
    {
    
        return 0;
    }
    
    @Override
    public boolean propagatesSkylightDown(BlockState p_200123_1_, BlockGetter p_200123_2_, BlockPos p_200123_3_)
    {
    
        return false;
    }
    
    ///////////////// waterlogged//////////////

    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos)
    {

        if (stateIn.getValue(WATERLOGGED))
        {
            worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        }

        return stateIn;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {

        builder.add(WATERLOGGED);
    }

    @Override
    public boolean placeLiquid(LevelAccessor worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn)
    {

        return SimpleWaterloggedBlock.super.placeLiquid(worldIn, pos, state, fluidStateIn);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {

        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    ///////////////// rendering//////////////

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {

        return SHAPE_VOX;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {

        return VOX;
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter worldIn, BlockPos pos)
    {

        return SHAPE_VOX;
    }

    @Override
    public RenderShape getRenderShape(BlockState state)
    {

        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos)
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
    public BlockEntity createTileEntity(BlockState state, BlockGetter world)
    {

        return new TileEntityTelepad();
    }

    ////////// Interaction///////

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {

        ItemStack heldItem = player.getItemInHand(hand);

        if (world.isClientSide())
            return InteractionResult.FAIL;

        if (!heldItem.isEmpty())
        {
            Item item = heldItem.getItem();
            BlockEntity tile_entity = world.getBlockEntity(pos);

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

                    Component private_rod = text_public_rod.copy().append(" ").append(text_public_rod_private);
                    Component public_rod = text_public_rod.copy().append(" ").append(text_public_rod_public);

                    Component text = telepad_tile_entity.isPublic() ? public_rod : private_rod;

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
                        CoordinateHandler ch = new CoordinateHandler((ServerLevel) world, tpl[index]);
                        String name = ch.getName();

                        Component msg = text__cycle_rod.copy().append(name);
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
                        world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1, 1, true);
                        player.setItemInHand(hand, heldItem.getContainerItem());
                    }

                    return InteractionResult.SUCCESS;
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

                    if (entry != null && InteractionHand.MAIN_HAND.equals(hand))
                    {
                        // if the player cannot use this pad (is not registered to it), then add it
                        if (!entry.canUse(player.getUUID()))
                        {
                            Style style = Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.GREEN));

                            MutableComponent msg = cycle_add_success.copy().append(entry.entryName);
                            player.sendMessage(msg.setStyle(style), player.getUUID());

                            entry.addUser(player.getUUID());
                            return InteractionResult.SUCCESS;
                        }

                        // if the player is not registered to it and the pad is not public
                        else
                            if (!entry.isPublic)
                            {
                                Style style = Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.GOLD));

                                MutableComponent msg = cycle_add_remove.copy().append(entry.entryName).setStyle(style);
                                player.sendMessage(msg, player.getUUID());
                                entry.removeUser(player.getUUID());
                                return InteractionResult.SUCCESS;
                            }
                            else
                            {
                                MutableComponent msg = cycle_add_fail.copy()
                                        .setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.RED)));
                                player.sendMessage(msg, player.getUUID());
                                return InteractionResult.SUCCESS;

                            }
                    }
                }
            }
        }

        return InteractionResult.SUCCESS;

    }

    private void wash(int color, Level world, BlockPos pos)
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
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving)
    {

        BlockEntity te = world.getBlockEntity(pos);
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
    public void onNeighborChange(BlockState state, LevelReader world, BlockPos pos, BlockPos neighbor)
    {

        super.onNeighborChange(state, world, pos, neighbor);
    }

    //////////////// Block Placed///////////////////////////////////

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {

        if (world.isClientSide)
            return;

        BlockEntity tile_entity = world.getBlockEntity(pos);
        TileEntityTelepad tile_entity_telepad = null;
        if (tile_entity == null || !(tile_entity instanceof TileEntityTelepad))
            return;

        tile_entity_telepad = (TileEntityTelepad) tile_entity;

        if (placer instanceof ServerPlayer)
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

            NetworkHandler.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) placer), new CPacketRequestNamingScreen(pos));
        }
    }

    @Override
    public boolean removedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid)
    {

        BlockEntity tile_entity = world.getBlockEntity(pos);
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

    private void dropPad(Level world, TileEntityTelepad telepad, BlockPos pos)
    {

        ItemEntity item_entity = new ItemEntity(EntityType.ITEM, world);
        item_entity.setPos(pos.getX(), pos.getY(), pos.getZ());

        ItemStack stack = new ItemStack(ObjectHolders.TELEPAD_BLOCK);
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("colorBase", telepad.getColorArrow());
        nbt.putInt("colorFrame", telepad.getColorFeet());
        stack.setTag(nbt);

        item_entity.setItem(stack);
        world.addFreshEntity(item_entity);
    }

    /////////////// inherited methods////////////////////

    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter world, BlockPos pos, Entity entity)
    {

        if (entity instanceof Player)
            return true;
        return false;
    }

    @Override
    public float getExplosionResistance(BlockState state, BlockGetter world, BlockPos pos, Explosion explosion)
    {

        return Float.MAX_VALUE;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state)
    {

        return true;
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, Random random)
    {

        BlockEntity te = world.getBlockEntity(pos);

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
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player)
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
    public void onBlockExploded(BlockState state, Level world, BlockPos pos, Explosion explosion)
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
