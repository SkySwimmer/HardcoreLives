package org.asf.mods.hardcoreminecraft.commands;

import modkit.commands.Command;

public class HcspCommand implements Command {

	@Override
	public String getPermission() {
		return "cyan.commands.admin.hardcore.spectator";
	}

	@Override
	public String getId() {
		return "hcsp";
	}

	@Override
	public String getDisplayName() {
		return "Hardcore Spectator (hcsp)";
	}

	@Override
	public String getDescription() {
		return "HCSP Command Line Utilitiy";
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
