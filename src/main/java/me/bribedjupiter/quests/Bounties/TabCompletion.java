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

    @Override
    public List<String> onTabComplete (CommandSender sender, Command cmd, String label, String[] args) {
        if (perms == null) {
            perms = Main.getPermissions();
        }
        List<String> completions = new ArrayList<String>();
        if (cmd.getName().equalsIgnoreCase("bounty") && args.length >= 0) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length <= 1) {
                    completions.clear();
                    completions.add("place");
                    completions.add("help");
                    completions.add("edit");
                    completions.add("remove");
                    completions.add("list");
                    if (p.isOp() || perms.has(((Player) sender).getPlayer(), "bounties.admin")) {
                        completions.add("clearall");
                    }
                }
                else if (args.length == 2 && !args[0].equalsIgnoreCase("list") && !args[0].equalsIgnoreCase("clearall")) {
                    completions.clear();
                    if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("edit")) {
                        bountyCommands = main.getBountyCommands();
                        if (perms.has(sender, "bounties.admin") || sender.isOp()) {
                            for (Bounty b : bountyCommands.bounties) {
                                completions.add(b.target);
                            }
                        } else {
                            for (Bounty b : bountyCommands.bounties) {
                                if (b.sender.equalsIgnoreCase(sender.getName())) { // Servers don't have auto-completion, so I don't need to add "God" here
                                    completions.add(b.target); // Only adds players the player has already placed a bounty on, the only ones they can edit or remove
                                }
                            }
                        }
                    } else {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            completions.add(player.getName());
                        }
                    }
                }
                else if (args.length == 3 && !args[0].equalsIgnoreCase("list") && !args[0].equalsIgnoreCase("remove") && !args[0].equalsIgnoreCase("clearall")) {
                    completions.clear();
                    completions.add("10");
                    completions.add("100");
                    completions.add("1000");
                }
                else {
                    completions.clear();
                }
                return completions;
            }
        }
        return null;
    }
}
