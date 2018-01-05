package subaraki.telepads.handler;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigurationHandler {

	public Configuration configFile;
	public static ConfigurationHandler instance;

	public boolean allowDragonBlocking = true;
	public boolean allowParticles = true;
	
	public boolean allowAnvilPearls = true;

	public String[] tp_locations = new String[]{};
	public int expConsume;
	
	public ConfigurationHandler(File file) {
		instance = this;

		configFile = new Configuration(file);
		configFile.load();

		allowDragonBlocking = configFile.getBoolean("allowJamming", "Various", true, "EnderDragon blocks passage");
		allowParticles = configFile.getBoolean("allowParticles", "Various", true, "Telepads spawn particles");

		allowAnvilPearls = configFile.getBoolean("Ender Bead Creation", "Various", true, "Allow creation of Ender Beads");
		
		expConsume = configFile.getInt("Teleport Cost", "Various", 0, 0, Integer.MAX_VALUE, "Experience consumed (in units, not levels) per teleport. If the demand is higher then a exp bar cap, only a level will be substracted");
		
		tp_locations = configFile.getStringList("teleport locations", "teleport", new String[]{}, "[x,y,z,dimension,locationName] locations can be defined exactly (100/64/100/0/Any Name really), with margin (-500#1000/64#128/0#500/-1#1,Some Location Name) or random (random/random/random/random/LocationNameHere). values can be mixed (-100#5000/random/100/0/yourLocationNameHere) is totally possible");

		configFile.save();
	}
}
