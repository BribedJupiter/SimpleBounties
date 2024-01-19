package me.bribedjupiter.quests.Bounties;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import me.bribedjupiter.quests.Main;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class BountyEvents implements Listener {
	private final Main main;
	public BountyCommands  bountyCommands = null;

	public BountyEvents (Main main, Economy econ, Permission perms)
	{
		this.main = main;
	}

	@EventHandler
	public void onKill(PlayerDeathEvent e) {

		Player killed = null;
		Player killer = null;
		try {
			if (e.getEntity() instanceof Player) {
			    killed = (Player) e.getEntity();
			}
			if (e.getEntity().getKiller() instanceof Player) {
			    killer = (Player) e.getEntity().getKiller();
			}
			if (killed.getName() != killer.getName()) {
				
				if(Main.getDuelsApi().getArenaManager().isInMatch(killed) || Main.getDuelsApi().getArenaManager().isInMatch(killer)) {
					
					int killersKillCount = killer.getStatistic(Statistic.PLAYER_KILLS);
					killer.setStatistic(Statistic.PLAYER_KILLS, (killersKillCount - 1));
					
					int victimsDeathCount = killed.getStatistic(Statistic.DEATHS);
					killed.setStatistic(Statistic.DEATHS, (victimsDeathCount - 1));
					
				}
				else {
					//e.setDeathMessage(ChatColor.RED + killed + " has been murdered by " + killer);
					bountyCheck(killed, killer);
				}
			}
		} catch (Exception exception) {
			//exception.printStackTrace();
		}
		/*(catch (Exception ex) {
            String killed = e.getEntity().getName();
            String killer = "God";
            //e.setDeathMessage(ChatColor.RED + killed + " has been murdered by " + killer);
            bountyCheck(killed, killer);
        }*/
	}

	private void bountyCheck(Player killed, Player killer) {
		
		bountyCommands = main.getBountyCommands();
		if (bountyCommands == null) {
			Bukkit.getLogger().warning("BountyEvents has no reference to BountyCommands");
		}
		else {
			if (bountyCommands.isValidBounty(killed)) {
				bountyCommands.completeBounty(killed, killer);
			} else {
				Bukkit.getLogger().info("bountyCheck: isValidBounty is false");
			}
		}
		// check if killed player has a bounty on them
		// If was a valid Bounty
		// complete bounty

	}

}
