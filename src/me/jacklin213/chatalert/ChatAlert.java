package me.jacklin213.chatalert;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

import me.jacklin213.chatalert.Updater.UpdateResult;
import me.jacklin213.chatalert.Updater.UpdateType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatAlert extends JavaPlugin{
	
	public static ChatAlert plugin;
	
	public Logger log;
	public ChatListener chatListener = new ChatListener(this);
	public ArrayList<String> onCooldown = new ArrayList<String>();
	public Updater updater;
	public String chatPluginPrefix = ChatColor.GOLD + "[" + ChatColor.GREEN + "ChatAlert"+ ChatColor.GOLD + "] " + ChatColor.RESET;
	private String tagColor;
	private String msgColor;
	private int cooldownTime;
	
	@Override
	public void onEnable() {
		setLogger();
		createConfig();
		//Update Checking
		Boolean updateCheck = Boolean.valueOf(getConfig().getBoolean("UpdateCheck"));
		Boolean autoUpdate = Boolean.valueOf(getConfig().getBoolean("AutoUpdate"));
		
		this.updateCheck(updateCheck, autoUpdate, 61677);
		
		//Register Events
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(chatListener, this);
		//Finish loading
		log.info(String.format("Enabled Version %s by jacklin213", getDescription().getVersion()));
	}
	
	@Override
	public void onDisable() {
		log.info(String.format("Disabled Version %s", getDescription().getVersion()));
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd,	String commandLabel, String[] args) {
		if (commandLabel.equalsIgnoreCase("chatalert") && sender.hasPermission("chatalert.reload")){
			reloadConfig();
			sender.sendMessage(chatPluginPrefix + ChatColor.GREEN + "All in check, Files reloaded, ChatColor:" + 
					getConfig().getString("MsgColor") + " Tagging color: " + getConfig().getString("Color"));
		}
		return false;
	}
	
	public void createConfig(){
		File configFile = new File(getDataFolder() + File.separator + "config.yml");
		if (!configFile.exists()) {
			// Tells console its creating a config.yml
			log.info("Cannot find config.yml, Generating now....");
			this.getConfig().options().copyDefaults(true);
			this.saveDefaultConfig();
			log.info("Config generated !");
		}
	}
	
	public String getMsgColor(){
		msgColor = ChatColor.translateAlternateColorCodes('&', getConfig().getString("MsgColor"));
		return msgColor;
	}
	
	public String getTagColor(){
		tagColor = ChatColor.translateAlternateColorCodes('&', getConfig().getString("Color"));
		return tagColor;
	}
	
	public Integer getCooldownTime(){
		String cdTime = getConfig().getString("CooldownTime","10");
		cooldownTime = Integer.parseInt(cdTime)*20;
		return cooldownTime;
	}
	
	// Every Plugin must have
	public void setLogger(){
		log = getLogger();
	}
	
	public void updateCheck(boolean updateCheck, boolean autoUpdate, int ID){
		if(updateCheck && (autoUpdate == false)){
			updater = new Updater(this, ID, this.getFile(), UpdateType.NO_DOWNLOAD, true);
			if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE) {
			    log.info("New version available! " + updater.getLatestName());
			}
			if (updater.getResult() == UpdateResult.NO_UPDATE){
				log.info("You are running the latest version of QuickBar+");
			}
		}
		if(autoUpdate && (updateCheck == false)){
			updater = new Updater(this, ID, this.getFile(), UpdateType.NO_VERSION_CHECK, true);
		} 
		if(autoUpdate && updateCheck){
			updater = new Updater(this, ID, this.getFile(), UpdateType.DEFAULT, true);
			if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE) {
			    log.info("New version available! " + updater.getLatestName());
			}
			if (updater.getResult() == UpdateResult.NO_UPDATE){
				log.info("You are running the latest version of QuickBar+");
			}
		}
	}
}
