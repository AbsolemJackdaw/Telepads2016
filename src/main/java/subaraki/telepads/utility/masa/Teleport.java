package subaraki.telepads.utility.masa;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;

public class Teleport {

    public static Entity teleportEntityInsideSameDimension(Entity entity, BlockPos pos)
    {

        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        // Load the chunk first
        entity.getEntityWorld().getChunk((int) Math.floor(x / 16D), (int) Math.floor(z / 16D));

        entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
        entity.setPositionAndUpdate(x, y, z);
        return entity;
    }

    public static Entity teleportEntityToDimension(ServerPlayerEntity player, BlockPos pos, int dimDst)
    {

        DimensionType destination = DimensionType.getById(dimDst);
        if (!net.minecraftforge.common.ForgeHooks.onTravelToDimension(player, destination))
            return null;

        ServerWorld nextWorld = player.getServer().getWorld(destination);
        nextWorld.getChunk(pos); // make sure the chunk is loaded
        player.teleport(nextWorld, pos.getX(), pos.getY(), pos.getZ(), player.rotationYaw, player.rotationPitch);

        return player;
    }
}
