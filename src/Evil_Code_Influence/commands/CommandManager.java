package Evil_Code_Influence.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import Evil_Code_Influence.Influence;

public class CommandManager extends CommandBase implements TabExecutor{
	private static Influence plugin;
	private static Map<String, Set<String>> prefixedCommands;
	public static Map<String, Set<String>> getPluginCommands(){return prefixedCommands;}
	
	public CommandManager(){
		plugin = Influence.getPlugin();
		
		// Load a list of this plugin's commands
		prefixedCommands = new HashMap<String, Set<String>>();
		Map<String, Map<String, Object>> commands = plugin.getDescription().getCommands();
		for(String cmdName : commands.keySet()){
			if(cmdName.equals("influence")) continue;
			
			Set<String> aliases = new HashSet<String>();
			
			for(String alias : plugin.getCommand(cmdName).getAliases()){
				aliases.add(alias.replace("infl", "").replace("servant", "").replace("influence", ""));
			}
			
			prefixedCommands.put(cmdName, aliases);
		}
		plugin.getCommand("collectservant").setExecutor(new CommandCollectServant());
		plugin.getCommand("enderchestservant").setExecutor(new CommandEnderchestServant());
		plugin.getCommand("giveservant").setExecutor(new CommandGiveServant());
		plugin.getCommand("influencehelp").setExecutor(new CommandInfluenceHelp());
		plugin.getCommand("hireservant").setExecutor(new CommandHireServant());
		plugin.getCommand("influencegui").setExecutor(new CommandInfluenceGUI());
		plugin.getCommand("influenceoffer").setExecutor(new CommandInfluenceOffer());
		plugin.getCommand("invseeservant").setExecutor(new CommandInvseeServant());
		plugin.getCommand("permsservant").setExecutor(new CommandPermsServant());
		plugin.getCommand("punishservant").setExecutor(new CommandPunishServant());
		plugin.getCommand("releaseservant").setExecutor(new CommandReleaseServant());
		plugin.getCommand("sellservant").setExecutor(new CommandSellServant());
		plugin.getCommand("setwageservant").setExecutor(new CommandSetWageServant());
		plugin.getCommand("tphereservant").setExecutor(new CommandTphereServant());
		plugin.getCommand("tpservant").setExecutor(new CommandTpServant());
		plugin.getCommand("tradeservant").setExecutor(new CommandTradeServant());
		plugin.getCommand("influence").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(args.length == 0 || args[0].equals("?")){
			plugin.getCommand("influencehelp").getExecutor().onCommand(sender, command, label, args);
			return true;
		}
		args[0] = args[0].toLowerCase().replace("servant", "").replace("influence", "").replace("infl", "");
		
		PluginCommand cmd = getCommand(args[0]);
		if(cmd != null){
			String[] oldArgs = args;
			args = new String[args.length-1];
			for(int i=0; i<args.length; ++i) args[i] = oldArgs[i+1];

			if(!cmd.getExecutor().onCommand(sender, cmd, oldArgs[0], args)){
				sender.sendMessage(cmd.getUsage());
			}
		}
		else sender.sendMessage(CommandBase.prefix+"Unknown command. Try §2/i ?"+CommandBase.msgC+" for Influence help");
		return true;
	}
	
	public static PluginCommand getCommand(String thisCmd){
//		thisCmd = thisCmd.toLowerCase().replace("servant", "").replace("influence", "");
		for(String cmdName : prefixedCommands.keySet()){
			if(thisCmd.equals(cmdName) || prefixedCommands.get(cmdName).contains(thisCmd)){
				return plugin.getCommand(cmdName);
			}
		}
		return null;
	}
	
/*	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		String cmdName="";
		
		if(args.length != 0){
			cmdName = args[0].toLowerCase().replace("servant", "").replace("influence", "");
			String[] oldArgs = args;
			args = new String[args.length-1];
			for(int i=0; i<args.length; ++i) args[i] = oldArgs[i+1];
		}
		
		if(cmdName.isEmpty() || cmdName.equals("?") || cmdName.equals("help")){
			sender.sendMessage("§"+randColor()+"+ §7§m--------------------§b §"+randColor()+'+');
			// Send help/info/commands
			for(String name : plugin.getDescription().getCommands().keySet()){
				Command cmd = plugin.getCommand(name);
				if(sender.hasPermission(cmd.getPermission())){
					sender.sendMessage("§8 - §7 "+cmd.getUsage());//+"§f  -  "+cmd.getDescription());
				}
			}
			sender.sendMessage("§"+randColor()+"+ §7§m--------------------§b §"+randColor()+'+');
			
			//
			// The old version of /s help:
			//
//			sender.sendMessage(
//			"§8 - §2/§7i help <#>§f  -  Display this menu\n" +
//			"§8 - §2/§7i release <Name/all>§f  -  Release a servant from bondage\n" +
//			"§8 - §2/§7i give <Name/all> to <Name>§f  -  Give a servant to another player\n" +
//			"§8 - §2/§7i sell <Name/all> to <Name> for <$>§f  -  Offer to trade a servant for the specified sum\n" +
//			"§8 - §2/§7i trade <Name/all> for <Name/all> <$>§f  -  Offer to trade a servant you own for another servant\n" +
//			"§8 - §2/§7i hire <Name> <$>§f  -  Offer a sum of cash to someone in return for their service\n" +
//			"§8 - §2/§7i punish <Name/all> <#amt>§f  -  Punish a servant by damaging their health the specified amount\n" +
//			"§8 - §2/§7i collect <Name/all> <items/xp/all>§f  -  Collect items or experience from a servant"
//			//
//			// The VERY OLD verstion of /s help (about 3.5 years old, completely irrelevant now after plugin rewrite):
//			//
//			"§8§l--- §7§o~ §6§o§lSlaveMaster Commands §7§o~ §8§l---\n" +
//			"§51§8. /s on/off §6--§8 Toggle the plugin on and off\n" +
//			"§52§8. /s grant [name] §6--§8 Grant a slave you own access to a command\n" +
//			"§7       (eg., '/s deny §abuild§7' '/s deny §aempty bucket§7' '/s deny §c/home§7')\n" +
//			"§53§8. /s deny  [name] §6--§8 Deny a slave you own access to a command\n" +
//			"§54§8. /s clearperms [name] §6--§8 Remove all access perms you have given to a slave\n" +
//			"§55§8. /s release [name] §6--§8 Release a slave from your service\n" +
//			"§56§8. /s give [name] to [name] §6--§8 Give away one of your slaves to someone else\n" +
//			"§57§8. /s tphere [name] §6--§8 Teleport a slave to yourself\n" +
//			"§58§8. /s tp [name] §6--§8 Teleport yourself to a slave\n" +
//			"§59§8. /s perms [name] §6--§8 See what permissions a slave of yours has\n" +
//			"§510§8./s gather-all [name] §6-- §8 Collect a slave's inv. (Make sure you have room in yours)");
			return true;
		}
		else if(cmdName.equals("gui")){
			return gui.onCommand(sender, command, label, args);//TODO: basically everything
		}
		else if(cmdName.equals("release")){
			return release.onCommand(sender, command, label, args);// DONE!
		}
		else if(cmdName.equals("tp")){
			return tp.onCommand(sender, command, label, args);// DONE!
		}
		else if(cmdName.equals("tphere")){
			return tphere.onCommand(sender, command, label, args);// DONE!
		}
		else if(cmdName.equals("give")){
			return give.onCommand(sender, command, label, args);// DONE!
		}
		else if(cmdName.equals("sell")){
			return sell.onCommand(sender, command, label, args);//TODO: finish sellServant() method - Done but needs testing
		}
		else if(cmdName.equals("trade")){
			return trade.onCommand(sender, command, label, args);//TODO: write tradeServant() method - Done but needs testing
		}
		else if(cmdName.equals("hire")){
			return hire.onCommand(sender, command, label, args);// DONE! - Done but needs testing
		}
		else if(cmdName.equals("punish")){
			return punish.onCommand(sender, command, label, args);// DONE!
		}
		else if(cmdName.equals("collect")){
			return collect.onCommand(sender, command, label, args);// DONE! - Done but needs testing
		}
		else if(cmdName.equals("setwage")){
			return setWage.onCommand(sender, command, label, args);// DONE! - Done but needs testing
		}
		else if(cmdName.equals("invsee")){
			return invsee.onCommand(sender, command, label, args);// DONE! - Done but needs testing
		}
		else if(cmdName.equals("enderchest")){
			return enderchest.onCommand(sender, command, label, args);// DONE! - Done but needs testing
		}
		// Player only commands beyond this point
		else if(sender instanceof Player == false){
			sender.sendMessage("§cThis command can only be run by in-game players");
			return true;
		}
		else if(cmdName.equals("accept")){//TODO: add "/i accept" for trade/sell deal confirmations - Done but needs testing
			UUID senderUUID = ((Player)sender).getUniqueId();
			
			for(long key : pendingTrades.keySet()){
				TradeOffer offer = pendingTrades.get(key);
				
				if(senderUUID.equals(offer.buyerUUID)){
					OfflinePlayer seller = plugin.getServer().getOfflinePlayer(offer.sellerUUID);
					
					sender.sendMessage(Influence.prefix+" §aYou accepted §7"+seller.getName()+"§a's offer!");
					if(seller.isOnline()){
						seller.getPlayer().sendMessage(Influence.prefix+" §7"+sender.getName()+"§a accepted your offer!");
					}
					if(pendingTrades.get(key).carryOutOffer() == false){
						sender.sendMessage("§4ERROR: Could not carry out transaction, trade cancelled");
						if(seller.isOnline()){
							seller.getPlayer().sendMessage("§4ERROR: Could not carry out transaction, trade cancelled");
						}
					}
					pendingTrades.remove(key);
					break;
				}
			}
			return true;
		}
		else if(cmdName.equals("deny")){//TODO: add "/i deny" for trade/sell deal expunging - Done but needs testing
			UUID senderUUID = ((Player)sender).getUniqueId();
			
			for(long key : pendingTrades.keySet()){
				if(senderUUID.equals(pendingTrades.get(key).buyerUUID)){
					OfflinePlayer seller = plugin.getServer().getOfflinePlayer(pendingTrades.get(key).sellerUUID);
					
					sender.sendMessage(Influence.prefix+" §cYou denied "+seller.getName()+"§c's offer");
					if(seller.isOnline()){
						seller.getPlayer().sendMessage(Influence.prefix+" §7"+sender.getName()+"§c denied your offer");
					}
					
					pendingTrades.remove(key);
					break;
				}
			}
			return true;
		}
		else return false;
	}*/
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if(args.length == 1){
			List<String> completions = new ArrayList<String>();
			
			for(String cmdName : prefixedCommands.keySet()){
				completions.add(cmdName);
				completions.addAll(prefixedCommands.get(cmdName));
			}
			
			List<String> possibleCompletions = TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, completions);
			
			return possibleCompletions;
		}
		return null;
	}
}
