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
import java.util.Arrays;
import java.util.Random;

public class BlockTelepad extends BaseEntityBlock implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final AABB AABB = new AABB(0.0D, 0.0D, 0.0D, 1.0D, 0.20D, 1.0D);
    protected static final VoxelShape VOX = Shapes.create(AABB);
    private static final VoxelShape shape = Shapes.create(new AABB(0.0D, 0D, 0.0D, 1.0D, 0.20D, 1.0D));
    private static final VoxelShape shapeNorth = Shapes.create(new AABB(0.32D, 0.0D, -.22D, .68D, 0.15D, 0D));
    private static final VoxelShape shapeSouth = Shapes.create(new AABB(0.32D, 0.0D, 1.00D, .68D, 0.15D, 1.22D));
    private static final VoxelShape shapeEast = Shapes.create(new AABB(0D, 0.0D, 0.32D, 1.22D, 0.15D, .68D));
    private static final VoxelShape shapeWest = Shapes.create(new AABB(0D, 0.0D, 0.32D, -.22D, 0.15D, .68D));
    private static final VoxelShape FULLSHAPE = Shapes.or(shapeWest, shapeEast, shapeSouth, shapeNorth, shape);
    private static final Properties PROPERTIES = Properties.of(Material.GLASS).strength(5F, Float.MAX_VALUE).sound(SoundType.GLASS).requiresCorrectToolForDrops().noOcclusion();
    private final TranslatableComponent textPublicRod;
    private final TranslatableComponent textPublicRodPrivate;
    private final TranslatableComponent textPublicRodPublic;
    private final TranslatableComponent textCycleRodNormal;
    private final TranslatableComponent textCycleRod;
    private final TranslatableComponent cycleAddSuccess;
    private final TranslatableComponent cycleAddRemove;
    private final TranslatableComponent cycleAddFail;

    public BlockTelepad() {

        super(PROPERTIES);

        textPublicRod = new TranslatableComponent("block.info.rod");
        textPublicRodPrivate = new TranslatableComponent("block.info.rod.private");
        textPublicRodPublic = new TranslatableComponent("block.info.rod.public");
        textCycleRodNormal = new TranslatableComponent("block.info.cycle.normal");
        textCycleRod = new TranslatableComponent("block.info.cycle");
        cycleAddSuccess = new TranslatableComponent("block.info.add.succes");
        cycleAddRemove = new TranslatableComponent("block.info.add.fail");
        cycleAddFail = new TranslatableComponent("block.info.add.remove");

        this.registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        return 0;
    }

    ///////////////// waterlogged//////////////

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter getter, BlockPos pos) {

        return false;
    }

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

    ///////////////// rendering//////////////

    @Override
    @Deprecated
    public FluidState getFluidState(BlockState state) {

        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    @Deprecated
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {

        return FULLSHAPE;
    }

    @Override
    @Deprecated
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {

        return VOX;
    }

    @Override
    @Deprecated
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter worldIn, BlockPos pos) {

        return FULLSHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {

        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    @Deprecated
    public int getLightBlock(BlockState state, BlockGetter getter, BlockPos blockPos) {
        return 1;
    }

    ////////// Interaction///////

    /////////////// TE Stuff//////////////////////
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityTelepad(pos, state);
    }

    @Override
    @Deprecated
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {

        if (level.isClientSide())
            return InteractionResult.PASS;

        ItemStack heldStack = player.getItemInHand(hand);

        if (!heldStack.isEmpty()) {
            Item heldItem = heldStack.getItem();

            if (level.getBlockEntity(pos) instanceof TileEntityTelepad telepad) {

                if (heldItem.equals(TelepadItems.TRANSMITTER.get())) {
                    if (TelepadBlockInteraction.interact(TelepadBlockInteraction.Action.TRANSMITTER, level, pos, telepad, state, this))
                        heldStack.shrink(player.isCreative() ? 0 : 1);
                }

                if (heldItem.equals(TelepadItems.PUBLIC_TOGGLE_ROD.get())) {
                    if (TelepadBlockInteraction.interact(TelepadBlockInteraction.Action.TOGGLE_ACCESS, level, pos, telepad, state, this)) {
                        Component private_rod = textPublicRod.copy().append(" ").append(textPublicRodPrivate);
                        Component public_rod = textPublicRod.copy().append(" ").append(textPublicRodPublic);
                        Component text = telepad.isPublic() ? public_rod : private_rod;
                        player.sendMessage(text, player.getUUID());
                    }
                }

                if (heldItem.equals(TelepadItems.TOGGLER.get())) {
                    if (TelepadBlockInteraction.interact(TelepadBlockInteraction.Action.TOGGLER_UPGRADE, level, pos, telepad, state, this)) {
                        heldStack.shrink(player.isCreative() ? 0 : 1);
                    }
                }

                // check server side only, so server side config is read
                if (heldItem.equals(TelepadItems.CYCLE_ROD.get())) {
                    if (TelepadBlockInteraction.interact(TelepadBlockInteraction.Action.CYCLE, level, pos, telepad, state, this)) {
                        int index = telepad.getCoordinateHandlerIndex();
                        if (index > -1) {
                            CoordinateHandler handler = new CoordinateHandler((ServerLevel) level, ConfigData.teleportLocations[index]);
                            player.sendMessage(textCycleRod.copy().append(handler.getTelepadName()), player.getUUID());
                            this.neighborChanged(state, level, pos, this, pos, false);
                        } else {
                            player.sendMessage(textCycleRodNormal, player.getUUID());
                        }
                    }
                }

                if (heldItem instanceof DyeItem) {
                    if (TelepadBlockInteraction.interact(TelepadBlockInteraction.Action.DYE, level, pos, telepad, state, this, DyeColor.getColor(heldStack))) {
                        if (!player.isCreative()) {
                            heldStack.shrink(1);
                            return InteractionResult.CONSUME; //was able to color and is survival
                        }
                    } else {
                        return InteractionResult.PASS; //wasnt able to color
                    }
                }

                if (heldItem.equals(Items.WATER_BUCKET)) {
                    if (TelepadBlockInteraction.interact(TelepadBlockInteraction.Action.WASH, level, pos, telepad, state, this)) {
                        level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1, 1, true);
                        if (!player.isCreative()) {
                            player.setItemInHand(hand, heldStack.getContainerItem());
                            return InteractionResult.SUCCESS;
                        }
                    } else return InteractionResult.FAIL;
                }
            }
        } else {
            if (player.isShiftKeyDown()) {
                if (level.getBlockEntity(pos) instanceof TileEntityTelepad) {
                    // check if the player has this pad
                    TelepadEntry entry = WorldDataHandler.get(level).getEntryForLocation(pos, level.dimension());

                    if (entry != null && InteractionHand.MAIN_HAND.equals(hand)) {
                        // if the player cannot use this pad (is not registered to it), then add it
                        if (!entry.canUse(player.getUUID())) {
                            Style style = Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.GREEN));

                            MutableComponent msg = cycleAddSuccess.copy().append(entry.entryName);
                            player.sendMessage(msg.setStyle(style), player.getUUID());

                            entry.addUser(player.getUUID());
                            return InteractionResult.SUCCESS;
                        }

                        // if the player is not registered to it and the pad is not public
                        else if (!entry.isPublic) {
                            Style style = Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.GOLD));

                            MutableComponent msg = cycleAddRemove.copy().append(entry.entryName).setStyle(style);
                            player.sendMessage(msg, player.getUUID());
                            entry.removeUser(player.getUUID());
                            return InteractionResult.SUCCESS;
                        } else {
                            MutableComponent msg = cycleAddFail.copy().setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.RED)));
                            player.sendMessage(msg, player.getUUID());
                            return InteractionResult.SUCCESS;

                        }
                    }
                }
            }
        }

        return InteractionResult.SUCCESS;

    }

    ///////////////////////////// REDSTONE////////////////////////////////////////
    @Override
    @Deprecated
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {

        if (level.getBlockEntity(pos) instanceof TileEntityTelepad telepad) {
            if (telepad.hasRedstoneUpgrade()) {
                boolean isPowered = Arrays.stream(Direction.values()).filter(direction -> !direction.equals(Direction.UP)).anyMatch(face -> {
                    BlockState other = level.getBlockState(pos.relative(face));
                    return other.isSignalSource() && (other.getDirectSignal(level, pos, face) > 0 || other.getSignal(level, pos, face) > 0);
                });
                WorldDataHandler wdh = WorldDataHandler.get(level);
                TelepadEntry entry = wdh.getEntryForLocation(pos, level.dimension());
                entry.setPowered(isPowered);
                telepad.setPowered(isPowered);
                level.sendBlockUpdated(pos, state, state, 3);
                wdh.updateEntry(entry);
                wdh.setDirty();
            }
        }

    }

    //////////////// Block Placed///////////////////////////////////

    @Override
    public void onNeighborChange(BlockState state, LevelReader world, BlockPos pos, BlockPos neighbor) {

        super.onNeighborChange(state, world, pos, neighbor);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {

        if (world.getBlockEntity(pos) instanceof TileEntityTelepad tileEntityTelepad && !world.isClientSide()) {
            if (placer instanceof ServerPlayer serverPlayer) { //implicit !world isclientide check is here
                if (stack.hasTag() && stack.getTag() != null) {
                    if (stack.getTag().contains("colorFrame"))
                        tileEntityTelepad.setFeetColor(stack.getTag().getInt("colorFrame"));
                    if (stack.getTag().contains("colorBase"))
                        tileEntityTelepad.setArrowColor(stack.getTag().getInt("colorBase"));
                }
                tileEntityTelepad.setDimension(world.dimension());
                tileEntityTelepad.setChanged();

                NetworkHandler.NETWORK.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new CPacketRequestNamingScreen(pos));
            }
        }
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof TileEntityTelepad telepad) {
            setMissing(level, pos);
            dropPad(level, telepad, pos);
            if (telepad.hasDimensionUpgrade())
                level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(TelepadItems.TRANSMITTER.get(), 1)));
            if (telepad.hasRedstoneUpgrade())
                level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(TelepadItems.TOGGLER.get(), 1)));
            return level.removeBlock(pos, false);
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    /////////////// inherited methods////////////////////

    private void dropPad(Level world, TileEntityTelepad telepad, BlockPos pos) {

        ItemEntity itemEntity = new ItemEntity(EntityType.ITEM, world);
        itemEntity.setPos(pos.getX(), pos.getY(), pos.getZ());

        ItemStack stack = new ItemStack(TelepadBlocks.TELEPAD.get());
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("colorBase", telepad.getColorArrow());
        nbt.putInt("colorFrame", telepad.getColorFeet());
        stack.setTag(nbt);

        itemEntity.setItem(stack);
        world.addFreshEntity(itemEntity);
    }

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
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(level, pos, state, player);
        setMissing(level, pos);
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        super.onBlockExploded(state, level, pos, explosion);
        setMissing(level, pos);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> be) {
        if (!level.isClientSide)
            return be == TelepadBlockEntities.TELEPAD.get() ? (BlockEntityTicker<T>) TileEntityTelepad.TICKER : super.getTicker(level, state, be);
        return super.getTicker(level, state, be);
    }

    private void setMissing(Level level, BlockPos pos) {
        if (!level.isClientSide()) {
            WorldDataHandler wdh = WorldDataHandler.get(level);
            TelepadEntry entry = wdh.getEntryForLocation(pos, level.dimension());
            if (entry != null)
                entry.isMissingFromLocation = true;
        }
    }
}
