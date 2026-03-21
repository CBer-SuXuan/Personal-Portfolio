package me.suxuan.game.game.event.impl;

import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class InventoryExchangeEvent extends LuckyEvent {
	public InventoryExchangeEvent() {
		super("INVENTORY_EXCHANGE", "背包交换", "这何尝不是一种NTR", Rarity.RARE);
	}

	private static final class InventorySnapshot {
		private final ItemStack[] contents;
		private final ItemStack[] armorContents;
		private final ItemStack offHand;

		private InventorySnapshot(ItemStack[] contents, ItemStack[] armorContents, ItemStack offHand) {
			this.contents = contents;
			this.armorContents = armorContents;
			this.offHand = offHand;
		}

		private static ItemStack safeClone(ItemStack item) {
			return item == null ? null : item.clone();
		}

		private static ItemStack[] safeClone(ItemStack[] items) {
			ItemStack[] copy = new ItemStack[items.length];
			for (int i = 0; i < items.length; i++) {
				copy[i] = safeClone(items[i]);
			}
			return copy;
		}

		private static InventorySnapshot capture(Player p) {
			return new InventorySnapshot(
					safeClone(p.getInventory().getContents()),
					safeClone(p.getInventory().getArmorContents()),
					safeClone(p.getInventory().getItemInOffHand())
			);
		}

		private void applyTo(Player p) {
			p.getInventory().setContents(safeClone(contents));
			p.getInventory().setArmorContents(safeClone(armorContents));
			p.getInventory().setItemInOffHand(safeClone(offHand));
			p.updateInventory();
		}
	}

	@Override
	public void execute(Arena arena) {
		List<Player> players = new ArrayList<>();
		for (UUID uuid : arena.getParticipants()) {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null && p.isOnline()) {
				players.add(p);
			}
		}

		if (players.size() < 2) return;

		for (Player p : players) {
			p.closeInventory();
		}

		Collections.shuffle(players);

		Map<UUID, InventorySnapshot> snapshots = new HashMap<>();
		for (Player p : players) {
			snapshots.put(p.getUniqueId(), InventorySnapshot.capture(p));
		}

		for (int i = 0; i < players.size(); i++) {
			Player receiver = players.get(i);
			Player giver = players.get((i + 1) % players.size());
			InventorySnapshot snap = snapshots.get(giver.getUniqueId());
			if (snap != null) {
				snap.applyTo(receiver);
			}
		}
	}
}