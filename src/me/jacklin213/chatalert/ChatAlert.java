package me.jacklin213.chatalert;

import java.io.File;
import java.security.acl.Permission;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.earth2me.essentials.Essentials;

import me.jacklin213.chatalert.Updater.UpdateResult;
import me.jacklin213.chatalert.Updater.UpdateType;
import net.milkbowl.vault.chat.Chat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatAlert extends JavaPlugin{
	
	public static Chat chat = null;
	public static Permission perms = null;
	public Essentials essentials = null;
	
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
		
		//Essentials setup
		this.essCheck();
		
		//Vault setup
		if (getConfig().getBoolean("Advanced.UseVault")){
			if (!setupChat()) {
				log.severe("Disabled due to no Vault dependency found!");
				getServer().getPluginManager().disablePlugin(this);
				return;
			}
		}
		
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
			/*if (args.length == 2){
				if (args[0].equalsIgnoreCase("check")){
					String targetName = args[1];
					Player targetPlayer = Bukkit.getPlayer(targetName);
					if (targetPlayer != null ){
						String displayName = targetPlayer.getDisplayName();
						sender.sendMessage(displayName);
						return true;
					} else {
						sender.sendMessage("Player is not online");
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("match")){
					String phrase = args[1];
					if (phrase.length() >= 3){
						for (Player player : Bukkit.getOnlinePlayers()){
							String nick = player.getDisplayName();
							String nickName = nick.replaceAll("[]", " "); // this lines needs revising
							String playerName = player.getName();
							if (nickName.startsWith(phrase)){
								Bukkit.broadcastMessage(nickName + " Match found.");
								log.info(nickName + " Match found.");
								return true;
							} else if (playerName.startsWith(phrase)){
								Bukkit.broadcastMessage(playerName + "Match found.");
								log.info(playerName + " Match found.");
								return true;
							} else {
								Bukkit.broadcastMessage("No match found.");
								log.info("No match found.");
								return true;
							}
						}
					}
					sender.sendMessage("Too short");
					return true;
				}
			}*/
			if (args.length == 0){
				reloadConfig();
				sender.sendMessage(chatPluginPrefix + ChatColor.GREEN + "All in check, Files reloaded, ChatColor:" + 
						getConfig().getString("MsgColor") + " Tagging color: " + getConfig().getString("Color"));
			}
			return true;
		}
		return false;
	}
	
	public void createConfig(){
		File configFile = new File(getDataFolder() + File.separator + "config.yml");
		if (!configFile.exists()) {
			// Tells console its creating a config.yml
			log.info("Cannot find config.yml, Generating now....");
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
	
	private boolean setupChat() {		
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false	;
		}
		RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
		if (rsp == null) {
			return false;
		}
		chat = rsp.getProvider();
		return chat != null;
	}
	 
	private void essCheck() {
		if (getConfig().getBoolean("Advanced.UseEssentialsNickTag")) {
			Plugin essInstance = getServer().getPluginManager().getPlugin("Essentials");
			if (essInstance != null) {
				essentials = (Essentials) essInstance;
				log.info("Successfully hooked into Essentials, Essentials Nickname Tagging enabled");
			} else {
				log.severe("Unable to find Essentials, Essentials Nickname Tagging disabled");
				log.severe("Set UseEssentialsNickTag to false in your config to ignore this message");
				essentials = null;
			}
		}
	}

	 // Every Plugin must have
	private void setLogger(){
		log = getLogger();
	}	
	
	private void updateCheck(boolean updateCheck, boolean autoUpdate, int ID){
		if(updateCheck && (autoUpdate == false)){
			updater = new Updater(this, ID, this.getFile(), UpdateType.NO_DOWNLOAD, true);
			if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE) {
				log.info("New version available! " + updater.getLatestName());
			}
			if (updater.getResult() == UpdateResult.NO_UPDATE){
				log.info(String.format("You are running the latest version of %s", getDescription().getName()));
			 	}
		 	}
		if (autoUpdate && (updateCheck == false)){
			updater = new Updater(this, ID, this.getFile(), UpdateType.NO_VERSION_CHECK, true);
		}		 
		if (autoUpdate && updateCheck){
			updater = new Updater(this, ID, this.getFile(), UpdateType.DEFAULT, true);
			if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE) {
				log.info("New version available! " + updater.getLatestName());
			}
			if (updater.getResult() == UpdateResult.NO_UPDATE){
				log.info(String.format("You are running the latest version of %s", getDescription().getName()));
			}
		}
	}
}
