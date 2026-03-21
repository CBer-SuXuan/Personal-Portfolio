package me.suxuan.game.game.event.impl;

import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class CreeperEvent extends LuckyEvent {
	public CreeperEvent() {
		super("CREEPER", "CREEPER", "Creeper?", Rarity.RARE);
	}

	@Override
	public void execute(Arena arena) {
		for (UUID uuid : arena.getParticipants()) {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) {
				Creeper creeper = p.getWorld().spawn(p.getLocation().add(0, 0.5, 0), Creeper.class);
				if (ThreadLocalRandom.current().nextDouble() < 0.10D) {
					creeper.setPowered(true);
				}
			}
		}
	}
}