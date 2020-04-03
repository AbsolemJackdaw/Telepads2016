package subaraki.telepads.item;

import java.util.ArrayList;
import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
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

public class ItemEnderBead extends Item {

    public ItemEnderBead() {

        super(PropertiesWrapper.getItemProperties().maxStackSize(16).group(ItemGroup.MATERIALS));
        setRegistryName(Telepads.MODID, "ender_bead");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
    {

        if (ConfigData.disableBeadsUsage)
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
            {
                player.sendMessage(new StringTextComponent(ChatFormatting.ITALIC + "The Pearl Bounces... no telepads are found nearby"));
                return super.onItemRightClick(world, player, hand); // pass
            }

            int dim = player.dimension.getId();

            locations.stream().filter(filter -> filter.dimensionID == dim && filter.canUse(player.getUniqueID()) && !filter.isPowered)
                    .forEach(telepad -> thisDim.add(telepad));

            if (thisDim.isEmpty())
            {
                player.sendMessage(new StringTextComponent(ChatFormatting.ITALIC + "The Pearl Bounces... no telepads are found nearby"));
                return super.onItemRightClick(world, player, hand);// pass
            }

            TelepadEntry tpe = thisDim.get(world.rand.nextInt(thisDim.size()));

            if (tpe != null)
            {
                player.getHeldItem(hand).shrink(1);
                Teleport.teleportEntityInsideSameDimension(player, tpe.position.south().west());
                world.playSound((PlayerEntity) null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 0.2F,
                        0.4F / (random.nextFloat() * 0.4F + 0.8F));
                world.playSound((PlayerEntity) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ENDER_PEARL_THROW, SoundCategory.NEUTRAL, 0.5F,
                        0.4F / (random.nextFloat() * 0.4F + 0.8F));
                world.playSound((PlayerEntity) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.NEUTRAL, 0.6F,
                        0.4F / (random.nextFloat() * 0.4F + 0.8F));

            }
            else
            {
                world.playSound((PlayerEntity) null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_SLIME_BLOCK_PLACE, SoundCategory.NEUTRAL, 0.5F,
                        0.4F / (random.nextFloat() * 0.4F + 0.8F));
                player.sendMessage(new StringTextComponent(ChatFormatting.ITALIC + "The Pearl Bounces... no telepads are found nearby"));
            }

        }
        return super.onItemRightClick(world, player, hand);
    }
}
