package me.bribedjupiter.quests.Bounties;

import org.bukkit.entity.Player;

import me.bribedjupiter.quests.Main;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class Util {

	// Sends a formatted message to the player (including name replacement).
	public static void simpleBountiesMessage(Player p, String message) {

		p.sendMessage(legacySerializerAnyCase(Main.getPlugin().getConfig().getString("prefix") + message));

	}

	public static TextComponent legacySerializerAnyCase(String subject) {

		int count = 0;
		// Count the number of '&' characters to determine the size of the array
		for (char c : subject.toCharArray()) {

			if (c == '&') {

				count++;

			}

		}

		// Create an array to store the positions of '&' characters
		int[] positions = new int[count];
		int index = 0;
		// Find the positions of '&' characters and store in the array
		for (int i = 0; i < subject.length(); i++) {

			if (subject.charAt(i) == '&') {

				if (isUpperBukkitCode(subject.charAt(i + 1))) {

					subject = replaceCharAtIndex(subject, (i + 1), Character.toLowerCase(subject.charAt(i + 1)));

				}

				positions[index++] = i;

			}

		}

		return LegacyComponentSerializer.legacyAmpersand().deserialize(subject);

	}

	private static boolean isUpperBukkitCode(char input) {

		char[] bukkitColorCodes = {'A', 'B', 'C', 'D', 'E', 'F', 'K', 'L', 'M', 'N', 'O', 'R'};
		boolean match = false;

		// Loop through each character in the array.
		for (char c : bukkitColorCodes) {
			// Check if the current character in the array is equal to the input character.
			if (c == input) {

				match = true;

			}

		}

		return match;

	}

	private static String replaceCharAtIndex(String original, int index, char newChar) {

		// Check if the index is valid
		if (index >= 0 && index < original.length()) {

			// Create a new string with the replaced character
			return original.substring(0, index) + newChar + original.substring(index + 1);

		}

		// If the index is invalid, return the original string
		return original;

	}

}