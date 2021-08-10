package org.asf.mods.hardcoreminecraft.config;

import java.io.IOException;
import java.util.UUID;

import org.asf.cyan.api.config.Configuration;
import org.asf.cyan.api.config.annotations.Comment;
import org.asf.cyan.api.config.annotations.Exclude;

import modkit.enhanced.player.EnhancedPlayer;
import net.minecraft.server.MinecraftServer;

public class PlayerInfo extends Configuration<PlayerInfo> {

	private String playerUUID = "";

	@Comment("Last recorded playername (not used by the system, purely for file identification")
	public String playerName = "";

	@Comment("The amount of recorded player deaths")
	public int deaths = 0;

	@Comment("This will tell the system to show a title if the player died,")
	@Comment("if true, this won't be shown (automatically assigned by the mod)")
	public boolean markedDead = false;

	@Exclude
	public boolean dying = false;

	@Exclude
	public EnhancedPlayer player;

	public PlayerInfo(String base, EnhancedPlayer player) throws IOException {
		super(null);
		this.player = player;
		playerUUID = player.getUUID().toString();
		assignFile(base);
		readAll();
		playerName = player.getName().getString();
		writeAll();
	}

	@Override
	public String filename() {
		return playerUUID + ".ccfg";
	}

	@Override
	public String folder() {
		return "hardcore-lives";
	}

	public void updatePlayer(MinecraftServer server) {
		if (server.getPlayerList().getPlayer(UUID.fromString(playerUUID)) != null)
			player = EnhancedPlayer.from(server.getPlayerList().getPlayer(UUID.fromString(playerUUID)));
	}

}
