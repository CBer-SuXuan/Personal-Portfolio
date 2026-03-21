package me.suxuan.game.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 计分板封装
 */
public class SimpleScoreboard {

	private final Player player;
	private final Scoreboard scoreboard;
	private final Objective objective;
	private final List<Component> lines = new ArrayList<>();

	public SimpleScoreboard(Player player) {
		this.player = player;
		this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		this.objective = scoreboard.registerNewObjective("dummy", Criteria.DUMMY, Component.text("Title"));
		this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		player.setScoreboard(scoreboard);
	}

	public void updateTitle(Component title) {
		objective.displayName(title);
	}

	public void updateLines(List<Component> newLines) {
		while (lines.size() > newLines.size()) {
			removeLine(lines.size() - 1);
		}

		for (int i = 0; i < newLines.size(); i++) {
			Component text = newLines.get(i);
			if (i >= lines.size()) {
				createLine(i, text);
			} else if (!text.equals(lines.get(i))) {
				updateLine(i, text);
			}
		}
	}

	private void createLine(int index, Component text) {
		Team team = scoreboard.registerNewTeam("line-" + index);
		String entry = ChatColor.values()[index].toString();
		team.addEntry(entry);

		team.prefix(text);
		objective.getScore(entry).setScore(15 - index);
		lines.add(text);
	}

	private void updateLine(int index, Component text) {
		Team team = scoreboard.getTeam("line-" + index);
		if (team != null) {
			team.prefix(text);
			lines.set(index, text);
		}
	}

	private void removeLine(int index) {
		scoreboard.resetScores(ChatColor.values()[index].toString());
		Team team = scoreboard.getTeam("line-" + index);
		if (team != null) team.unregister();
		lines.remove(index);
	}

	public void delete() {
		player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
	}
}