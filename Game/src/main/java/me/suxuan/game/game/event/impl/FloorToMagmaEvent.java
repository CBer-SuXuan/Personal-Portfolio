package me.suxuan.game.game.event.impl;

import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FloorToMagmaEvent extends LuckyEvent {
	public FloorToMagmaEvent() {
		super("FLOOR_MAGMA", "烫脚地板", "脚下的方块变成了岩浆块！", Rarity.RARE);
	}

	@Override
	public void execute(Arena arena) {
		for (UUID uuid : arena.getParticipants()) {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) {
				Block b = p.getLocation().subtract(0, 1, 0).getBlock();
				if (b.getType() != Material.AIR) {
					b.setType(Material.MAGMA_BLOCK);
				}
			}
		}
	}
}