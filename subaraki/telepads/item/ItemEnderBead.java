package subaraki.telepads.item;

import java.util.ArrayList;
import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import subaraki.telepads.capability.TelePadDataCapability;
import subaraki.telepads.capability.TelepadData;
import subaraki.telepads.handler.WorldDataHandler;
import subaraki.telepads.utility.TelepadEntry;
import subaraki.telepads.utility.masa.Teleport;

public class ItemEnderBead extends Item{


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

			TelepadEntry tpe = null;
			boolean found = false;

			while(!found)
			{
				if(thisDim.isEmpty())
				{
					found = true;
					break;
				}
				int randomEntry = world.rand.nextInt(thisDim.size());

				tpe = thisDim.get(randomEntry);

				if(!WorldDataHandler.get(world).contains(tpe) || WorldDataHandler.get(world).isEntryPowered(tpe))
				{
					thisDim.remove(tpe);
					tpe=null;
				}
				else
					found = true;

			}

			if(tpe != null)
			{
				player.getHeldItem(hand).shrink(1);
				Teleport.teleportEntityInsideSameDimension(player, tpe.position.south().west());
		        world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 0.2F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
		        world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ENDERPEARL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
				world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.NEUTRAL, 0.6F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

			}
			else
			{
		        world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_SLIME_PLACE, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
				player.sendMessage(new TextComponentString(ChatFormatting.ITALIC+"The Pearl Bounces... no telepads are found nearby"));
				return super.onItemRightClick(world, player, hand);
			}
		}
		return super.onItemRightClick(world, player, hand);
	}
}
