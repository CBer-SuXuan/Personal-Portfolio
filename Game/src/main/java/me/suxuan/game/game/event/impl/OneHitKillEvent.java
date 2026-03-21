package me.suxuan.game.game.event.impl;

import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class OneHitKillEvent extends LuckyEvent {
	public OneHitKillEvent() {
		super("ONE_HIT_KILL", "一击必杀", "所有人获得力量 X，持续 5 秒！", Rarity.LEGENDARY);
	}

	@Override
	public void execute(Arena arena) {
		for (UUID uuid : arena.getParticipants()) {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) p.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 100, 9)); // Strength X
		}
	}
}