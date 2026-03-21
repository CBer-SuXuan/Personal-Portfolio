package me.suxuan.game.game.event.impl;

import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LightningStormEvent extends LuckyEvent {
	public LightningStormEvent() {
		super("LIGHTNING", "雷击", "我的避雷针呢？", Rarity.RARE);
	}

	@Override
	public void execute(Arena arena) {
		for (UUID uuid : arena.getParticipants()) {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) p.getWorld().strikeLightning(p.getLocation());
		}
	}
}