package me.suxuan.game.game.event.impl;

import me.suxuan.game.Game;
import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PunchEvent extends LuckyEvent {
	public PunchEvent() {
		super("PUNCH", "一击必杀", "一万暴击！(10s)", Rarity.COMMON);
	}

	@Override
	public void execute(Arena arena) {
		for (UUID uuid : arena.getParticipants()) {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null)
				p.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(50);
		}
		Bukkit.getScheduler().runTaskLater(Game.getInstance(), () -> {
			for (UUID uuid : arena.getParticipants()) {
				Player p = Bukkit.getPlayer(uuid);
				if (p != null)
					p.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(1);
			}
		}, 200);
	}
}