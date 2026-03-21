package me.suxuan.game.game.effect.impl;

import me.suxuan.game.game.effect.GlobalEffect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ThornsArmor extends GlobalEffect {
	public ThornsArmor() {
		super("THORNS", "反伤甲", "受到的伤害将反弹 50% 给攻击者。");
	}

	@Override
	public void onStart() {
	}

	@Override
	public void onEnd() {
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player victim && e.getDamager() instanceof Player attacker) {
			if (isPlayerInArena(victim)) {
				attacker.damage(e.getFinalDamage() * 0.5);
			}
		}
	}
}
