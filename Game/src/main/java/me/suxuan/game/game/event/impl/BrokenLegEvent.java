package me.suxuan.game.game.event.impl;

import me.suxuan.game.Game;
import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BrokenLegEvent extends LuckyEvent {
	public BrokenLegEvent() {
		super("BROKEN_LEG", "断腿", "啊哦，不能跳喽(10s)", Rarity.COMMON);
	}

	@Override
	public void execute(Arena arena) {
		for (UUID uuid : arena.getParticipants()) {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null)
				p.getAttribute(Attribute.JUMP_STRENGTH).setBaseValue(0);
		}
		Bukkit.getScheduler().runTaskLater(Game.getInstance(), () -> {
			for (UUID uuid : arena.getParticipants()) {
				Player p = Bukkit.getPlayer(uuid);
				if (p != null)
					p.getAttribute(Attribute.JUMP_STRENGTH).setBaseValue(0.42);
			}
		}, 200);
	}
}