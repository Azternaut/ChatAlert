package me.jacklin213.chatalert;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatAlert extends JavaPlugin{
	
	public static ChatAlert plugin;
	
	public Logger log = Logger.getLogger("Minecraft");
	public ChatListener chatListener = new ChatListener(this);
	public ArrayList<String> onCooldown = new ArrayList<String>();
	public UpdateChecker updateChecker;
	public String chatPluginPrefix = ChatColor.GOLD + "[" + ChatColor.GREEN + "ChatAlert"+ ChatColor.GOLD + "] " + ChatColor.RESET;
	
	@Override
	public void onEnable() {
		
		createConfig();
		//Update Checking
		Boolean updateCheck = Boolean.valueOf(getConfig().getBoolean("UpdateCheck"));

		this.updateChecker = new UpdateChecker(this, "http://dev.bukkit.org/server-mods/chatalert/files.rss");

		if ((updateCheck) && (this.updateChecker.updateNeeded())) {
		log.info(String.format("[%s] A new update is avalible, Version: %s", getDescription().getName(), this.updateChecker.getVersion()));
		log.info(String.format("[%s] Get it now from: %s", getDescription().getName(), this.updateChecker.getLink()));
		}
		
		//Register Events
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(chatListener, this);
		//Finish loading
		this.log.info(String.format("[%s] Enabled Version %s by jacklin213", getDescription()
				.getName(), getDescription().getVersion()));
	}
	
	@Override
	public void onDisable() {
		this.log.info(String.format("[%s] Disabled Version %s", getDescription()
				.getName(), getDescription().getVersion()));
	}
	
	public void createConfig(){
		File configFile = new File(getDataFolder() + File.separator + "config.yml");
		if (!configFile.exists()) {
			// Tells console its creating a config.yml
			this.getLogger().info(String.format("[%s] Cannot find config.yml, Generating now....", getDescription().getName()));
			this.getConfig().options().copyDefaults(true);
			this.saveDefaultConfig();
			this.getLogger().info(String.format("[%s] Config generated !", getDescription().getName()));
		}
	}
}
