package me.suxuan.game.config.luckpillar;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.List;

public record ArenaConfig(
		String identifier,
		String displayName,
		String templateName,

		int minPlayers,
		int maxPlayers,
		int countdownSeconds,

		Vector lobbyLocation,

		String returnLocationWorldName,
		Location returnLocation,

		Vector center,

		Material pillarMaterial,
		double pillarBaseRadius,
		double pillarRadiusGrowth,
		int pillarHeight,

		double floorMargin,
		int floorThickness,
		String floorStyle,
		List<Material> floorBlocks,

		int itemGiveInterval,
		List<Material> itemPool,

		int eventInterval,
		List<String> enabledEvents,

		String fixedEffect,

		double minBorderRadius,
		double borderShrinkAmount,
		int boarderSafeTime,
		int boarderShrinkTime,
		double borderDamage
) {
}
