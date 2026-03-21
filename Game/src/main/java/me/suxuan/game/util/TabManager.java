package me.suxuan.game.util;

import me.suxuan.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TabManager {

	private final Game plugin;

	public TabManager(Game plugin) {
		this.plugin = plugin;
	}

	public void updateTab(Player player) {
		for (Player other : Bukkit.getOnlinePlayers()) {
			if (other.equals(player)) continue;

			boolean sameWorld = other.getWorld().equals(player.getWorld());

			if (sameWorld) {
				player.showPlayer(plugin, other);
				other.showPlayer(plugin, player);
			} else {
				player.hidePlayer(plugin, other);
				other.hidePlayer(plugin, player);
			}
		}
	}

	public void resetTab(Player player) {
		for (Player other : Bukkit.getOnlinePlayers()) {
			player.showPlayer(plugin, other);
			other.showPlayer(plugin, player);
		}
	}
}