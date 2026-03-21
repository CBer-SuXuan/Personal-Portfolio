package me.suxuan.game.game.event.impl;

import me.suxuan.game.Game;
import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TouchEvent extends LuckyEvent {
	public TouchEvent() {
		super("TOUCH", "摸摸", "摸得更远(5s)", Rarity.COMMON);
	}

	@Override
	public void execute(Arena arena) {
		for (UUID uuid : arena.getParticipants()) {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null)
				p.getAttribute(Attribute.ENTITY_INTERACTION_RANGE).setBaseValue(10);
		}
		Bukkit.getScheduler().runTaskLater(Game.getInstance(), () -> {
			for (UUID uuid : arena.getParticipants()) {
				Player p = Bukkit.getPlayer(uuid);
				if (p != null)
					p.getAttribute(Attribute.ENTITY_INTERACTION_RANGE).setBaseValue(3);
			}
		}, 100);
	}
}