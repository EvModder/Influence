package Evil_Code_Influence;

import org.bukkit.plugin.Plugin;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {
	private static boolean vaultEnabled;
	public static Economy econ = null;
	public static Permission perms = null;
	public static Chat chat = null;
	
	public VaultHook(Plugin plugin){
		if(!setupEconomy(plugin)) plugin.getLogger().warning("Vault not found, falling back to EssentialsEco as economy base");
		else vaultEnabled = true;
		setupPermissions(plugin);
		setupChat(plugin);
	}
	
	private boolean setupEconomy(Plugin plugin) {
		if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	private boolean setupChat(Plugin plugin) {
		RegisteredServiceProvider<Chat> rsp = plugin.getServer().getServicesManager().getRegistration(Chat.class);
		chat = rsp.getProvider();
		return chat != null;
	}

	private boolean setupPermissions(Plugin plugin) {
		RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;
	}
	
	public static boolean vaultEnabled(){return vaultEnabled;}
}
