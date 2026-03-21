package me.suxuan.game.game.event.impl;

import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class GiveGoldenAppleEvent extends LuckyEvent {

	public GiveGoldenAppleEvent() {
		super("GIVE_GOLDEN_APPLE", "幸运补给", "每人获得一颗金苹果！", Rarity.COMMON);
	}

	@Override
	public void execute(Arena arena) {
		for (UUID uuid : arena.getParticipants()) {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) p.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE));
		}
	}
}