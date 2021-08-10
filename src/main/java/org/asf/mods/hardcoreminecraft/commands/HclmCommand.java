package org.asf.mods.hardcoreminecraft.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import modkit.commands.Command;
import modkit.commands.CommandManager;
import net.minecraft.commands.CommandSourceStack;

public class HclmCommand implements Command {

	@Override
	public String getPermission() {
		return "cyan.commands.admin.hardcore.lives";
	}

	@Override
	public String getId() {
		return "hclm";
	}

	@Override
	public String getDisplayName() {
		return "Hardcore Lives (hclm)";
	}

	@Override // Prevent execution if no arguments are present
	public ArgumentBuilder<CommandSourceStack, ?> setupCommand(CommandManager manager,
			LiteralArgumentBuilder<CommandSourceStack> cmd) {
		return cmd;
	}

	@Override
	public String getDescription() {
		return "HCSM Command Line Utilitiy";
	}

	@Override
	public String getUsage() {
		return "<reload/revive> <arguments...>";
	}

	@Override
	public Command[] childCommands() {
		return new Command[] { new ReloadCommand(), new ReviveCommand() };
	}

	@Override
	public int execute(CommandExecutionContext context) {
		return 1;
	}

}
