package subaraki.telepads.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.PacketDistributor;
import subaraki.telepads.handler.ConfigData;
import subaraki.telepads.handler.CoordinateHandler;
import subaraki.telepads.handler.WorldDataHandler;
import subaraki.telepads.network.NetworkHandler;
import subaraki.telepads.network.client.CPacketRequestNamingScreen;
import subaraki.telepads.registry.TelepadBlockEntities;
import subaraki.telepads.registry.TelepadBlocks;
import subaraki.telepads.registry.TelepadItems;
import subaraki.telepads.tileentity.TileEntityTelepad;
import subaraki.telepads.utility.TelepadEntry;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockTelepad extends BaseEntityBlock implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final AABB AABB = new AABB(0.0D, 0.0D, 0.0D, 1.0D, 0.20D, 1.0D);
    protected static final VoxelShape VOX = Shapes.create(AABB);
    private static final VoxelShape shape = Shapes.create(new AABB(0.0D, 0D, 0.0D, 1.0D, 0.20D, 1.0D));
    private static final VoxelShape shape_n = Shapes.create(new AABB(0.32D, 0.0D, -.22D, .68D, 0.15D, 0D));
    private static final VoxelShape shape_s = Shapes.create(new AABB(0.32D, 0.0D, 1.00D, .68D, 0.15D, 1.22D));
    private static final VoxelShape shape_e = Shapes.create(new AABB(0D, 0.0D, 0.32D, 1.22D, 0.15D, .68D));
    private static final VoxelShape shape_w = Shapes.create(new AABB(0D, 0.0D, 0.32D, -.22D, 0.15D, .68D));
    private static final VoxelShape SHAPE_VOX = Shapes.or(shape_w, shape_e, shape_s, shape_n, shape);
    private static final Properties block_properties = Properties.of(Material.GLASS).strength(5F, Float.MAX_VALUE).sound(SoundType.GLASS).requiresCorrectToolForDrops().noOcclusion();
    private final TranslatableComponent text_public_rod;
    private final TranslatableComponent text_public_rod_private;
    private final TranslatableComponent text_public_rod_public;
    private final TranslatableComponent text__cycle_rod_normal;
    private final TranslatableComponent text__cycle_rod;
    private final TranslatableComponent cycle_add_success;
    private final TranslatableComponent cycle_add_remove;
    private final TranslatableComponent cycle_add_fail;

    public BlockTelepad() {

        super(block_properties);

        text_public_rod = new TranslatableComponent("block.info.rod");
        text_public_rod_private = new TranslatableComponent("block.info.rod.private");
        text_public_rod_public = new TranslatableComponent("block.info.rod.public");
        text__cycle_rod_normal = new TranslatableComponent("block.info.cycle.normal");
        text__cycle_rod = new TranslatableComponent("block.info.cycle");
        cycle_add_success = new TranslatableComponent("block.info.add.succes");
        cycle_add_remove = new TranslatableComponent("block.info.add.fail");
        cycle_add_fail = new TranslatableComponent("block.info.add.remove");

        this.registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        return 0;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState p_200123_1_, BlockGetter p_200123_2_, BlockPos p_200123_3_) {

        return false;
    }

    ///////////////// waterlogged//////////////

    @Override
    @Deprecated
    public BlockState updateShape(BlockState state, Direction direciton, BlockState toState, LevelAccessor levelAccessor, BlockPos pos, BlockPos newPos) {

        if (asBlock().defaultBlockState().getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }

        return asBlock().defaultBlockState();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {

        builder.add(WATERLOGGED);
    }

    @Override
    public boolean placeLiquid(LevelAccessor worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {

        return SimpleWaterloggedBlock.super.placeLiquid(worldIn, pos, state, fluidStateIn);
    }

    @Override
    @Deprecated
    public FluidState getFluidState(BlockState state) {

        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    ///////////////// rendering//////////////

    @Override
    @Deprecated
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {

        return SHAPE_VOX;
    }

    @Override
    @Deprecated
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {

        return VOX;
    }

    @Override
    @Deprecated
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter worldIn, BlockPos pos) {

        return SHAPE_VOX;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {

        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    @Deprecated
    public int getLightBlock(BlockState p_60585_, BlockGetter p_60586_, BlockPos p_60587_) {
        return 1;
    }

    /////////////// TE Stuff//////////////////////
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityTelepad(pos, state);
    }

    ////////// Interaction///////


    @Override
    @Deprecated
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {

        if (world.isClientSide())
            return InteractionResult.FAIL;

        ItemStack heldStack = player.getItemInHand(hand);

        if (!heldStack.isEmpty()) {
            Item heldItem = heldStack.getItem();

            if (world.getBlockEntity(pos) instanceof TileEntityTelepad tileEntityTelepad) {

                TelepadEntry entry = WorldDataHandler.get(world).getEntryForLocation(pos, world.dimension());
                if (entry != null) {
                    if (heldItem.equals(TelepadItems.TRANSMITTER.get())) {
                        // check for server only. syncs automatically with client. if doing both sides,
                        // client setter will make it look jumpy
                        if (!entry.hasTransmitter) {
                            tileEntityTelepad.addDimensionUpgrade(true);
                            world.sendBlockUpdated(pos, world.getBlockState(pos), defaultBlockState(), 3);
                            entry.hasTransmitter = true;
                            heldStack.shrink(player.isCreative() ? 0 : 1);
                            WorldDataHandler.get(world).updateEntry(entry);
                        }
                    }

                    if (heldItem.equals(TelepadItems.CREATIVE_ROD_PUBLIC.get())) {
                        tileEntityTelepad.toggleAcces();
                        world.sendBlockUpdated(pos, world.getBlockState(pos), defaultBlockState(), 3);
                        entry.setPublic(tileEntityTelepad.isPublic());

                        Component private_rod = text_public_rod.copy().append(" ").append(text_public_rod_private);
                        Component public_rod = text_public_rod.copy().append(" ").append(text_public_rod_public);

                        Component text = tileEntityTelepad.isPublic() ? public_rod : private_rod;

                        player.sendMessage(text, player.getUUID());
                        WorldDataHandler.get(world).updateEntry(entry);

                    }
                }

                // check for server only. syncs automatically with client. if doing both sides,
                // client setter will make it look jumpy
                if (heldItem.equals(TelepadItems.TOGGLER.get())) {
                    if (!tileEntityTelepad.hasRedstoneUpgrade()) {
                        tileEntityTelepad.addRedstoneUpgrade();
                        world.sendBlockUpdated(pos, world.getBlockState(pos), defaultBlockState(), 3);
                        this.neighborChanged(asBlock().defaultBlockState(), world, pos, this, pos, false);
                        heldStack.shrink(player.isCreative() ? 0 : 1);
                    }
                }

                // check server sideo nly, so server side config is read
                if (heldItem.equals(TelepadItems.CREATIVE_ROD.get())) {
                    tileEntityTelepad.rotateCoordinateHandlerIndex();

                    int index = tileEntityTelepad.getCoordinateHandlerIndex();
                    if (index > -1) {
                        String[] tpl = ConfigData.tp_locations;
                        CoordinateHandler ch = new CoordinateHandler((ServerLevel) world, tpl[index]);
                        String name = ch.getName();

                        Component msg = text__cycle_rod.copy().append(name);
                        player.sendMessage(msg, player.getUUID());

                        world.sendBlockUpdated(pos, world.getBlockState(pos), defaultBlockState(), 3);
                        this.neighborChanged(asBlock().defaultBlockState(), world, pos, this, pos, false);
                    } else
                        player.sendMessage(text__cycle_rod_normal, player.getUUID());

                }

                if (heldItem instanceof DyeItem) {
                    DyeColor edc = DyeColor.getColor(heldStack);

                    if (edc != null) {
                        float red = edc.getTextureDiffuseColors()[0];
                        float green = edc.getTextureDiffuseColors()[1];
                        float blue = edc.getTextureDiffuseColors()[2];

                        int color = (int) (red * 255f);
                        color = (color << 8) + (int) (green * 255f);
                        color = (color << 8) + (int) (blue * 255f);

                        if (tileEntityTelepad.getColorFeet() == TileEntityTelepad.COLOR_FEET_BASE)
                            tileEntityTelepad.setFeetColor(color);
                        else if (tileEntityTelepad.getColorArrow() == TileEntityTelepad.COLOR_ARROW_BASE)
                            tileEntityTelepad.setArrowColor(color);
                        tileEntityTelepad.setChanged();
                        world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 3);

                        if (!player.isCreative())
                            heldStack.shrink(1);
                    }

                }

                if (heldItem.equals(Items.WATER_BUCKET)) {
                    boolean hasWashed = false;
                    if (tileEntityTelepad.getColorFeet() != TileEntityTelepad.COLOR_FEET_BASE) {
                        wash(tileEntityTelepad.getColorFeet(), world, pos);
                        tileEntityTelepad.setFeetColor(TileEntityTelepad.COLOR_FEET_BASE);
                        hasWashed = true;
                    }
                    if (tileEntityTelepad.getColorArrow() != TileEntityTelepad.COLOR_ARROW_BASE) {
                        wash(tileEntityTelepad.getColorArrow(), world, pos);
                        tileEntityTelepad.setArrowColor(TileEntityTelepad.COLOR_ARROW_BASE);
                        hasWashed = true;
                    }
                    if (!player.isCreative() && hasWashed) {
                        world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1, 1, true);
                        player.setItemInHand(hand, heldStack.getContainerItem());
                    }

                    return InteractionResult.SUCCESS;
                }
            }
        } else {
            if (player.isShiftKeyDown()) {
                if (world.getBlockEntity(pos) instanceof TileEntityTelepad) {
                    // check if the player has this pad
                    TelepadEntry entry = WorldDataHandler.get(world).getEntryForLocation(pos, world.dimension());

                    if (entry != null && InteractionHand.MAIN_HAND.equals(hand)) {
                        // if the player cannot use this pad (is not registered to it), then add it
                        if (!entry.canUse(player.getUUID())) {
                            Style style = Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.GREEN));

                            MutableComponent msg = cycle_add_success.copy().append(entry.entryName);
                            player.sendMessage(msg.setStyle(style), player.getUUID());

                            entry.addUser(player.getUUID());
                            return InteractionResult.SUCCESS;
                        }

                        // if the player is not registered to it and the pad is not public
                        else if (!entry.isPublic) {
                            Style style = Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.GOLD));

                            MutableComponent msg = cycle_add_remove.copy().append(entry.entryName).setStyle(style);
                            player.sendMessage(msg, player.getUUID());
                            entry.removeUser(player.getUUID());
                            return InteractionResult.SUCCESS;
                        } else {
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

    private void wash(int color, Level world, BlockPos pos) {

        DyeColor edc = DyeColor.WHITE;
        for (DyeColor dye : DyeColor.values())
            if (dye.getTextureDiffuseColors()[0] == (float) ((color & 16711680) >> 16) / 255f
                    && dye.getTextureDiffuseColors()[1] == (float) ((color & 65280) >> 8) / 255f
                    && dye.getTextureDiffuseColors()[2] == (float) ((color & 255)) / 255f)
                edc = dye;

        ItemStack stack = new ItemStack(DyeItem.byColor(edc), 1);

        if (!world.isClientSide)
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack));
    }

    ///////////////////////////// REDSTONE////////////////////////////////////////
    @Override
    @Deprecated
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {

        BlockEntity te = world.getBlockEntity(pos);
        if (!(te instanceof TileEntityTelepad tileEntityTelepad))
            return;


        if (!tileEntityTelepad.hasRedstoneUpgrade())
            return;

        Direction[] facesThatCanPower = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.DOWN};

        boolean isPowered = false;
        for (Direction face : facesThatCanPower) {
            if (!world.getBlockState(pos.relative(face)).isSignalSource())
                continue;
            int power = world.getBlockState(pos.relative(face)).getDirectSignal(world, pos, face);
            int weakPower = world.getBlockState(pos.relative(face)).getSignal(world, pos, face);
            if (power > 0 || weakPower > 0) {
                isPowered = true;
                break;
            }
        }

        tileEntityTelepad.setPowered(isPowered);
        tileEntityTelepad.setChanged();
        world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 3);

        WorldDataHandler wdh = WorldDataHandler.get(world);
        TelepadEntry entry = wdh.getEntryForLocation(pos, world.dimension());
        entry.setPowered(isPowered);
        wdh.updateEntry(entry);
        wdh.setDirty();
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader world, BlockPos pos, BlockPos neighbor) {

        super.onNeighborChange(state, world, pos, neighbor);
    }

    //////////////// Block Placed///////////////////////////////////

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {

        if (world.getBlockEntity(pos) instanceof TileEntityTelepad tileEntityTelepad && !world.isClientSide) {
            if (placer instanceof ServerPlayer serverPlayer) {
                tileEntityTelepad.setDimension(world.dimension());
                if (stack.hasTag() && stack.getTag() != null) {
                    if (stack.getTag().contains("colorFrame"))
                        tileEntityTelepad.setFeetColor(stack.getTag().getInt("colorFrame"));
                    if (stack.getTag().contains("colorBase"))
                        tileEntityTelepad.setArrowColor(stack.getTag().getInt("colorBase"));
                }
                tileEntityTelepad.setChanged();
                world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                world.setBlockEntity(tileEntityTelepad);

                NetworkHandler.NETWORK.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new CPacketRequestNamingScreen(pos));
            }
        }
    }


    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {

        if (world.getBlockEntity(pos) instanceof TileEntityTelepad tileEntityTelepad && !world.isClientSide) {
            TelepadEntry entry = WorldDataHandler.get(world).getEntryForLocation(pos, world.dimension());
            if (entry != null) {
                entry.isMissingFromLocation = true;
                dropPad(world, tileEntityTelepad, pos);
                if (tileEntityTelepad.hasDimensionUpgrade())
                    world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(TelepadItems.TRANSMITTER.get(), 1)));
                if (tileEntityTelepad.hasRedstoneUpgrade())
                    world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(TelepadItems.TOGGLER.get(), 1)));

                return world.removeBlock(pos, false);
            }
        }

        return super.onDestroyedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    private void dropPad(Level world, TileEntityTelepad telepad, BlockPos pos) {

        ItemEntity itemEntity = new ItemEntity(EntityType.ITEM, world);
        itemEntity.setPos(pos.getX(), pos.getY(), pos.getZ());

        ItemStack stack = new ItemStack(TelepadBlocks.TELEPAD_BLOCK.get());
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("colorBase", telepad.getColorArrow());
        nbt.putInt("colorFrame", telepad.getColorFeet());
        stack.setTag(nbt);

        itemEntity.setItem(stack);
        world.addFreshEntity(itemEntity);
    }

    /////////////// inherited methods////////////////////

    @Override
    public boolean canEntityDestroy(BlockState state, BlockGetter world, BlockPos pos, Entity entity) {

        return entity instanceof Player;
    }

    @Override
    public float getExplosionResistance(BlockState state, BlockGetter world, BlockPos pos, Explosion explosion) {

        return Float.MAX_VALUE;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {

        return true;
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, Random random) {

        if (world.getBlockEntity(pos) instanceof TileEntityTelepad tileEntityTelepad && !tileEntityTelepad.isPowered()) {
            int maxParticleCount = tileEntityTelepad.isStandingOnPlatform() ? 15 : 1;

            for (int particleCount = 0; particleCount < maxParticleCount; ++particleCount) {

                if (tileEntityTelepad.getCoordinateHandlerIndex() > -1) {
                    for (int i = -2; i <= 2; ++i) {
                        for (int j = -2; j <= 2; ++j) {
                            if (i > -2 && i < 2 && j == -1) {
                                j = 2;
                            }

                            if (random.nextInt(4) == 0) {
                                for (int k = 0; k <= 2; ++k) {
                                    world.addParticle(ParticleTypes.ENCHANT, (double) pos.getX() + 0.5D, (double) pos.getY() + 1.0D, (double) pos.getZ() + 0.5D,
                                            ((double) i + random.nextDouble()) - 0.5D, ((double) k - random.nextDouble() - 0D),
                                            ((double) j + random.nextDouble()) - 0.5D);
                                }
                            }
                        }
                    }
                } else {
                    double posX = pos.getX() + 0.5f;
                    double posY = pos.getY() + (random.nextFloat() * 1.5f);
                    double posZ = pos.getZ() + 0.5f;
                    int velocityXOffset = (random.nextInt(2) * 2) - 1;
                    int velocityZOffset = (random.nextInt(2) * 2) - 1;

                    double velocityY = (random.nextFloat() - 0.5D) * 0.125D;
                    double velocityX = random.nextFloat() * 1.0F * velocityXOffset;
                    double velocityZ = random.nextFloat() * 1.0F * velocityZOffset;
                    world.addParticle(ParticleTypes.PORTAL, posX, posY, posZ, velocityX, velocityY, velocityZ);
                }
            }
        }
    }

    @Override
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {

        super.playerWillDestroy(worldIn, pos, state, player);

        if (!worldIn.isClientSide()) {
            WorldDataHandler wdh = WorldDataHandler.get(worldIn);
            TelepadEntry entry = wdh.getEntryForLocation(pos, worldIn.dimension());
            if (entry != null)
                entry.isMissingFromLocation = true;
        }
    }

    @Override
    public void onBlockExploded(BlockState state, Level world, BlockPos pos, Explosion explosion) {

        super.onBlockExploded(state, world, pos, explosion);

        if (!world.isClientSide()) {
            WorldDataHandler wdh = WorldDataHandler.get(world);
            TelepadEntry entry = wdh.getEntryForLocation(pos, world.dimension());
            if (entry != null)
                entry.isMissingFromLocation = true;
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> be) {
        if (!level.isClientSide)
            return be == TelepadBlockEntities.TILE_ENTITY_TELEPAD.get() ? (BlockEntityTicker<T>) TileEntityTelepad.TICKER : super.getTicker(level, state, be);
        return super.getTicker(level, state, be);
    }
}
