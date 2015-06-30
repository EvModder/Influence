package Evil_Code_Influence.listeners;

import java.util.UUID;

import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;

import Evil_Code_Influence.Influence;
import Evil_Code_Influence.InfluenceAPI;
import Evil_Code_Influence.master.Master;
import Evil_Code_Influence.servant.AbilityConfig.Ability;

public class Listener_PlayerAction implements Listener{
	private Influence plugin;
	
	public Listener_PlayerAction(){
		plugin = Influence.getPlugin();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent evt){
		if(!evt.isCancelled()) evt.setCancelled(checkActionBlocked(evt.getPlayer(), Ability.BREAK_BLOCKS));
	}
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent evt){
		if(!evt.isCancelled()) evt.setCancelled(checkActionBlocked(evt.getPlayer(), Ability.PLACE_BLOCKS));
	}
	@EventHandler
	public void onBucketEmpty(PlayerBucketEmptyEvent evt){
		if(!evt.isCancelled()) evt.setCancelled(checkActionBlocked(evt.getPlayer(), Ability.EMPTY_BUCKET));
	}
	@EventHandler
	public void onSleep(PlayerBedEnterEvent evt){
		if(!evt.isCancelled()) evt.setCancelled(checkActionBlocked(evt.getPlayer(), Ability.ENTER_BED));
	}
	@EventHandler
	public void onTeleport(PlayerTeleportEvent evt){
		/** 
		 * Allows enslaves players to teleport if the distance is < 20 blocks or if they are teleporting to their master's coords
		**/
		if(!evt.isCancelled() && evt.getTo().distanceSquared(evt.getFrom()) > 400){// 20 blocks
			Master master = InfluenceAPI.getMasterOf(evt.getPlayer().getUniqueId());
			
			if(master != null){
				Player p = plugin.getServer().getPlayer(master.getPlayerUUID());
				if(p != null && p.getLocation().getBlockX() == evt.getTo().getBlockX() &&
								p.getLocation().getBlockZ() == evt.getTo().getBlockZ()) return;
			}
			evt.setCancelled(checkActionBlocked(evt.getPlayer(), Ability.TELEPORT));
		}
	}
	@EventHandler
	public void onPlayerAttack(EntityDamageByEntityEvent evt){
		if(!evt.isCancelled() && evt.getDamager() instanceof Player && evt.getEntity() instanceof LivingEntity){
			Player player = (Player) evt.getDamager();
			evt.setCancelled(checkActionBlocked(player, Ability.ATTACK));
			
			if(evt.getEntity() instanceof Player){
				evt.setCancelled(checkActionBlocked(player, Ability.ATTACK_PLAYER));
				
				if(InfluenceAPI.checkIsMaster(((Player)evt.getEntity()).getUniqueId(), player.getUniqueId())){
					evt.setCancelled(checkActionBlocked(player, Ability.ATTACK_MASTER));
				}
			}
			else if(evt.getEntity() instanceof Monster){
				evt.setCancelled(checkActionBlocked(player, Ability.ATTACK_MONSTER));
			}
			else if(evt.getEntity() instanceof Creature){
				evt.setCancelled(checkActionBlocked(player, Ability.ATTACK_ANIMAL));
			}
		}
	}
	@EventHandler
	public void onInteractEntity(PlayerInteractEntityEvent evt){
		if(!evt.isCancelled()) evt.setCancelled(checkActionBlocked(evt.getPlayer(), Ability.INTERACT_ENTITY));
		if(evt.isCancelled()) return;
		
		// onEnterVehicle
		if(checkActionBlocked(evt.getPlayer(), Ability.RIDE_MOB) == false){
			
			EntityType type = evt.getRightClicked().getType();
			if(type == EntityType.MINECART || type == EntityType.BOAT){
				evt.setCancelled(true);
			}
			else if(type == EntityType.HORSE || type == EntityType.PIG){
				final UUID uuid = evt.getPlayer().getUniqueId();
				new BukkitRunnable(){@Override public void run(){
					if(plugin.getServer().getPlayer(uuid) != null) plugin.getServer().getPlayer(uuid).leaveVehicle();
				}}.runTaskLater(plugin, 1);
			}
		}
	}
	@EventHandler
	public void onInteractBlock(PlayerInteractEvent evt){
		if(!evt.isCancelled()){
			evt.setCancelled(checkActionBlocked(evt.getPlayer(), Ability.INTERACT_BLOCK));
			if(evt.getClickedBlock().getState() instanceof InventoryHolder){
				evt.setCancelled(checkActionBlocked(evt.getPlayer(), Ability.USE_CHESTS));
			}
		}
	}
	@EventHandler
	public void onPreCommand(PlayerCommandPreprocessEvent evt){
		if(!evt.isCancelled()){
			evt.setCancelled(checkActionBlocked(evt.getPlayer(), Ability.COMMANDS));
//			if(evt.isCancelled()) evt.getPlayer().sendMessage(
//					Influence.prefix+"§c Your current master does not allow you to use commands!");
		}
	}
	
	//
	public boolean checkActionBlocked(Player player, Ability action){
		if(InfluenceAPI.checkIfServantHasPermission(player, action) == false){
			player.sendMessage(Influence.prefix+"§c Your current master does not allow you to use §7Ability:"+action.name()+"§c!");
			return true;
		}
		else return false;
		
//		return (plugin.isServant(player.getUniqueId()) == false || plugin.getServant(player.getUniqueId()).hasAbility(action) ||
//				player.hasPermission("influence.servant.override."+action.name().toLowerCase().replace("_", "")) ||
//				player.hasPermission("influence.servant.override.*"));
	}
}
