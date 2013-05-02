package de.uvwxy.habitrpg.api;

public class SpriteConverterMale implements ISpriteConverter {

	public int getOMHair(String s) {

		if (s.equals("blond")) {
			return 19;
		} else if (s.equals("black")) {
			return 20;
		} else if (s.equals("brown")) {
			return 21;
		} else if (s.equals("white")) {
			return 2;
		}

		// default: "blond"
		return 19;
	}

	@Override
	public int getOMSkin(String s) {

		if (s.equals("dead")) {
			return 29;
		} else if (s.equals("orc")) {
			return 30;
		} else if (s.equals("asian")) {
			return 31;
		} else if (s.equals("black")) {
			return 32;
		} else if (s.equals("white")) {
			return 33;
		}

		// default: "dead"
		return 29;
	}

	public int getOMArmor(int i) {
		switch (i) {
		case 0:
			return 28;
		case 1:
			return 27;
		case 2:
			return 26;
		case 3:
			return 25;
		case 4:
			return 24;
		case 5:
			return 23;
		default:
			return 28;
		}

	}

	@Override
	public int getOMWeapon(int i) {
		switch (i) {
		case 0:
			return 6;
		case 1:
			return 5;
		case 2:
			return 4;
		case 3:
			return 3;
		case 4:
			return 2;
		case 5:
			return 1;
		default:
			return 0;
		}
	}

	@Override
	public int getOMShield(int i) {
		switch (i) {
		case 0:
			return 12;
		case 1:
			return 11;
		case 2:
			return 10;
		case 3:
			return 9;
		case 4:
			return 8;
		case 5:
			return 7;
		default:
			return 12;
		}
	}

	@Override
	public int getOMHead(int i) {
		// HEAD_0("0", 18), HEAD_1("1", 17), HEAD_2("2", 16), HEAD_3("3", 15), HEAD_4("4", 14), HEAD_5("5", 13),
		switch (i) {
		case 0:
			return 18;
		case 1:
			return 17;
		case 2:
			return 16;
		case 3:
			return 15;
		case 4:
			return 14;
		case 5:
			return 13;
		default:
			return 18;
		}
	}

}
