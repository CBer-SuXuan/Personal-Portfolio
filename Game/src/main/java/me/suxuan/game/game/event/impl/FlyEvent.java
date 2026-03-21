package me.suxuan.game.game.event.impl;

import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class FlyEvent extends LuckyEvent {

	public FlyEvent() {
		super("FLY", "FLY", "<bold>自由的风", Rarity.RARE);
	}

	@Override
	public void execute(Arena arena) {
		for (UUID uuid : arena.getParticipants()) {
			Player p = Bukkit.getPlayer(uuid);
			if (p == null) continue;
			p.getInventory().addItem(new ItemStack(Material.ELYTRA));
			p.getInventory().addItem(new ItemStack(Material.FIREWORK_ROCKET, 3));
		}
	}
}
