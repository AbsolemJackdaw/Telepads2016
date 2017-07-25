package subaraki.telepads.handler;

import java.io.File;

import lib.item.ItemUtil;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

public class ConfigurationHandler {

	public Configuration configFile;
	public static ConfigurationHandler instance;

	public boolean allowDragonBlocking = true;
	public boolean allowParticles = true;

	public ConfigurationHandler(File file) {
		instance = this;

		configFile = new Configuration(file);
		configFile.load();

		allowDragonBlocking = configFile.getBoolean("allowJamming", "Various", true, "EnderDragon blocks passage");
		allowParticles = configFile.getBoolean("allowParticles", "Various", true, "Telepads spawn particles");

		configFile.save();
	}
}
