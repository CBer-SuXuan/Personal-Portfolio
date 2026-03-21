package me.suxuan.game.game.event.impl;

import me.suxuan.game.game.Arena;
import me.suxuan.game.game.event.LuckyEvent;

public class NothingEvent extends LuckyEvent {

	public NothingEvent() {
		super("NOTHING", "无事发生", "什么都没有发生。", Rarity.COMMON);
	}

	@Override
	public void execute(Arena arena) {

	}
}
