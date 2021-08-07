package org.asf.mods.hardcoreminecraft.events;

import modkit.enhanced.events.chat.ChatEvent;
import modkit.enhanced.events.objects.chat.ChatEventObject;
import modkit.enhanced.events.objects.player.PlayerDamageEventObject;
import modkit.enhanced.events.objects.player.PlayerJoinEventObject;
import modkit.enhanced.events.objects.server.ServerEventObject;
import modkit.enhanced.events.player.PlayerDeathEvent;
import modkit.enhanced.events.player.PlayerJoinEvent;
import modkit.enhanced.events.server.ServerStartupEvent;
import modkit.enhanced.player.titles.TitleType;
import modkit.enhanced.player.titles.MinecraftTitle;
import modkit.enhanced.player.titles.TitleIntervalConfiguration;
import modkit.enhanced.player.titles.MinecraftTitle.TitleComponent;

import modkit.events.core.ServerShutdownEvent;
import modkit.events.network.PlayerLogoutEvent;
import modkit.events.objects.core.ServerShutdownEventObject;
import modkit.events.objects.network.PlayerLogoutEventObject;
import modkit.events.objects.resources.ResourceManagerEventObject;
import modkit.events.resources.manager.ResourceManagerStartupEvent;
import modkit.resources.Resource;
import modkit.resources.Resources;
import modkit.util.Colors;
import modkit.util.server.language.ClientLanguage;
import net.minecraft.Util;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;

import org.asf.cyan.mods.AbstractMod;
import org.asf.cyan.mods.events.IEventListenerContainer;
import org.asf.cyan.mods.events.SimpleEvent;
import org.asf.mods.hardcoreminecraft.HardcoreSpectator;
import org.asf.mods.hardcoreminecraft.config.PlayerInfo;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CommonEvents implements IEventListenerContainer {

	private static HashMap<MinecraftServer, HashMap<String, PlayerInfo>> servers = new HashMap<MinecraftServer, HashMap<String, PlayerInfo>>();

	private boolean ready = false;

	@SimpleEvent(ServerShutdownEvent.class)
	public void onShutdown(ServerShutdownEventObject obj) {
		if (servers.containsKey(obj.getServer()))
			servers.remove(obj.getServer());
	}

	@SimpleEvent(ServerStartupEvent.class)
	public void onStartup(ServerEventObject obj) {
		if (ready)
			return;
		ready = true;

		HashMap<String, PlayerInfo> players = servers.get(obj.getServer());
		if (players == null) {
			players = new HashMap<String, PlayerInfo>();
			servers.put(obj.getServer(), players);
		}

		final HashMap<String, PlayerInfo> playersF = players;
		HardcoreSpectator mod = HardcoreSpectator.getInstance(HardcoreSpectator.class);

		new Thread(() -> {
			while (obj.getServer().isRunning() && servers.containsKey(obj.getServer())) {

				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					break;
				}

				if (mod.isDisabled(obj.getServer()))
					continue;

				while (true) {
					try {
						for (PlayerInfo info : playersF.values()) {
							info.updatePlayer(obj.getServer());
							if ((info.dying && !info.player.isDeadOrDying())
									|| (info.deaths >= mod.getLives(obj.getServer()) && !info.markedDead
											&& !info.player.isDeadOrDying())) {
								info.dying = false;

								if (info.deaths >= mod.getLives(obj.getServer())) {
									info.player.setGameMode(GameType.SPECTATOR);

									obj.getServer().getPlayerList().broadcastMessage(
											new TextComponent(mod.getMessageConfig().finalDeathMessage.replace("%p",
													info.player.getDisplayName().getString())),
											ChatType.SYSTEM, Util.NIL_UUID);
									info.player.showTitle(MinecraftTitle.create()
											.setIntervalConfiguration(TitleIntervalConfiguration.create(10, 30, 10))
											.addComponent(TitleComponent.create(TitleType.TITLE,
													mod.getMessageConfig().finalDeathTitle.replace("%p",
															info.player.getDisplayName().getString())))
											.addComponent(TitleComponent.create(TitleType.SUBTITLE,
													mod.getMessageConfig().finalDeathSubtitle.replace("%p",
															info.player.getDisplayName().getString()))));

									try {
										info.markedDead = true;
										info.writeAll();
									} catch (IOException e) {
									}
								} else {
									if ((mod.getLives(obj.getServer()) - info.deaths) <= 2
											&& (mod.getLives(obj.getServer()) - info.deaths) > 0) {
										obj.getServer().getPlayerList()
												.broadcastMessage(
														new TextComponent(mod.getMessageConfig().lastDeathMessages
																.replace("%p", info.player.getDisplayName().getString())
																.replace("%l",
																		Integer.toString(mod.getLives(obj.getServer())
																				- info.deaths))),
														ChatType.SYSTEM, Util.NIL_UUID);
									} else if (mod.getLives(obj.getServer()) - info.deaths > 0) {
										obj.getServer().getPlayerList()
												.broadcastMessage(
														new TextComponent(mod.getMessageConfig().firstDeathMessages
																.replace("%p", info.player.getDisplayName().getString())
																.replace("%l",
																		Integer.toString(mod.getLives(obj.getServer())
																				- info.deaths))),
														ChatType.SYSTEM, Util.NIL_UUID);
									}

									if (mod.getLives(obj.getServer()) - info.deaths > 0) {
										info.player.showTitle(MinecraftTitle.create()
												.setIntervalConfiguration(TitleIntervalConfiguration.create(10, 30, 10))
												.addComponent(TitleComponent.create(TitleType.TITLE,
														mod.getMessageConfig().deathTitle
																.replace("%p", info.player.getDisplayName().getString())
																.replace("%l",
																		Integer.toString(mod.getLives(obj.getServer())
																				- info.deaths))))
												.addComponent(
														TitleComponent.create(TitleType.SUBTITLE,
																mod.getMessageConfig().deathSubtitle
																		.replace("%p",
																				info.player.getDisplayName()
																						.getString())
																		.replace("%l",
																				Integer.toString(
																						mod.getLives(obj.getServer())
																								- info.deaths)))));
									}
								}
							}
						}
						break;
					} catch (ConcurrentModificationException e) {
					}
				}
			}
		}, "Hardcore Spectator Player Manager").start();
	}

	public static List<PlayerInfo> getAllPlayers(MinecraftServer server) {
		HashMap<String, PlayerInfo> players = servers.get(server);
		if (players == null) {
			players = new HashMap<String, PlayerInfo>();
			servers.put(server, players);
		}

		return List.of(players.values().toArray(t -> new PlayerInfo[t]));
	}

	@SimpleEvent(ChatEvent.class)
	public void onChat(ChatEventObject obj) throws IOException {
		HardcoreSpectator mod = HardcoreSpectator.getInstance(HardcoreSpectator.class);
		if (mod.isDisabled(obj.getServer()))
			return;

		HashMap<String, PlayerInfo> players = servers.get(obj.getServer());
		if (players == null) {
			players = new HashMap<String, PlayerInfo>();
			servers.put(obj.getServer(), players);
		}

		if (!players.containsKey(obj.getPlayer().getUUID().toString())) {
			File f = obj.getServer().getWorldPath(LevelResource.ROOT).toFile();

			PlayerInfo info;
			try {
				info = new PlayerInfo(f.getCanonicalPath(), obj.getPlayer());
			} catch (IOException e) {
				info = new PlayerInfo(f.getAbsolutePath(), obj.getPlayer());
			}

			players.put(obj.getPlayer().getUUID().toString(), info);
		}

		PlayerInfo info = players.get(obj.getPlayer().getUUID().toString());
		if (info.deaths >= mod.getLives(obj.getServer())) {
			obj.setFormatter((msg, pl) -> {
				return new TextComponent(mod.getMessageConfig().spectatorMessagePrefix + "<"
						+ mod.getMessageConfig().spectatorMessagePrefix + pl.getDisplayName().getString()
						+ mod.getMessageConfig().spectatorMessagePrefix + "> "
						+ msg.replaceAll(Colors.CONTROL_CHAR + ".", "")).copy();
			});
		}
	}

	@SimpleEvent(PlayerJoinEvent.class)
	public void onJoin(PlayerJoinEventObject obj) throws IOException {
		File f = obj.getServer().getWorldPath(LevelResource.ROOT).toFile();

		HashMap<String, PlayerInfo> players = servers.get(obj.getServer());
		if (players == null) {
			players = new HashMap<String, PlayerInfo>();
			servers.put(obj.getServer(), players);
		}

		PlayerInfo info;
		try {
			info = new PlayerInfo(f.getCanonicalPath(), obj.getPlayer());
		} catch (IOException e) {
			info = new PlayerInfo(f.getAbsolutePath(), obj.getPlayer());
		}

		players.put(obj.getPlayer().getUUID().toString(), info);
		if (info.deaths >= HardcoreSpectator.getInstance(HardcoreSpectator.class).getLives(obj.getServer())) {
			obj.getPlayer().setGameMode(GameType.SPECTATOR);
		}
	}

	@SimpleEvent(PlayerLogoutEvent.class)
	public void onLeave(PlayerLogoutEventObject obj) throws IOException {
		if (!servers.containsKey(obj.getServer()))
			return;

		HashMap<String, PlayerInfo> players = servers.get(obj.getServer());

		if (players.containsKey(obj.getPlayer().getUUID().toString())) {
			players.get(obj.getPlayer().getUUID().toString()).writeAll();
			players.remove(obj.getPlayer().getUUID().toString());
		}
	}

	@SimpleEvent(PlayerDeathEvent.class)
	public void onDeath(PlayerDamageEventObject obj) throws IOException {
		HardcoreSpectator mod = HardcoreSpectator.getInstance(HardcoreSpectator.class);
		if (mod.isDisabled(obj.getServer()))
			return;

		HashMap<String, PlayerInfo> players = servers.get(obj.getServer());
		if (players == null) {
			players = new HashMap<String, PlayerInfo>();
			servers.put(obj.getServer(), players);
		}

		if (!players.containsKey(obj.getPlayer().getUUID().toString())) {
			File f = obj.getServer().getWorldPath(LevelResource.ROOT).toFile();

			PlayerInfo info;
			try {
				info = new PlayerInfo(f.getCanonicalPath(), obj.getPlayer());
			} catch (IOException e) {
				info = new PlayerInfo(f.getAbsolutePath(), obj.getPlayer());
			}

			players.put(obj.getPlayer().getUUID().toString(), info);
		}

		PlayerInfo info = players.get(obj.getPlayer().getUUID().toString());
		info.deaths++;
		info.dying = true;
		info.writeAll();
	}

	@SimpleEvent(ResourceManagerStartupEvent.class)
	public void resourceManagerStartup(ResourceManagerEventObject event) {
		loadLanguage(Resources.getFor(AbstractMod.getInstance(HardcoreSpectator.class)).getResource("lang/en_us.json"));
	}

	private void loadLanguage(Resource resource) {
		JsonObject lang = JsonParser.parseString(resource.readAsString()).getAsJsonObject();

		lang.entrySet().forEach(ent -> {
			ClientLanguage.registerLanguageKey(ent.getKey(), ent.getValue().getAsString());
		});
	}

}
