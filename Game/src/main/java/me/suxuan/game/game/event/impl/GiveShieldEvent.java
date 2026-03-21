package me.suxuan.game.game.event.impl;

import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class GiveShieldEvent extends LuckyEvent {
	public GiveShieldEvent() {
		super("GIVE_SHIELD", "护盾充能", "获得一个全新的盾牌！", Rarity.RARE);
	}

	@Override
	public void execute(Arena arena) {
		for (UUID uuid : arena.getParticipants()) {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) p.getInventory().addItem(new ItemStack(Material.SHIELD));
		}
	}
}