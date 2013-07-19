package me.jacklin213.chatalert;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener{
	
	public static ChatAlert plugin;
	
	public ChatListener(ChatAlert instance) {
		plugin = instance;
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event){
		Player p = event.getPlayer();
		final String playerName = p.getName();
		if (p.hasPermission("chatalert.alert")){
			String rawMessage = event.getMessage();
			String[] message = rawMessage.split(" ");
			for(int i = 0; i < message.length; i++){
				String txt = message[i];
				if (txt.startsWith("@")){
					String[] pName = txt.split("@");
					try {
						final Player player = Bukkit.getServer().getPlayerExact(pName[1]);
						if (player.isOnline()){
							if (plugin.onCooldown.contains(playerName)){
								p.sendMessage(plugin.chatPluginPrefix + "Tagging is on cooldown");
								event.setCancelled(true);
							} else {
								final Location location = player.getLocation();
								player.getWorld().playSound(location, Sound.NOTE_PIANO, 1.0F, pitch(13));
								// Note played after one sec
								Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
									public void run(){
										player.getWorld().playSound(location, Sound.NOTE_PIANO, 1.0F, pitch(18));
									}
								}, (long) 4);
								String formatMessage = ChatColor.WHITE + rawMessage;
								String cusMessage = formatMessage.replace(txt, ChatColor.GOLD + "" + ChatColor.UNDERLINE + txt + ChatColor.WHITE);
								event.setMessage(cusMessage);
								if (p.hasPermission("chatalert.nocooldown")){
									return;
								}
								plugin.onCooldown.add(playerName);
								Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
									public void run(){
										plugin.onCooldown.remove(playerName);
									}
								}, 200);
							}
						}	
					} catch (Exception ex){
						event.getPlayer().sendMessage(plugin.chatPluginPrefix + ChatColor.RED + "Invalid username");
						event.setCancelled(true);
					}
				}
			}
		} 
	}
	
	private Float pitch(int numberOfClicks){
		switch (numberOfClicks){
		case 0:
			return 0.5F;
		case 1:
		    return 0.53F;
		case 2:
		    return 0.56F;
		case 3:
		    return 0.6F;
		case 4:
		    return 0.63F;
		case 5:
			return 0.67F;
		case 6:
			return 0.7F;
		case 7:
			return 0.76F;
		case 8:
			return 0.8F;
		case 9:
			return 0.84F;
		case 10:
			return 0.9F;
		case 11:
			return 0.94F;
		case 12:
			return 1.0F;
		case 13:
			return 1.06F;
		case 14:
			return 1.12F;
		case 15:
			return 1.18F;
		case 16:
			return 1.26F;
		case 17:
			return 1.34F;
		case 18:
			return 1.42F;
		case 19:
			return 1.5F;
		case 20:
			return 1.6F;
		case 21:
			return 1.68F;
		case 22:
			return 1.78F;
		case 23:
			return 1.88F;
		case 24:
	    	return 2.0F;
		}
		return 0.0F;
	}
}
