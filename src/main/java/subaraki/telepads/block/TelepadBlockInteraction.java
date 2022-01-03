package subaraki.telepads.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import subaraki.telepads.handler.WorldDataHandler;
import subaraki.telepads.tileentity.TileEntityTelepad;
import subaraki.telepads.utility.TelepadEntry;

import javax.annotation.Nonnull;

public class TelepadBlockInteraction {
    public static boolean interact(Action action, Level level, BlockPos pos, TileEntityTelepad telepad, BlockState state, BlockTelepad block) {
        return interact(action, level, pos, telepad, state, block, null);
    }

    public static boolean interact(Action action, Level level, BlockPos pos, TileEntityTelepad telepad, BlockState state, BlockTelepad block, DyeColor color) {
        TelepadEntry worldSaveEntryAtLocation = getEntry(pos, level);
        if (worldSaveEntryAtLocation != null) {
            boolean success = false;
            switch (action) {
                case TOGGLER_UPGRADE -> {
                    if (!telepad.hasRedstoneUpgrade()) {
                        telepad.addRedstoneUpgrade(); //toggle redstone upgrade
                        block.neighborChanged(state, level, pos, block, pos, false); //check neighbours for power
                        success = true;
                    }
                }
                case TOGGLE_ACCESS -> {
                    telepad.toggleAcces();
                    worldSaveEntryAtLocation.setPublic(telepad.isPublic());
                    success = true;
                }
                case TRANSMITTER -> {
                    if (!worldSaveEntryAtLocation.hasTransmitter) {
                        telepad.addDimensionUpgrade(true);
                        worldSaveEntryAtLocation.hasTransmitter = true;
                        success = true;
                    }
                }
                case CYCLE -> {
                    telepad.rotateCoordinateHandlerIndex();
                    success = true;
                }

                case WASH -> {
                    //both parts can be washed at once. two if's, no else
                    if (telepad.getColorFeet() != TileEntityTelepad.COLOR_FEET_BASE) {
                        wash(telepad.getColorFeet(), level, pos);
                        telepad.setFeetColor(TileEntityTelepad.COLOR_FEET_BASE);
                        success = true;
                    }
                    if (telepad.getColorArrow() != TileEntityTelepad.COLOR_ARROW_BASE) {
                        wash(telepad.getColorArrow(), level, pos);
                        telepad.setArrowColor(TileEntityTelepad.COLOR_ARROW_BASE);
                        success = true;
                    }
                }

                case DYE -> {
                    if (color != null) {
                        float red = color.getTextureDiffuseColors()[0];
                        float green = color.getTextureDiffuseColors()[1];
                        float blue = color.getTextureDiffuseColors()[2];

                        int rgb = (int) (red * 255f);
                        rgb = (rgb << 8) + (int) (green * 255f);
                        rgb = (rgb << 8) + (int) (blue * 255f);

                        //only color if the base color is default
                        if (telepad.getColorFeet() == TileEntityTelepad.COLOR_FEET_BASE) {
                            telepad.setFeetColor(rgb);
                            success = true;
                        }
                        //only color if the feet are colored and the base color is default
                        else if (telepad.getColorArrow() == TileEntityTelepad.COLOR_ARROW_BASE) {
                            telepad.setArrowColor(rgb);
                            success = true;
                        }
                    }
                }
            }

            telepad.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
            WorldDataHandler.get(level).updateEntry(worldSaveEntryAtLocation);
            return success;
        }

        return false;
    }

    private static void wash(int color, Level world, BlockPos pos) {

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

    private static TelepadEntry getEntry(@Nonnull BlockPos pos, @Nonnull Level level) {
        return WorldDataHandler.get(level).getEntryForLocation(pos, level.dimension());
    }

    public enum Action {
        TOGGLE_ACCESS,
        TRANSMITTER,
        TOGGLER_UPGRADE,
        CYCLE,
        DYE,
        WASH
    }
}
