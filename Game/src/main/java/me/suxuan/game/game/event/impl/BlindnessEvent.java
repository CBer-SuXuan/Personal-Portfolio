package me.suxuan.game.game.event.impl;

import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class BlindnessEvent extends LuckyEvent {
	public BlindnessEvent() {
		super("BLINDNESS", "失明挑战", "所有人失明 5 秒！", Rarity.COMMON);
	}

	@Override
	public void execute(Arena arena) {
		for (UUID uuid : arena.getParticipants()) {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));
		}
	}
}