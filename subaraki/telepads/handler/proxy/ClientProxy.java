package subaraki.telepads.handler.proxy;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import subaraki.telepads.block.TelepadBlocks;
import subaraki.telepads.gui.client.GuiRemoveTelepad;
import subaraki.telepads.item.TelepadItems;
import subaraki.telepads.tileentity.TileEntityTelepad;
import subaraki.telepads.tileentity.render.TileEntityTelepadSpecialRenderer;

public class ClientProxy extends ServerProxy {

	@Override
	public EntityPlayer getClientPlayer(){
		return Minecraft.getMinecraft().player;
	}

	@Override
	public void registerRenders() {
		TelepadItems.registerRenders();
		TelepadBlocks.registerRenders();
	}

	@Override
	public void openRemovalGui(EntityPlayer player) {
		Minecraft.getMinecraft().displayGuiScreen(new GuiRemoveTelepad(player));
	}

	@Override
	public void registerTileEntityAndRender() {
		GameRegistry.registerTileEntity(TileEntityTelepad.class, "TileEntityTelepad");
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTelepad.class, new TileEntityTelepadSpecialRenderer());
	}

	private Random rand = new Random();

	@Override
	public void createTelepadParticleEffect(BlockPos pos, boolean isStandingOnPlatform) {
		World world = Minecraft.getMinecraft().world;

		if (world == null )
			return;

		int maxParticleCount = (isStandingOnPlatform) ? 15 : 1;

		for (int particleCount = 0; particleCount < maxParticleCount; ++particleCount) {

			double posX = pos.getX() + 0.5f;
			double posY = pos.getY() + (rand.nextFloat() * 1.5f);
			double posZ = pos.getZ() + 0.5f;
			double velocityX = 0.0D;
			double volocityY = 0.0D;
			double velocityZ = 0.0D;
			int velocityXOffset = (rand.nextInt(2) * 2) - 1;
			int velocityZOffset = (rand.nextInt(2) * 2) - 1;

			velocityX = (rand.nextFloat() - 0.5D) * 0.125D;
			volocityY = (rand.nextFloat() - 0.5D) * 0.125D;
			velocityZ = (rand.nextFloat() - 0.5D) * 0.125D;
			velocityX = rand.nextFloat() * 1.0F * velocityXOffset;
			velocityZ = rand.nextFloat() * 1.0F * velocityZOffset;
			world.spawnParticle(EnumParticleTypes.PORTAL, posX, posY, posZ, velocityX, volocityY, velocityZ);
		}
	}
}
