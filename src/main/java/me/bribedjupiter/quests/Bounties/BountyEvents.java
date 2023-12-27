package me.bribedjupiter.quests.Bounties;

import me.bribedjupiter.quests.Main;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

// This class will listen for player death events, get the players involved, and initiate a bounty check.

public class BountyEvents implements Listener {
    private final Main main;
    public BountyCommands  bountyCommands = null;

    public BountyEvents (Main main, Economy econ, Permission perms)
    {
        this.main = main;
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

    private void bountyCheck(String killed, String killer) {
        bountyCommands = main.getBountyCommands();
        if (bountyCommands == null) {
            Bukkit.getLogger().warning("BountyEvents has no reference to BountyCommands");
        } else {
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
