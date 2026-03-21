package me.suxuan.game.game.generator;

import me.suxuan.game.config.luckpillar.ArenaConfig;
import me.suxuan.game.game.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public interface MapGenerator {
	/**
	 * 生成地图并返回玩家的出生点列表
	 *
	 * @param arena 目标房间
	 * @return 玩家应该被传送到的位置列表 (与 arena.getPlayers() 顺序对应)
	 */
	default List<Location> generate(Arena arena, int playerCount) {
		ArenaConfig config = arena.getConfig();
		World world = Bukkit.getWorld(arena.getWorldName());
		Vector center = config.center();

		double pillarRadius = config.pillarBaseRadius() + (playerCount * config.pillarRadiusGrowth());

		double floorRadius = pillarRadius + config.floorMargin();

		generateFloor(world, center, floorRadius, config);

		return generatePillars(world, center, pillarRadius, playerCount, config.pillarMaterial(), config);
	}

	void generateFloor(World world, Vector center, double radius, ArenaConfig config);

	default List<Location> generatePillars(World world, Vector center, double radius, int count, Material mat, ArenaConfig config) {

		List<Location> spawnLocs = new ArrayList<>();
		double angleStep = (2 * Math.PI) / count;

		for (int i = 0; i < count; i++) {
			double angle = i * angleStep;
			double x = center.getX() + (radius * Math.cos(angle));
			double z = center.getZ() + (radius * Math.sin(angle));
			double y = center.getY();

			Location loc = new Location(world, x, y + config.pillarHeight() + 1, z);

			loc.setDirection(new Location(world, center.getX(), y + config.pillarHeight() + 1, center.getZ()).toVector().subtract(loc.toVector()));
			spawnLocs.add(loc);

			for (double j = y + 1; j <= y + config.pillarHeight(); j++) {
				loc.getWorld().getBlockAt(new Location(world, x, j, z)).setType(mat);
			}
		}
		return spawnLocs;
	}
}
