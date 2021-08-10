package org.asf.mods.hardcoreminecraft;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import modkit.config.ConfigManager;
import modkit.enhanced.player.EnhancedPlayer;
import modkit.util.Colors;
import modkit.util.ContainerConditions;
import modkit.util.EventUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import org.asf.cyan.api.modloader.Modloader;
import org.asf.cyan.api.modloader.information.game.GameSide;
import org.asf.cyan.mods.AbstractMod;
import org.asf.cyan.mods.config.CyanModfileManifest;
import org.asf.cyan.mods.events.AttachEvent;
import org.asf.mods.hardcoreminecraft.config.HardcoreConfiguration;
import org.asf.mods.hardcoreminecraft.config.MessageConfiguration;
import org.asf.mods.hardcoreminecraft.config.PlayerInfo;
import org.asf.mods.hardcoreminecraft.config.WorldConfiguration;
import org.asf.mods.hardcoreminecraft.events.CommonEvents;

public class HardcoreLives extends AbstractMod {

	private ConfigManager<HardcoreLives> configManager;
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

		info("Initializing the Hardcore: Lives mod configuration files...");

		// Load the configuration manager
		configManager = ConfigManager.getFor(HardcoreLives.class);

		// Load the configurations
		hardcoreConfig = configManager.getConfiguration(HardcoreConfiguration.class);
		messages = configManager.getConfiguration(MessageConfiguration.class);

		// Log amount of lives
		info(messages.systemMessagePrefix + " " + messages.systemMessageColor + "Running " + Colors.GOLD
				+ "Hardcore: Lives" + messages.systemMessageColor + ", each player has " + hardcoreConfig.lives
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

	private HashMap<MinecraftServer, WorldConfiguration> configs = new HashMap<MinecraftServer, WorldConfiguration>();

	public boolean isDisabled(MinecraftServer server) {
		if (server == null)
			return hardcoreConfig.disable;
		else {
			if (!configs.containsKey(server)) {
				File f = server.getWorldPath(LevelResource.ROOT).toFile();
				if (!f.exists())
					f.mkdir();

				WorldConfiguration conf = new WorldConfiguration(f.getAbsolutePath());
				conf.lives = hardcoreConfig.lives;
				conf.disable = hardcoreConfig.disable;
				try {
					conf.readAll();
					conf.writeAll();
				} catch (IOException e) {
				}
				configs.put(server, conf);
			}
			return configs.get(server).disable;
		}
	}

	public int getLives(MinecraftServer server) {
		if (server == null)
			return hardcoreConfig.lives;
		else {
			if (!configs.containsKey(server)) {
				File f = server.getWorldPath(LevelResource.ROOT).toFile();
				if (!f.exists())
					f.mkdir();

				WorldConfiguration conf = new WorldConfiguration(f.getAbsolutePath());
				conf.lives = hardcoreConfig.lives;
				conf.disable = hardcoreConfig.disable;
				try {
					conf.readAll();
					conf.writeAll();
				} catch (IOException e) {
				}
				configs.put(server, conf);
			}
			return configs.get(server).lives;
		}
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

	public void shutdownServer(MinecraftServer server) {
		if (configs.containsKey(server))
			configs.remove(server);
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

	public void reload() throws IOException {
		hardcoreConfig.readAll();
		for (MinecraftServer srv : new ArrayList<MinecraftServer>(configs.keySet())) {
			if (srv.isRunning())
				configs.get(srv).readAll();
			else
				configs.remove(srv);
		}
		messages.readAll();
	}

}
