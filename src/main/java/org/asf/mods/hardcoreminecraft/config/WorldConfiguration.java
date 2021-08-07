package org.asf.mods.hardcoreminecraft.config;

import org.asf.cyan.api.config.Configuration;
import org.asf.cyan.api.config.annotations.Comment;

public class WorldConfiguration extends Configuration<WorldConfiguration> {

	public WorldConfiguration(String base) {
		super(base);
	}

	@Override
	public String folder() {
		return "";
	}

	@Override
	public String filename() {
		return "hardcore-spectator.ccfg";
	}

	@Comment("The amount of lives each starting player has")
	public int lives = 5;

	@Comment("Disables the mod's features")
	public boolean disable = true;

}
