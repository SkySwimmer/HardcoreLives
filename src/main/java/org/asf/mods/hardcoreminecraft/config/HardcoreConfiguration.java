package org.asf.mods.hardcoreminecraft.config;

import java.io.IOException;

import modkit.config.ModConfiguration;

import org.asf.cyan.api.config.annotations.Comment;
import org.asf.mods.hardcoreminecraft.HardcoreSpectator;

public class HardcoreConfiguration extends ModConfiguration<HardcoreConfiguration, HardcoreSpectator> {

	public HardcoreConfiguration(HardcoreSpectator instance) throws IOException {
		super(instance);
	}

	@Override
	public String filename() {
		return "hardcore-spectator.ccfg";
	}

	@Comment("The amount of lives each starting player has")
	public int lives = 5;

	@Comment("Disables the mod's features")
	public boolean disable = false;

}
