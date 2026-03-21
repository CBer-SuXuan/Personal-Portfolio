package me.suxuan.game.game.event.impl;

import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class GiveBlocksEvent extends LuckyEvent {
	public GiveBlocksEvent() {
		super("GIVE_BLOCKS", "建筑师之梦", "获得 10 个羊毛！", Rarity.COMMON);
	}

	@Override
	public void execute(Arena arena) {
		for (UUID uuid : arena.getParticipants()) {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) p.getInventory().addItem(new ItemStack(Material.WHITE_WOOL, 10));
		}
	}
}