package Evil_Code_Influence.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Evil_Code_Influence.Influence;
import Evil_Code_Influence.InfluenceAPI;

public class CommandTpServant implements CommandExecutor{
	private Influence plugin;
	
	public CommandTpServant(){
		plugin = Influence.getPlugin();
		plugin.getCommand("tpservant").setExecutor(this);
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]){
		//cmd:   /tpservant <Name>
		if(sender instanceof Player == false){
			sender.sendMessage("§cThis command can only be run by in-game players");
			return true;
		}
		if(args.length < 1){
			sender.sendMessage("§cToo few arguments!");
			return false;
		}
		Player p = plugin.getServer().getPlayer(args[0]);
		if(p == null){
			sender.sendMessage("§cPlayer not found!");
			return false;
		}
		if(InfluenceAPI.checkIsMaster(((Player)sender).getUniqueId(), p.getUniqueId()) == false){
			sender.sendMessage("§cYou are not the master of "+p.getName());
			return true;
		}
		sender.sendMessage(Influence.prefix+"§aTeleporting to §7"+p.getName()+"§a...");
		((Player)sender).teleport(p);
		
		return true;
	}
}
