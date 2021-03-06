package org.asf.mods.hardcoreminecraft.commands;

import java.io.IOException;

import org.asf.cyan.api.common.CyanComponent;
import org.asf.mods.hardcoreminecraft.HardcoreLives;
import org.fusesource.jansi.Ansi.Color;

import modkit.commands.Command;
import net.minecraft.network.chat.TextComponent;

public class ReloadCommand extends CyanComponent implements Command {

	@Override
	public String getPermission() {
		return "cyan.commands.admin.hardcore.lives.reload";
	}

	@Override
	public String getId() {
		return "reload";
	}

	@Override
	public String getDisplayName() {
		return "Reload";
	}

	@Override
	public String getDescription() {
		return "Reloads the Harcore: Lives configuration";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public int execute(CommandExecutionContext context) {
		boolean error = false;
		HardcoreLives mod = HardcoreLives.getInstance(HardcoreLives.class);
		try {
			HardcoreLives.getInstance(HardcoreLives.class).reload();
			HardcoreLives.getInstance(HardcoreLives.class).getPlayers(context.getServer()).forEach(t -> {
				try {
					t.getInfo().readAll();
				} catch (IOException e) {
					error("Failed to reload a player configuration! (" + t.getPlayer().getName().getString() + ")", e);
				}
			});
		} catch (IOException e) {
			error("Failed to reload the Hardcore: Lives configuration!", e);
			error = true;
		}
		if (context.getPlayer() == null) {
			if (!error)
				context.success(new TextComponent(mod.getMessageConfig().systemMessagePrefix + " "
						+ mod.getMessageConfig().systemMessageColor + "Reloaded the configuration!"));
		} else {
			if (!error)
				context.success(new TextComponent(mod.getMessageConfig().systemMessagePrefix + " "
						+ mod.getMessageConfig().systemMessageColor + "Reloaded the configuration!"));
			else
				context.failure(new TextComponent(mod.getMessageConfig().systemMessagePrefix + " " + Color.RED
						+ "Failed to reload the configuration! Check the server log!"));
		}
		return 0;
	}
}
