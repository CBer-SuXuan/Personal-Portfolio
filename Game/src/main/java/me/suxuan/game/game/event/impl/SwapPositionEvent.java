package me.suxuan.game.game.event.impl;

import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SwapPositionEvent extends LuckyEvent {

	public SwapPositionEvent() {
		super("SWAP_POSITIONS", "乾坤大挪移", "所有人随机交换位置！", Rarity.EPIC);
	}

	@Override
	public void execute(Arena arena) {
		List<Player> onlinePlayers = new ArrayList<>();
		for (UUID uuid : arena.getParticipants()) {
			Player p = org.bukkit.Bukkit.getPlayer(uuid);
			if (p != null) onlinePlayers.add(p);
		}

		if (onlinePlayers.size() < 2) return;

		List<Location> locations = new ArrayList<>();
		for (Player p : onlinePlayers) {
			locations.add(p.getLocation());
		}

		Collections.rotate(locations, 1);

		for (int i = 0; i < onlinePlayers.size(); i++) {
			Player p = onlinePlayers.get(i);
			p.teleport(locations.get(i));
			p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
		}
	}
}