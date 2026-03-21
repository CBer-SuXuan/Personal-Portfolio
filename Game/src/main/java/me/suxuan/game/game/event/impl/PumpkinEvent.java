package me.suxuan.game.game.event.impl;

import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import me.suxuan.game.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

import java.util.UUID;

public class PumpkinEvent extends LuckyEvent {
	public PumpkinEvent() {
		super("PUMPKIN", "南瓜头", "喜欢我的南瓜头吗？", Rarity.EPIC);
	}

	@Override
	public void execute(Arena arena) {
		for (UUID uuid : arena.getParticipants()) {
			Player p = Bukkit.getPlayer(uuid);
			if (p == null) continue;
			p.getInventory().setItem(EquipmentSlot.HEAD,
					ItemBuilder.of(Material.CARVED_PUMPKIN).enchant(Enchantment.BINDING_CURSE, 1).build());
		}
	}
}