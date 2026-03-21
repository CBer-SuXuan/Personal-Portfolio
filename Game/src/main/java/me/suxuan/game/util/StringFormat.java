package me.suxuan.game.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class StringFormat {

	private static final MiniMessage mm = MiniMessage.miniMessage();

	public static Component componentString(String str) {
		return mm.deserialize(str).decoration(TextDecoration.ITALIC, false);
	}

}
