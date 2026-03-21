package me.suxuan.game.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;

public abstract class AbstractRootCommand implements CommandExecutor, TabCompleter {

	private final Map<String, SubCommand> byName = new HashMap<>();
	private final Map<String, SubCommand> byAlias = new HashMap<>();

	protected void register(SubCommand cmd) {
		String name = cmd.name().toLowerCase(Locale.ROOT);
		if (byName.containsKey(name) || byAlias.containsKey(name)) {
			throw new IllegalStateException("Duplicate subcommand: " + name);
		}
		byName.put(name, cmd);

		for (String a : cmd.aliases()) {
			if (a == null || a.isBlank()) continue;
			String alias = a.toLowerCase(Locale.ROOT);
			if (byName.containsKey(alias) || byAlias.containsKey(alias)) {
				throw new IllegalStateException("Duplicate subcommand alias: " + alias);
			}
			byAlias.put(alias, cmd);
		}
	}

	protected abstract String rootName();

	protected void sendHelp(CommandSender sender) {
		sender.sendMessage("§e用法: §f/" + rootName() + " <sub>");
		List<String> names = new ArrayList<>(byName.keySet());
		Collections.sort(names);
		sender.sendMessage("§e子命令: §f" + String.join(", ", names));
	}

	private SubCommand find(String keyRaw) {
		String key = keyRaw.toLowerCase(Locale.ROOT);
		SubCommand cmd = byName.get(key);
		if (cmd != null) return cmd;
		return byAlias.get(key);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			sendHelp(sender);
			return true;
		}

		SubCommand sub = find(args[0]);
		if (sub == null) {
			sender.sendMessage("§c未知子命令: §f" + args[0]);
			sendHelp(sender);
			return true;
		}

		if (!sub.checkPlayerOnly(sender)) {
			sender.sendMessage("§c该子命令只能由玩家执行");
			return true;
		}

		if (!sub.canUse(sender)) {
			sender.sendMessage("§c你没有权限执行该命令");
			return true;
		}

		String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
		return sub.execute(sender, subArgs);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length <= 1) {
			String prefix = args.length == 0 ? "" : args[0].toLowerCase(Locale.ROOT);
			List<String> out = new ArrayList<>();
			for (SubCommand s : byName.values()) {
				if (!s.checkPlayerOnly(sender)) continue;
				if (!s.canUse(sender)) continue;
				String n = s.name();
				if (n.toLowerCase(Locale.ROOT).startsWith(prefix)) out.add(n);
			}
			Collections.sort(out);
			return out;
		}

		SubCommand sub = find(args[0]);
		if (sub == null) return Collections.emptyList();
		if (!sub.checkPlayerOnly(sender)) return Collections.emptyList();
		if (!sub.canUse(sender)) return Collections.emptyList();

		String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
		return sub.tabComplete(sender, subArgs);
	}
}
