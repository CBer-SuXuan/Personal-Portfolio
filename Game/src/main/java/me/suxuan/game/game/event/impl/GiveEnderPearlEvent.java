package me.suxuan.game.game.event.impl;

import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class GiveEnderPearlEvent extends LuckyEvent {
	public GiveEnderPearlEvent() {
		super("GIVE_PEARL", "末影人快递", "获得 2 个末影珍珠！", Rarity.EPIC);
	}

	@Override
	public void execute(Arena arena) {
		for (UUID uuid : arena.getParticipants()) {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) {
				p.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 2));
			}
		}
	}
}