package me.suxuan.game.game.event.impl;

import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DissolveFloorEvent extends LuckyEvent {
	public DissolveFloorEvent() {
		super("DISSOLVE_FLOOR", "方块消失", "脚下的方块消失了！快跑！", Rarity.LEGENDARY);
	}

	@Override
	public void execute(Arena arena) {
		for (UUID uuid : arena.getParticipants()) {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) {
				Block b = p.getLocation().subtract(0, 1, 0).getBlock();
				if (b.getType() != Material.AIR && b.getType() != Material.BEDROCK) {
					b.setType(Material.AIR);
				}
			}
		}
	}
}