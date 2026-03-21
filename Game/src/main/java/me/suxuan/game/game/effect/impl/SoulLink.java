package me.suxuan.game.game.effect.impl;

import me.suxuan.game.game.effect.GlobalEffect;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.UUID;

public class SoulLink extends GlobalEffect {
	public SoulLink() {
		super("LINK", "生命共享", "一人受伤，全员平摊！");
	}

	@Override
	public void onStart() {
	}

	@Override
	public void onEnd() {
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player victim)) return;
		if (!isPlayerInArena(victim)) return;

		if (e.getCause() == EntityDamageEvent.DamageCause.CUSTOM) return;

		double originalDamage = e.getFinalDamage();
		long aliveCount = arena.getPlayers().stream()
				.filter(uuid -> !arena.getSpectators().contains(uuid))
				.count();

		if (aliveCount <= 1) return;

		double splitDamage = originalDamage / aliveCount;

		e.setDamage(splitDamage);
		for (UUID uuid : arena.getPlayers()) {
			if (uuid.equals(victim.getUniqueId())) continue;
			if (arena.getSpectators().contains(uuid)) continue;

			Player other = Bukkit.getPlayer(uuid);
			if (other != null) {
				other.damage(splitDamage);
			}
		}
	}
}