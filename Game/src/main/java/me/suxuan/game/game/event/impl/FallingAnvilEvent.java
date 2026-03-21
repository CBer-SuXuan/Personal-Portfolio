package me.suxuan.game.game.event.impl;

import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FallingAnvilEvent extends LuckyEvent {
	public FallingAnvilEvent() {
		super("FALLING_ANVIL", "铁砧下落", "注意头顶！", Rarity.COMMON);
	}

	@Override
	public void execute(Arena arena) {
		for (UUID uuid : arena.getParticipants()) {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) {
				for (int i = 1; i < 5; i++)
					p.getWorld().setBlockData(p.getLocation().add(0, i, 0), Material.AIR.createBlockData());

				FallingBlock anvil = p.getWorld().spawn(p.getLocation().add(0, 5, 0), FallingBlock.class);
				anvil.setBlockData(Material.DAMAGED_ANVIL.createBlockData());
				anvil.setDropItem(false);
				anvil.setHurtEntities(true);
			}
		}
	}
}