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
	
	public boolean allowAnvilPearls = true;

	public int expConsume;
	
	public ConfigurationHandler(File file) {
		instance = this;

		configFile = new Configuration(file);
		configFile.load();

		allowDragonBlocking = configFile.getBoolean("allowJamming", "Various", true, "EnderDragon blocks passage");
		allowParticles = configFile.getBoolean("allowParticles", "Various", true, "Telepads spawn particles");

		allowAnvilPearls = configFile.getBoolean("Ender Bead Creation", "Various", true, "Allow creation of Ender Beads");
		
		expConsume = configFile.getInt("Teleport Cost", "Various", 0, 0, Integer.MAX_VALUE, "Experience consumed (in units, not levels) per teleport. If the demand is higher then a exp bar cap, only a level will be substracted");
		
		configFile.save();
	}
}
