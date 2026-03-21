package me.suxuan.game.game.effect.impl;

import me.suxuan.game.game.effect.GlobalEffect;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MoonGravity extends GlobalEffect {
	public MoonGravity() {
		super("MOON", "月球引力", "永久跳跃提升与缓降。");
	}

	@Override
	public void onStart() {
		arena.getPlayers().forEach(uuid -> {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, PotionEffect.INFINITE_DURATION, 1));
				p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, PotionEffect.INFINITE_DURATION, 0));
			}
		});
	}

	@Override
	public void onEnd() {
		arena.getPlayers().forEach(uuid -> {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) {
				p.removePotionEffect(PotionEffectType.JUMP_BOOST);
				p.removePotionEffect(PotionEffectType.SLOW_FALLING);
			}
		});
	}

	@Override
	public void onPlayerEliminated(Player p) {
		if (p != null) {
			p.removePotionEffect(PotionEffectType.JUMP_BOOST);
			p.removePotionEffect(PotionEffectType.SLOW_FALLING);
		}
	}
}