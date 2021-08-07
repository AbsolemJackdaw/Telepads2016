package subaraki.telepads.item;

import net.minecraft.network.chat.Style;
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
import net.minecraft.world.level.Level;
import subaraki.telepads.handler.ConfigData;
import subaraki.telepads.handler.WorldDataHandler;
import subaraki.telepads.mod.Telepads;
import subaraki.telepads.utility.PropertiesWrapper;
import subaraki.telepads.utility.TelepadEntry;
import subaraki.telepads.utility.masa.Teleport;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ItemEnderBead extends Item {

    Random random = new Random();

    public ItemEnderBead() {

        super(PropertiesWrapper.getItemProperties().stacksTo(16).tab(CreativeModeTab.TAB_MATERIALS));
        setRegistryName(Telepads.MODID, "ender_bead");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {

        if (ConfigData.disableBeadsUsage) {
            if (!world.isClientSide)
                player.sendMessage(new TextComponent("This Functionality has been disabled by the server operator."), player.getUUID());
            return super.use(world, player, hand);
        }
        if (!world.isClientSide) {

            WorldDataHandler wdh = WorldDataHandler.get(world);

            List<TelepadEntry> locations = wdh.getEntries();
            List<TelepadEntry> thisDim = new ArrayList<TelepadEntry>();

            if (locations.isEmpty()) {
                player.sendMessage(new TextComponent("The Pearl Bounces... no telepads are found nearby").setStyle(Style.EMPTY.withItalic(true)), player.getUUID());
                return super.use(world, player, hand); // pass
            }

            ResourceKey<Level> dim = player.level.dimension();

            locations.stream().filter(filter -> filter.dimensionID == dim && filter.canUse(player.getUUID()) && !filter.isPowered)
                    .forEach(telepad -> thisDim.add(telepad));

            if (thisDim.isEmpty()) {
                player.sendMessage(new TextComponent("The Pearl Bounces... no telepads are found nearby").setStyle(Style.EMPTY.withItalic(true)), player.getUUID());
                return super.use(world, player, hand);// pass
            }

            TelepadEntry tpe = thisDim.get(world.random.nextInt(thisDim.size()));

            if (tpe != null) {
                player.getItemInHand(hand).shrink(1);
                Teleport.teleportEntityInsideSameDimension(player, tpe.position.south().west());
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL, 0.2F,
                        0.4F / (random.nextFloat() * 0.4F + 0.8F));
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_PEARL_THROW, SoundSource.NEUTRAL, 0.5F,
                        0.4F / (random.nextFloat() * 0.4F + 0.8F));
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.NEUTRAL, 0.6F,
                        0.4F / (random.nextFloat() * 0.4F + 0.8F));

            } else {
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SLIME_BLOCK_PLACE, SoundSource.NEUTRAL, 0.5F,
                        0.4F / (random.nextFloat() * 0.4F + 0.8F));
                player.sendMessage(new TextComponent("The Pearl Bounces... no telepads are found nearby").setStyle(Style.EMPTY.withItalic(true)), player.getUUID());
            }

        }
        return super.use(world, player, hand);
    }
}
