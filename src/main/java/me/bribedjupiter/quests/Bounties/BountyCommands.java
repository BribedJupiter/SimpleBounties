package me.bribedjupiter.quests.Bounties;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.bribedjupiter.quests.Main;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;

public class BountyCommands implements CommandExecutor {

	private final Main main;
	private Permission perms = null;
	public List<Bounty> bounties = new ArrayList<Bounty>();

	public BountyCommands(Main main, Economy econ, Permission perms) {

		this.main = main;
		this.perms = perms;

	}

	// bounty (place, cancel, edit, list, clearall) player reward.
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (perms == null) {
			perms = Main.getPermissions();
		}
		if (sender.isOp() || perms.has(sender, "bounties.*") && sender instanceof Player) {

			Player player = (Player) sender;

			if(args.length == 0) {

				Util.simpleBountiesMessage(player, "&cERROR: Too few arguments! &6Run &e/bounty help &6for a list of commands.");

			}
			else if (args.length > 0) {
				if (args[0].equalsIgnoreCase("help")) {
					if (perms.has(player, "bounties.admin") || sender.isOp()) {
						Util.simpleBountiesMessage(player, "&6 How to use the /bounty command:");
						Util.simpleBountiesMessage(player, "&e /bounty place target $reward " + "&f"
								+ "- Place a bounty on a target for a reward.");
						Util.simpleBountiesMessage(player, "&e /bounty edit target (optional - placer) $new_reward "
								+ "&f" + "- Change the reward of an already placed bounty.");
						Util.simpleBountiesMessage(player, "&e /bounty remove target (optional - placer) " + "&f"
								+ "- Remove an already placed bounty.");
						Util.simpleBountiesMessage(player, "&e /bounty clearall " + "&f" + "- Clear all bounties.");
						Util.simpleBountiesMessage(player,
								"&f You can also do " + "&6" + "/bn" + "&f" + " instead of " + "&6" + "/bounty.");
						Util.simpleBountiesMessage(player,
								"&f If you remove or edit someone else's bounty, or clear all bounties, refunds will not be issued to the original bounty placers");
						return true;
					} else {
						Util.simpleBountiesMessage(player, "&6 How to use the /bounty command:");
						Util.simpleBountiesMessage(player, "&e /bounty place target $reward " + "&f"
								+ "- Place a bounty on a target for a reward.");
						Util.simpleBountiesMessage(player, "&e /bounty edit target $new_reward " + "&f"
								+ "- Change the reward of an already placed bounty.");
						Util.simpleBountiesMessage(player,
								"&e /bounty remove target " + "&f" + "- Remove an already placed bounty.");
						Util.simpleBountiesMessage(player, "&f You can also do " + "&6" + "/bn" + "&f" + " instead of "
								+ "&6" + "/bounty" + "&f.");
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("place")) { // create bounty
					try {
						Double.parseDouble(args[2]); // To make sure the reward you entered was number
						if (CheckIfNegative(args[2])) {
							Util.simpleBountiesMessage(player, "&c" + "You cannot enter a negative reward");
						} else {
							placeBounty(sender, args[1], args[2]);
						}
					} catch (Exception e) {
						Util.simpleBountiesMessage(player,
								"&c" + "You are missing arguments or a reward you entered was not a number");
						// e.printStackTrace();
					}
					return true;
				} else if (args[0].equalsIgnoreCase("remove")) {
					// cancel bounty
					// main.getLogger().info("args.length is " + args.length);
					try {
						if (sender instanceof Player) {
							if (args.length >= 3) {
								if (args[1] != null && args[2] != null
										&& (sender.isOp() || perms.has(((Player) sender).getPlayer(), "bounties.admin"))) {
									// main.getLogger().info("Remove - sender is a player and has bounties.admin
									// permission");
									cancelBounty(sender, args[1], args[2]); // Should allow someone w/ permission to remove
									// any bounty that has been placed
								} else {
									Util.simpleBountiesMessage(player, "&c" + "You have too many arguments");
								}
							} else {
								// main.getLogger().info("Remove - sender is a player but args are <3");
								cancelBounty(sender, args[1], "null");
							}
							// main.getLogger().info("Player tried to remove a bounty");
						} else {
							// main.getLogger().info("Remove - sender is NOT a player");
							if (args.length >= 3) {
								cancelBounty(sender, args[1], args[2]);
							} else {
								cancelBounty(sender, args[1], "God");
							}
							// Server needs to specify which bounty to remove
							// main.getLogger().info("Server tried to remove a bounty");
						}
					} catch (Exception e) {
						Util.simpleBountiesMessage(player,
								"&c" + "You are missing arguments or a reward you entered was not a number");
					}
					return true;
				} else if (args[0].equalsIgnoreCase("edit")) {
					// edit bounty
					// main.getLogger().info("args.length is " + args.length);
					try {
						if (sender instanceof Player) {
							if (args.length >= 4) {
								if (args[1] != null && args[2] != null && args[3] != null
										&& (sender.isOp() || perms.has(((Player) sender).getPlayer(), "bounties.admin"))) {
									// main.getLogger().info("Edit - sender is a player and has bounties.admin
									// permission");
									Double.parseDouble(args[3]); // To make sure the player gave a number as a reward
									if (CheckIfNegative(args[3])) {
										Util.simpleBountiesMessage(player, "&c" + "You cannot enter a negative reward");
									} else {
										editBounty(sender, args[1], args[2], args[3]); // Allow someone with permission to
										// edit a bounty another has placed
									}
								} else {
									Util.simpleBountiesMessage(player, "&c" + "You have too many arguments");
								}
							} else {
								// main.getLogger().info("Edit - sender is a player but args are <4");
								Double.parseDouble(args[2]); // To make sure the player gave a number as a reward
								if (CheckIfNegative(args[2])) {
									Util.simpleBountiesMessage(player, "&c" + "You cannot enter a negative reward");
								} else {
									editBounty(sender, args[1], "null", args[2]);
								}
							}
						} else {
							// main.getLogger().info("Edit - sender is NOT a player");
							if (args.length >= 4) {
								Double.parseDouble(args[3]);
								if (CheckIfNegative(args[3])) {
									Main.getPlugin().getLogger().info("ERROR: You cannot enter a negative reward");
								} else {
									editBounty(sender, args[1], args[2], args[3]); // Allow someone with permission to edit
									// a bounty another has placed
								}
							} else {
								Double.parseDouble(args[2]);
								if (CheckIfNegative(args[2])) {
									Main.getPlugin().getLogger().info("ERROR: You cannot enter a negative reward");
								} else {
									editBounty(sender, args[1], "God", args[2]); // Allow someone with permission to edit a
									// bounty another has placed
								}
							}

						}
					} catch (Exception e) {
						Util.simpleBountiesMessage(player,
								"&c" + "You are missing arguments or a reward you entered was not a number");
						// e.printStackTrace();
					}
					return true;
				} else if (args[0].equalsIgnoreCase("list")) {
					// list all active bounties
					if (bounties.toArray().length <= 0) { // Check to see if there are bounties
						Util.simpleBountiesMessage(player, "&c" + "No bounties!");
						return true;
					} else {
						for (int i = 0; i < +bounties.toArray().length; i++) { // loops over all bounties and lists them in
							// chat
							String message = "BOUNTY: " + bounties.get(i).target + " PLACER: " + bounties.get(i).sender
									+ " REWARD: " + "&c" + "$" + bounties.get(i).reward;
							Util.simpleBountiesMessage(player, "&6" + message); // update later
						}
						Util.simpleBountiesMessage(player, "&2" + "Bounties shown");
						return true;
					}
				} else if (args[0].equalsIgnoreCase("clearall")) {
					// clear all bounties only if player is an operator or has permission
					if (sender instanceof Player) {
						if (sender.isOp() || perms.has(((Player) sender).getPlayer(), "bounties.admin")) {
							bounties.clear();
							Util.simpleBountiesMessage(player, "&2" + "Bounties cleared");
						} else {
							Util.simpleBountiesMessage(player, "&c" + "You don't have permission to clear bounties");
						}
					} else {
						bounties.clear();
						Util.simpleBountiesMessage(player, "&2" + "Bounties cleared");
					}
					return true;
				}
				return true;
			}
		} else {
			Main.getPlugin().getLogger()
			.info("ERROR: You are missing arguments, or else a reward you entered was not a number!");
			return true;
		}

		return true;

	}

	private void placeBounty(CommandSender sender, String target, String reward) {
		Bounty bounty = new Bounty();
		if (isValidTarget(target)) { // Check if player exists with this name, only works for online players
			// main.getLogger().info("Bounty place target is valid");
			bounty.target = target; // Will this work for saving data? I guess you can only kill them when online,
			// thus can only complete bounties when online
			bounty.reward = reward;
			if (sender instanceof Player) { // If sender is a player
				Player playerToMessage = (Player) sender;
				if (!hasPlacedBounty(((Player) sender).getPlayer().getName(), target)) {
					Player pSender = ((Player) sender).getPlayer();
					bounty.sender = pSender.getName();
					if (Withdraw(pSender, reward)) {
						bounties.add(bounty); // Will this end up saving incomplete data if the player isn't valid? Only
						// seems to be placed when is complete
						Util.simpleBountiesMessage(playerToMessage, "&2" + "Bounty placed");
						for (Player player : Bukkit.getOnlinePlayers()) {
							Util.simpleBountiesMessage(player, "&6" + pSender.getName() + " has placed a BOUNTY on "
									+ target + " for " + "&c" + "$" + reward);
						}
					} else {
						Util.simpleBountiesMessage(playerToMessage,
								"&c" + "Bounty not placed: insufficient funds or provided reward is not a number");
					}
				} else {
					Util.simpleBountiesMessage(playerToMessage, "&c" + "You have already placed a bounty on " + target);
				}
			} else {
				bounty.sender = "God"; // Because the server isn't a player
				if (!hasPlacedBounty("God", target)) {
					bounties.add(bounty); // Will this end up saving incomplete data if the player isn't valid? Only
					// seems to be placed when is complete
					Main.getPlugin().getLogger().info("Bounty Placed!");
				} else {
					Main.getPlugin().getLogger()
					.info("ERROR: The server has already placed a bounty on " + target + "!");
				}
			}
		} else {
			Main.getPlugin().getLogger().info("ERROR: Unknown player");
		}

	}

	private void cancelBounty(CommandSender sender, String target, String placer) { // CommandSender, target, person who
		// placed the bounty
		// Cancel a bounty on a target that the sender has placed.
		boolean found = false;
		Bounty toCancel = new Bounty();
		// main.getLogger().info("cancelBounty intro - Placer is " + placer);
		if (isValidTarget(target)) {
			// main.getLogger().info("Bounty remove target is valid");
			if (sender instanceof Player && placer == "null") {
				Player player = (Player) sender;
				// main.getLogger().info("cancelBounty - sender is a player and placer ==
				// null");
				for (Bounty bounty : bounties) {
					if (bounty.sender.equalsIgnoreCase(sender.getName())) {
						if (bounty.target.equalsIgnoreCase(target)) {
							found = true;
							// main.getLogger().info("Bounty remove player - bounty found");
							toCancel = bounty;
							Player p = ((Player) sender).getPlayer();
							Deposit(p, bounty.reward);
							Util.simpleBountiesMessage(player, "&2" + "Bounty on " + target + " removed");
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
					Util.simpleBountiesMessage(player, "&c" + "You have not placed a bounty on " + target);
				}
			} else {
				// main.getLogger().info("cancelBounty - sender is not a player or placer !=
				// null");
				// main.getLogger().info("Placer is " + placer);
				for (Bounty bounty : bounties) {
					if (bounty.sender.equalsIgnoreCase(placer)) {
						if (bounty.target.equalsIgnoreCase(target)) {
							found = true;
							// main.getLogger().info("Bounty remove server - bounty found");
							toCancel = bounty;
							Main.getPlugin().getLogger().info("Bounty on " + target + " removed.");
							break;
						} else {
							found = false;
						}
					} else {
						found = false;
					}
				}
				if (!found) {
					Main.getPlugin().getLogger().info("ERROR: Bounty on " + target + " not found");
				}
			}
			bounties.remove(toCancel);
		} else {
			Main.getPlugin().getLogger().info("ERROR: Unknown player");
		}

	}

	private void editBounty(CommandSender sender, String target, String placer, String reward) { // Only can change
		// reward
		boolean found = false;
		if (isValidTarget(target)) {
			// main.getLogger().info("Bounty edit target is valid");
			// main.getLogger().info("cancelBounty intro - Placer is " + placer);
			if (sender instanceof Player && placer == "null") {
				Player player = (Player) sender;
				// main.getLogger().info("cancelBounty - sender is a player and placer ==
				// null");
				for (Bounty bounty : bounties) {
					if (bounty.sender.equalsIgnoreCase(sender.getName())) {
						if (bounty.target.equalsIgnoreCase(target)) {
							found = true;
							// main.getLogger().info("Bounty edit player - bounty found");
							Player p = ((Player) sender).getPlayer();
							String oldReward = bounty.reward;
							Deposit(p, oldReward);
							if (Withdraw(p, reward)) {
								bounty.reward = reward;
								Util.simpleBountiesMessage(player,
										"&2" + "Bounty on " + target + ": reward edited to " + "&c" + "$" + reward);
							} else {
								Util.simpleBountiesMessage(player, "&c" + "Bounty on " + target
										+ ": unable to edit reward to " + "&c" + "$" + reward);
								Withdraw(p, oldReward);
							}
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
					Util.simpleBountiesMessage(player, "&c" + "You have not placed a bounty on " + target);
				}
			} else {
				// main.getLogger().info("editBounty - sender is not a player or placer !=
				// null");
				// main.getLogger().info("Placer is " + placer);
				for (Bounty bounty : bounties) {
					if (bounty.sender.equalsIgnoreCase(placer)) { // Server sets bounties as 'God'
						if (bounty.target.equalsIgnoreCase(target)) {
							found = true;
							// main.getLogger().info("Bounty edit server - bounty found");
							bounty.reward = reward;
							Main.getPlugin().getLogger().info(target + " reward edited to " + "$" + reward + ".");
							break;
						} else {
							found = false;
						}
					} else {
						found = false;
					}
				}
				if (!found) {
					Main.getPlugin().getLogger().info("ERROR: Bounty on " + target + " not found!");
				}
			}
		} else {
			Main.getPlugin().getLogger().info("ERROR: Unknown player");
		}

	}

	public void loadBounty(List<String> bounty) { // Called from Main when loading bounties, loads bounties
		Bounty loadBounty = new Bounty();
		if (bounty.toArray().length != 3) {
			// main.getLogger().info("loadBounty - Bounty to be added has improper length");
		} else {
			loadBounty.sender = bounty.get(0);
			loadBounty.target = bounty.get(1);
			loadBounty.reward = bounty.get(2);
			// main.getLogger().info("loadBounty - bounty loaded!");
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
		/*
		 * if (Bukkit.getPlayerExact(target) != null) { return true; } else { return
		 * false; }
		 */
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getName().equalsIgnoreCase(target)) {
				// main.getLogger().info("Online Target - Target is valid");
				return true;
			}
		}
		for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
			if (op.getName().equalsIgnoreCase(target)) {
				// main.getLogger().info("Offline Target - Target is valid");
				return true;
			}
		}
		main.getLogger().info("Target is invalid");
		return false;
	}

	public boolean isValidBounty(Player target) {
		// Bukkit.getLogger().info("isValidBounty: Checking for " + target);
		for (Bounty b : bounties) {
			if (b.target.equalsIgnoreCase(target.getName())) {
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

	public void completeBounty(Player killed, Player killer) {
		Bukkit.getLogger().info("A Bounty on " + killed + " has been completed by " + killer);
		// If there is a valid bounty, remove it as completed
		List<Bounty> toCancel = new ArrayList<Bounty>();
		toCancel.clear();
		String reward = "$$$";
		for (Bounty b : bounties) {
			if (b.target.equalsIgnoreCase(killed.getName())) {
				// Bukkit.getLogger().info("Completed bounty on " + b.target + " removed from
				// Bounties");
				toCancel.add(b);
			}
		}
		for (Bounty b : toCancel) {
			String amt = b.reward;
			reward = amt;
			Deposit(killer, amt);
			bounties.remove(b);
		}

		for (Player player : Bukkit.getOnlinePlayers()) {
			Util.simpleBountiesMessage(player,
					"&6" + killer + " has completed a BOUNTY on " + killed + " for " + "&c" + "$" + reward);
		}

	}

	public boolean Withdraw(Player player, String amt) {

		double d = 0.0;
		try {
			d = Double.parseDouble(amt);
		} catch (Exception err) {
			main.getLogger().info("Could not convert String reward to a Double: " + String.valueOf(d));
		}
		Economy e = main.getEconomy();
		double bal;
		bal = e.getBalance(player); // Gets before it checks so that it can tell if the player cannot afford to
		// place the bounty
		int iBal = (int) bal;

		if (bal < d) {
			return false;
		}

		EconomyResponse r = e.withdrawPlayer(player, d);
		bal = e.getBalance(player); // Gets again after so that the balance we tell the player is accurate
		iBal = (int) bal;
		if (r.transactionSuccess()) {
			main.getLogger().info("Transaction success");
			try {
				Util.simpleBountiesMessage(player,
						"&2" + "$" + amt + " has been withdrawn. Your new balance is " + "&c" + "$" + iBal);
			} catch (Exception err) {
				System.out.println("Could not send player message");
			}
			return true;
		} else {
			main.getLogger().info("Transaction failure");
			try {
				Util.simpleBountiesMessage(player,
						"&c" + "$" + amt + " has failed to be withdrawn. Your balance is " + "&c" + "$" + iBal);
			} catch (Exception err) {
				System.out.println("Could not send player message");
			}
			return false;
		}

	}

	public void Deposit(Player player, String amt) {
		double d = 0.0;
		try {
			d = Double.parseDouble(amt);
			// main.getLogger().info("amt is now Doubled: " + String.valueOf(d));
		} catch (Exception err) {
			// main.getLogger().info("Could not Double reward: " + String.valueOf(d));
		}
		Economy e = main.getEconomy();
		EconomyResponse r = e.depositPlayer(player, d);
		double bal;
		bal = e.getBalance(player);
		int iBal = (int) bal;

		if (r.transactionSuccess()) {
			main.getLogger().info("Transaction success");
			try {
				Util.simpleBountiesMessage(player,
						"&2" + "$" + amt + " has been deposited. Your new balance is " + "&c" + "$" + iBal);
			} catch (Exception err) {
				System.out.println("Could not send player message");
			}
		} else {
			main.getLogger().info("Transaction failure");
			try {
				Util.simpleBountiesMessage(player,
						"&c" + "$" + amt + " has failed to be deposited. Your balance is " + "&c" + "$" + iBal);
			} catch (Exception err) {
				System.out.println("Could not send player message");
			}
		}
	}
}