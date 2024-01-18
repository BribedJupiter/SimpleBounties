package me.bribedjupiter.quests;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger; // Vault

import org.bukkit.Bukkit;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.bribedjupiter.quests.Bounties.Bounty;
import me.bribedjupiter.quests.Bounties.BountyCommands;
import me.bribedjupiter.quests.Bounties.BountyEvents;
import me.bribedjupiter.quests.Bounties.TabCompletion;
// Vault
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public final class Main extends JavaPlugin {

	FileConfiguration config = getConfig();

	BountyCommands bountyCommands = new BountyCommands(this, econ, perms);
	BountyEvents bountyEvents = new BountyEvents(this, econ, perms);
	TabCompleter tabCompleter = new TabCompletion(this, perms);

	// Vault.
	private static final Logger log = Logger.getLogger("Minecraft");
	private static Economy econ = null;
	private static Permission perms = null;

	@Override
	public void onEnable() {
		// Plugin startup logic.
		config.options().copyDefaults(true); // Saves any new added key-value pairs as default so server doesn't get rid of them on restart. I think?
		saveConfig();
		getServer().getPluginCommand("bounty").setExecutor(bountyCommands);
		getServer().getPluginManager().registerEvents(bountyEvents, this);
		getCommand("bounty").setTabCompleter(tabCompleter);
		bountyCommands.clearBounties(); // so we have an empty bounty buffer and it all comes from saved data
		loadBounties();
		// Vault
		if (! setupEconomy()) {

			log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", this.getName()));
			getServer().getPluginManager().disablePlugin(this);

			return;

		}
		setupPermissions();
		System.out.println("SimpleBounties Enabled!");

	}

	@Override
	public void onDisable() {

		// Plugin shutdown logic
		saveBounties();
		config.options().copyDefaults(true);
		saveConfig();
		log.info(String.format("[%s] Disabled Version %s", this.getName(), config.getString("version")));

	}

	private void loadBounties() { // DATA STRUCTURE: SENDER, TARGET, REWARD
		Bukkit.getLogger().info("[Bounties] Loading bounties...");
		List<String> bountiesToLoad = new ArrayList<String>();
		List<String> tempBountyInfo = new ArrayList<String>();
		bountiesToLoad = config.getStringList("bounties");
		//Bukkit.getLogger().info("Bounties to load: " + bountiesToLoad.toString());
		int i = 0; // 0, 3, 6, 9, 12, etc.
		int t = 0; // temp counter, for use inside the sets of three
		for (String s : bountiesToLoad) {
			if (i % 3 <= 0) { // reset t every 3
				t = 0;
				//Bukkit.getLogger().info("loadBounties in Main - Reset at 3");
			}
			if (t == 0 || t == 1) {
				tempBountyInfo.add(s);
				t++;
				//Bukkit.getLogger().info("tempBountyInfo in main: " + tempBountyInfo.toString());
			}
			else if (t == 2) {
				tempBountyInfo.add(s);
				//Bukkit.getLogger().info("tempBountyInfo, should be three, in main: " + tempBountyInfo.toString());
				bountyCommands.loadBounty(tempBountyInfo); // Tells bountycommands to load a bounty with the tempinfo data
				tempBountyInfo.clear();
				//Bukkit.getLogger().info("tempBountyinfo cleared: " + tempBountyInfo.toString());
			}
			i++;
		}
	}

	private void saveBounties() {
		Bukkit.getLogger().info("[Bounties] Saving bounties...");
		List<String> tempBountyInfo = new ArrayList<String>();
		for (Bounty b: bountyCommands.bounties) {
			tempBountyInfo.addAll(bountyCommands.seperateBounty(b));
		}
		config.set("bounties", tempBountyInfo);
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	/*private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    } NOT NEEDED B/C THIS PLUGIN DOES NOT AFFECT CHAT, removed the Dependency for chat */

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;
	}

	public BountyCommands getBountyCommands() {
		return bountyCommands;
	}

	public static Economy getEconomy() {
		return econ;
	}

	public static Permission getPermissions() {
		return perms;
	}

}