package org.asf.mods.hardcoreminecraft.events;

import modkit.commands.CommandManager;

import org.asf.cyan.mods.events.AttachEvent;
import org.asf.cyan.mods.events.IEventListenerContainer;
import org.asf.mods.hardcoreminecraft.commands.HcspCommand;
import org.asf.mods.hardcoreminecraft.commands.LivesCommand;

public class CommandEvents implements IEventListenerContainer {

	@AttachEvent(value = "mods.preinit", synchronize = true)
	public void preInit() {
		CommandManager.getMain().registerCommand(new HcspCommand());
		CommandManager.getMain().registerCommand(new LivesCommand());
	}

}
