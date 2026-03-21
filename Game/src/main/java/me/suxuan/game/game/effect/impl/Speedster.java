package me.suxuan.game.game.effect.impl;

import me.suxuan.game.game.effect.GlobalEffect;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Speedster extends GlobalEffect {
	public Speedster() {
		super("SPEED", "极速领域", "速度 II + 急迫 II");
	}

	@Override
	public void onStart() {
		arena.getPlayers().forEach(uuid -> {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 1));
				p.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, PotionEffect.INFINITE_DURATION, 1));
			}
		});
	}

	@Override
	public void onEnd() {
		arena.getPlayers().forEach(uuid -> {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) {
				p.removePotionEffect(PotionEffectType.SPEED);
				p.removePotionEffect(PotionEffectType.HASTE);
			}
		});
	}

	@Override
	public void onPlayerEliminated(Player p) {
		if (p != null) {
			p.removePotionEffect(PotionEffectType.SPEED);
			p.removePotionEffect(PotionEffectType.HASTE);
		}
	}
}