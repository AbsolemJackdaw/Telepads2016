package subaraki.telepads.item;

import static lib.item.ItemRegistry.registerItem;
import static lib.item.ItemRegistry.registerRender;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEnderEye;
import net.minecraft.item.ItemStack;
import subaraki.telepads.block.TelepadBlocks;
import subaraki.telepads.mod.Telepads;

public class TelepadItems {

	public static Item toggler;
	public static Item transmitter;
	public static Item redstone_upgrade;
	
	public static Item ender_bead;
	public static Item ender_bead_necklace;

	private static String modid = Telepads.MODID;

	private static ItemBlock telepad_block;

	public static void loadItems(){

		ender_bead = new ItemEnderBead().setMaxStackSize(16).setUnlocalizedName(modid+".ender_bead").setRegistryName("ender_bead").setCreativeTab(CreativeTabs.MATERIALS);
		ender_bead_necklace = new ItemEnderNecklace().setMaxStackSize(8).setUnlocalizedName(modid+".ender_bead_necklace").setRegistryName("ender_bead_necklace").setCreativeTab(CreativeTabs.MATERIALS);

		toggler = new Item().setUnlocalizedName(modid+".toggler").setRegistryName("toggler").setCreativeTab(CreativeTabs.REDSTONE);
		transmitter = new Item().setUnlocalizedName(modid+".transmitter").setRegistryName("transmitter").setCreativeTab(CreativeTabs.REDSTONE);
		redstone_upgrade = new Item().setUnlocalizedName(modid+".upgrade").setRegistryName("upgrade");
		telepad_block =  (ItemBlock) new ItemBlock(TelepadBlocks.blockTelepad){
			@Override
			public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
				if(stack.hasTagCompound()){
					if (stack.getTagCompound().hasKey("colorFrame")){
						int color = stack.getTagCompound().getInteger("colorFrame");
						EnumDyeColor edc = null;
						for(EnumDyeColor dye : EnumDyeColor.values())
							if(dye.getMapColor().colorValue == color)
								edc = dye;	
						tooltip.add("Feet : "+ (edc == null ? I18n.format("feet.color") : edc.getName()));
					}

					if (stack.getTagCompound().hasKey("colorBase")){
						int color = stack.getTagCompound().getInteger("colorBase");
						EnumDyeColor edc = null;
						for(EnumDyeColor dye : EnumDyeColor.values())
							if(dye.getMapColor().colorValue == color)
								edc = dye;	
						tooltip.add("Arrows : "+ (edc == null ? I18n.format("arrow.color") : edc.getName()));
					}
				}
			}
		}.setRegistryName(TelepadBlocks.blockTelepad.getRegistryName());

		register();
	}

	private static void register(){
		registerItem(redstone_upgrade);
		registerItem(transmitter);
		registerItem(toggler);
		registerItem(telepad_block);
		registerItem(ender_bead);
		registerItem(ender_bead_necklace);
	}


	public static void registerRenders(){
		registerRender(toggler, "toggler", modid);
		registerRender(transmitter, "transmitter", modid);
		registerRender(redstone_upgrade, "redstone_upgrade", modid);
		registerRender(telepad_block, "telepad", modid);
		registerRender(ender_bead, "ender_bead", modid);
		registerRender(ender_bead_necklace, "ender_bead_necklace", modid);
	}
}
