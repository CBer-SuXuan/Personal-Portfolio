package me.suxuan.game.listener;

import me.suxuan.game.Game;
import me.suxuan.game.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;

public class PlayerConnectionListener implements Listener {

	private final GameManager gameManager;
	private final Location lobbyLocation;

	private String lobbyWorld;

	public PlayerConnectionListener(GameManager gameManager) {
		this.gameManager = gameManager;
		Game plugin = gameManager.getPlugin();

		File file = new File(plugin.getDataFolder(), "config.yml");
		YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
		Location lobbyLocation = null;
		String lobbyLocStr = yml.getString("main-lobby");

		// TODO
//		if (lobbyLocStr == null || lobbyLocStr.equalsIgnoreCase("world:-16.0, 66, -7.0")) {
//			plugin.getLogger().warning("请在config.yml中配置主大厅位置，当前内容为本地测试内容");
//		}

		try {
			String world = lobbyLocStr.split(":")[0];
			lobbyWorld = world;
			String returnStr = lobbyLocStr.split(":")[1];
			String[] parts = returnStr.replace(" ", "").split(",");
			if (parts.length >= 3) {
				double x = Double.parseDouble(parts[0]);
				double y = Double.parseDouble(parts[1]);
				double z = Double.parseDouble(parts[2]);
				lobbyLocation = new Location(Bukkit.getWorld(world), x, y, z);
			}
		} catch (Exception e) {
			plugin.getLogger().warning("解析坐标失败: " + lobbyLocStr + " 在文件 " + file.getName());
		}
		this.lobbyLocation = lobbyLocation;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		lobbyLocation.setWorld(Bukkit.getWorld(lobbyWorld));
		event.getPlayer().teleport(lobbyLocation);
		gameManager.getPlugin().getTabManager().updateTab(event.getPlayer());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		// 玩家离线时，强制退出游戏
		gameManager.quitGame(event.getPlayer());
		event.setQuitMessage(null);
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Bukkit.getScheduler().runTaskLater(gameManager.getPlugin(), () -> {
			gameManager.getPlugin().getTabManager().updateTab(event.getPlayer());
		}, 5L);
	}

}