package Evil_Code_Influence.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import Evil_Code_Influence.InfluenceAPI;

public class CommandTpServant extends CommandBase{
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]){
		//cmd: /i tp <Name>
		if(sender instanceof Player == false){
			sender.sendMessage("§cThis command can only be run by in-game players");
			return true;
		}
		if(args.length < 1){
			sender.sendMessage("§cToo few arguments!");
			return false;
		}
		Player p = sender.getServer().getPlayer(args[0]);
		if(p == null){
			sender.sendMessage("§cPlayer not found!");
			return false;
		}
		if(InfluenceAPI.checkIsMaster(((Player)sender).getUniqueId(), p.getUniqueId()) == false){
			sender.sendMessage("§cYou are not the master of "+p.getName());
			return true;
		}
		sender.sendMessage(prefix+"§aTeleporting to §7"+p.getName()+"§a...");
		((Player)sender).teleport(p);
		
		return true;
	}
}
