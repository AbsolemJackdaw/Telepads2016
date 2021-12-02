package subaraki.telepads.tileentity;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import subaraki.telepads.capability.player.TelepadData;
import subaraki.telepads.handler.ConfigData;
import subaraki.telepads.handler.CoordinateHandler;
import subaraki.telepads.handler.WorldDataHandler;
import subaraki.telepads.network.NetworkHandler;
import subaraki.telepads.network.client.CPacketRequestTeleportScreen;
import subaraki.telepads.registry.TelepadBlockEntities;
import subaraki.telepads.registry.TelepadBlocks;
import subaraki.telepads.utility.TelepadEntry;
import subaraki.telepads.utility.masa.Teleport;

import java.util.List;
import java.util.Random;

public class TileEntityTelepad extends BlockEntity {

    public static final BlockEntityTicker<TileEntityTelepad> TICKER = (level, pos, state, be) -> be.tick();

    public static final int COLOR_FEET_BASE = new java.awt.Color(26, 246, 172).getRGB();
    public static final int COLOR_ARROW_BASE = new java.awt.Color(243, 89, 233).getRGB();
    private ResourceKey<Level> dimension;
    private int colorFrame = COLOR_FEET_BASE;
    private int colorBase = COLOR_ARROW_BASE;

    /**
     * rotation set when inter-dimension upgrade is applied. NR from 0 to 3 to
     * determines the position of the transmitter
     */
    private int upgradeRotation = 0;

    private boolean hasDimensionUpgrade = false;
    private boolean hasRedstoneUpgrade = false;
    private boolean isPowered = false;
    private boolean isPublic = false;

    private boolean isStandingOnPlatform;

    private int coordinate_handler_index = -1;

    // private AxisAlignedBB aabb;

    public TileEntityTelepad(BlockPos pos, BlockState state) {
        super(TelepadBlockEntities.TILE_ENTITY_TELEPAD.get(), pos, state);
    }

    ///////////////// 4 METHODS ABSOLUTELY NEEDED FOR CLIENT/SERVER
    ///////////////// SYNCING/////////////////////

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {

        return new ClientboundBlockEntityDataPacket(getBlockPos(), 0, this.save(new CompoundTag()));

    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {

        this.load(pkt.getTag());
    }

    @Override
    public CompoundTag getUpdateTag() {

        return save(new CompoundTag());
    }

    // calls readFromNbt by default. no need to add anything in here
    @Override
    public void handleUpdateTag(CompoundTag tag) {

        super.handleUpdateTag(tag);
    }
    ////////////////////////////////////////////////////////////////////

    @Override
    public void load(CompoundTag compound) {

        super.load(compound);
        String dim = compound.getString("dimension");
        hasDimensionUpgrade = compound.getBoolean("upgrade_dimension");
        hasRedstoneUpgrade = compound.getBoolean("upgrade_redstone");
        isPowered = compound.getBoolean("is_powered");
        this.colorBase = compound.getInt("colorBase");
        this.colorFrame = compound.getInt("colorFrame");
        this.upgradeRotation = compound.getInt("upgradeRotation");
        isStandingOnPlatform = compound.getBoolean("standingon");
        this.coordinate_handler_index = compound.getInt("mod_tp");
        this.isPublic = compound.getBoolean("public");

        if (!dim.isEmpty())
            dimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(dim));
    }

    @Override
    public CompoundTag save(CompoundTag compound) {

        super.save(compound);
        compound.putString("dimension", dimension.location().toString());
        compound.putBoolean("upgrade_dimension", hasDimensionUpgrade);
        compound.putBoolean("upgrade_redstone", hasRedstoneUpgrade);
        compound.putBoolean("is_powered", isPowered);
        compound.putInt("colorBase", this.colorBase);
        compound.putInt("colorFrame", this.colorFrame);
        compound.putInt("upgradeRotation", upgradeRotation);
        compound.putBoolean("standingon", isStandingOnPlatform);
        compound.putInt("mod_tp", coordinate_handler_index);
        compound.putBoolean("public", isPublic);
        return compound;
    }

    public void tick() {

        if (isPowered || level == null)
            return;

        if (!level.isClientSide) {

            AABB aabb = new AABB(getBlockPos());
            List<ServerPlayer> list = level.getEntitiesOfClass(ServerPlayer.class, aabb);

            if (!list.isEmpty()) {

                setPlatform(true);

                for (ServerPlayer playerOnPad : list) {
                    TelepadData.get(playerOnPad).ifPresent(data -> {

                        // if in the previous tick, all data has been transfered to the player, then it
                        // will request a teleport screen
                        if (data.getRequestTeleportScreen()) {
                            data.setRequestTeleportScreen(false);
                            TelepadEntry telepad = WorldDataHandler.get(getLevel()).getEntryForLocation(getBlockPos(), dimension);
                            boolean is_transmitter = telepad != null && telepad.hasTransmitter;

                            NetworkHandler.NETWORK.send(PacketDistributor.PLAYER.with(() -> playerOnPad),
                                    new CPacketRequestTeleportScreen(data.getEntries(), data.getWhitelist().values(), is_transmitter));

                            // break out of it, he's teleporting away !
                            return;
                        }

                        // if no screen request has been set , do the regular logic to get a request
                        if (data.getCounter() > 0 && !data.isInTeleportGui()) {
                            data.counter--;
                        } else if (data.getCounter() == 0 && !data.isInTeleportGui()) {
                            if (level.dimension().equals(Level.END) && ConfigData.dragonIsBlocking) {
                                if (level instanceof ServerLevel) {
                                    if (!((ServerLevel) level).getDragons().isEmpty()) {
                                        data.setCounter(TelepadData.getMaxTime());

                                        playerOnPad.sendMessage(new TranslatableComponent("dragon.obstructs").setStyle(Style.EMPTY
                                                        .withColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_PURPLE)).withItalic(true)),
                                                playerOnPad.getUUID());
                                        return;
                                    }
                                }
                            }

                            if (getCoordinateHandlerIndex() > -1) {
                                int index = getCoordinateHandlerIndex();
                                if (ConfigData.tp_locations instanceof String[] tpl) {
                                    CoordinateHandler coords = new CoordinateHandler((ServerLevel) level, tpl[index]);
                                    ResourceLocation dimension = coords.getDimension();
                                    if (!playerOnPad.level.dimension().location().equals(dimension)) {
                                        MinecraftServer server = playerOnPad.getServer();

                                        ResourceKey<Level> dim_key = null;
                                        if (server != null)
                                            for (ServerLevel dim : server.getAllLevels()) {
                                                if (dim.dimension().location().equals(dimension))
                                                    dim_key = dim.dimension();
                                            }
                                        if (dim_key == null)
                                            return;

                                        ServerLevel worldDestination = server.getLevel(level.dimension());
                                        BlockPos pos = coords.getPosition(worldDestination);
                                        Teleport.teleportEntityToDimension(playerOnPad, pos, dim_key);
                                    } else {
                                        BlockPos pos = coords.getPosition(getLevel());
                                        Teleport.teleportEntityInsideSameDimension(playerOnPad, pos);
                                    }
                                }
                            } else {
                                // if no dragon is found, or dimension != the end, you end up here
                                data.setInTeleportGui(true);
                                data.setCounter(TelepadData.getMaxTime());
                                activateTelepadGui(playerOnPad);
                            }
                        }
                    });
                }
            } else {
                setPlatform(false);
            }
        }
    }

    /**
     * Resets the count down of the pad, sets that there is no player on the pad,
     * and no player using the gui. And causes a block update.
     */
    public void setPlatform(boolean onPlatform) {

        if (level != null) {
            isStandingOnPlatform = onPlatform;
            level.sendBlockUpdated(worldPosition, level.getBlockState(getBlockPos()), TelepadBlocks.TELEPAD_BLOCK.get().defaultBlockState(), 3);
        }
    }

    // called server side only in tick
    private void activateTelepadGui(ServerPlayer player) {

        TelepadData.get(player).ifPresent(data -> {

            ServerLevel world = player.getLevel();
            WorldDataHandler save = WorldDataHandler.get(world);

            List<TelepadEntry> allTelepads = save.getEntries();

            // copy all user entries to the player
            data.getEntries().clear();
            allTelepads.stream().filter(entry -> entry.canUse(player.getUUID())).forEach(data::addEntry);

            data.setRequestTeleportScreen(true);
        });
    }

    ///////////////// Setters and Getters/////////////////////////

    public ResourceKey<Level> getDimension() {

        return dimension;
    }

    public void setDimension(ResourceKey<Level> dimensionID) {

        dimension = dimensionID;
    }

    public void setFeetColor(int rgb) {

        colorFrame = rgb;
    }

    public void setArrowColor(int rgb) {

        colorBase = rgb;
    }

    public int getColorFeet() {

        return colorFrame;
    }

    public int getColorArrow() {

        return colorBase;
    }

    public boolean hasDimensionUpgrade() {

        return hasDimensionUpgrade;
    }

    public void addDimensionUpgrade(boolean allowed) {

        this.upgradeRotation = allowed ? new Random().nextInt(4) : 0;
        hasDimensionUpgrade = true;
    }

    public int getUpgradeRotation() {

        return upgradeRotation;
    }

    public boolean hasRedstoneUpgrade() {

        return hasRedstoneUpgrade;
    }

    public void addRedstoneUpgrade() {

        hasRedstoneUpgrade = true;
    }

    public boolean isPowered() {

        return isPowered;
    }

    public void setPowered(boolean flag) {

        isPowered = flag;
    }

//    public void setCoordinateHandlerIndex(int index) {
//
//        this.coordinate_handler_index = index;
//    }

    public void rotateCoordinateHandlerIndex() {

        this.coordinate_handler_index++;
        if (this.coordinate_handler_index >= ConfigData.tp_locations.length) {
            coordinate_handler_index = -1;
        }
    }

    public int getCoordinateHandlerIndex() {

        return coordinate_handler_index;
    }

//    @Override
//    public boolean isUsableByPlayer(Player player) {
//
//        return this.level.getBlockEntity(this.worldPosition) != this ? false
//                : player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) <= 64.0D;
//    }

    public boolean isStandingOnPlatform() {

        return isStandingOnPlatform;
    }

    public void toggleAcces() {

        boolean flag = !isPublic;

        TelepadEntry tpe = WorldDataHandler.get(getLevel()).getEntryForLocation(getBlockPos(), getDimension());
        tpe.setPublic(flag); // set opposite of current value for public.
        this.isPublic = flag;
    }

    public boolean isPublic() {

        return isPublic;
    }
}
