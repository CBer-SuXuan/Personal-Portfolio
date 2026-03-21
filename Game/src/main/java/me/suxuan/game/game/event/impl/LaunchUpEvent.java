package me.suxuan.game.game.event.impl;

import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.UUID;

public class LaunchUpEvent extends LuckyEvent {
	public LaunchUpEvent() {
		super("LAUNCH_UP", "重力翻转", "所有人起飞！", Rarity.EPIC);
	}

	@Override
	public void execute(Arena arena) {
		for (UUID uuid : arena.getParticipants()) {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) p.setVelocity(new Vector(0, 1.5, 0));
		}
	}
}