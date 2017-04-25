package subaraki.telepads.utility;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldServer;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.end.DragonFightManager;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToAccessFieldException;
/**
 * 
 * Based On Masa's teleport utlility
 * 
 * https://github.com/maruohon/justenoughdimensions/blob/master/src/main/java/fi/dy/masa/justenoughdimensions/command/CommandTeleportJED.java
 * 
 * 
 * */
public class TeleportUtility
{

	public static void teleportEntityTo(Entity entity, BlockPos pos, float yaw, float pitch)
	{
		teleportEntityTo(entity, new Vec3d(pos), yaw, pitch);
	}

	private static void teleportEntityTo(Entity entity, Vec3d pos, float yaw, float pitch)
	{
		pos = getClampedDestinationPosition(pos, entity.getEntityWorld());
		entity.setLocationAndAngles(pos.xCoord, pos.yCoord, pos.zCoord, yaw, pitch);
		entity.setPositionAndUpdate(pos.xCoord, pos.yCoord, pos.zCoord);
	}

	private static Vec3d getClampedDestinationPosition(Vec3d posIn, World worldDst)
	{
		WorldBorder border = worldDst.getWorldBorder();

		double x = MathHelper.clamp(posIn.xCoord, border.minX() + 2, border.maxX() - 2);
		double y = MathHelper.clamp(posIn.yCoord, -4096, 4096);
		double z = MathHelper.clamp(posIn.zCoord, border.minZ() + 2, border.maxZ() - 2);

		return new Vec3d(x, y, z);
	}

	public static Entity changeToDimension(Entity entity, BlockPos destination, int dimension, MinecraftServer server)
	{
		WorldServer worldDst = server.worldServerForDimension(dimension);
		if (worldDst == null)
		{
			return entity;
		}

		double x = entity.posX;
		double y = entity.posY;
		double z = entity.posZ;
		float yaw = entity.rotationYaw;
		float pitch = entity.rotationPitch;

		x = destination.getX();
		y = destination.getY();
		z = destination.getZ();

		Vec3d pos = getClampedDestinationPosition(new Vec3d(x, y, z), worldDst);
		x = pos.xCoord;
		y = pos.yCoord;
		z = pos.zCoord;

		if (entity instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP) entity;
			World worldOld = player.getEntityWorld();
			// Set the yaw and pitch at this point
			entity.setLocationAndAngles(x, y, z, yaw, pitch);
			server.getPlayerList().transferPlayerToDimension(player, dimension, new DummyTeleporter(worldDst));
			player.setPositionAndUpdate(x, y, z);

			// Teleporting FROM The End
			if (worldOld.provider instanceof WorldProviderEnd)
			{
				player.setPositionAndUpdate(x, y, z);
				worldDst.spawnEntity(player);
				worldDst.updateEntityWithOptionalForce(player, false);
				removeDragonBossBarHack(player, (WorldServer) worldOld);
			}
		}

		return entity;
	}

	private static void removeDragonBossBarHack(EntityPlayerMP player, WorldServer worldSrc)
	{
		// FIXME 1.9 - Somewhat ugly way to clear the Boss Info stuff when teleporting FROM The End
		if (worldSrc.provider instanceof WorldProviderEnd)
		{
			DragonFightManager manager = ((WorldProviderEnd) worldSrc.provider).getDragonFightManager();

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
					player.sendMessage(new TextComponentString("Failed to get DragonFightManager#bossInfo"));
				}
			}
		}
	}
	
	private static class DummyTeleporter extends Teleporter
	{
		public DummyTeleporter(WorldServer worldIn)
		{
			super(worldIn);
		}

		@Override
		public boolean makePortal(Entity entityIn)
		{
			return true;
		}

		@Override
		public boolean placeInExistingPortal(Entity entityIn, float rotationYaw)
		{
			return true;
		}

		@Override
		public void placeInPortal(Entity entityIn, float rotationYaw)
		{
		}
	}
	//	public static Entity teleportEntity(Entity entity, BlockPos pos, int dimDst)
	//	{
	//		double x,y,z;
	//		x=pos.getX();y=pos.getY();z=pos.getZ();
	//
	//		if (entity == null || entity.isDead == true || entity.world.isRemote == true)
	//		{
	//			return null;
	//		}
	//
	//		if (!entity.world.isRemote && entity.world instanceof WorldServer)
	//		{
	//			WorldServer worldServerDst = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(dimDst);
	//			if (worldServerDst == null)
	//			{
	//				return null;
	//			}
	//
	//			int chunkX = ((int)x) >> 4;
	//			int chunkZ = ((int)z) >> 4;
	//
	//			if (worldServerDst.getChunkProvider().chunkExists(chunkX, chunkZ) == false)
	//			{
	//				worldServerDst.getChunkProvider().loadChunk(chunkX, chunkZ);
	//			}
	//
	//			if (entity instanceof EntityLiving)
	//			{
	//				((EntityLiving)entity).setMoveForward(0.0f);
	//				((EntityLiving)entity).getNavigator().clearPathEntity();
	//			}
	//
	//			if (entity.dimension != dimDst || (entity.world instanceof WorldServer && entity.world != worldServerDst))
	//			{}
	//			else if (entity instanceof EntityPlayerMP)
	//			{
	//				((EntityPlayerMP) entity).connection.setPlayerLocation(x, y, z, entity.rotationYaw, entity.rotationPitch);
	//			}
	//			else
	//			{
	//				//entity.setPositionAndUpdate(x, y, z);
	//				entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
	//			}
	//		}
	//
	//		return entity;
	//	}
	//
	//	public static EntityPlayer transferPlayerToDimension(EntityPlayerMP player, int dimDst, BlockPos pos)
	//	{
	//		double x,y,z;
	//		x=pos.getX();y=pos.getY();z=pos.getZ();
	//
	//		if (player == null || player.isDead == true || player.dimension == dimDst || player.world.isRemote == true)
	//		{
	//			return null;
	//		}
	//
	//		int dimSrc = player.dimension;
	//		x = MathHelper.clamp(x, -30000000.0d, 30000000.0d);
	//		z = MathHelper.clamp(z, -30000000.0d, 30000000.0d);
	//		player.setLocationAndAngles(x, y, z, player.rotationYaw, player.rotationPitch);
	//
	//		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
	//		WorldServer worldServerSrc = server.worldServerForDimension(dimSrc);
	//		WorldServer worldServerDst = server.worldServerForDimension(dimDst);
	//
	//		if (worldServerSrc == null || worldServerDst == null)
	//		{
	//			return null;
	//		}
	//
	//		player.dimension = dimDst;
	//		player.connection.sendPacket(new SPacketRespawn(player.dimension, player.world.getDifficulty(), player.world.getWorldInfo().getTerrainType(), player.interactionManager.getGameType()));
	//		player.mcServer.getPlayerList().updatePermissionLevel(player);
	//		//worldServerSrc.removePlayerEntityDangerously(player); // this crashes
	//		worldServerSrc.removeEntity(player);
	//		player.isDead = false;
	//
	//		worldServerDst.spawnEntity(player);
	//		worldServerDst.updateEntityWithOptionalForce(player, false);
	//		player.setWorld(worldServerDst);
	//		player.mcServer.getPlayerList().preparePlayer(player, worldServerSrc); // remove player from the source world
	//		player.connection.setPlayerLocation(x, y, z, player.rotationYaw, player.rotationPitch);
	//		player.interactionManager.setWorld(worldServerDst);
	//		player.connection.sendPacket(new SPacketPlayerAbilities(player.capabilities));
	//		player.mcServer.getPlayerList().updateTimeAndWeatherForPlayer(player, worldServerDst);
	//		player.mcServer.getPlayerList().syncPlayerInventory(player);
	//		player.addExperienceLevel(0);
	//		player.setPlayerHealthUpdated();
	//
	//
	//		// FIXME 1.9 - Somewhat ugly way to clear the Boss Info stuff when teleporting FROM The End
	//		if (worldServerSrc.provider instanceof WorldProviderEnd)
	//		{
	//			DragonFightManager manager = ((WorldProviderEnd)worldServerSrc.provider).getDragonFightManager();
	//
	//			if (manager != null)
	//			{
	//				try
	//				{
	//					BossInfoServer bossInfo = ReflectionHelper.getPrivateValue(DragonFightManager.class, manager, "field_186109_c", "bossInfo");
	//					if (bossInfo != null)
	//					{
	//						bossInfo.removePlayer(player);
	//					}
	//				}
	//				catch (UnableToAccessFieldException e)
	//				{
	//				}
	//			}
	//		}
	//
	//		for (PotionEffect potioneffect : player.getActivePotionEffects())
	//		{
	//			player.connection.sendPacket(new SPacketEntityEffect(player.getEntityId(), potioneffect));
	//		}
	//
	//		FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, dimSrc, dimDst);
	//
	//		return player;
	//	}
}
