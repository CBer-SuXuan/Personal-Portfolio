package me.suxuan.game.game.event.impl;

import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class NightPhantomEvent extends LuckyEvent {
	public NightPhantomEvent() {
		super("NIGHT_PHANTOM", "夜晚降临", "黑暗里有点东西！", Rarity.COMMON);
	}

	@Override
	public void execute(Arena arena) {
		World w = Bukkit.getWorld(arena.getWorldName());
		if (w != null) {
			w.setTime(18000);
			List<UUID> participants = pickNRandom(arena.getParticipants(), 2);
			for (UUID uuid : participants) {
				w.spawnEntity(Bukkit.getPlayer(uuid).getLocation().add(0, 30, 0), EntityType.PHANTOM);
			}
		}
	}

	public static List<UUID> pickNRandom(List<UUID> lst, int n) {
		List<UUID> copy = new ArrayList<>(lst);
		Collections.shuffle(copy);
		return n > copy.size() ? copy.subList(0, copy.size()) : copy.subList(0, n);
	}
}