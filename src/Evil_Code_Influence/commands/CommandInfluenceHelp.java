package Evil_Code_Influence.commands;

import java.util.Random;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import Evil_Code_Influence.Influence;

public class CommandInfluenceHelp extends CommandBase{
	
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]){
		//cmd: /i help <#>
		if(args.length != 0){
			PluginCommand cmd = CommandManager.getCommand(args[0].toLowerCase().replace("servant", "").replace("influence", ""));
			if(cmd != null){
				sender.sendMessage("§2 - §6 "+cmd.getUsage()+"\n§2  >§7  "+cmd.getDescription());
				return true;
			}
		}
		//grab plugin instance
		Influence plugin = Influence.getPlugin();
		
		sender.sendMessage(new StringBuilder("§")
		.append(randColor()).append("+ §7§m--------------------§b §").append(randColor()).append('+').toString());
		
		// Send help/info/commands
		for(String name : plugin.getDescription().getCommands().keySet()){
			Command cmd = plugin.getCommand(name);
			if(sender.hasPermission(cmd.getPermission())){
				sender.sendMessage("§8 - §7 "+cmd.getUsage());//+"§f  -  "+cmd.getDescription());
			}
		}
		sender.sendMessage(new StringBuilder("§")
		.append(randColor()).append("+ §7§m--------------------§b §").append(randColor()).append('+').toString());
		return true;
	}
	
	Random rand = new Random();
	private char randColor(){
		String colors = "1239ab";
		return colors.charAt(rand.nextInt(colors.length()));
	}
}
