package me.suxuan.game.game.event.impl;

import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ShuffleInventoryEvent extends LuckyEvent {
	public ShuffleInventoryEvent() {
		super("SHUFFLE_INV", "背包混乱", "你的背包乱套了！", Rarity.RARE);
	}

	@Override
	public void execute(Arena arena) {
		for (UUID uuid : arena.getParticipants()) {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) {
				List<ItemStack> contents = new ArrayList<>();
				for (ItemStack is : p.getInventory().getContents()) {
					if (is != null) contents.add(is);
				}
				Collections.shuffle(contents);
				p.getInventory().clear();
				for (ItemStack is : contents) {
					p.getInventory().addItem(is);
				}
			}
		}
	}
}