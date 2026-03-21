package me.suxuan.game.command;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class RootCommandRegistry {

	private final JavaPlugin plugin;
	private final Map<String, AbstractRootCommand> roots = new HashMap<>();

	public RootCommandRegistry(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	public void registerRoot(String rootName, AbstractRootCommand root) {
		String key = rootName.toLowerCase(Locale.ROOT);
		if (roots.containsKey(key)) {
			throw new IllegalStateException("发现重复的根命令 " + rootName);
		}

		PluginCommand pc = plugin.getCommand(rootName);
		if (pc == null) {
			throw new IllegalStateException("命令 " + rootName + " 没有在plugin.yml中声明" + rootName);
		}

		pc.setExecutor(root);
		pc.setTabCompleter(root);
		roots.put(key, root);
	}
}
