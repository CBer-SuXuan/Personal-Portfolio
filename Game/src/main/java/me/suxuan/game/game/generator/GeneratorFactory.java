package me.suxuan.game.game.generator;

import me.suxuan.game.game.generator.impl.CircleGenerator;
import me.suxuan.game.game.generator.impl.SquareGenerator;

public class GeneratorFactory {

	public static MapGenerator getGenerator(String type) {
		switch (type) {
			case "CIRCLE":
				return new CircleGenerator();
			case "SQUARE":
				return new SquareGenerator();
		}

		return new CircleGenerator();
	}

}
