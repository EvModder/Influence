package Evil_Code_Influence.commands;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Evil_Code_Influence.Influence;
import Evil_Code_Influence.InfluenceAPI;
import Evil_Code_Influence.master.Master;

public class CommandTradeServant implements CommandExecutor{
	private Influence plugin;
	private CommandManager cmdManager;
	
	public CommandTradeServant(CommandManager cmdManager){
		this.cmdManager = cmdManager;
		plugin = Influence.getPlugin();
		plugin.getCommand("tradeservant").setExecutor(this);
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]){
		//cmd:   /tradeservant <Name/all> <[Optional:$]> to <Name> for <Name/all> <[Optional:$]>
		if(sender instanceof Player == false){
			sender.sendMessage("§cThis command can only be run by in-game players");
			return true;
		}
		if(args.length < 5){
			sender.sendMessage("§cToo few arguments!");
			return false;
		}
		
		Player p = plugin.getServer().getPlayer(args[0]);
		if(p != null && InfluenceAPI.checkIsMaster(((Player)sender).getUniqueId(), p.getUniqueId()) == false){
			sender.sendMessage("§cYou are not the master of "+p.getName());
			return true;
		}
		Master master = InfluenceAPI.getMasterByUUID(((Player)sender).getUniqueId());
		if(master == null){
			sender.sendMessage("§4ERROR: §cYou do not own any servants");
			return true;
		}
		
		Set<OfflinePlayer> sellerServants = CommandUtils.getTargetServants(master, args[0], true);
		if(sellerServants.isEmpty()){
			sender.sendMessage("§cPlayer[Servant] not found!");
			return true;
		}
		boolean noCashOffer = false;
		double cashOffer = 0;
		try{cashOffer = Double.parseDouble(args[2]);}
		catch(NumberFormatException ex){noCashOffer = true;}
		if(cashOffer < 0){
			sender.sendMessage("§cInvalid cash offer! To charge a buyer, use the price variable");
			return false;
		}
		
		Player pTo = plugin.getServer().getPlayer((noCashOffer) ? args[2] : args[3]);
		if(pTo == null){
			sender.sendMessage("§cPlayer[To] not found!");
			return false;
		}
		//----------- Servants of other master ----------------------------------------------
		Master master2 = InfluenceAPI.getMasterByUUID(pTo.getUniqueId());
		if(master2 == null){
			sender.sendMessage("§4ERROR: §cCounterpart (§7"+pTo.getName()+"§c) has no servants to trade for!");
			return true;
		}
		
		Set<OfflinePlayer> buyerServants = CommandUtils.getTargetServants(master2, (noCashOffer) ? args[4] : args[5], true);
		if(buyerServants.isEmpty()){
			sender.sendMessage("§cCounterpart (§7"+pTo.getName()+"§c) does not own the specified servant!");
			return true;
		}
		//-----------------------------------------------------------------------------------
		
		if(args.length > 6){
			sender.sendMessage("§cToo many arguments!\n§7Note: You cannot both offer and demand a cash bonus in a single trade");
			return false;
		}
		double price = 0;
		if(args.length == 6){
			try{price = Double.parseDouble(args[5]);}
			catch(NumberFormatException ex){
				sender.sendMessage("§cInvalid price! Please enter a number value");
				return false;
			}
		}
		if(price < 0){
			sender.sendMessage("§cInvalid price!");
			return false;
		}
		
		// Send trade offer
		StringBuilder sellerServantNames = new StringBuilder();
		Set<UUID> sellerServantUUIDS = new HashSet<UUID>();
		
		for(OfflinePlayer servant : sellerServants){
			sellerServantUUIDS.add(servant.getUniqueId());
			sellerServantNames.append(servant.getName()); sellerServantNames.append("§6, §7");
		}
		StringBuilder buyerServantNames = new StringBuilder();
		Set<UUID> buyerServantUUIDS = new HashSet<UUID>();
		
		for(OfflinePlayer servant : buyerServants){
			buyerServantUUIDS.add(servant.getUniqueId());
			buyerServantNames.append(servant.getName()); buyerServantNames.append("§6, §7");
		}
		
		pTo.sendMessage(Influence.prefix+" §7"+sender.getName()+"§6 is offering to trade their servant(s): " + 
						sellerServantNames.substring(0, sellerServantNames.length()-6) +
						((cashOffer != 0) ? "§6 and §c"+cashOffer+"$§6 out of their account" : "") +
						"§6 for your servant(s): " +
						buyerServantNames.substring(0, buyerServantNames.length()-6) +
						((price != 0) ? "§6 and §c"+price+"$§6 out of your account." : "§6."));
		
		sender.sendMessage(Influence.prefix+"§a Offer sent!");
		
		cmdManager.addTradeOffer(new TradeOffer(sender, pTo, sellerServantUUIDS, buyerServantUUIDS, cashOffer, price));
		return true;
	}
}
