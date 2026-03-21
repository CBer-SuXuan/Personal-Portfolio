package me.suxuan.game.game.effect;

import lombok.Getter;
import me.suxuan.game.game.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

@Getter
public abstract class GlobalEffect implements Listener {

	private final String id;
	private final String name;
	private final String description;

	protected Arena arena;

	public GlobalEffect(String id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	/**
	 * 游戏开始时调用
	 */
	public void apply(Arena arena, Plugin plugin) {
		this.arena = arena;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		onStart();
	}

	/**
	 * 游戏结束时调用
	 */
	public void remove() {
		onEnd();
		HandlerList.unregisterAll(this);
		this.arena = null;
	}


	/**
	 * 当某个玩家被淘汰时调用
	 */
	public void onPlayerEliminated(Player player) {
	}

	/**
	 * 开启逻辑
	 */
	public abstract void onStart();

	/**
	 * 关闭逻辑
	 */
	public abstract void onEnd();

	// 判断事件中的玩家是否属于当前受影响的 Arena
	protected boolean isPlayerInArena(Player p) {
		return arena != null && arena.getPlayers().contains(p.getUniqueId());
	}
}