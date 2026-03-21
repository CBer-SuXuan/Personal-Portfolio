package me.suxuan.game.listener;

import me.suxuan.game.game.Arena;
import me.suxuan.game.game.ArenaState;
import me.suxuan.game.game.GameManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LobbyInteractListener implements Listener {

	private final GameManager gameManager;
	private final Map<UUID, Long> cooldowns = new HashMap<>();

	public LobbyInteractListener(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (!event.getAction().isRightClick()) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		Player p = event.getPlayer();
		UUID uuid = p.getUniqueId();

		long now = System.currentTimeMillis();
		long lastClick = cooldowns.getOrDefault(uuid, 0L);
		if (now - lastClick < 200) {
			return;
		}
		cooldowns.put(uuid, now);

		Arena arena = gameManager.getArena(p);
		if (arena == null || arena.getState() != ArenaState.WAITING) return;

		ItemStack item = event.getItem();
		if (item == null) return;

		// 离开
		if (item.getType() == Material.RED_BED) {
			gameManager.quitGame(p);
			return;
		}

		// 房主开始
		if (item.getType() == Material.EMERALD && p.getUniqueId().equals(arena.getHostUuid())) {
			if (arena.canStart()) {
				arena.startCountdown();
			} else {
				p.sendMessage(MiniMessage.miniMessage().deserialize("<red>人数不足！"));
			}
			return;
		}

		// 玩家准备
		if ((item.getType() == Material.GRAY_DYE || item.getType() == Material.LIME_DYE)
				&& !p.getUniqueId().equals(arena.getHostUuid())) {
			boolean current = arena.isPlayerReady(p);
			arena.setPlayerReady(p, !current);
			arena.updateReadyItem(p);
			return;
		}

		// 设置
		if (item.getType() == Material.COMPARATOR) {
			arena.getVoteGUI().open(p);
			return;
		}

		event.setCancelled(true);
	}
}