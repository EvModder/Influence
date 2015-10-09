package Evil_Code_Influence.commands;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.ess3.api.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;

import Evil_Code_Influence.Influence;
import Evil_Code_Influence.InfluenceAPI;
import Evil_Code_Influence.VaultHook;
import Evil_Code_Influence.master.Master;
import Evil_Code_Influence.servant.Servant;

public class CommandUtils {
	private static Influence plugin;
	private static Random rand = new Random();
	
	public CommandUtils(){
		plugin = Influence.getPlugin();
	}
	
	@SuppressWarnings("deprecation")
	public static Set<Player> getTargetPlayers(CommandSender sender, String arg){
		Set<Player> targets = new HashSet<Player>();
		
		Player p = plugin.getServer().getPlayer(arg);
		if(p != null) targets.add(p);
		
		else if(plugin.getServer().getOnlinePlayers().size() > 0){
			arg = arg.toLowerCase();
			
			if(arg.equals("all") || arg.equals("@a") || arg.equals("@all")){
				int maxDist = 0;
				if(sender instanceof Player && arg.startsWith("@a[") && arg.endsWith("]")){
					try{
						maxDist = Integer.parseInt(arg.replace("@a[", "").replace("]", ""));
						if(maxDist < 0) maxDist *= -1 ;
					}
					catch(IllegalArgumentException ex){}
				}
				if(maxDist == 0) targets.addAll(plugin.getServer().getOnlinePlayers());
				else{
					maxDist = maxDist*maxDist;
					Player pSender = (Player) sender;
					
					for(Player player : plugin.getServer().getOnlinePlayers()){
						if(player.getLocation().distanceSquared(pSender.getLocation()) <= maxDist) targets.add(player);
					}
				}
			}
			else if(arg.contains("@p") && sender instanceof Player){
				int maxDist = 0;
				if(arg.replace("@p", "").startsWith("[") && arg.endsWith("]")){
					try{
						maxDist = Integer.parseInt(arg.replace("@p[", "").replace("]", ""));
						if(maxDist < 0) maxDist = 0;
						else maxDist = maxDist * maxDist;//squared
					}
					catch(IllegalArgumentException ex){}
				}
				
				Player senderP = (Player) sender;
				Player closestP = null;
				double closestDist=0;
				for(Player player : plugin.getServer().getOnlinePlayers()){
					double dist = player.getLocation().distanceSquared(senderP.getLocation());
					
					if(closestP == null){
						if(maxDist == 0 || dist <= maxDist){
							closestP = player;
							closestDist = dist;
						}
					}
					else if(dist < closestDist){
						closestP = player;
						closestDist = dist;
					}
				}
				if(closestP != null) targets.add(closestP);
			}
			else if(arg.equals("@r")){
				// grab a random player
				List<Player> online = new ArrayList<Player>();
				online.addAll(plugin.getServer().getOnlinePlayers());
				
				targets.add(online.get(rand.nextInt(online.size())));
			}
		}
		return targets;
	}
	
	//TODO: add function that gets target servants from ANY master
	@SuppressWarnings("deprecation")
	public static Set<OfflinePlayer> getTargetPlayers(CommandSender sender, String arg, boolean includeOffline){
		Set<OfflinePlayer> targets = new HashSet<OfflinePlayer>();
		if(includeOffline == false){
			targets.addAll(getTargetPlayers(sender, arg));
			return targets;
		}
		
		OfflinePlayer p = plugin.getServer().getOfflinePlayer(arg);
		if(p.hasPlayedBefore()) targets.add(p);

		//Just too deadly-- Causes lag, is a killer greif option, and basically ruins the whole point of the plugin.
/**		else{
			arg = arg.toLowerCase();
			
			if(arg.equals("all") || arg.equals("@a") || arg.equals("@all")){
				targets.addAll(Arrays.asList(plugin.getServer().getOfflinePlayers()));
			}
			else if(arg.contains("@p")){
				// return most recently joined player. Larger times are more recent
				OfflinePlayer closestPlayer = null;
				long closestTime = -1;
				for(OfflinePlayer player : plugin.getServer().getOfflinePlayers()){
					if(player.getLastPlayed() > closestTime){
						closestPlayer = player;
						closestTime = player.getLastPlayed();
					}
				}
				if(closestPlayer != null) targets.add(closestPlayer);
			}
			else if(arg.equals("@r")){
				targets.add(plugin.getServer().getOfflinePlayers()[rand.nextInt(plugin.getServer().getOfflinePlayers().length)]);
			}
		}*/
		return targets;
	}
	
	@SuppressWarnings("deprecation")
	public static Set<Player> getTargetServants(CommandSender sender, String arg){
		Set<Player> targets = new HashSet<Player>();
		
		Player p = plugin.getServer().getPlayer(arg);
		if(p != null) if(InfluenceAPI.isServant(p.getUniqueId())) targets.add(p);
		
		else if(plugin.getServer().getOnlinePlayers().size() > 0){
			List<Player> servantsOnline = new ArrayList<Player>();
			for(Player player : plugin.getServer().getOnlinePlayers()){
				if(InfluenceAPI.isServant(player.getUniqueId())) servantsOnline.add(player);
			}
			if(servantsOnline.size() > 0){
				arg = arg.toLowerCase();
				
				if(arg.equals("all") || arg.equals("@a") || arg.equals("@all")){
					int maxDist = 0;
					if(sender instanceof Player && arg.startsWith("@a[") && arg.endsWith("]")){
						try{
							maxDist = Integer.parseInt(arg.replace("@a[", "").replace("]", ""));
							if(maxDist < 0) maxDist *= -1 ;
						}
						catch(IllegalArgumentException ex){}
					}
					if(maxDist == 0) targets.addAll(servantsOnline);
					else{
						maxDist = maxDist*maxDist;
						Player pSender = (Player) sender;
						
						for(Player player : servantsOnline){
							if(player.getLocation().distanceSquared(pSender.getLocation()) <= maxDist) targets.add(player);
						}
					}
				}
				else if(arg.contains("@p") && sender instanceof Player){
					int maxDist = 0;
					if(arg.replace("@p", "").startsWith("[") && arg.endsWith("]")){
						try{
							maxDist = Integer.parseInt(arg.replace("@p[", "").replace("]", ""));
							if(maxDist < 0) maxDist = 0;
							else maxDist = maxDist * maxDist;//squared
						}
						catch(IllegalArgumentException ex){}
					}
					
					Player senderP = (Player) sender;
					Player closestP = null;
					double closestDist=0;
					for(Player player : servantsOnline){
						double dist = player.getLocation().distanceSquared(senderP.getLocation());
						
						if(closestP == null){
							if(maxDist == 0 || dist <= maxDist){
								closestP = player;
								closestDist = dist;
							}
						}
						else if(dist < closestDist){
							closestP = player;
							closestDist = dist;
						}
					}
					if(closestP != null) targets.add(closestP);
				}
				else if(arg.equals("@r")){
					// grab a random player
					targets.add(servantsOnline.get(rand.nextInt(servantsOnline.size())));
				}
			}
		}
		return targets;
	}
	
	@SuppressWarnings("deprecation")
	public static Set<OfflinePlayer> getTargetServants(CommandSender sender, String arg, boolean includeOffline){
		Set<OfflinePlayer> targets = new HashSet<OfflinePlayer>();
		if(includeOffline == false){
			targets.addAll(getTargetServants(sender, arg));
			return targets;
		}
		
		OfflinePlayer p = plugin.getServer().getOfflinePlayer(arg);
		if(p.hasPlayedBefore()) if(InfluenceAPI.isServant(p.getUniqueId())) targets.add(p);
		else{
			List<OfflinePlayer> servants = new ArrayList<OfflinePlayer>();
			for(Servant servant : InfluenceAPI.getAllServants()){
				servants.add(plugin.getServer().getOfflinePlayer(servant.getPlayerUUID()));
			}
//			servants.removeAll(null);
			if(servants.size() > 0){
				arg = arg.toLowerCase();
				
				if(arg.equals("all") || arg.equals("@a") || arg.equals("@all")){
					targets.addAll(servants);
				}
				else if(arg.contains("@p")){
					// return the servant that has logged on most recently. Larger times are more recent
					OfflinePlayer closestPlayer = servants.get(0);
					long closestTime = closestPlayer.getLastPlayed();
					
					for(OfflinePlayer player : servants){
						if(player.getLastPlayed() > closestTime){
							closestPlayer = player;
							closestTime = player.getLastPlayed();
						}
					}
					targets.add(closestPlayer);
				}
				else if(arg.equals("@r")){
					targets.add(servants.get(rand.nextInt(servants.size())));
				}
			}
		}
		return targets;
	}
	
	@SuppressWarnings("deprecation")
	public static Set<Player> getTargetServants(Master master, String arg){
		Set<Player> targets = new HashSet<Player>();
		
		Player p = plugin.getServer().getPlayer(arg);
		if(p != null) if(master.hasServant(p.getUniqueId())) targets.add(p);
		
		else if(plugin.getServer().getOnlinePlayers().size() > 0){
			List<Player> servantsOnline = new ArrayList<Player>();
			for(Player player : plugin.getServer().getOnlinePlayers()){
				if(master.hasServant(player.getUniqueId())) servantsOnline.add(player);
			}
			if(servantsOnline.size() == 0) return targets;
			
			arg = arg.toLowerCase();
			Player pSender = plugin.getServer().getPlayer(master.getPlayerUUID());// if NULL, then the sender is offline
			
			if(arg.equals("all") || arg.equals("@a") || arg.equals("@all")){
				int maxDist = 0;
				if(pSender != null && arg.startsWith("@a[") && arg.endsWith("]")){
					try{
						maxDist = Integer.parseInt(arg.replace("@a[", "").replace("]", ""));
						if(maxDist < 0) maxDist *= -1 ;
					}
					catch(IllegalArgumentException ex){}
				}
				if(maxDist == 0) targets.addAll(servantsOnline);
				else{
					maxDist = maxDist*maxDist;
					
					for(Player player : plugin.getServer().getOnlinePlayers()){
						if(player.getLocation().distanceSquared(pSender.getLocation()) <= maxDist) targets.add(player);
					}
				}
			}
			else if(arg.contains("@p") && pSender != null){
				int maxDist = 0;
				if(arg.replace("@p", "").startsWith("[") && arg.endsWith("]")){
					try{
						maxDist = Integer.parseInt(arg.replace("@p[", "").replace("]", ""));
						if(maxDist < 0) maxDist = 0;
					}
					catch(IllegalArgumentException ex){}
				}
				
				Player closestP = null;
				double closestDist=0;
				for(Player player : plugin.getServer().getOnlinePlayers()){
					double dist = player.getLocation().distanceSquared(pSender.getLocation());
					
					if(closestP == null) if(maxDist == 0 || dist <= maxDist){
						closestP = player;
						closestDist = dist;
					}
					else if(dist < closestDist){
						closestP = player;
						closestDist = dist;
					}
				}
				if(closestP == null) targets.add(closestP);
			}
			else if(arg.equals("@r")){
				int maxDist = 0;
				if(pSender != null && arg.replace("@r", "").startsWith("[") && arg.endsWith("]")){
					try{
						maxDist = Integer.parseInt(arg.replace("@p[", "").replace("]", ""));
						if(maxDist < 0) maxDist *= -1 ;
					}
					catch(IllegalArgumentException ex){}
				}
				if(maxDist == 0) targets.add((Player) servantsOnline.get(rand.nextInt(servantsOnline.size())));
				else{
					maxDist = maxDist*maxDist;
					List<Player> servantsInRange = new ArrayList<Player>();
					for(Player player : servantsOnline){
						if(player.getLocation().distanceSquared(pSender.getLocation()) <= maxDist) servantsInRange.add(player);
					}
					
					targets.add(servantsInRange.get(rand.nextInt(servantsInRange.size())));
				}
			}
		}
		return targets;
	}
	
	@SuppressWarnings("deprecation")
	public static Set<OfflinePlayer> getTargetServants(Master master, String arg, boolean includeOffline){
		Set<OfflinePlayer> targets = new HashSet<OfflinePlayer>();
		if(includeOffline == false){
			targets.addAll(getTargetServants(master, arg));
			return targets;
		}
		
		OfflinePlayer p = plugin.getServer().getOfflinePlayer(arg);
		if(p != null) if(master.hasServant(p.getUniqueId())) targets.add(p);
		
		List<OfflinePlayer> servants = new ArrayList<OfflinePlayer>();
		for(OfflinePlayer player : plugin.getServer().getOfflinePlayers()){
			if(master.hasServant(player.getUniqueId())) servants.add(player);
		}
		
		arg = arg.toLowerCase();
		
		if(arg.equals("all") || arg.equals("@a") || arg.equals("@all")){
			targets.addAll(servants);
		}
		else if(arg.contains("@p")){
			// return the servant that has logged on most recently. Larger times are more recent
			OfflinePlayer closestPlayer = servants.get(0);
			long closestTime = closestPlayer.getLastPlayed();
			
			for(OfflinePlayer player : servants){
				if(player.getLastPlayed() > closestTime){
					closestPlayer = player;
					closestTime = player.getLastPlayed();
				}
			}
			targets.add(closestPlayer);
		}
		else if(arg.equals("@r")){
			targets.add(servants.get(rand.nextInt(servants.size())));
		}
		
		return targets;
	}
	
	public static boolean editEssentialsBalance(OfflinePlayer p, double amount){
		// check money
		boolean enough;
		try{enough = Economy.hasEnough(p.getName(), new BigDecimal(amount));}
		catch(UserDoesNotExistException e){enough = false;}
		
		if(enough){
			// take money
			try{
				if(amount > 0) Economy.add(p.getName(), new BigDecimal(amount));
				else Economy.substract(p.getName(), new BigDecimal(-amount));//add -sign to make neg value positive
			}
			// returns false if it encounters an error
			catch(NoLoanPermittedException e){return false;}catch(UserDoesNotExistException e){return false;}
		}
		return enough;
	}
	
	public static boolean transferMoneyFromTo(OfflinePlayer p1, OfflinePlayer p2, double amount){
		if(amount < 0){
			OfflinePlayer temp = p1;
			p1 = p2; p2 = temp;
		}
		
		if(VaultHook.vaultEnabled()){
			EconomyResponse r = VaultHook.econ.withdrawPlayer(p1, amount);
			if(r.transactionSuccess() == false) return false;
			
			r = VaultHook.econ.depositPlayer(p2, amount);
			if(r.transactionSuccess() == false){
				VaultHook.econ.depositPlayer(p1, amount);
				return false;
			}
		}
		else{
			if(CommandUtils.editEssentialsBalance(p1, -amount) == false) return false;
			if(CommandUtils.editEssentialsBalance(p2, amount) == false){
				CommandUtils.editEssentialsBalance(p1, amount);
				return false;
			}
		}
		return true;
	}
}
