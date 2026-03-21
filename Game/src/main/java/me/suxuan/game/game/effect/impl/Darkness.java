package me.suxuan.game.game.effect.impl;

import me.suxuan.game.game.effect.GlobalEffect;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Darkness extends GlobalEffect {
	public Darkness() {
		super("DARK", "无尽黑暗", "视野极度受限。");
	}

	@Override
	public void onStart() {
		arena.getPlayers().forEach(uuid -> {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, PotionEffect.INFINITE_DURATION, 0));
				p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, PotionEffect.INFINITE_DURATION, 0));
			}
		});
	}

	@Override
	public void onEnd() {
		arena.getPlayers().forEach(uuid -> {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) {
				p.removePotionEffect(PotionEffectType.BLINDNESS);
				p.removePotionEffect(PotionEffectType.NIGHT_VISION);
			}
		});
	}

	@Override
	public void onPlayerEliminated(Player p) {
		if (p != null) {
			p.removePotionEffect(PotionEffectType.BLINDNESS);
			p.removePotionEffect(PotionEffectType.NIGHT_VISION);
		}
	}
}