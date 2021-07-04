package subaraki.telepads.tileentity;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;
import subaraki.telepads.capability.player.TelepadData;
import subaraki.telepads.handler.ConfigData;
import subaraki.telepads.handler.CoordinateHandler;
import subaraki.telepads.handler.WorldDataHandler;
import subaraki.telepads.mod.Telepads.ObjectHolders;
import subaraki.telepads.network.NetworkHandler;
import subaraki.telepads.network.client.CPacketRequestTeleportScreen;
import subaraki.telepads.utility.TelepadEntry;
import subaraki.telepads.utility.masa.Teleport;

public class TileEntityTelepad extends TileEntity implements ITickableTileEntity {

    private RegistryKey<World> dimension;
    public static final int COLOR_FEET_BASE = new java.awt.Color(26, 246, 172).getRGB();
    public static final int COLOR_ARROW_BASE = new java.awt.Color(243, 89, 233).getRGB();
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

    public TileEntityTelepad() {

        super(ObjectHolders.TILE_ENTITY_TELEPAD);
    }

    ///////////////// 3 METHODS ABSOLUTELY NEEDED FOR CLIENT/SERVER
    ///////////////// SYNCING/////////////////////

    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {

        CompoundNBT nbt = new CompoundNBT();
        this.save(nbt);

        return new SUpdateTileEntityPacket(getBlockPos(), 0, nbt);

    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {

        this.load(getBlockState(), pkt.getTag());
    }

    @Override
    public CompoundNBT getUpdateTag()
    {

        CompoundNBT nbt = super.getUpdateTag();
        save(nbt);
        return nbt;
    }

    // calls readFromNbt by default. no need to add anything in here
    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag)
    {

        super.handleUpdateTag(state, tag);
    }
    ////////////////////////////////////////////////////////////////////

    @Override
    public void load(BlockState state, CompoundNBT compound)
    {

        super.load(state, compound);
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
            dimension = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(dim));
    }

    @Override
    public CompoundNBT save(CompoundNBT compound)
    {

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

    @Override
    public void tick()
    {

        if (isPowered)
            return;

        if (!level.isClientSide)
        {

            AxisAlignedBB aabb = new AxisAlignedBB(getBlockPos());
            List<ServerPlayerEntity> list = level.getEntitiesOfClass(ServerPlayerEntity.class, aabb);

            if (!list.isEmpty())
            {

                setPlatform(true);

                for (ServerPlayerEntity player_standing_on_pad : list)
                {
                    TelepadData.get(player_standing_on_pad).ifPresent(player_data -> {

                        // if in the previous tick, all data has been transfered to the player, then it
                        // will request a teleport screen
                        if (player_data.getRequestTeleportScreen())
                        {
                            player_data.setRequestTeleportScreen(false);
                            TelepadEntry telepad = WorldDataHandler.get(getLevel()).getEntryForLocation(getBlockPos(), dimension);
                            boolean is_transmitter = telepad == null ? false : telepad.hasTransmitter;

                            NetworkHandler.NETWORK.send(PacketDistributor.PLAYER.with(() -> player_standing_on_pad),
                                    new CPacketRequestTeleportScreen(player_data.getEntries(), player_data.getWhitelist().values(), is_transmitter));

                            // break out of it, he's teleporting away !
                            return;
                        }

                        // if no screen request has been set , do the regular logic to get a request
                        if (player_data.getCounter() > 0 && !player_data.isInTeleportGui())
                        {
                            player_data.counter--;
                        }

                        else
                            if (player_data.getCounter() == 0 && !player_data.isInTeleportGui())
                            {
                                if (level.dimension().equals(World.END) && ConfigData.allowDragonBlocking)
                                {
                                    if (level instanceof ServerWorld)
                                    {
                                        if (!((ServerWorld) level).getDragons().isEmpty())
                                        {
                                            player_data.setCounter(TelepadData.getMaxTime());

                                            player_standing_on_pad.sendMessage(new TranslationTextComponent("dragon.obstructs").setStyle(Style.EMPTY
                                                    .withColor(Color.fromLegacyFormat(TextFormatting.DARK_PURPLE)).withItalic(true)),
                                                    player_standing_on_pad.getUUID());
                                            return;
                                        }
                                    }
                                }

                                if (getCoordinateHandlerIndex() > -1)
                                {
                                    int index = getCoordinateHandlerIndex();
                                    String[] tpl = ConfigData.tp_locations;
                                    CoordinateHandler coords = new CoordinateHandler((ServerWorld) level, tpl[index]);

                                    ResourceLocation dimension = coords.getDimension();

                                    if (!player_standing_on_pad.level.dimension().location().equals(dimension))
                                    {
                                        MinecraftServer server = player_standing_on_pad.getServer();

                                        RegistryKey<World> dim_key = null;
                                        for (ServerWorld dim : server.getAllLevels())
                                        {
                                            if (dim.dimension().location().equals(dimension))
                                                dim_key = dim.dimension();
                                        }
                                        if (dim_key == null)
                                            return;

                                        ServerWorld worldDestination = server.getLevel(level.dimension());
                                        BlockPos pos = coords.getPosition(worldDestination);
                                        Teleport.teleportEntityToDimension(player_standing_on_pad, pos, dim_key);
                                    }
                                    else
                                    {
                                        BlockPos pos = coords.getPosition(getLevel());
                                        Teleport.teleportEntityInsideSameDimension(player_standing_on_pad, pos);
                                    }
                                }
                                else
                                {
                                    // if no dragon is found, or dimension != the end, you end up here
                                    player_data.setInTeleportGui(true);
                                    player_data.setCounter(TelepadData.getMaxTime());
                                    activateTelepadGui(player_standing_on_pad);
                                }
                            }
                    });
                }
            }

            else
            {
                setPlatform(false);
            }
        }
    }

    /**
     * Resets the count down of the pad, sets that there is no player on the pad,
     * and no player using the gui. And causes a block update.
     */
    public void setPlatform(boolean onPlatform)
    {

        isStandingOnPlatform = onPlatform;
        level.sendBlockUpdated(worldPosition, level.getBlockState(getBlockPos()), ObjectHolders.TELEPAD_BLOCK.defaultBlockState(), 3);
    }

    // called server side only in tick
    private void activateTelepadGui(ServerPlayerEntity player)
    {

        TelepadData.get(player).ifPresent(data -> {

            ServerWorld world = player.getLevel();
            WorldDataHandler save = WorldDataHandler.get(world);

            List<TelepadEntry> world_save_entries = save.getEntries();

            // copy all user entries to the player
            data.getEntries().clear();
            world_save_entries.stream().filter(entry -> entry.canUse(player.getUUID())).forEach(entry -> data.addEntry(entry));

            data.setRequestTeleportScreen(true);
        });
    }

    ///////////////// Setters and Getters/////////////////////////

    public RegistryKey<World> getDimension()
    {

        return dimension;
    }

    public void setDimension(RegistryKey<World> dimensionID)
    {

        dimension = dimensionID;
    }

    public void setFeetColor(int rgb)
    {

        colorFrame = rgb;
    }

    public void setArrowColor(int rgb)
    {

        colorBase = rgb;
    }

    public int getColorFeet()
    {

        return colorFrame;
    }

    public int getColorArrow()
    {

        return colorBase;
    }

    public boolean hasDimensionUpgrade()
    {

        return hasDimensionUpgrade;
    }

    public void addDimensionUpgrade(boolean allowed)
    {

        this.upgradeRotation = allowed ? new Random().nextInt(4) : 0;
        hasDimensionUpgrade = true;
    }

    public int getUpgradeRotation()
    {

        return upgradeRotation;
    }

    public boolean hasRedstoneUpgrade()
    {

        return hasRedstoneUpgrade;
    }

    public void addRedstoneUpgrade()
    {

        hasRedstoneUpgrade = true;
    }

    public void setPowered(boolean flag)
    {

        isPowered = flag;
    }

    public boolean isPowered()
    {

        return isPowered;
    }

    public void setCoordinateHandlerIndex(int index)
    {

        this.coordinate_handler_index = index;
    }

    public void rotateCoordinateHandlerIndex()
    {

        this.coordinate_handler_index++;
        if (this.coordinate_handler_index >= ConfigData.tp_locations.length)
        {
            coordinate_handler_index = -1;
        }
    }

    public int getCoordinateHandlerIndex()
    {

        return coordinate_handler_index;
    }

    public boolean isUsableByPlayer(PlayerEntity player)
    {

        return this.level.getBlockEntity(this.worldPosition) != this ? false
                : player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    public boolean isStandingOnPlatform()
    {

        return isStandingOnPlatform;
    }

    public void toggleAcces()
    {

        boolean flag = !isPublic;

        WorldDataHandler wdh = WorldDataHandler.get(getLevel());
        TelepadEntry tpe = wdh.getEntryForLocation(getBlockPos(), getDimension());
        tpe.setPublic(flag); // set opposite of current value for public.
        this.isPublic = flag;
    }

    public boolean isPublic()
    {

        return isPublic;
    }
}
