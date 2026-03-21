package me.suxuan.game.command.luckpillar;

import me.suxuan.game.command.AbstractRootCommand;
import me.suxuan.game.command.SubCommand;

public final class PillarRootCommand extends AbstractRootCommand {

	public PillarRootCommand(SubCommand... commands) {
		for (SubCommand c : commands) register(c);
	}

	@Override
	protected String rootName() {
		return "pillar";
	}
}
