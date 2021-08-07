package org.asf.mods.hardcoreminecraft.commands;

import modkit.commands.Command;

public class HcspCommand implements Command {

	@Override
	public String getPermission() {
		return "cyan.commands.admin.hardcore.spectator";
	}

	@Override
	public String getId() {
		return "hcsm";
	}

	@Override
	public String getDisplayName() {
		return "Hardcore Spectator (hcsm)";
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
		return new Command[] {
			new ReloadCommand(),
			new ReviveCommand()
		};
	}

	@Override
	public int execute(CommandExecutionContext context) {
		return 1;
	}

}
