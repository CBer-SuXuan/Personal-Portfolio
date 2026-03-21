package me.suxuan.game.game.event.impl;

import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class GiveBowEvent extends LuckyEvent {
	public GiveBowEvent() {
		super("GIVE_BOW", "神射手", "获得弓和光灵箭！", Rarity.COMMON);
	}

	@Override
	public void execute(Arena arena) {
		ItemStack bow = new ItemStack(Material.BOW);
		ItemStack arrows = new ItemStack(Material.SPECTRAL_ARROW, 3);
		for (UUID uuid : arena.getParticipants()) {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) {
				p.getInventory().addItem(bow, arrows);
			}
		}
	}
}