package me.suxuan.game.game.effect.impl;

import me.suxuan.game.game.effect.GlobalEffect;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class TitanMode extends GlobalEffect {
	public TitanMode() {
		super("TITAN", "泰坦降临", "所有人血量翻倍，体型变大，移速变慢！");
	}

	@Override
	public void onStart() {
		arena.getPlayers().forEach(uuid -> {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) {
				p.getAttribute(Attribute.MAX_HEALTH).setBaseValue(40.0);
				p.setHealth(40.0);
				p.getAttribute(Attribute.SCALE).setBaseValue(1.5);
				p.setWalkSpeed(0.15f); // 略慢
			}
		});
	}

	@Override
	public void onEnd() {
		arena.getPlayers().forEach(uuid -> {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) {
				p.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20.0);
				p.getAttribute(Attribute.SCALE).setBaseValue(1.0);
				p.setWalkSpeed(0.2f);
			}
		});
	}

	@Override
	public void onPlayerEliminated(Player p) {
		if (p != null) {
			p.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20.0);
			p.getAttribute(Attribute.SCALE).setBaseValue(1.0);
			p.setWalkSpeed(0.2f);
		}
	}
}