package me.suxuan.game.game.generator.impl;

import me.suxuan.game.config.luckpillar.ArenaConfig;
import me.suxuan.game.game.generator.MapGenerator;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class CircleGenerator implements MapGenerator {

	@Override
	public void generateFloor(World world, Vector center, double radius, ArenaConfig config) {
		int cy = center.getBlockY();
		int cx = center.getBlockX();
		int cz = center.getBlockZ();

		int r = (int) Math.ceil(radius);
		int rSq = r * r;

		for (int x = -r; x <= r; x++) {
			for (int z = -r; z <= r; z++) {
				// 圆形判定
				if (x * x + z * z <= rSq) {
					double dist = Math.sqrt(x * x + z * z);

					int colorIndex = (int) (dist) % config.floorBlocks().size();

					Block block = world.getBlockAt(cx + x, cy, cz + z);
					block.setType(config.floorBlocks().get(colorIndex));
				}
			}
		}
	}

}