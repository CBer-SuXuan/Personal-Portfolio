package me.suxuan.game.game.effect.impl;

import me.suxuan.game.game.effect.GlobalEffect;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public class ExplosivePunch extends GlobalEffect {
	public ExplosivePunch() {
		super("EXPLOSIVE", "爆炸拳套", "攻击附带强力击退效果！");
	}

	@Override
	public void onStart() {
	}

	@Override
	public void onEnd() {
	}

	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player attacker) || !(e.getEntity() instanceof Player victim)) return;

		if (isPlayerInArena(attacker)) {
			victim.getWorld().spawnParticle(Particle.EXPLOSION, victim.getLocation(), 1);
			victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);

			Vector direction = attacker.getLocation().getDirection();
			Vector knockback = direction.normalize().multiply(2.0).setY(0.8);

			victim.setVelocity(knockback);

			e.setDamage(e.getDamage());
		}
	}
}