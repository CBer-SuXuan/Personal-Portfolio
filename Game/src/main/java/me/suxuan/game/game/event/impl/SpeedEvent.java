package me.suxuan.game.game.event.impl;

import me.suxuan.game.Game;
import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SpeedEvent extends LuckyEvent {
	public SpeedEvent() {
		super("SPEED", "Speed", "<bold>♿冲刺冲刺♿", Rarity.COMMON);
	}

	@Override
	public void execute(Arena arena) {
		for (UUID uuid : arena.getParticipants()) {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null)
				p.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.3);
		}
		Bukkit.getScheduler().runTaskLater(Game.getInstance(), () -> {
			for (UUID uuid : arena.getParticipants()) {
				Player p = Bukkit.getPlayer(uuid);
				if (p != null)
					p.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.1);
			}
		}, 200);
	}
}