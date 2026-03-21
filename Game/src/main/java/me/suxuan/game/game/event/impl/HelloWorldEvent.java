package me.suxuan.game.game.event.impl;

import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class HelloWorldEvent extends LuckyEvent {
	public HelloWorldEvent() {
		super("HELLO_WORLD", "你好,世界", "Hello World!", Rarity.COMMON);
	}

	@Override
	public void execute(Arena arena) {
		for (UUID uuid : arena.getParticipants()) {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) p.addPotionEffect(new PotionEffect(PotionEffectType.INSTANT_HEALTH, 20, 5));
		}
	}
}