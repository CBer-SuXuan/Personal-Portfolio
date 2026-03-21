package me.suxuan.game.game.event.impl;

import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import me.suxuan.game.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UndeadEvent extends LuckyEvent {
	public UndeadEvent() {
		super("UNDEAD", "「不死」", "+1 life", Rarity.RARE);
	}

	@Override
	public void execute(Arena arena) {
		for (UUID uuid : arena.getParticipants()) {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null)
				p.getInventory().addItem(ItemBuilder.of(Material.TOTEM_OF_UNDYING).build());
		}
	}
}