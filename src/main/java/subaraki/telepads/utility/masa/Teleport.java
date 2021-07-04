package subaraki.telepads.utility.masa;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class Teleport {

    public static Entity teleportEntityInsideSameDimension(Entity entity, BlockPos pos)
    {

        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        // Load the chunk first
        entity.getCommandSenderWorld().getChunk((int) Math.floor(x / 16D), (int) Math.floor(z / 16D));

        entity.moveTo(x, y, z, entity.yRot, entity.xRot);
        entity.teleportTo(x, y, z);
        return entity;
    }

    public static Entity teleportEntityToDimension(ServerPlayerEntity player, BlockPos pos, RegistryKey<World> dimension)
    {

        if (!net.minecraftforge.common.ForgeHooks.onTravelToDimension(player, dimension))
            return null;

        ServerWorld nextWorld = player.getServer().getLevel(dimension);
        nextWorld.getChunk(pos); // make sure the chunk is loaded
        player.teleportTo(nextWorld, pos.getX(), pos.getY(), pos.getZ(), player.yRot, player.xRot);

        return player;
    }
}
