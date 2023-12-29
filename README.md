# SimpleBounties
A simple, straight-forward Spigot plugin for servers using Minecraft 1.19+ to allow players to place and complete bounties on each other through a command system. 

### Version 1.1
- Add the ability to pay off your own bounty
- Clean up auto completion
- Clean up error messages
- Clean up code
- Revise /bounty help
- Limit bounty reward decimal places to 2
- A player will now receive a message telling them how many active bounties are on them when they join, if there are any. If there aren't, they will not receive a message

# Dependencies
EssentialsX and Vault.

# Install
Just download and drop the "Bounties-1.1-RELEASE.jar" file from the "Versions" folder into your server's plugin folder, and you're done!

# Permissions
bounties.* - Allows basic access to the plugin. 

bounties.admin - Allows operator permissions, such as the abilities to edit and remove other people's bounties. When an operator does that, it won't send a refund to the original bounty placer so use with caution. There is potential here to exploit the system and get infinite money by editing rewards to a really high number since it won't withdraw from your account, so grant this permission with caution. It allows the use of the /bounty clearall command which removes all active bounties. Operators also have these permissions.

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

To pay off a bounty and remove it from the list do /bounty pay [the person who placed the bounty]

Do /bounty help for instructions on how to use the plugin.

### For Those With bounties.admin Permissions
Operators can use the plugin the same way regular users can, or they can utilize a couple more options.

They can do /bounty clearall to remove all active bounties.

They can do /bounty edit [bounty target] [bounty placer] [new reward] to change the reward of another player's bounty without refunding the original bounty placers.

They can do /bounty remove [bounty target] [bounty placer] to remove another player's bounty without refunding the original bounty placers.

Enjoy!

# Future ideas
- Adding the option to give rewards that aren't solely monetary, such as items. 
- Adding a bounty board or headhunter system, where bounties could be placed as signs and players would have to go to a physical location to start or collect a bounty. 
 Maybe they would need to return with some item from the player they killed, like a head, to complete the bounty. 
- Add a log of completed bounties, maybe available as a book or a list of heads.
- Add integration with Discord plugins so completion messages show up in Discord chats.
- Add the ability to create a list of players that cannot have a bounty.
- Add the ability to set the bounty decimal limit in-game
- Add a toggle for the join messages
- Add the ability to do /list player to get bounties on a specific player
- Clean up Tab Completion to make it more extensible and easier to edit

# Known issues
As of version 1.1-RELEASE, if one is using the "Duels" plugin, bounties will be completed upon the completion of a duel. This may be desirable or not depending on the server. 

### Note:
Sometimes throughout the code you'll notice the word Quests in reference to this plugin. Originally, it
was intended to become a fully featured questing plugin, but as development progressed I shifted focus and narrowed 
the scope to just the bounties portion of the plugin without refactoring completely. 
