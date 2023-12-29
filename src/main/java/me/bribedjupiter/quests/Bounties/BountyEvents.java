package me.bribedjupiter.quests.Bounties;

import me.bribedjupiter.quests.Main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.event.player.PlayerJoinEvent;

// This class will listen for player death events, get the players involved, and initiate a bounty check.
// This will also listen for player join events, and send them a messgae saying how many bounties they have on them

public class BountyEvents implements Listener {
    private final Main main;
    public BountyCommands  bountyCommands = null;

    public BountyEvents (Main main, Economy econ, Permission perms)
    {
        this.main = main;
        bountyCommands = main.getBountyCommands();
    }

    @EventHandler
    public void onKill(PlayerDeathEvent e) {
        try {
            String killed = e.getEntity().getName();
            String killer = e.getEntity().getKiller().getName();
            if (killed != killer) {
                bountyCheck(killed, killer);
            }
        } catch (Exception exception) {
            main.getLogger().info("Bounties has detected a death, but no check has been performed.");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        // Tell the player how many active bounties they have on them
        Player p = e.getPlayer();
        int counter = 0;
        for (Bounty b : bountyCommands.bounties) {
            if (b.target.equalsIgnoreCase(p.getName()) ) {
                counter += 1;
            }
        }
        if (counter != 0) {
            if (counter == 1) {
                p.sendMessage(ChatColor.GOLD + "There is " + ChatColor.RED + String.valueOf(counter) + ChatColor.GOLD + " active bounty on you. Do /bounty list to see it");
            } else {
                p.sendMessage(ChatColor.GOLD + "There are " + ChatColor.RED + String.valueOf(counter) + ChatColor.GOLD + " active bounties on you. Do /bounty list to see them");
            }
        }
    }

    private void bountyCheck(String killed, String killer) {
        // check if killed player has a bounty on them
        // If was a valid Bounty
        // complete bounty
        if (bountyCommands == null) {
            Bukkit.getLogger().warning("BountyEvents has no reference to BountyCommands");
        } else {
            if (bountyCommands.isValidBounty(killed)) {
                bountyCommands.completeBounty(killed, killer);
            } else {
                Bukkit.getLogger().info("bountyCheck: isValidBounty is false");
            }
        }
    }
}
