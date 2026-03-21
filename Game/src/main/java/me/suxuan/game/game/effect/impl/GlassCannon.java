package me.suxuan.game.game.effect.impl;

import me.suxuan.game.game.effect.GlobalEffect;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GlassCannon extends GlobalEffect {
	public GlassCannon() {
		super("GLASS_CANNON", "玻璃大炮", "只有1点血，但攻击力爆表！");
	}

	@Override
	public void onStart() {
		arena.getPlayers().forEach(uuid -> {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) {
				p.getAttribute(Attribute.MAX_HEALTH).setBaseValue(2.0); // 1心
				p.setHealth(2.0);
				p.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, PotionEffect.INFINITE_DURATION, 10));
			}
		});
	}

	@Override
	public void onEnd() {
		arena.getPlayers().forEach(uuid -> {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) {
				p.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20.0);
				p.removePotionEffect(PotionEffectType.STRENGTH);
			}
		});
	}

	@Override
	public void onPlayerEliminated(Player p) {
		if (p != null) {
			p.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20.0);
			p.removePotionEffect(PotionEffectType.STRENGTH);
		}
	}

}