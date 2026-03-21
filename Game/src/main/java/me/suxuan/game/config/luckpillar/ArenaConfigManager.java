package me.suxuan.game.config.luckpillar;

import me.suxuan.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * 管理所有游戏地图配置的类
 * 负责加载 plugins/LuckyPillars/games/ 目录下的所有 .yml 文件
 */
public class ArenaConfigManager {

	private final Game plugin;
	private final Map<String, ArenaConfig> configs = new HashMap<>();

	public ArenaConfigManager(Game plugin) {
		this.plugin = plugin;
	}

	public void loadConfigs() {
		configs.clear();

		File gamesDir = new File(plugin.getDataFolder(), "games");
		if (!gamesDir.exists()) {
			gamesDir.mkdirs();
			// 生成一个默认配置文件
			plugin.saveResource("games/desert.yml", false);
			plugin.saveResource("games/rainbow.yml", false);
		}

		File[] files = gamesDir.listFiles((dir, name) -> name.endsWith(".yml"));
		if (files == null) return;

		for (File file : files) {
			try {
				String id = file.getName().replace(".yml", "");
				YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

				// 解析lobby location
				Vector lobbyVector = null;
				String lobbyLocStr = yml.getString("lobby-location");
				lobbyVector = getVector(file, lobbyLocStr);

				// 解析return location
				Location returnLoc = null;
				String returnLocStr = yml.getString("return-location");
				try {
					String world = returnLocStr.split(":")[0];
					String returnStr = returnLocStr.split(":")[1];
					String[] parts = returnStr.replace(" ", "").split(",");
					if (parts.length >= 3) {
						double x = Double.parseDouble(parts[0]);
						double y = Double.parseDouble(parts[1]);
						double z = Double.parseDouble(parts[2]);
						returnLoc = new Location(Bukkit.getWorld(world), x, y, z);
					}
				} catch (Exception e) {
					plugin.getLogger().warning("解析坐标失败: " + returnLocStr + " 在文件 " + file.getName());
				}

				// 读取random item pool
				List<String> matNames = yml.getStringList("random-item-pool");
				List<Material> pool = new ArrayList<>();
				if (matNames.size() == 1 && matNames.getFirst().equalsIgnoreCase("ALL")) {
					for (Material m : Material.values()) {
						if (m.isItem() && !m.isAir() && !m.name().contains("COMMAND")
								&& !m.name().contains("BARRIER") && !m.equals(Material.LIGHT)
								&& !m.equals(Material.JIGSAW) && !m.name().contains("STRUCTURE")
								&& !m.name().contains("TEST") && !m.name().contains("DEBUG")
								&& !m.equals(Material.PAINTING)) {
							pool.add(m);
						}
					}
				}

				// 解析center location
				Vector centerVector = null;
				String centerLocStr = yml.getString("generator.center");
				centerVector = getVector(file, centerLocStr);

				// 解析pillar material
				String pillarMaterialStr = yml.getString("generator.pillar.material");
				if (pillarMaterialStr == null) pillarMaterialStr = "STONE";

				// 解析floor blocks
				List<String> floorBlocksList = yml.getStringList("generator.floor.blocks");
				List<Material> floorBlocks = new ArrayList<>();
				for (String floorBlockStr : floorBlocksList) {
					Material material = Material.getMaterial(floorBlockStr);
					if (material == null) material = Material.STONE;
					floorBlocks.add(material);
				}

				ArenaConfig config = new ArenaConfig(
						id,
						yml.getString("display-name", id),
						yml.getString("template-name", "default_template"),

						yml.getInt("min-players", 2),
						yml.getInt("max-players", 12),
						yml.getInt("countdown-seconds", 10),
						lobbyVector,

						yml.getString("return-location").split(":")[0],
						returnLoc,

						centerVector,

						Material.getMaterial(pillarMaterialStr),
						yml.getDouble("generator.pillar.base-radius"),
						yml.getDouble("generator.pillar.radius-growth"),
						yml.getInt("generator.pillar.height"),

						yml.getDouble("generator.floor.margin"),
						yml.getInt("generator.floor.thickness"),
						yml.getString("generator.floor.style"),
						floorBlocks,

						yml.getInt("item-give-interval", 20),
						pool,

						yml.getInt("event-interval", 60),
						yml.getStringList("event"),

						yml.getString("fixed-effect", "NONE"),

						yml.getDouble("border.min-radius", 5.0),
						yml.getDouble("border.shrink-amount", 3.0),
						yml.getInt("border.safe-time", 30),
						yml.getInt("border.shrink-time", 10),
						yml.getDouble("border.damage", 2.0)
				);

				configs.put(id, config);
				plugin.getLogger().info("已加载游戏配置: " + id);

			} catch (Exception e) {
				plugin.getLogger().log(Level.SEVERE, "加载配置文件失败: " + file.getName(), e);
			}
		}
	}

	private Vector getVector(File file, String lobbyLocStr) {
		Vector locVector = null;
		try {
			String[] parts = lobbyLocStr.replace(" ", "").split(",");
			if (parts.length >= 3) {
				double x = Double.parseDouble(parts[0]);
				double y = Double.parseDouble(parts[1]);
				double z = Double.parseDouble(parts[2]);
				locVector = new Vector(x, y, z);
			}
		} catch (Exception e) {
			plugin.getLogger().warning("解析坐标失败: " + lobbyLocStr + " 在文件 " + file.getName());
		}
		return locVector;
	}

	public ArenaConfig getConfig(String id) {
		return configs.get(id);
	}

	public List<ArenaConfig> getAllConfigs() {
		return new ArrayList<>(configs.values());
	}

	public List<String> getAllTemplateNames() {
		return configs.values().stream()
				.map(ArenaConfig::templateName)
				.distinct()
				.collect(Collectors.toList());
	}

}