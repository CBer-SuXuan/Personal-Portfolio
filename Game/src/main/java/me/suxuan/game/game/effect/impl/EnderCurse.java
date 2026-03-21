package me.suxuan.game.game.effect.impl;

import me.suxuan.game.game.effect.GlobalEffect;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.concurrent.ThreadLocalRandom;

public class EnderCurse extends GlobalEffect {
	public EnderCurse() {
		super("ENDER", "末影诅咒", "你会怕水，但有时能闪避攻击！");
	}

	@Override
	public void onStart() {
	}

	@Override
	public void onEnd() {
	}

	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && isPlayerInArena((Player) e.getEntity())) {
			if (ThreadLocalRandom.current().nextInt(100) < 20) {
				e.setCancelled(true);
				Player p = (Player) e.getEntity();
				p.teleport(p.getLocation().add(
						ThreadLocalRandom.current().nextDouble(-3, 3),
						0,
						ThreadLocalRandom.current().nextDouble(-3, 3)
				));
				p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
				p.sendMessage(MiniMessage.miniMessage().deserialize("<light_purple>[末影诅咒] 你闪避了这次攻击！"));
			}
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if (isPlayerInArena(e.getPlayer())) {
			Material m = e.getPlayer().getLocation().getBlock().getType();
			if (m == Material.WATER || m == Material.BUBBLE_COLUMN) {
				e.getPlayer().damage(1.0); // 进水扣血
			}
		}
	}
}