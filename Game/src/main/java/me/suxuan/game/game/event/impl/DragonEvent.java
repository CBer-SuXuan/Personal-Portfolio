package me.suxuan.game.game.event.impl;

import me.suxuan.game.Game;
import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class DragonEvent extends LuckyEvent {

	public DragonEvent() {
		super("DRAGON", "龙袭", "要结束了？", Rarity.LEGENDARY);
	}

	@Override
	public void execute(Arena arena) {
		World w = Bukkit.getWorld(arena.getWorldName());
		if (w == null) return;
		List<UUID> participants = arena.getParticipants();
		Player player = Bukkit.getPlayer(participants.get(new Random().nextInt(participants.size())));
		if (player == null) return;
		EnderDragon dragon = (EnderDragon) w.spawnEntity(player.getLocation().add(0, 30, 0), EntityType.ENDER_DRAGON);
		dragon.setPhase(EnderDragon.Phase.CHARGE_PLAYER);

		Bukkit.getScheduler().runTaskTimer(Game.getInstance(), task -> {
			if (dragon.isDead() || !dragon.isValid()) {
				task.cancel();
			} else {
				dragon.setPhase(EnderDragon.Phase.CHARGE_PLAYER);
			}
		}, 0, 10);
	}
}
