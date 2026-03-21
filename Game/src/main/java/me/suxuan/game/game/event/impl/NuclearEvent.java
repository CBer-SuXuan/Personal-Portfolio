package me.suxuan.game.game.event.impl;

import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NuclearEvent extends LuckyEvent {
	public NuclearEvent() {
		super("NUCLEAR", "核电", "核电，轻而易举", Rarity.LEGENDARY);
	}

	@Override
	public void execute(Arena arena) {
		for (UUID uuid : arena.getParticipants()) {
			Player p = Bukkit.getPlayer(uuid);
			if (p != null) {
				Creeper creeper = p.getWorld().spawn(p.getLocation().add(0, 0.5, 0), Creeper.class);
				creeper.setPowered(true);
				creeper.setExplosionRadius(10);
				creeper.customName(Component.text("坏了坏了")
						.decoration(TextDecoration.ITALIC, false)
						.decoration(TextDecoration.BOLD, true)
						.color(TextColor.color(0xff5555)));
			}
		}
	}
}