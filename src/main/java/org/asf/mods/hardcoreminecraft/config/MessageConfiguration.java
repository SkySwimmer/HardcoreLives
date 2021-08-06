package org.asf.mods.hardcoreminecraft.config;

import java.io.IOException;

import modkit.config.ModConfiguration;
import modkit.util.Colors;

import org.asf.cyan.api.config.annotations.Comment;
import org.asf.mods.hardcoreminecraft.HardcoreSpectator;

public class MessageConfiguration extends ModConfiguration<MessageConfiguration, HardcoreSpectator> {

	public MessageConfiguration(HardcoreSpectator instance) throws IOException {
		super(instance);
	}

	@Override
	public String filename() {
		return "messages.ccfg";
	}

	@Comment("The system message prefix (for the chat and in the console)")
	public String systemMessagePrefix = Colors.DARK_PURPLE + "--[-- " + Colors.GOLD + "HC" + Colors.RESET + ":"
			+ Colors.LIGHT_PURPLE + "SM" + Colors.DARK_PURPLE + " --]--";

	@Comment("The system message color")
	public String systemMessageColor = Colors.DARK_AQUA;

	@Comment("The spectator message prefix (dead players)")
	public String spectatorMessagePrefix = Colors.LIGHT_GREY;

	@Comment("The first death messages")
	public String firstDeathMessages = Colors.YELLOW + "Ouch, " + Colors.GOLD + "%p" + Colors.YELLOW
			+ " has just died, " + Colors.LIGHT_GREEN + Colors.UNDERLINE + "%l" + Colors.RESET + Colors.YELLOW
			+ " live(s) left!";

	@Comment("The last 2 death messages")
	public String lastDeathMessages = Colors.GOLD + "Oh no! Player " + Colors.LIGHT_BLUE + "%p" + Colors.GOLD
			+ " only has " + Colors.LIGHT_RED + Colors.UNDERLINE + "%l" + Colors.RESET + Colors.GOLD + " live(s) left!";

	@Comment("The final death message")
	public String finalDeathMessage = Colors.LIGHT_RED + "The player " + Colors.GOLD + "%p" + Colors.LIGHT_RED
			+ " has lost their final life and is now spectating.";

	@Comment("The death title")
	public String deathTitle = Colors.GOLD + "Oh No! You Died!";

	@Comment("The death subtitle")
	public String deathSubtitle = Colors.LIGHT_BLUE + "You have " + Colors.LIGHT_GREEN + "%l" + Colors.LIGHT_BLUE
			+ " live(s) left.";

	@Comment("The final death title")
	public String finalDeathTitle = Colors.LIGHT_RED + "You Died!";

	@Comment("The final death subtitle")
	public String finalDeathSubtitle = Colors.GOLD + "You are now spectating";
}
