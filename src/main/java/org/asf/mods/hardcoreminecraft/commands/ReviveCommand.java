package org.asf.mods.hardcoreminecraft.commands;

import java.io.IOException;
import java.util.List;

import org.asf.cyan.api.common.CyanComponent;
import org.asf.mods.hardcoreminecraft.HardcoreSpectator;
import org.asf.mods.hardcoreminecraft.config.PlayerInfo;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import modkit.commands.Command;
import modkit.commands.CommandManager;
import modkit.util.Colors;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;

public class ReviveCommand extends CyanComponent implements Command {

	@Override
	public String getPermission() {
		return "cyan.commands.admin.hardcore.spectator.revive";
	}

	@Override
	public String getId() {
		return "revive";
	}

	@Override
	public String getDisplayName() {
		return "Reload";
	}

	@Override
	public String getDescription() {
		return "Revives player(s)";
	}

	@Override
	public String getUsage() {
		return "<players...>";
	}

	@Override
	public LiteralArgumentBuilder<CommandSourceStack> setupCommand(CommandManager manager,
			LiteralArgumentBuilder<CommandSourceStack> cmd) {
		CommandContainer container = CommandContainer.getFor(this);

		container.add(Commands.argument("players", EntityArgument.players()));
		container.attachExecutionEngine();
		container.attachPermission();

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
			throw new RuntimeException(e);
		}
		HardcoreSpectator mod = HardcoreSpectator.getInstance(HardcoreSpectator.class);
		if (players != null) {
			for (ServerPlayer pl : players) {
				PlayerInfo d = mod.getPlayerInfo(context.getServer(), pl.getUUID());
				if (d != null) {
					if (d.deaths >= mod.getConfig().lives) {
						d.deaths = 0;
						d.dying = false;
						d.markedDead = false;
						try {
							d.writeAll();

							pl.setGameMode(context.getServer().getDefaultGameType());
							if (pl.getRespawnDimension() == null)
								pl.setLevel(context.getServer().overworld());
							else
								pl.setLevel(context.getServer().getLevel(pl.getRespawnDimension()));
							if (pl.getRespawnPosition() != null)
								pl.teleportTo(pl.getRespawnPosition().getX(), pl.getRespawnPosition().getY(),
										pl.getRespawnPosition().getZ());
							else
								pl.teleportTo(pl.getLevel().getSharedSpawnPos().getX(),
										pl.getLevel().getSharedSpawnPos().getY(),
										pl.getLevel().getSharedSpawnPos().getZ());

							context.success(new TextComponent(mod.getMessageConfig().systemMessagePrefix + " "
									+ mod.getMessageConfig().systemMessageColor + "Revived " + pl.getName().getString()
									+ mod.getMessageConfig().systemMessageColor + "."));
						} catch (IOException e) {
							context.failure(new TextComponent(mod.getMessageConfig().systemMessagePrefix + " "
									+ Colors.LIGHT_RED + "Cannot revive " + pl.getName().getString()
									+ mod.getMessageConfig().systemMessageColor
									+ ", failed to save the player files."));
						}
					} else {
						context.failure(new TextComponent(mod.getMessageConfig().systemMessagePrefix + " "
								+ Colors.LIGHT_RED + "Cannot revive " + pl.getName().getString()
								+ mod.getMessageConfig().systemMessageColor + " as they are alive."));
					}
				}
			}
		}
		return 0;
	}
}
