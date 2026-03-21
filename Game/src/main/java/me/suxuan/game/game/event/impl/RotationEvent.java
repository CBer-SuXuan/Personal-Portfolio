package me.suxuan.game.game.event.impl;

import me.suxuan.game.Game;
import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class RotationEvent extends LuckyEvent {

	public RotationEvent() {
		super("ROTATION", "自转", "oiiaioooooiai(10s)", Rarity.COMMON);
	}

	@Override
	public void execute(Arena arena) {
		new BukkitRunnable() {
			int ticks = 200;

			@Override
			public void run() {
				if (ticks-- <= 0) {
					cancel();
					return;
				}
				for (UUID uuid : arena.getParticipants()) {
					Player p = Bukkit.getPlayer(uuid);
					if (p == null || !p.isOnline()) continue;

					Location loc = p.getLocation();
					float yaw = loc.getYaw();
					yaw = (yaw + 6.0f) % 360.0f; // 每 tick 旋转 6°
					loc.setYaw(yaw);
					p.teleport(loc);
				}
			}
		}.runTaskTimer(Game.getInstance(), 0L, 1L);
	}
}