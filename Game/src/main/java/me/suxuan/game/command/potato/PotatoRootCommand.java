package me.suxuan.game.command.potato;

import me.suxuan.game.command.AbstractRootCommand;
import me.suxuan.game.command.SubCommand;

public final class PotatoRootCommand extends AbstractRootCommand {

	public PotatoRootCommand(SubCommand... commands) {
		for (SubCommand c : commands) register(c);
	}

	@Override
	protected String rootName() {
		return "potato";
	}
}
