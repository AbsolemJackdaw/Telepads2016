package subaraki.telepads.handler;

import java.io.File;

import lib.item.ItemUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

public class ConfigurationHandler {

	public Configuration configFile;
	public static ConfigurationHandler instance;

	public ItemStack pad_recipe_glass;
	public ItemStack pad_recipe_pearl;
	public ItemStack pad_recipe_compass;
	public ItemStack pad_recipe_iron;

	public ItemStack toggler_recipe_repeater;
	public ItemStack toggler_recipe_dust;
	public ItemStack toggler_recipe_block;

	public ItemStack transmitter_recipe_iron;
	public ItemStack transmitter_recipe_dust;
	public ItemStack transmitter_recipe_diamond;

	public boolean allowDragonBlocking = true;
	public boolean allowParticles = true;

	public ConfigurationHandler(File file) {
		instance = this;

		configFile = new Configuration(file);
		configFile.load();

		pad_recipe_glass = ItemUtil.getItemStack(configFile.getString("glass", "Telepad Recipe", "minecraft:glass@0x1", "Top "));
		pad_recipe_pearl = ItemUtil.getItemStack(configFile.getString("pearl", "Telepad Recipe", "minecraft:ender_pearl@0x1", "Middle"));
		pad_recipe_compass = ItemUtil.getItemStack(configFile.getString("compass", "Telepad Recipe", "minecraft:compass@0x1", "Bottom Middle"));
		pad_recipe_iron = ItemUtil.getItemStack(configFile.getString("ironblock", "Telepad Recipe", "minecraft:iron_block@0x1", "Bottom Sides"));

		toggler_recipe_repeater = ItemUtil.getItemStack(configFile.getString("repeater", "Toggler Recipe", "minecraft:repeater@0x1", "Middle Sides"));
		toggler_recipe_dust = ItemUtil.getItemStack(configFile.getString("redstone", "Toggler Recipe", "minecraft:redstone@0x1", "Middle"));
		toggler_recipe_block = ItemUtil.getItemStack(configFile.getString("redstone block", "Toggler Recipe", "minecraft:redstone_block@0x1", "Bottom"));
		
		transmitter_recipe_iron = ItemUtil.getItemStack(configFile.getString("iron", "Transmitter Recipe", "minecraft:iron_ingot@0x1", "Top and Bottom"));
		transmitter_recipe_dust = ItemUtil.getItemStack(configFile.getString("glowstone", "Transmitter Recipe", "minecraft:glowstone@0x1", "Midddle Sides"));
		transmitter_recipe_diamond = ItemUtil.getItemStack(configFile.getString("diamond", "Transmitter Recipe", "minecraft:diamond@0x1", "Middle"));

		allowDragonBlocking = configFile.getBoolean("allowJamming", "Various", true, "EnderDragon blocks passage");
		allowParticles = configFile.getBoolean("allowParticles", "Various", true, "Telepads spawn particles");

		configFile.save();
	}
}
