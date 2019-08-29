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
	
	public int teleport_seconds;
	
	public int expConsume;
	public int lvlConsume;
	public boolean consumeLvl;

	
	public ConfigurationHandler(File file) {
		instance = this;

		configFile = new Configuration(file);
		configFile.load();

		allowDragonBlocking = configFile.getBoolean("allowJamming", "Various", true, "EnderDragon blocks passage");
		allowParticles = configFile.getBoolean("allowParticles", "Various", true, "Telepads spawn particles");

		allowAnvilPearls = configFile.getBoolean("Ender Bead Creation", "Various", true, "Allow creation of Ender Beads");

		consumeLvl = configFile.getBoolean("Level Penalty", "TP Penalty", false, "Wether to consume levels or exp units");
		expConsume = configFile.getInt("Teleport Cost", "TP Penalty", 0, 0, Integer.MAX_VALUE, "Experience consumed (in units, not levels) per teleport. If the demand is higher then a exp bar cap, only a level will be substracted");
		lvlConsume = configFile.getInt("Teleport Cost", "TP Penalty", 0, 0, Integer.MAX_VALUE, "Levels consumed.");
		
		tp_locations = configFile.getStringList("teleport locations", "teleport", new String[]{}, "[x,y,z,dimension,locationName] locations can be defined exactly (100/64/100/0/Any Name really), with margin (-500#1000/64#128/0#500/-1#1,Some Location Name) or random (random/random/random/random/LocationNameHere). values can be mixed (-100#5000/random/100/0/yourLocationNameHere) is totally possible");

		teleport_seconds = configFile.getInt("Teleport Cooldown", "Various", 3, 1, 60, "Timer , in seconds, of how long a player has to wait before the teleport gui shows up while standing on a pad");
		
		configFile.save();
	}
}
