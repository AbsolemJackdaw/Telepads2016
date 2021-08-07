package subaraki.telepads.network.server;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import subaraki.telepads.capability.player.TelepadData;
import subaraki.telepads.handler.ConfigData;
import subaraki.telepads.handler.WorldDataHandler;
import subaraki.telepads.network.IPacketBase;
import subaraki.telepads.network.NetworkHandler;
import subaraki.telepads.utility.TelepadEntry;
import subaraki.telepads.utility.masa.Teleport;

import java.util.function.Supplier;

public class SPacketTeleport implements IPacketBase {

    /**
     * The position to send the player to.
     */
    public TelepadEntry goTo;

    /**
     * The position the player comes from.
     */
    public BlockPos oldPos;

    /**
     * flag to force teleport and bypass checking if a tile entity exists to
     * teleport too
     */
    public boolean force;

    /**
     * A packet to teleport the player to a given position from the client side.
     * This packet must be sent from a client thread.
     *
     * @param goTo          : The position/entry to send the player to.
     * @param playerFrom    : The position the player comes from.
     * @param forceTeleport : Flag to force teleport and bypass checking if a telepad entry
     *                      exists to teleport too
     */
    public SPacketTeleport(BlockPos playerFrom, TelepadEntry goTo, boolean forceTeleport) {

        this.oldPos = playerFrom;
        this.goTo = goTo;
        this.force = forceTeleport;
    }

    public SPacketTeleport(FriendlyByteBuf buf) {

        decode(buf);
    }

    /**
     * Necessary empty constructor
     */
    public SPacketTeleport() {

    }

    /**
     * Teleport Penalty is removed here if any is given in the config file
     */
    private static boolean teleportPenalty(Player player) {

        // lessons learned from fiddling with experience stuff :
        // 1 : don't use the experienceTotal to calculate whatever. its inconsistent and
        // can be messed up by manually adding levels
        // 2 : don't set levels manually, use addExperienceLevel, it does all the
        // needed stuff for you
        // 3 : experience is only the representing of the bar, and is calculated from
        // 0.0 to 1.0 to draw the green bar,
        // it is some amount of experience devided by the level cap
        int expConsuming = ConfigData.expConsume;
        int lvlConsuming = ConfigData.lvlConsume;

        if (expConsuming == 0 && lvlConsuming == 0)
            return true;

        if (lvlConsuming > 0) {
            if (player.experienceLevel >= lvlConsuming) {
                player.giveExperienceLevels(-lvlConsuming);
                return true;
            }
            return false;
        } else {

            float actualExpInBar = player.experienceProgress * (float) player.getXpNeededForNextLevel();

            if (actualExpInBar < expConsuming)// less exp then penalty
            {
                // if the player does not have enough experience
                if (player.experienceLevel == 0) {
                    return false;
                }

                expConsuming -= actualExpInBar; // remove resting exp from penalty
                player.giveExperienceLevels(-1); // down a level
                actualExpInBar = (float) player.getXpNeededForNextLevel(); // exp bar is considered full here when going down a level
                float total = actualExpInBar - expConsuming; // the total refund is one level of exp minus the penalty left
                player.experienceProgress = 0.0f; // reset the 'exp bar' to 0
                if (total < 0)
                    total = 0;
                player.giveExperiencePoints((int) total); // give exp back to set to correct level
                return true;
            } else {
                float total = actualExpInBar - (float) expConsuming;
                player.experienceProgress = 0.0f;
                player.giveExperiencePoints((int) total);
                return true;
            }
        }
    }

    @Override
    public void encode(FriendlyByteBuf buf) {

        buf.writeLong(oldPos.asLong());
        goTo.writeToBuffer(buf);
        buf.writeBoolean(force);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {

        oldPos = BlockPos.of(buf.readLong());
        goTo = new TelepadEntry(buf);
        force = buf.readBoolean();
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {

        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            WorldDataHandler wdh = WorldDataHandler.get(player.level);

            TelepadData.get(player).ifPresent(data -> {
                data.setInTeleportGui(false);

                BlockPos destination = goTo.position.above();
                ResourceKey<Level> destination_dimension_id = goTo.dimensionID;
                int penalty = ConfigData.expConsume;

                if (penalty > 0 && (player.experienceLevel == 0 && player.experienceProgress * player.getXpNeededForNextLevel() <= penalty)) {
                    player.displayClientMessage(new TranslatableComponent("no.exp")
                            .setStyle(Style.EMPTY.withItalic(true).withColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_RED))), true);
                    return;
                }

                if (goTo.dimensionID.equals(player.level.dimension())) {
                    if (force) {
                        if (teleportPenalty(player)) {
                            Teleport.teleportEntityInsideSameDimension(player, destination);
                            return;
                        }
                    }

                    if (wdh.contains(goTo)) {
                        if (!goTo.isPowered) {
                            if (destination_dimension_id.equals(player.level.dimension())) {
                                if (teleportPenalty(player)) {
                                    Teleport.teleportEntityInsideSameDimension(player, destination);
                                }
                            }
                        } else {
                            player.displayClientMessage(
                                    new TranslatableComponent("no.power").setStyle(Style.EMPTY.withItalic(true).withColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_RED))), true);
                        }
                    } else {
                        data.setInTeleportGui(true); // set to true so when changing gui, it doesnt try to open the teleport gui.
                    }
                } else {
                    if (force) {
                        if (teleportPenalty(player)) {
                            Teleport.teleportEntityToDimension(player, destination, destination_dimension_id);
                            return;
                        }
                    }
                    if (wdh.contains(goTo)) {
                        if (!goTo.isPowered) {
                            if (teleportPenalty(player)) {
                                Teleport.teleportEntityToDimension(player, destination, destination_dimension_id);
                            }
                        } else {
                            player.displayClientMessage(
                                    new TranslatableComponent("no.power").setStyle(Style.EMPTY.withItalic(true).withColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_RED))), true);
                        }
                    }
                }

            });
        });
        context.get().setPacketHandled(true);
    }

    @Override
    public void register(int id) {

        NetworkHandler.NETWORK.registerMessage(id, SPacketTeleport.class, SPacketTeleport::encode, SPacketTeleport::new, SPacketTeleport::handle);

    }
}