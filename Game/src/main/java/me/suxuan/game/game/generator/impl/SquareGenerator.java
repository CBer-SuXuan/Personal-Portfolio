package me.suxuan.game.game.generator.impl;

import me.suxuan.game.config.luckpillar.ArenaConfig;
import me.suxuan.game.game.generator.MapGenerator;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class SquareGenerator implements MapGenerator {
	@Override
	public void generateFloor(World world, Vector center, double halfSideLength, ArenaConfig config) {
		int cy = center.getBlockY();
		int cx = center.getBlockX();
		int cz = center.getBlockZ();

		int side = (int) Math.ceil(halfSideLength);

		for (int x = -side; x <= side; x++) {
			for (int z = -side; z <= side; z++) {
				double dist = Math.max(Math.abs(x), Math.abs(z));

				int colorIndex = (int) (dist) % config.floorBlocks().size();

				world.getBlockAt(cx + x, cy, cz + z).setType(config.floorBlocks().get(colorIndex));
			}
		}
	}
}
