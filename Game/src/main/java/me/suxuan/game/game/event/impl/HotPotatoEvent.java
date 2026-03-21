package me.suxuan.game.game.event.impl;

import me.suxuan.game.Game;
import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import me.suxuan.game.util.StringFormat;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class HotPotatoEvent extends LuckyEvent {
	public HotPotatoEvent() {
		super("HOT_POTATO", "死亡倒计时", "随机一名玩家将在 10 秒后暴毙！", Rarity.LEGENDARY);
	}

	@Override
	public void execute(Arena arena) {
		List<Player> players = new ArrayList<>();
		for (UUID uuid : arena.getParticipants()) {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) players.add(p);
		}
		if (players.isEmpty()) return;

		Player unlucky = players.get(new Random().nextInt(players.size()));

		arena.broadcast(StringFormat.componentString("<red><bold>" + unlucky.getName() + " 被死神盯上了！"));

		new BukkitRunnable() {
			int count = 10;

			@Override
			public void run() {
				if (!unlucky.isOnline() || !arena.getParticipants().contains(unlucky.getUniqueId())) {
					this.cancel();
					return;
				}

				if (count <= 0) {
					unlucky.setHealth(0);
					unlucky.getWorld().strikeLightningEffect(unlucky.getLocation());
					arena.broadcast(StringFormat.componentString("<red>" + unlucky.getName() + " 已经被死神带走。"));
					this.cancel();
					return;
				}

				unlucky.sendMessage(StringFormat.componentString("<red>你将在 " + count + " 秒后死亡！"));
				unlucky.getWorld().spawnParticle(Particle.FLAME, unlucky.getLocation(), 20, 0.5, 1, 0.5, 0.1);
				unlucky.playSound(unlucky.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1f, 1f);
				count--;
			}
		}.runTaskTimer(Game.getPlugin(Game.class), 0L, 20L);
	}
}