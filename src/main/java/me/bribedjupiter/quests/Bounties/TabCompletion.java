package me.bribedjupiter.quests.Bounties;

import me.bribedjupiter.quests.Main;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TabCompletion implements TabCompleter {
    private final Main main;
    private static Permission perms = null;
    public BountyCommands bountyCommands = null;

    public TabCompletion (Main main, Permission perms)
    {
        this.main = main;
        this.perms = perms;
    }

    // Regular user
    // help
    // list
    // remove TARGET
    // pay    PLACER
    // place  TARGET REWARD
    // edit   TARGET REWARD

    // Admin only
    // clearall
    // edit    TARGET (PLACER) REWARD
    // remove  TARGET (PLACER)

    @Override
    public List<String> onTabComplete (CommandSender sender, Command cmd, String label, String[] args) {
        if (perms == null) {
            perms = Main.getPermissions();
        }
        List<String> completions = new ArrayList<String>();
        if (cmd.getName().equalsIgnoreCase("bounty")) {
            if (sender instanceof Player) {
                bountyCommands = main.getBountyCommands();
                Player p = (Player) sender;
                if (args.length <= 1) {
                    completions.add("place");
                    completions.add("help");
                    completions.add("edit");
                    completions.add("remove");
                    completions.add("list");
                    completions.add("pay");
                    if (p.isOp() || perms.has(((Player) sender).getPlayer(), "bounties.admin")) {
                        completions.add("clearall");
                    }
                    return completions;
                }
                else if (args.length == 2 && (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("pay") || args[0].equalsIgnoreCase("place") || args[0].equalsIgnoreCase("edit"))) {
                    if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("edit")) {
                        // If we have an admin, we want all of the targets to show
                        if (perms.has(sender, "bounties.admin") || sender.isOp()) {
                            for (Bounty b : bountyCommands.bounties) {
                                completions.add(b.target);
                            }
                        } else {
                            // Otherwise, we only want to show a player the bounties they themselves have placed
                            for (Bounty b : bountyCommands.bounties) {
                                if (b.sender.equalsIgnoreCase(sender.getName())) { // Servers don't have auto-completion, so I don't need to add "God" here
                                    completions.add(b.target); // Only adds players the player has already placed a bounty on, the only ones they can edit or remove
                                }
                            }
                        }
                    }
                    else if (args[0].equalsIgnoreCase("pay")) {
                        // Get a list of all players who have placed bounties on the sender
                        for (Bounty b : bountyCommands.bounties) {
                            if (b.target.equals(sender.getName())) {
                                completions.add(b.sender); // If the target of the bounty is the player who sent the command, then add that placer to the list
                            }
                        }
                    }
                    else { // If it is place
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            completions.add(player.getName());
                        }
                    }
                    return completions;
                }
                else if (args.length == 3 && (args[0].equalsIgnoreCase("place") || args[0].equalsIgnoreCase("edit") || args[0].equalsIgnoreCase("remove"))) {
                    if (!args[0].equalsIgnoreCase("remove")) {
                        completions.add("100");
                        completions.add("1000");
                        completions.add("10000");
                    }
                    if ((perms.has(sender, "bounties.admin") || sender.isOp()) && !args[0].equalsIgnoreCase("place")) {
                        for (Bounty b : bountyCommands.bounties) {
                            // Add the list of people who have placed bounties so an admin can specify which particular bounty they want to edit or remove
                            completions.add(b.sender);
                        }
                    }
                    return completions;
                }
                else if (args.length == 4 && args[0].equalsIgnoreCase("edit") && (perms.has(sender, "bounties.admin") || sender.isOp())) {
                    try {
                        Double d = Double.parseDouble(args[2]);
                    } catch (Exception e) {
                        completions.add("100");
                        completions.add("1000");
                        completions.add("10000");
                    }
                    return completions;
                }
                return completions;
            }
        }
        return null;
    }
}
