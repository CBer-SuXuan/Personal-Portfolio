package me.suxuan.game.game.event.impl;

import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class GiveTntKitEvent extends LuckyEvent {
	public GiveTntKitEvent() {
		super("GIVE_TNT_KIT", "爆破鬼才", "获得 5 个 TNT 和打火石！", Rarity.RARE);
	}

	@Override
	public void execute(Arena arena) {
		for (UUID uuid : arena.getParticipants()) {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) {
				p.getInventory().addItem(new ItemStack(Material.TNT, 5));
				p.getInventory().addItem(new ItemStack(Material.FLINT_AND_STEEL));
			}
		}
	}
}