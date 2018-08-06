package subaraki.telepads.utility.masa;

import java.lang.invoke.MethodHandle;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldServer;
import net.minecraft.world.end.DragonFightManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToAccessFieldException;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.utility.masa.MethodHandleUtils.UnableToFindMethodHandleException;

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

	public static Entity teleportEntityToDimension(Entity entity, BlockPos pos, int dimDst)
	{
		double x = pos.getX();
		double y = pos.getY();
		double z = pos.getZ();
		
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		WorldServer worldDst = server.getWorld(dimDst);

		if (worldDst == null || net.minecraftforge.common.ForgeHooks.onTravelToDimension(entity, dimDst) == false)
		{
			return null;
		}

		// Load the chunk first
		int chunkX = (int) Math.floor(x / 16D);
		int chunkZ = (int) Math.floor(z / 16D);
		entity.getEntityWorld().getChunk((int) Math.floor(x / 16D), (int) Math.floor(z / 16D));

		if (entity instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP) entity;
			World worldOld = player.getEntityWorld();
			DummyTeleporter teleporter = new DummyTeleporter(worldDst);

			player.setLocationAndAngles(x, y, z, player.rotationYaw, player.rotationPitch);
			server.getPlayerList().transferPlayerToDimension(player, dimDst, teleporter);

			// See PlayerList#transferEntityToWorld()
			if (worldOld.provider.getDimension() == 1)
			{
				worldDst.spawnEntity(player);
			}

			// Teleporting FROM The End, remove the boss bar that would otherwise get stuck on
			if (worldOld.provider instanceof WorldProviderEnd)
			{
				removeDragonBossBarHack(player, (WorldProviderEnd) worldOld.provider);
			}

			player.setPositionAndUpdate(x, y, z);
			worldDst.updateEntityWithOptionalForce(player, false);
			player.addExperience(0);
			player.setPlayerHealthUpdated();
			// TODO update food level?
		}
		else
		{
			WorldServer worldSrc = (WorldServer) entity.getEntityWorld();

			// FIXME ugly special case to prevent the chest minecart etc from duping items
			if (entity instanceof EntityMinecartContainer)
			{
				((EntityMinecartContainer) entity).setDropItemsWhenDead(false);
			}

			worldSrc.removeEntity(entity);
			entity.isDead = false;
			worldSrc.updateEntityWithOptionalForce(entity, false);

			Entity entityNew = EntityList.createEntityByIDFromName(new ResourceLocation(EntityList.getEntityString(entity)), worldDst);

			if (entityNew != null)
			{
				copyDataFromOld(entityNew, entity);
				entityNew.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);

				boolean flag = entityNew.forceSpawn;
				entityNew.forceSpawn = true;
				worldDst.spawnEntity(entityNew);
				entityNew.forceSpawn = flag;

				worldDst.updateEntityWithOptionalForce(entityNew, false);
				entity.isDead = true;

				worldSrc.resetUpdateEntityTick();
				worldDst.resetUpdateEntityTick();
			}

			entity = entityNew;
		}

		return entity;
	}

	private static void removeDragonBossBarHack(EntityPlayerMP player, WorldProviderEnd provider)
	{
		// Somewhat ugly way to clear the Boss Info stuff when teleporting FROM The End
		DragonFightManager manager = provider.getDragonFightManager();

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
				Telepads.log.warn("TP: Failed to get DragonFightManager#bossInfo");
			}
		}
	}

	private static class DummyTeleporter extends Teleporter
	{
		public DummyTeleporter(WorldServer world)
		{
			super(world);
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
		public void removeStalePortalLocations(long worldTime)
		{
			// NO-OP
		}

		@Override
		public void placeInPortal(Entity entityIn, float rotationYaw)
		{
		}
	}


	////////////////////ENTITY UTILS///////////////////////////////

	private static MethodHandle methodHandle_Entity_copyDataFromOld;
	private static MethodHandle methodHandle_EntityLiving_canDespawn;

	static
	{
		try
		{
			methodHandle_Entity_copyDataFromOld = MethodHandleUtils.getMethodHandleVirtual(
					Entity.class, new String[] { "func_180432_n", "copyDataFromOld" }, Entity.class);
			methodHandle_EntityLiving_canDespawn = MethodHandleUtils.getMethodHandleVirtual(
					EntityLiving.class, new String[] { "func_70692_ba", "canDespawn" });
		}
		catch (UnableToFindMethodHandleException e)
		{
			Telepads.log.error("EntityUtils: Failed to get a MethodHandle for Entity#copyDataFromOld() or for EntityLiving#canDespawn()", e);
		}
	}

	public static void copyDataFromOld(Entity newEntity, Entity oldEntity)
	{
		try
		{
			methodHandle_Entity_copyDataFromOld.invokeExact(newEntity, oldEntity);
		}
		catch (Throwable e)
		{
			Telepads.log.error("Error while trying invoke Entity#copyDataFromOld()", e);
		}
	}
}
