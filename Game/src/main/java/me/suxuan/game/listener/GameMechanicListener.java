package me.suxuan.game.listener;

import me.suxuan.game.game.Arena;
import me.suxuan.game.game.ArenaState;
import me.suxuan.game.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.util.Vector;

public class GameMechanicListener implements Listener {

	private final GameManager gameManager;

	public GameMechanicListener(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	/**
	 * 虚空掉落判定 & 伤害控制
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;

		Arena arena = gameManager.getArena(player);
		if (arena == null) return;

		if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
			event.setCancelled(true);

			if (arena.getState() == ArenaState.IN_GAME) {
				arena.eliminatePlayer(player);
			} else {
				Vector lobbyVec = arena.getConfig().lobbyLocation();
				player.teleport(new Location(Bukkit.getWorld(arena.getWorldName()),
						lobbyVec.getX(), lobbyVec.getY(), lobbyVec.getZ()));
			}
			return;
		}

		if (arena.getState() != ArenaState.IN_GAME) {
			event.setCancelled(true);
		}
	}

	/**
	 * PVP 控制
	 */
	@EventHandler
	public void onPVP(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player victim) || !(event.getDamager() instanceof Player attacker)) return;

		Arena arena = gameManager.getArena(victim);

		if (arena == null || !arena.getPlayers().contains(attacker.getUniqueId())) {
			event.setCancelled(true);
			return;
		}

		if (arena.getSpectators().contains(attacker.getUniqueId())) {
			event.setCancelled(true);
			return;
		}

		if (arena.getState() != ArenaState.IN_GAME) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		Arena arena = gameManager.getArena(player);

		if (arena == null) return;

		if (arena.getState() == ArenaState.IN_GAME) {
			event.setCancelled(true);
			arena.eliminatePlayer(player);
		}
	}

}