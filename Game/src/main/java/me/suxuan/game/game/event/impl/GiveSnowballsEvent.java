package me.suxuan.game.game.event.impl;

import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class GiveSnowballsEvent extends LuckyEvent {
	public GiveSnowballsEvent() {
		super("GIVE_SNOWBALLS", "雪球大战", "获得 16 个雪球！", Rarity.COMMON);
	}

	@Override
	public void execute(Arena arena) {
		for (UUID uuid : arena.getParticipants()) {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) p.getInventory().addItem(new ItemStack(Material.SNOWBALL, 16));
		}
	}
}