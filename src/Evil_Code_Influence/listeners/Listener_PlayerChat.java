package Evil_Code_Influence.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import Evil_Code_Influence.Influence;

public class Listener_PlayerChat implements Listener{
	private Influence plugin;
	
	public Listener_PlayerChat(){
		plugin = Influence.getPlugin();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent evt){
		
	}
}
