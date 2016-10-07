package subaraki.telepads.utility;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldServer;
import net.minecraft.world.end.DragonFightManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToAccessFieldException;

public class TeleportUtility
{

	public static Entity teleportEntity(Entity entity, BlockPos pos, int dimDst)
	{
		double x,y,z;
		x=pos.getX();y=pos.getY();z=pos.getZ();

		if (entity == null || entity.isDead == true || entity.worldObj.isRemote == true)
		{
			return null;
		}

		if (!entity.worldObj.isRemote && entity.worldObj instanceof WorldServer)
		{
			WorldServer worldServerDst = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(dimDst);
			if (worldServerDst == null)
			{
				return null;
			}

			int chunkX = ((int)x) >> 4;
			int chunkZ = ((int)z) >> 4;

			if (worldServerDst.getChunkProvider().chunkExists(chunkX, chunkZ) == false)
			{
				worldServerDst.getChunkProvider().loadChunk(chunkX, chunkZ);
			}

			if (entity instanceof EntityLiving)
			{
				((EntityLiving)entity).setMoveForward(0.0f);
				((EntityLiving)entity).getNavigator().clearPathEntity();
			}

			if (entity.dimension != dimDst || (entity.worldObj instanceof WorldServer && entity.worldObj != worldServerDst))
			{}
			else if (entity instanceof EntityPlayerMP)
			{
				((EntityPlayerMP) entity).connection.setPlayerLocation(x, y, z, entity.rotationYaw, entity.rotationPitch);
			}
			else
			{
				//entity.setPositionAndUpdate(x, y, z);
				entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
			}
		}

		return entity;
	}

	public static EntityPlayer transferPlayerToDimension(EntityPlayerMP player, int dimDst, BlockPos pos)
	{
		double x,y,z;
		x=pos.getX();y=pos.getY();z=pos.getZ();

		if (player == null || player.isDead == true || player.dimension == dimDst || player.worldObj.isRemote == true)
		{
			return null;
		}

		int dimSrc = player.dimension;
		x = MathHelper.clamp_double(x, -30000000.0d, 30000000.0d);
		z = MathHelper.clamp_double(z, -30000000.0d, 30000000.0d);
		player.setLocationAndAngles(x, y, z, player.rotationYaw, player.rotationPitch);

		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		WorldServer worldServerSrc = server.worldServerForDimension(dimSrc);
		WorldServer worldServerDst = server.worldServerForDimension(dimDst);

		if (worldServerSrc == null || worldServerDst == null)
		{
			return null;
		}

		player.dimension = dimDst;
		player.connection.sendPacket(new SPacketRespawn(player.dimension, player.worldObj.getDifficulty(), player.worldObj.getWorldInfo().getTerrainType(), player.interactionManager.getGameType()));
		player.mcServer.getPlayerList().updatePermissionLevel(player);
		//worldServerSrc.removePlayerEntityDangerously(player); // this crashes
		worldServerSrc.removeEntity(player);
		player.isDead = false;

		worldServerDst.spawnEntityInWorld(player);
		worldServerDst.updateEntityWithOptionalForce(player, false);
		player.setWorld(worldServerDst);
		player.mcServer.getPlayerList().preparePlayer(player, worldServerSrc); // remove player from the source world
		player.connection.setPlayerLocation(x, y, z, player.rotationYaw, player.rotationPitch);
		player.interactionManager.setWorld(worldServerDst);
		player.connection.sendPacket(new SPacketPlayerAbilities(player.capabilities));
		player.mcServer.getPlayerList().updateTimeAndWeatherForPlayer(player, worldServerDst);
		player.mcServer.getPlayerList().syncPlayerInventory(player);
		player.addExperienceLevel(0);
		player.setPlayerHealthUpdated();


		// FIXME 1.9 - Somewhat ugly way to clear the Boss Info stuff when teleporting FROM The End
		if (worldServerSrc.provider instanceof WorldProviderEnd)
		{
			DragonFightManager manager = ((WorldProviderEnd)worldServerSrc.provider).getDragonFightManager();

			if (manager != null)
			{
				try
				{
					BossInfoServer bossInfo = ReflectionHelper.getPrivateValue(DragonFightManager.class, manager, "field_186109_c", "bossInfo");
					if (bossInfo != null)
					{
						bossInfo.removePlayer(player);
					}
				}
				catch (UnableToAccessFieldException e)
				{
				}
			}
		}

		for (PotionEffect potioneffect : player.getActivePotionEffects())
		{
			player.connection.sendPacket(new SPacketEntityEffect(player.getEntityId(), potioneffect));
		}

		FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, dimSrc, dimDst);

		return player;
	}
}
