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
import net.minecraft.util.RegistryKey;
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

        super(PropertiesWrapper.getItemProperties().stacksTo(8).tab(ItemGroup.TAB_MATERIALS));

        setRegistryName(Telepads.MODID, "ender_bead_necklace");
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
    {

        if (ConfigData.disableNecklaceUsage)
        {
            if (!world.isClientSide)
                player.sendMessage(new StringTextComponent("This Functionality has been disabled by the server operator."), player.getUUID());
            return super.use(world, player, hand);
        }

        if (!world.isClientSide)
        {

            WorldDataHandler wdh = WorldDataHandler.get(world);

            List<TelepadEntry> locations = wdh.getEntries();
            List<TelepadEntry> thisDim = new ArrayList<TelepadEntry>();

            if (locations.isEmpty())
                return super.use(world, player, hand); // pass

            RegistryKey<World> dim = world.dimension();

            locations.stream().filter(filter -> filter.dimensionID.equals(dim) && filter.canUse(player.getUUID()) && !filter.isPowered)
                    .forEach(telepad -> thisDim.add(telepad));

            if (thisDim.isEmpty())
                return super.use(world, player, hand);// pass

            double distance = Double.MAX_VALUE;
            TelepadEntry closestEntry = null;

            for (TelepadEntry entry : thisDim)
            {

                double distanceSQ = entry.position.distSqr(player.getX(), player.getY(), player.getZ(), true);

                if (distance > distanceSQ)
                {
                    distance = distanceSQ;
                    closestEntry = entry;
                }
            }

            if (closestEntry != null)
            {

                player.getItemInHand(hand).shrink(1);
                if (!player.isCreative())
                    player.inventory.add(new ItemStack(Items.STRING, world.random.nextInt(2) + 1));
                Teleport.teleportEntityInsideSameDimension(player, closestEntry.position.south().west());

                world.playSound((PlayerEntity) null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundCategory.NEUTRAL, 0.6F,
                        0.4F / (random.nextFloat() * 0.4F + 0.8F));
                world.playSound((PlayerEntity) null, player.getX(), player.getY(), player.getZ(), SoundEvents.GLASS_BREAK, SoundCategory.NEUTRAL, 0.1F,
                        0.4F / (random.nextFloat() * 0.4F + 0.8F));
                world.playSound((PlayerEntity) null, player.getX(), player.getY(), player.getZ(), SoundEvents.WOOL_BREAK, SoundCategory.NEUTRAL, 1.0F,
                        0.4F / (random.nextFloat() * 0.4F + 0.8F));
            }

        }

        return super.use(world, player, hand);
    }
}
