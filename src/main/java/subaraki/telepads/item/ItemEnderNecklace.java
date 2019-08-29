package subaraki.telepads.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import subaraki.telepads.capability.TelePadDataCapability;
import subaraki.telepads.capability.TelepadData;
import subaraki.telepads.handler.WorldDataHandler;
import subaraki.telepads.utility.TelepadEntry;
import subaraki.telepads.utility.masa.Teleport;

public class ItemEnderNecklace extends Item {

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

		if(!world.isRemote)
		{

			TelepadData data = player.getCapability(TelePadDataCapability.CAPABILITY, null);

			List<TelepadEntry> locations = data.getEntries();
			List<TelepadEntry> thisDim = new ArrayList<TelepadEntry>();

			if(locations.isEmpty())
				return super.onItemRightClick(world, player, hand); //pass


			int dim = player.dimension;

			for(TelepadEntry entry : locations)
			{
				if(entry.dimensionID == dim )
				{
					thisDim.add(entry);
				}
			}

			if(thisDim.isEmpty())
				return super.onItemRightClick(world, player, hand); //pass

			double distance = Double.MAX_VALUE;
			TelepadEntry closestEntry = null;

			for(TelepadEntry entry : thisDim){

				if(!WorldDataHandler.get(world).contains(entry) || WorldDataHandler.get(world).isEntryPowered(entry))
					continue;

				double distanceSQ = entry.position.distanceSq(player.posX, player.posY, player.posZ);

				if(distance > distanceSQ)
				{
					distance = distanceSQ;
					closestEntry = entry;
				}
			}

			if(closestEntry != null)
			{
				BlockPos pos = new BlockPos(player.posX, player.posY, player.posZ);

				player.getHeldItem(hand).shrink(1);
				if(!player.isCreative())
					player.inventory.addItemStackToInventory(new ItemStack(Items.STRING , world.rand.nextInt(2)+1));
				Teleport.teleportEntityInsideSameDimension(player, closestEntry.position.south().west());

				world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.NEUTRAL, 0.6F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
				world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 0.1F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
				world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_CLOTH_BREAK, SoundCategory.NEUTRAL, 1.0F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
			}
		}

		return super.onItemRightClick(world, player, hand);
	}
}
