package Evil_Code_Influence.commands;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Evil_Code_Influence.Influence;

public class CommandHireServant implements CommandExecutor{
	private Influence plugin;
	private CommandManager cmdManager;
	
	public CommandHireServant(CommandManager cmdManager){
		this.cmdManager = cmdManager;
		plugin = Influence.getPlugin();
		plugin.getCommand("hireservant").setExecutor(this);
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]){
		//cmd:   /hireservant <Name> <$>
		if(sender instanceof Player == false){
			sender.sendMessage("§cThis command can only be run by in-game players");
			return true;
		}
		if(args.length < 2){
			sender.sendMessage("§cToo few arguments!");
			return false;
		}
		Player p = plugin.getServer().getPlayer(args[0]);
		if(p == null){
			sender.sendMessage("§cPlayer not found!");
			return false;
		}
		if(p.getName().equals(sender.getName())){
			sender.sendMessage("§cYou cannot hire yourself");
			return false;
		}
		double wage;
		try{wage = Double.parseDouble(args[1]);}
		catch(NumberFormatException e){
			sender.sendMessage("§cInvalid wage! Please enter a positive number");
			return false;
		}
		if(wage < 0) wage = 0;
		
		hireServant((Player)sender, p, wage);
		
		return true;
	}
	
	public void hireServant(Player employer, Player target, Double wage){
		
		Set<UUID> uuid = new HashSet<UUID>();
		uuid.add(target.getUniqueId());
		
		if(cmdManager.addTradeOffer(new TradeOffer(employer, target, uuid, null, -wage))){//the price of become the servant is -wage
			target.sendMessage(Influence.prefix +
					" §7"+employer.getName()+CommandManager.msgC+" would like to offer you §a"+wage+CommandManager.msgC +
					" in exchange for you becoming §7" +
					employer.getName()+CommandManager.msgC+'\''+"§7s"+CommandManager.msgC+" servant.");
		}
	}
}
