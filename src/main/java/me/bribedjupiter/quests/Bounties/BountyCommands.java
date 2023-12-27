package me.bribedjupiter.quests.Bounties;

import me.bribedjupiter.quests.Main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;

import java.util.ArrayList;
import java.util.List;

public class BountyCommands implements CommandExecutor {
    private final Main main;
    private static Economy econ = null;
    private static Permission perms = null;
    public List<Bounty> bounties = new ArrayList<Bounty>();

    public BountyCommands (Main main, Economy econ, Permission perms)
    {
        this.main = main;
        this.econ = econ;
        this.perms = perms;
    }

    // bounty (place, cancel, edit, list, clearall) player reward

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (perms == null) {
            perms = Main.getPermissions();
        }
        if (sender.isOp() || perms.has(sender, "bounties.*")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("help")) {
                    if (sender instanceof Player) {
                        if (perms.has(((Player) sender).getPlayer(), "bounties.admin") || sender.isOp()) {
                            sender.sendMessage(ChatColor.GOLD + "How to use the /bounty command: \n/bounty place target $reward " + ChatColor.WHITE + "- Place a bounty on a target for a reward \n" + ChatColor.GOLD + "/bounty edit target (optional - placer) $new_reward " + ChatColor.WHITE + "- Change the reward of an already placed bounty \n" + ChatColor.GOLD + "/bounty remove target (optional - placer) " + ChatColor.WHITE + "- Remove an already placed bounty \n" + ChatColor.GOLD + "/bounty clearall " + ChatColor.WHITE + "- Clear all bounties \nYou can also do " + ChatColor.GOLD + "/bn" + ChatColor.WHITE + " instead of " + ChatColor.GOLD + "/bounty\n" + ChatColor.WHITE + "If you remove or edit someone else's bounty, or clear all bounties, refunds will not be issued to the original bounty placers");
                        } else {
                            sender.sendMessage(ChatColor.GOLD + "How to use the /bounty command: \n/bounty place target $reward " + ChatColor.WHITE + "- Place a bounty on a target for a reward \n" + ChatColor.GOLD + "/bounty edit target $new_reward " + ChatColor.WHITE + "- Change the reward of an already placed bounty \n" + ChatColor.GOLD + "/bounty remove target " + ChatColor.WHITE + "- Remove an already placed bounty \nYou can also do " + ChatColor.GOLD + "/bn" + ChatColor.WHITE + " instead of " + ChatColor.GOLD + "/bounty");
                        }
                    } else {
                        sender.sendMessage(ChatColor.GOLD + "How to use the /bounty command: \n/bounty place target $reward " + ChatColor.WHITE + "- Place a bounty on a target for a reward \n" + ChatColor.GOLD + "/bounty edit target (optional - placer) $new_reward " + ChatColor.WHITE + "- Change the reward of an already placed bounty \n" + ChatColor.GOLD + "/bounty remove target (optional - placer) " + ChatColor.WHITE + "- Remove an already placed bounty \n" + ChatColor.GOLD + "/bounty clearall " + ChatColor.WHITE + "- Clear all bounties \nYou can also do " + ChatColor.GOLD + "/bn" + ChatColor.WHITE + " instead of " + ChatColor.GOLD + "/bounty\n" + ChatColor.WHITE + "If you remove or edit someone else's bounty, or clear all bounties, refunds will not be issued to the original bounty placers");
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("place")) { // create bounty
                    try {
                        Double.parseDouble(args[2]); // To make sure the reward you entered was number
                        if (CheckIfNegative(args[2])) {
                            sender.sendMessage(ChatColor.RED + "You cannot enter a negative reward");
                        } else {
                            placeBounty(sender, args[1], args[2]);
                        }
                    }
                    catch (Exception e) {
                        sender.sendMessage(ChatColor.RED + "You are missing arguments or a reward you entered was not a number");
                        //e.printStackTrace();
                    }
                    return true;
                }
                else if (args[0].equalsIgnoreCase("remove")) {
                    // cancel bounty
                    try {
                        if (sender instanceof Player) {
                            if (args.length >= 3) {
                                if (args[1] != null && args[2] != null && (sender.isOp() || perms.has(((Player) sender).getPlayer(), "bounties.admin"))) {
                                    cancelBounty(sender, args[1], args[2]); //Should allow someone w/ permission to remove any bounty that has been placed
                                }
                                else {
                                    sender.sendMessage(ChatColor.RED + "You have too many arguments");
                                }
                            } else {
                                cancelBounty(sender, args[1], "null");
                            }
                        } else {
                            if (args.length >= 3) {
                                cancelBounty(sender, args[1], args[2]);
                            } else {
                                cancelBounty(sender, args[1], "God");
                            }
                            // Server needs to specify which bounty to remove
                        }
                    }
                    catch (Exception e) {
                        sender.sendMessage(ChatColor.RED + "You are missing arguments or a reward you entered was not a number");
                    }
                    return true;
                }
                else if (args[0].equalsIgnoreCase("edit")) {
                    // edit bounty
                    try {
                        if (sender instanceof Player) {
                            if (args.length >= 4) {
                                if (args[1] != null && args[2] != null && args[3] != null && (sender.isOp() || perms.has(((Player) sender).getPlayer(), "bounties.admin"))) {
                                    Double.parseDouble(args[3]); //To make sure the player gave a number as a reward
                                    if (CheckIfNegative(args[3])) {
                                        sender.sendMessage(ChatColor.RED + "You cannot enter a negative reward");
                                    } else {
                                        editBounty(sender, args[1], args[2], args[3]); // Allow someone with permission to edit a bounty another has placed
                                    }
                                }
                                else {
                                    sender.sendMessage(ChatColor.RED + "You have too many arguments");
                                }
                            } else {
                                Double.parseDouble(args[2]); //To make sure the player gave a number as a reward
                                if (CheckIfNegative(args[2])) {
                                    sender.sendMessage(ChatColor.RED + "You cannot enter a negative reward");
                                } else {
                                    editBounty(sender, args[1], "null", args[2]);
                                }
                            }
                        } else {
                            if (args.length >= 4) {
                                Double.parseDouble(args[3]);
                                if (CheckIfNegative(args[3])) {
                                    sender.sendMessage(ChatColor.RED + "You cannot enter a negative reward");
                                } else {
                                    editBounty(sender, args[1], args[2], args[3]); // Allow someone with permission to edit a bounty another has placed
                                }
                            } else {
                                Double.parseDouble(args[2]);
                                if (CheckIfNegative(args[2])) {
                                    sender.sendMessage(ChatColor.RED + "You cannot enter a negative reward");
                                } else {
                                    editBounty(sender, args[1], "God", args[2]); // Allow someone with permission to edit a bounty another has placed
                                }
                            }

                        }
                    }
                    catch (Exception e) {
                        sender.sendMessage(ChatColor.RED + "You are missing arguments or a reward you entered was not a number");
                    }
                    return true;
                }
                else if (args[0].equalsIgnoreCase("list")) {
                    // list all active bounties
                    if (bounties.toArray().length <= 0) { // Check to see if there are bounties
                        sender.sendMessage(ChatColor.RED + "No bounties!");
                        return true;
                    } else {
                        for (int i = 0; i < +bounties.toArray().length; i++) { // loops over all bounties and lists them in chat
                            String message = "BOUNTY: " + bounties.get(i).target + " PLACER: " + bounties.get(i).sender + " REWARD: " + ChatColor.RED + "$" + bounties.get(i).reward;
                            sender.sendMessage(ChatColor.GOLD + message); // update later
                        }
                        sender.sendMessage(ChatColor.GREEN + "Bounties shown");
                        return true;
                    }
                }
                else if (args[0].equalsIgnoreCase("clearall")) {
                    // clear all bounties only if player is an operator or has permission
                    if (sender instanceof Player) {
                        if (sender.isOp() || perms.has(((Player) sender).getPlayer(), "bounties.admin")) {
                            bounties.clear();
                            sender.sendMessage(ChatColor.GREEN + "Bounties cleared");
                        } else {
                            sender.sendMessage(ChatColor.RED + "You don't have permission to clear bounties");
                        }
                    } else {
                        bounties.clear();
                        sender.sendMessage(ChatColor.GREEN + "Bounties cleared");
                    }
                    return true;
                }
                else {
                    sender.sendMessage(ChatColor.RED + "Unrecognized command");
                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You are missing arguments or a reward you entered was not a number");
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command");
            return true;
        }
    }

    private void placeBounty(CommandSender sender, String target, String reward) {
        Bounty bounty = new Bounty();
        if (isValidTarget(target)) { // Check if player exists with this name, only works for online players
            bounty.target = target; // Will this work for saving data? I guess you can only kill them when online, thus can only complete bounties when online
            bounty.reward = reward;
            if (sender instanceof Player) { // If sender is a player
                if (!hasPlacedBounty(((Player) sender).getPlayer().getName(), target)) {
                    Player pSender = ((Player) sender).getPlayer();
                    bounty.sender = pSender.getName();
                    if (Withdraw(pSender, reward)) {
                        bounties.add(bounty); // Will this end up saving incomplete data if the player isn't valid? Only seems to be placed when is complete
                        sender.sendMessage(ChatColor.GREEN + "Bounty placed");
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.sendMessage(ChatColor.GOLD + pSender.getName() + " has placed a BOUNTY on " + target + " for " + ChatColor.RED + "$" + reward);
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Bounty not placed: insufficient funds or provided reward is not a number");
                    }
                }
                else {
                    sender.sendMessage(ChatColor.RED + "You have already placed a bounty on " + target);
                }
            } else {
                bounty.sender = "God"; // Because the server isn't a player
                if (!hasPlacedBounty("God", target)) {
                    bounties.add(bounty); // Will this end up saving incomplete data if the player isn't valid? Only seems to be placed when is complete
                    sender.sendMessage(ChatColor.GREEN + "Bounty placed");
                } else {
                    sender.sendMessage(ChatColor.RED + "The server has already placed a bounty on " + target);
                }
            }
        }
        else {
            sender.sendMessage(ChatColor.RED + "Unknown player");
        }

    }

    private void cancelBounty(CommandSender sender, String target, String placer) { // CommandSender, target, person who placed the bounty
        // Cancel a bounty on a target that the sender has placed.
        boolean found = false;
        Bounty toCancel = new Bounty();
        if (isValidTarget(target)) {
            if (sender instanceof Player && placer == "null") {
                for (Bounty bounty : bounties) {
                      if (bounty.sender.equalsIgnoreCase(sender.getName())) {
                          if (bounty.target.equalsIgnoreCase(target)) {
                              found = true;
                              toCancel = bounty;
                              Player p = ((Player) sender).getPlayer();
                              Deposit(p, bounty.reward);
                              sender.sendMessage(ChatColor.GREEN + "Bounty on " + target + " removed");
                              break;
                          }
                          else {
                              found = false;
                          }
                      }
                      else {
                          found = false;
                      }
                }
                if (!found) {
                    main.getLogger().info("Bounty not found");
                    sender.sendMessage(ChatColor.RED + "You have not placed a bounty on " + target);
                }
            } else {
                for (Bounty bounty : bounties) {
                    if (bounty.sender.equalsIgnoreCase(placer)) {
                        if (bounty.target.equalsIgnoreCase(target)) {
                            found = true;
                            toCancel = bounty;
                            sender.sendMessage(ChatColor.GREEN + "Bounty on " + target + " removed");
                            break;
                        }
                        else {
                            found = false;
                        }
                    } else {
                        found = false;
                    }
                }
                if (!found) {
                    main.getLogger().info("Bounty not found");
                    sender.sendMessage(ChatColor.RED + "Bounty on " + target + " not found");
                }
            }
            bounties.remove(toCancel);
        } else {
                sender.sendMessage(ChatColor.RED + "Unknown player");
        }

    }

    private void editBounty (CommandSender sender, String target, String placer, String reward) { // Only can change reward
        boolean found = false;
        if (isValidTarget(target)) {
            if (sender instanceof Player && placer == "null") {
                for (Bounty bounty : bounties) {
                    if (bounty.sender.equalsIgnoreCase(sender.getName())) {
                        if (bounty.target.equalsIgnoreCase(target)) {
                            found = true;
                            Player p = ((Player) sender).getPlayer();
                            String oldReward = bounty.reward;
                            Deposit(p, oldReward);
                            if (Withdraw(p, reward)) {
                                bounty.reward = reward;
                                sender.sendMessage(ChatColor.GREEN + "Bounty on " +  target + ": reward edited to " + ChatColor.RED + "$" + reward);
                            }
                            else {
                                sender.sendMessage(ChatColor.RED + "Bounty on " + target + ": unable to edit reward to " + ChatColor.RED + "$" + reward);
                                Withdraw(p, oldReward);
                            }
                            break;
                        }
                        else {
                            found = false;
                        }
                    }
                    else {
                        found = false;
                    }
                }
                if (!found) {
                    main.getLogger().info("Bounty not found");
                    sender.sendMessage(ChatColor.RED + "You have not placed a bounty on " + target);
                }
            } else {
                for (Bounty bounty : bounties) {
                    if (bounty.sender.equalsIgnoreCase(placer)) { // Server sets bounties as 'God'
                        if (bounty.target.equalsIgnoreCase(target)) {
                            found = true;
                            bounty.reward = reward;
                            sender.sendMessage(ChatColor.GREEN + target + " reward edited to " + ChatColor.RED + "$" + reward);
                            break;
                        } else {
                            found = false;
                        }
                    } else {
                        found = false;
                    }
                }
                if (!found) {
                    main.getLogger().info("Bounty not found");
                    sender.sendMessage(ChatColor.RED + "Bounty on " + target + " not found");
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Unknown player");
        }

    }

    public void loadBounty(List<String> bounty) { // Called from Main when loading bounties, loads bounties
        Bounty loadBounty = new Bounty();
        if (bounty.toArray().length != 3) {
        } else {
            loadBounty.sender = bounty.get(0);
            loadBounty.target = bounty.get(1);
            loadBounty.reward = bounty.get(2);
        }
        bounties.add(loadBounty);
    }

    public List<String> seperateBounty(Bounty b) { // Splits bounty data into a string so Main can save it
        List<String> tmpBounty = new ArrayList<String>();
        tmpBounty.add(b.sender);
        tmpBounty.add(b.target);
        tmpBounty.add(b.reward);
        return tmpBounty;
    }

    public void clearBounties() {
        bounties.clear();
    }

    private boolean isValidTarget(String target) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(target)) {
                return true;
            }
        }
        for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
            if (op.getName().equalsIgnoreCase(target)) {
                return true;
            }
        }
        main.getLogger().info("Target is invalid");
        return false;
    }

    public boolean isValidBounty(String target) {
        for (Bounty b : bounties) {
            if (b.target.equalsIgnoreCase(target)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPlacedBounty(String placer, String target) {
        for (Bounty b : bounties) {
            if (b.sender.equalsIgnoreCase(placer)) {
                if (b.target.equalsIgnoreCase(target)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean CheckIfNegative(String reward) {
        double d = Double.parseDouble(reward);
        if (d < 0) {
            return true;
        } else {
            return false;
        }
    }

    public void completeBounty(String killed, String killer) {
        Bukkit.getLogger().info("A Bounty on " + killed + " has been completed by " + killer);
        // If there is a valid bounty, remove it as completed
        List<Bounty> toCancel = new ArrayList<Bounty>();
        toCancel.clear();
        String reward = "$$$";
        for (Bounty b : bounties) {
            if (b.target.equalsIgnoreCase(killed)) {
                toCancel.add(b);
            }
        }
        for (Bounty b : toCancel) {
            String amt = b.reward;
            reward = amt;
            Player p = Bukkit.getPlayer(killer);
            Deposit(p, amt);
            bounties.remove(b);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(ChatColor.GOLD + killer + " has completed a BOUNTY on " + killed + " for " + ChatColor.RED + "$" + reward);
        }

    }

    public boolean Withdraw (Player p, String amt) {
        double d = 0.0;
        try {
            d = Double.parseDouble(amt);
        }
        catch (Exception err) {
            main.getLogger().info("Could not convert String reward to a Double: " + String.valueOf(d));
        }
        Economy e = main.getEconomy();
        double bal;
        bal = e.getBalance(p.getName()); //Gets before it checks so that it can tell if the player cannot afford to place the bounty
        int iBal = (int) bal;

        if (bal < d) {
            return false;
        }

        EconomyResponse r = e.withdrawPlayer(p, d);
        bal = e.getBalance(p.getName()); //Gets again after so that the balance we tell the player is accurate
        iBal = (int) bal;
        if (r.transactionSuccess()) {
            main.getLogger().info("Transaction success");
            try {
                p.sendMessage(ChatColor.GREEN + "$" + amt + " has been withdrawn. Your new balance is " + ChatColor.RED + "$" + iBal);
            }
            catch (Exception err) {
                main.getLogger().warning("Could not send player message");
            }
            return true;
        }
        else {
            main.getLogger().info("Transaction failure");
            try {
                p.sendMessage(ChatColor.RED + "$" + amt + " has failed to be withdrawn. Your balance is " + ChatColor.RED + "$" + iBal);
            }
            catch (Exception err) {
                main.getLogger().warning("Could not send player message");
            }
            return false;
        }
    }
    public void Deposit (Player p, String amt) {
        double d = 0.0;
        try {
            d = Double.parseDouble(amt);
        }
        catch (Exception err) {
            main.getLogger().warning("Could not parse reward string: " + String.valueOf(d));
        }
        Economy e = main.getEconomy();
        EconomyResponse r = e.depositPlayer(p, d);
        double bal;
        bal = e.getBalance(p.getName());
        int iBal = (int) bal;

        if (r.transactionSuccess()) {
            main.getLogger().info("Transaction success");
            try {
                p.sendMessage(ChatColor.GREEN + "$" + amt + " has been deposited. Your new balance is " + ChatColor.RED + "$" + iBal);
            }
            catch (Exception err) {
                main.getLogger().warning("Could not send player message");
            }
        }
        else {
            main.getLogger().info("Transaction failure");
            try {
                p.sendMessage(ChatColor.RED + "$" + amt + " has failed to be deposited. Your balance is " + ChatColor.RED + "$" + iBal);
            }
            catch (Exception err) {
                main.getLogger().warning("Could not send player message");
            }
        }
    }
}
