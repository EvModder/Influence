package Evil_Code_Influence.commands;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import Evil_Code_Influence.Influence;

public class CommandManager implements TabExecutor{
	private Influence plugin;
	private int offerTimer;//default in config is 120seconds
	private Map<Long, TradeOffer> pendingTrades;
	
	CommandGiveServant give;
	CommandSellServant sell;
	CommandTradeServant trade;
	CommandReleaseServant release;
	CommandCollectServant collect;
	CommandPunishServant punish;
	CommandHireServant hire;
	CommandTpServant tp;
	CommandTphereServant tphere;
	CommandInvseeServant invsee;
	CommandEnderchestServant enderchest;
	CommandInfluenceGUI gui;

	public CommandManager(){
		plugin = Influence.getPlugin();
		offerTimer = plugin.getConfig().getInt("path/to/setting");//TODO: finish making config
		pendingTrades = new HashMap<Long, TradeOffer>();
		
		give = new CommandGiveServant();
		sell = new CommandSellServant(this);
		trade = new CommandTradeServant(this);
		release = new CommandReleaseServant();
		collect = new CommandCollectServant();
		punish = new CommandPunishServant();
		hire = new CommandHireServant();
		tp = new CommandTpServant();
		tphere = new CommandTphereServant();
		invsee = new CommandInvseeServant();
		enderchest = new CommandEnderchestServant();
		gui = new CommandInfluenceGUI();
//		for(String cmd : plugin.getDescription().getCommands().keySet()){
//			plugin.getCommand(cmd).setExecutor(this);
//		}
		plugin.getCommand("influence").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		String cmdName="";
		
		if(args.length != 0){
			cmdName = args[0].toLowerCase().replace("servant", "").replace("influence", "");
			String[] oldArgs = args;
			args = new String[args.length-1];
			for(int i=0; i<args.length; i++) args[i] = oldArgs[i+1];
		}
		
		if(cmdName.isEmpty() || cmdName.equals("?") || cmdName.equals("help")){
			sender.sendMessage("§"+randColor()+"+ §7§m--------------------§b §"+randColor()+'+');
			// Send help/info/commands
			for(String name : plugin.getDescription().getCommands().keySet()){
				Command cmd = plugin.getCommand(name);
				if(sender.hasPermission(cmd.getPermission())){
					sender.sendMessage("§8 - §7 "+cmd.getUsage()+"§f");//  -  "+cmd.getDescription());
				}
			}
			sender.sendMessage("§"+randColor()+"+ §7§m--------------------§b §"+randColor()+'+');
			
			//
			// The old version of /s help:
			//
			/*	sender.sendMessage(
			"§8 - §2/§7i help <#>§f  -  Display this menu\n" +
			"§8 - §2/§7i release <Name/all>§f  -  Release a servant from bondage\n" +
			"§8 - §2/§7i give <Name/all> to <Name>§f  -  Give a servant to another player\n" +
			"§8 - §2/§7i sell <Name/all> to <Name> for <$>§f  -  Offer to trade a servant for the specified sum\n" +
			"§8 - §2/§7i trade <Name/all> for <Name/all> <$>§f  -  Offer to trade a servant you own for another servant\n" +
			"§8 - §2/§7i hire <Name> <$>§f  -  Offer a sum of cash to someone in return for their service\n" +
			"§8 - §2/§7i punish <Name/all> <#amt>§f  -  Punish a servant by damaging their health the specified amount\n" +
			"§8 - §2/§7i collect <Name/all> <items/xp/all>§f  -  Collect items or experience from a servant" */
			//
			// The VERY OLD verstion of /s help (about 3 years old, completely irrelevant now after plugin rewrite):
			//
			/* 		"§8§l--- §7§o~ §6§o§lSlaveMaster Commands §7§o~ §8§l---\n" +
			"§51§8. /s on/off §6--§8 Toggle the plugin on and off\n" +
			"§52§8. /s grant [name] §6--§8 Grant a slave you own access to a command\n" +
			"§7       (eg., '/s deny §abuild§7' '/s deny §aempty bucket§7' '/s deny §c/home§7')\n" +
			"§53§8. /s deny  [name] §6--§8 Deny a slave you own access to a command\n" +
			"§54§8. /s clearperms [name] §6--§8 Remove all access perms you have given to a slave\n" +
			"§55§8. /s release [name] §6--§8 Release a slave from your service\n" +
			"§56§8. /s give [name] to [name] §6--§8 Give away one of your slaves to someone else\n" +
			"§57§8. /s tphere [name] §6--§8 Teleport a slave to yourself\n" +
			"§58§8. /s tp [name] §6--§8 Teleport yourself to a slave\n" +
			"§59§8. /s perms [name] §6--§8 See what permissions a slave of yours has\n" +
			"§510§8./s gather-all [name] §6-- §8 Collect a slave's inv. (Make sure you have room in yours)\n" + */
		}
		else if(cmdName.equals("gui")){
			gui.onCommand(sender, command, label, args);//TODO: basically everything
		}
		else if(cmdName.equals("release")){
			release.onCommand(sender, command, label, args);// DONE!
		}
		else if(cmdName.equals("tp")){
			tp.onCommand(sender, command, label, args);// DONE!
		}
		else if(cmdName.equals("tphere")){
			tphere.onCommand(sender, command, label, args);// DONE!
		}
		else if(cmdName.equals("give")){
			give.onCommand(sender, command, label, args);// DONE!
		}
		else if(cmdName.equals("sell")){
			sell.onCommand(sender, command, label, args);//TODO: finish sellServant() method - Done but needs testing
		}
		else if(cmdName.equals("trade")){
			trade.onCommand(sender, command, label, args);//TODO: write tradeServant() method - Done but needs testing
		}
		else if(cmdName.equals("hire")){
			hire.onCommand(sender, command, label, args);//TODO: write onCommand
		}
		else if(cmdName.equals("punish")){
			punish.onCommand(sender, command, label, args);// DONE!
		}
		else if(cmdName.equals("collect")){
			collect.onCommand(sender, command, label, args);// DONE! - Done but needs testing
		}
		else if(cmdName.equals("invsee")){
			invsee.onCommand(sender, command, label, args);// DONE! - Done but needs testing
		}
		else if(cmdName.equals("enderchest")){
			enderchest.onCommand(sender, command, label, args);// DONE! - Done but needs testing
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
		}
		else return false;
		return true;
	}
	
	// You can ignore everything below this point.
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if(args.length == 0 || args.length == 1){
			List<String> completions = new ArrayList<String>();
			for(String cmd : plugin.getDescription().getCommands().keySet()) completions.add(cmd);
			//
			List<String> possibleCompletions = TabCompletionHelper.getPossibleCompletionsForGivenArgs(args, completions);
			
//			if(args[0].length() < 3 || possibleCompletions.isEmpty()){
				return possibleCompletions;
//			}
//			// Just return the 1st item from the list.
//			else possibleCompletions.subList(0, 1);
		}
		return null;
	}
	
	private char randColor(){
		String colors = "1239ab";
		Random rand = new Random();
		return colors.charAt(rand.nextInt(colors.length()));
	}

	public void addTradeOffer(TradeOffer tradeOffer){
		for(TradeOffer offer : pendingTrades.values()){
			if(offer.buyerUUID.equals(tradeOffer.buyerUUID)){
				Player seller = plugin.getServer().getPlayer(tradeOffer.sellerUUID);
				Player buyer = plugin.getServer().getPlayer(tradeOffer.buyerUUID);
				if(seller != null && buyer != null){
					seller.sendMessage(Influence.prefix+" §7"+buyer.getName()
							+"§c already has pending offers at this time, try again later");
				}
				return;
			}
		}
		
		//put a timestamp on the offer. Once the offer has expired, it will be removed automatically.
		pendingTrades.put(Calendar.getInstance().getTimeInMillis(), tradeOffer);
		
		Player buyer = plugin.getServer().getPlayer(tradeOffer.buyerUUID);
		if(buyer != null){
			buyer.sendMessage(Influence.prefix +
				" §6Type §2/ifl accept§6 to accept this offer and §2/ifl deny§6 to deny it."
				+ " Offer expires in §c"+offerTimer+"§6 seconds.");
		}
		
		clearOffersLoop();
	}
	
	boolean running = false;
	private void clearOffersLoop(){
		if(running) return;
		running = true;
		
		new BukkitRunnable(){
			@Override public void run(){
				long currentTime = Calendar.getInstance().getTimeInMillis();
				for(Long timeStamp : pendingTrades.keySet()){
					if((currentTime - timeStamp)/1000 >= offerTimer) pendingTrades.remove(timeStamp);
				}
				if(!pendingTrades.isEmpty()){
					running = false;
					clearOffersLoop();
				}
			}
		}.runTaskLater(plugin, 20);//20 ticks = 1 second
	}
	
	public int getOfferTimer(){
		return offerTimer;
	}
}
