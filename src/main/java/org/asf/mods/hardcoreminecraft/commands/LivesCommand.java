package org.asf.mods.hardcoreminecraft.commands;

import java.util.List;

import org.asf.cyan.api.common.CyanComponent;
import org.asf.mods.hardcoreminecraft.HardcoreLives;
import org.fusesource.jansi.Ansi.Color;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import modkit.commands.Command;
import modkit.commands.CommandManager;
import modkit.permissions.PermissionManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;

public class LivesCommand extends CyanComponent implements Command {

	@Override
	public String getPermission() {
		return "cyan.commands.player.hardcore.spectator.lives";
	}

	@Override
	public String getId() {
		return "lives";
	}

	@Override
	public String getDisplayName() {
		return "Lives";
	}

	@Override
	public String getDescription() {
		return "Retrieves a player's life count";
	}

	@Override
	public String getUsage() {
		return "[players...]";
	}

	@Override
	public LiteralArgumentBuilder<CommandSourceStack> setupCommand(CommandManager manager,
			LiteralArgumentBuilder<CommandSourceStack> cmd) {
		CommandContainer container = CommandContainer.getFor(this);

		cmd = cmd.requires(t -> {
			try {
				return PermissionManager.getInstance().hasPermission(t.getEntityOrException(), getPermission());
			} catch (CommandSyntaxException ex) {
				return t.hasPermission(5);
			}
		});
		cmd.executes(t -> {
			return execute(CommandExecutionContext.getNew(t));
		});

		container.add(Commands.argument("players", EntityArgument.players()));
		container.attachPermission("cyan.commands.player.hardcore.spectator.lives.others", 0);
		container.attachExecutionEngine();

		return container.build(cmd);
	}

	@Override
	public int execute(CommandExecutionContext context) {
		List<ServerPlayer> players = null;
		try {
			if (context.getArgument("players", EntitySelector.class) != null)
				players = context.getArgument("players", EntitySelector.class)
						.findPlayers(context.toGameType().getSource());
		} catch (CommandSyntaxException e) {
		}

		HardcoreLives mod = HardcoreLives.getInstance(HardcoreLives.class);
		if (players == null && context.getPlayer() == null) {
			context.failure(new TextComponent(
					mod.getMessageConfig().systemMessagePrefix + " " + Color.RED + "No players specified!"));
			return 1;
		}
		if (players == null)
			players = List.of(context.getPlayer());

		for (ServerPlayer player : players) {

			@SuppressWarnings("resource")
			int lives = mod.getLives(context.getServer()) - mod.getPlayerInfo(context.getServer(), player.getUUID()).deaths;
			if (lives < 0)
				lives = 0;

			context.success(new TextComponent(
					mod.getMessageConfig().systemMessagePrefix + " " + mod.getMessageConfig().systemMessageColor
							+ "Lives of player " + player.getDisplayName().getString() + ": " + lives));
		}

		return 0;
	}
}
