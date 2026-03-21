package me.suxuan.game.game.event.impl;

import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class IgniteAllEvent extends LuckyEvent {
	public IgniteAllEvent() {
		super("IGNITE_ALL", "炎热地狱", "所有人着火 3 秒！", Rarity.COMMON);
	}

	@Override
	public void execute(Arena arena) {
		for (UUID uuid : arena.getParticipants()) {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) p.setFireTicks(60);
		}
	}
}