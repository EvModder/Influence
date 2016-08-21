package Evil_Code_Influence.commands;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHireServant extends CommandBase{

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
		Player p = sender.getServer().getPlayer(args[0]);
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
		
		if(CommandInfluenceOffer.addTradeOffer(
				new TradeOffer(employer, target, uuid, null, -wage))){//the price of becoming the servant is -wage
			
			target.sendMessage(prefix +
					"§7"+employer.getName()+msgC+" would like to offer you §a"+wage+msgC +
					" in exchange for you becoming §7" +
					employer.getName()+msgC+'\''+"§7s"+msgC+" servant.");
		}
	}
}
