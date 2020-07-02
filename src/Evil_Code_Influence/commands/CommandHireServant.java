package Evil_Code_Influence.commands;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHireServant extends CommandBase{

	@Override
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
		
		if(CommandInfluenceOffer.addTradeOffer(new TradeOffer(employer, target, null, uuid, -wage))){
			
			target.sendMessage(new StringBuilder(prefix).append("§7").append(employer.getName())
					.append(msgC).append(" would like to offer you §a").append(wage)
					.append(msgC).append(" in exchange for you becoming §7").append(employer.getName())
					.append(msgC).append("'§7s").append(msgC).append(" servant.").toString());
		}
	}
}
