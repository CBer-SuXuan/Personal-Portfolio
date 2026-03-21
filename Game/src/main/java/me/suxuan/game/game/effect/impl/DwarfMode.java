package me.suxuan.game.game.effect.impl;

import me.suxuan.game.game.effect.GlobalEffect;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class DwarfMode extends GlobalEffect {
	public DwarfMode() {
		super("DWARF", "小小英雄", "体型减半，难以被击中！");
	}

	@Override
	public void onStart() {
		arena.getPlayers().forEach(uuid -> {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) {
				// 1.20.5+ API
				p.getAttribute(Attribute.SCALE).setBaseValue(0.5);
				p.setWalkSpeed(0.3f);
			}
		});
	}

	@Override
	public void onEnd() {
		arena.getPlayers().forEach(uuid -> {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) {
				p.getAttribute(Attribute.SCALE).setBaseValue(1.0);
				p.setWalkSpeed(0.2f);
			}
		});
	}

	@Override
	public void onPlayerEliminated(Player p) {
		if (p != null) {
			p.getAttribute(Attribute.SCALE).setBaseValue(1.0);
			p.setWalkSpeed(0.2f);
		}
	}
}