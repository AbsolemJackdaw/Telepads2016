package subaraki.telepads.item;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import subaraki.telepads.handler.ConfigData;
import subaraki.telepads.handler.WorldDataHandler;
import subaraki.telepads.utility.PropertiesWrapper;
import subaraki.telepads.utility.TelepadEntry;
import subaraki.telepads.utility.masa.Teleport;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ItemEnderNecklace extends Item {

    private final Random random = new Random();

    public ItemEnderNecklace() {

        super(PropertiesWrapper.getItemProperties().stacksTo(8).tab(CreativeModeTab.TAB_MATERIALS));

    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {

        if (ConfigData.disableNecklaceUsage) {
            if (!world.isClientSide)
                player.sendMessage(new TextComponent("This Functionality has been disabled by the server operator."), player.getUUID());
            return super.use(world, player, hand);
        }

        if (!world.isClientSide) {

            WorldDataHandler wdh = WorldDataHandler.get(world);

            List<TelepadEntry> locations = wdh.getEntries();
            List<TelepadEntry> thisDim = new ArrayList<TelepadEntry>();

            if (locations.isEmpty())
                return super.use(world, player, hand); // pass

            ResourceKey<Level> dim = world.dimension();

            locations.stream().filter(filter -> filter.dimensionID.equals(dim) && filter.canUse(player.getUUID()) && !filter.isPowered)
                    .forEach(telepad -> thisDim.add(telepad));

            if (thisDim.isEmpty())
                return super.use(world, player, hand);// pass

            double distance = Double.MAX_VALUE;
            TelepadEntry closestEntry = null;

            for (TelepadEntry entry : thisDim) {

                double distanceSQ = entry.position.distSqr(player.getX(), player.getY(), player.getZ(), true);

                if (distance > distanceSQ) {
                    distance = distanceSQ;
                    closestEntry = entry;
                }
            }

            if (closestEntry != null) {

                player.getItemInHand(hand).shrink(1);
                if (!player.isCreative())
                    player.getInventory().add(new ItemStack(Items.STRING, world.random.nextInt(2) + 1));
                Teleport.teleportEntityInsideSameDimension(player, closestEntry.position.south().west());

                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.NEUTRAL, 0.6F,
                        0.4F / (random.nextFloat() * 0.4F + 0.8F));
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL, 0.1F,
                        0.4F / (random.nextFloat() * 0.4F + 0.8F));
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WOOL_BREAK, SoundSource.NEUTRAL, 1.0F,
                        0.4F / (random.nextFloat() * 0.4F + 0.8F));
            }

        }

        return super.use(world, player, hand);
    }
}
