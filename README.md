# SimpleBounties
A simple, straight-forward Spigot plugin for servers using Minecraft 1.19 to allow players to place and complete bounties on each other. 

# Dependencies
EssentialsX and Vault.

# Install
Just download and drop the "Bounties-1.0-RELEASE.jar" file from the target folder into your server's plugin folder, and you're done!

# Permissions
bounties.* - Allows basic access to the plugin. 

bounties.admin - Allows operator permissions, such as the abilities to edit and remove other people's bounties. When an operator does that, it won't send a refund to the original bounty placer so use with caution. There is potential here to exploit the system and get infinite money by editing rewards to a really high number, so grant this permission with caution. It allows the use of the /bounty clearall command which removes all active bounties. Operators also have these permissions.

# Aliases
/bn is equivalent to /bounty

# Bounty Rules
You cannot place bounties as another person, and you cannot claim bounties on yourself. You cannot place multiple bounties on a person, and you have to be directly responsible for the kill. Be aware though, Minecraft is a little inconsistent in how it records kills, so you could blow someone up with TNT or push them off a cliff, but it wouldn't work if you pushed them into lava, for example. As of right now, you can only enter money as a reward. 

# How It Works

### For Regular Users
To place a bounty do /bounty place [target player] [reward amount].

To edit a bounty do /bounty edit [bounty target] [new reward amount].

To remove a bounty do /bounty remove [bounty target].

To list all current bounties do /bounty list.

Do /bounty help for instructions on how to use the plugin.

### For Those With bounties.admin Permissions
Operators can use the plugin the same way regular users can, or they can utilize a couple more options.

They can do /bounty clearall to remove all active bounties.

They can do /bounty edit [bounty target] [bounty placer] [new reward] to change the reward of another player's bounty without refunding the original bounty placers.

They can do /bounty remove [bounty target] [bounty placer] to remove another player's bounty without refunding the original bounty placers.

Enjoy!
