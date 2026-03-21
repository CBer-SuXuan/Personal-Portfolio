package me.suxuan.game.game.event.impl;

import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class LowGravityEvent extends LuckyEvent {
	public LowGravityEvent() {
		super("LOW_GRAVITY", "低重力模式", "跳跃提升 III，持续 15 秒！", Rarity.RARE);
	}

	@Override
	public void execute(Arena arena) {
		for (UUID uuid : arena.getParticipants()) {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 300, 2));
		}
	}
}