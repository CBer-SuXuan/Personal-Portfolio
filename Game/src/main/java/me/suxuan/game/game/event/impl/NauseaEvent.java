package me.suxuan.game.game.event.impl;

import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class NauseaEvent extends LuckyEvent {
	public NauseaEvent() {
		super("NAUSEA", "醉酒模式", "所有人反胃 10 秒！", Rarity.COMMON);
	}

	@Override
	public void execute(Arena arena) {
		for (UUID uuid : arena.getParticipants()) {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) p.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 200, 0));
		}
	}
}