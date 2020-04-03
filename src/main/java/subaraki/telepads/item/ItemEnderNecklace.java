package subaraki.telepads.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import subaraki.telepads.handler.ConfigData;
import subaraki.telepads.handler.WorldDataHandler;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.utility.PropertiesWrapper;
import subaraki.telepads.utility.TelepadEntry;
import subaraki.telepads.utility.masa.Teleport;

public class ItemEnderNecklace extends Item {

    public ItemEnderNecklace() {

        super(PropertiesWrapper.getItemProperties().maxStackSize(8).group(ItemGroup.MATERIALS));

        setRegistryName(Telepads.MODID, "ender_bead_necklace");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
    {

        if (ConfigData.disableNecklaceUsage)
        {
            if (!world.isRemote)
                player.sendMessage(new StringTextComponent("This Functionality has been disabled by the server operator."));
            return super.onItemRightClick(world, player, hand);
        }

        if (!world.isRemote)
        {

            WorldDataHandler wdh = WorldDataHandler.get(world);

            List<TelepadEntry> locations = wdh.getEntries();
            List<TelepadEntry> thisDim = new ArrayList<TelepadEntry>();

            if (locations.isEmpty())
                return super.onItemRightClick(world, player, hand); // pass

            int dim = player.dimension.getId();

            locations.stream().filter(filter -> filter.dimensionID == dim && filter.canUse(player.getUniqueID()) && !filter.isPowered)
                    .forEach(telepad -> thisDim.add(telepad));

            if (thisDim.isEmpty())
                return super.onItemRightClick(world, player, hand);// pass

            double distance = Double.MAX_VALUE;
            TelepadEntry closestEntry = null;

            for (TelepadEntry entry : thisDim)
            {

                double distanceSQ = entry.position.distanceSq(player.posX, player.posY, player.posZ, true);

                if (distance > distanceSQ)
                {
                    distance = distanceSQ;
                    closestEntry = entry;
                }
            }

            if (closestEntry != null)
            {

                player.getHeldItem(hand).shrink(1);
                if (!player.isCreative())
                    player.inventory.addItemStackToInventory(new ItemStack(Items.STRING, world.rand.nextInt(2) + 1));
                Teleport.teleportEntityInsideSameDimension(player, closestEntry.position.south().west());

                world.playSound((PlayerEntity) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.NEUTRAL, 0.6F,
                        0.4F / (random.nextFloat() * 0.4F + 0.8F));
                world.playSound((PlayerEntity) null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 0.1F,
                        0.4F / (random.nextFloat() * 0.4F + 0.8F));
                world.playSound((PlayerEntity) null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_WOOL_BREAK, SoundCategory.NEUTRAL, 1.0F,
                        0.4F / (random.nextFloat() * 0.4F + 0.8F));
            }

        }

        return super.onItemRightClick(world, player, hand);
    }
}
