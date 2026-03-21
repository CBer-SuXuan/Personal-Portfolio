package me.suxuan.game.game.event.impl;

import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import me.suxuan.game.util.StringFormat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class SwapHealthEvent extends LuckyEvent {
	public SwapHealthEvent() {
		super("SWAP_HEALTH", "血量互换", "最高血量者和最低血量者互换！", Rarity.LEGENDARY);
	}

	@Override
	public void execute(Arena arena) {
		List<Player> players = new ArrayList<>();
		for (UUID uuid : arena.getParticipants()) {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) players.add(p);
		}
		if (players.size() < 2) return;

		players.sort(Comparator.comparingDouble(Player::getHealth));

		Player lowest = players.getFirst();
		Player highest = players.getLast();

		double lowHp = lowest.getHealth();
		double highHp = highest.getHealth();

		lowest.setHealth(highHp);
		highest.setHealth(lowHp);

		lowest.sendMessage(StringFormat.componentString("<green>你获得了 " + highest.getName() + " 的血量！"));
		highest.sendMessage(StringFormat.componentString("<red>你和 " + lowest.getName() + " 互换了血量！"));
	}
}