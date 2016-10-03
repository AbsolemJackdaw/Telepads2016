package subaraki.telepads.item;

import static lib.item.ItemRegistry.*;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import subaraki.telepads.mod.Telepads;

public class TelepadItems {

	public static Item toggler;
	public static Item transmitter;
	public static Item redstone_upgrade;
	private static String modid = Telepads.MODID;
	
	public static void loadItems(){
		
		toggler = new Item().setUnlocalizedName(modid+".toggler").setRegistryName(modid+".toggler").setCreativeTab(CreativeTabs.REDSTONE);
		transmitter = new Item().setUnlocalizedName(modid+".transmitter").setRegistryName(modid+".transmitter").setCreativeTab(CreativeTabs.REDSTONE);
		redstone_upgrade = new Item().setUnlocalizedName(modid+".upgrade").setRegistryName(modid+".upgrade").setCreativeTab(CreativeTabs.REDSTONE);
		
		register();
	}
	
	private static void register(){
		registerItem(redstone_upgrade);
		registerItem(transmitter);
		registerItem(toggler);

	}
	
	
	public static void registerRenders(){
		registerRender(toggler, "toggler", modid);
		registerRender(transmitter, "transmitter", modid);
		registerRender(redstone_upgrade, "redstone_upgrade", modid);
	}
}
