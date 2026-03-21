package me.suxuan.game.game.event.impl;

import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class WitherEvent extends LuckyEvent {

	public WitherEvent() {
		super("WITHER", "凋灵", "<obfuscated>xxxxxx", Rarity.LEGENDARY);
	}

	@Override
	public void execute(Arena arena) {
		World w = Bukkit.getWorld(arena.getWorldName());
		if (w == null) return;
		List<UUID> participants = arena.getParticipants();
		Location location = Bukkit.getPlayer(participants.get(new Random().nextInt(participants.size()))).getLocation();
		w.spawnEntity(location.add(0, 30, 0), EntityType.WITHER);
	}
}
