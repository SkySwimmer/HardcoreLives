package org.asf.mods.hardcoreminecraft;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import modkit.config.ConfigManager;
import modkit.enhanced.player.EnhancedPlayer;
import modkit.util.Colors;
import modkit.util.ContainerConditions;
import modkit.util.EventUtil;
import net.minecraft.server.MinecraftServer;

import org.asf.cyan.api.modloader.Modloader;
import org.asf.cyan.api.modloader.information.game.GameSide;
import org.asf.cyan.mods.AbstractMod;
import org.asf.cyan.mods.config.CyanModfileManifest;
import org.asf.cyan.mods.events.AttachEvent;
import org.asf.mods.hardcoreminecraft.config.HardcoreConfiguration;
import org.asf.mods.hardcoreminecraft.config.MessageConfiguration;
import org.asf.mods.hardcoreminecraft.config.PlayerInfo;
import org.asf.mods.hardcoreminecraft.events.CommonEvents;

public class HardcoreSpectator extends AbstractMod {

	private ConfigManager<HardcoreSpectator> configManager;
	private HardcoreConfiguration hardcoreConfig;
	private MessageConfiguration messages;

	@Override
	public void setup(Modloader modloader, GameSide side, CyanModfileManifest manifest) {
		super.setup(modloader, side, manifest);

		// Events:
		EventUtil.registerContainer(ContainerConditions.COMMON, this::commonEvents);
		EventUtil.registerContainer(ContainerConditions.COMMON, this::commands);

	}

	@AttachEvent(value = "mods.init", synchronize = true)
	public void init() throws IOException {

		info("Initializing the Hardcore: Spectator mod configuration files...");

		// Load the configuration manager
		configManager = ConfigManager.getFor(HardcoreSpectator.class);

		// Load the configurations
		hardcoreConfig = configManager.getConfiguration(HardcoreConfiguration.class);
		messages = configManager.getConfiguration(MessageConfiguration.class);

		// Log amount of lives
		info(messages.systemMessagePrefix + " " + messages.systemMessageColor + "Running " + Colors.GOLD
				+ "Hardcore: Spectator" + messages.systemMessageColor + ", each player has " + hardcoreConfig.lives
				+ " lives.");
	}

	//
	// The following methods provide the event containers loaded by EventUtil
	//
	private String commonEvents() {
		return getClass().getPackageName() + ".events.CommonEvents";
	}

	private String commands() {
		return getClass().getPackageName() + ".events.CommandEvents";
	}

	public HardcoreConfiguration getConfig() {
		return hardcoreConfig;
	}

	public MessageConfiguration getMessageConfig() {
		return messages;
	}

	public static class PlayerData {
		private EnhancedPlayer player;
		private PlayerInfo info;

		public PlayerInfo getInfo() {
			return info;
		}

		public EnhancedPlayer getPlayer() {
			return player;
		}
	}

	public List<PlayerData> getPlayers(MinecraftServer server) {
		return List.of(CommonEvents.getAllPlayers(server).stream().map(t -> {
			PlayerData p = new PlayerData();
			t.updatePlayer(server);
			p.info = t;
			p.player = t.player;
			return p;
		}).toArray(t -> new PlayerData[t]));
	}

	public PlayerInfo getPlayerInfo(MinecraftServer server, UUID uuid) {
		for (PlayerInfo pl : CommonEvents.getAllPlayers(server)) {
			if (pl.player.getUUID().equals(uuid))
				return pl;
		}
		return null;
	}

}
