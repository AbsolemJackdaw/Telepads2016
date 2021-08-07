package subaraki.telepads.utility.masa;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class Teleport {

    public static Entity teleportEntityInsideSameDimension(Entity entity, BlockPos pos) {

        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        // Load the chunk first
        entity.getCommandSenderWorld().getChunk((int) Math.floor(x / 16D), (int) Math.floor(z / 16D));

        entity.moveTo(x, y, z, entity.getYRot(), entity.getXRot());
        entity.teleportTo(x, y, z);
        return entity;
    }

    public static Entity teleportEntityToDimension(ServerPlayer player, BlockPos pos, ResourceKey<Level> dimension) {

        if (!net.minecraftforge.common.ForgeHooks.onTravelToDimension(player, dimension))
            return null;

        ServerLevel nextWorld = player.getServer().getLevel(dimension);
        nextWorld.getChunk(pos); // make sure the chunk is loaded
        player.teleportTo(nextWorld, pos.getX(), pos.getY(), pos.getZ(), player.getYRot(), player.getXRot());

        return player;
    }
}
