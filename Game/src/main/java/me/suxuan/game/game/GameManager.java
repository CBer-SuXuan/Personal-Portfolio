package me.suxuan.game.game;

import lombok.Getter;
import me.suxuan.game.Game;
import me.suxuan.game.config.luckpillar.ArenaConfig;
import me.suxuan.game.config.luckpillar.ArenaConfigManager;
import me.suxuan.game.core.SlimeWorldFactory;
import me.suxuan.game.game.event.LuckyEvent;
import me.suxuan.game.util.StringFormat;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class GameManager {

	private final Game plugin;
	private final SlimeWorldFactory worldFactory;
	private final ArenaConfigManager configManager;

	// 运行中的房间: Map<房间ID, Arena对象>
	private final Map<String, Arena> activeArenas = new HashMap<>();

	public GameManager(Game plugin, SlimeWorldFactory worldFactory, ArenaConfigManager configManager) {
		this.plugin = plugin;
		this.worldFactory = worldFactory;
		this.configManager = configManager;
	}

	public void quitGame(Player player) {
		Arena arena = getArena(player);
		if (arena != null) {
			arena.removePlayer(player);
		}
	}

	public boolean isInGame(Player player) {
		return getArena(player) != null;
	}

	public Arena getArena(Player player) {
		for (Arena arena : activeArenas.values()) {
			if (arena.getPlayers().contains(player.getUniqueId()) || arena.getSpectators().contains(player.getUniqueId())) {
				return arena;
			}
		}
		return null;
	}

	public Arena getArena(String roomID) {
		for (Arena arena : activeArenas.values()) {
			if (arena.getId().equalsIgnoreCase(roomID)) {
				return arena;
			}
		}
		return null;
	}

	public void removeArena(String id) {
		activeArenas.remove(id);
	}

	public void shutdown() {
		for (Arena arena : new ArrayList<>(activeArenas.values())) {
			arena.stop(arena.getWorldName());
		}
		activeArenas.clear();
	}

	public void createAndJoinRoom(Player host, ArenaConfig config, String password) {
		if (isInGame(host)) return;

		String id = "room_" + host.getName() + "_" + System.currentTimeMillis();

		Arena arena = new Arena(plugin, worldFactory, config, id, host, password);

		activeArenas.put(id, arena);

		arena.addPlayer(host);
	}

	public List<Arena> getActiveArenas() {
		return new ArrayList<>(activeArenas.values());
	}

	// 修改 joinGame 方法，只负责加入匹配或指定房间
	public void joinArena(Player p, Arena arena) {
		if (arena.getPlayers().size() >= arena.getConfig().maxPlayers()) {
			p.sendMessage(MiniMessage.miniMessage().deserialize("<red>房间已满"));
			return;
		}
		arena.addPlayer(p);
	}

	public void forceTriggerEvent(Player admin, String eventId) {
		Arena arena = getArena(admin);
		if (arena == null || arena.getState() != ArenaState.IN_GAME) {
			admin.sendRichMessage("<red>你必须在一个正在进行游戏的房间内才能触发事件！");
			return;
		}

		LuckyEvent event = plugin.getEventManager().getEventById(eventId);

		if (event == null) {
			admin.sendRichMessage("<red>未找到ID为 " + eventId + " 的事件！");
			return;
		}

		// 3. 执行事件
		admin.sendRichMessage("<green>正在强制触发事件: " + event.getName());

		arena.broadcast(StringFormat.componentString("<red>[管理员] 强制触发了 " + event.getName()));

		event.execute(arena);
	}
}