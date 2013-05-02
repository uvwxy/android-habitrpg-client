package de.uvwxy.habitrpg.api;

public class SpriteConverterFemale implements ISpriteConverter {

	@Override
	public int getOMHair(String s) {
		if (s.equals("blond")) {
			return 26;
		} else if (s.equals("black")) {
			return 25;
		} else if (s.equals("brown")) {
			return 24;
		} else if (s.equals("white")) {
			return 23;
		}

		// default: "blond"
		return 26;
	}

	public final static int[] SKIN_DEAD = { 0, 34 };
	public final static int[] SKIN_ORC = { 1, 35 };
	public final static int[] SKIN_ASIAN = { 2, 36 };
	public final static int[] SKIN_BLACK = { 3, 37 };
	public final static int[] SKIN_WHITE = { 4, 38 };

	@Override
	public int getOMSkin(String s) {
		if (s.equals("dead")) {
			return 34;
		} else if (s.equals("orc")) {
			return 35;
		} else if (s.equals("asian")) {
			return 36;
		} else if (s.equals("black")) {
			return 37;
		} else if (s.equals("white")) {
			return 38;
		}

		// default: "blond"
		return 26;
	}

	public final static int[] ARMOR_0 = { 0, 33 };
	public final static int[] ARMOR_0_V2 = { 6, 32 };
	public final static int[] ARMOR_1 = { 1, 31 };
	public final static int[] ARMOR_2 = { 2, 30 };
	public final static int[] ARMOR_3 = { 3, 29 };
	public final static int[] ARMOR_4 = { 4, 28 };
	public final static int[] ARMOR_5 = { 5, 27 };

	@Override
	public int getOMArmor(int i) {
		switch (i) {
		case 0:
			return 33;
		case 1:
			return 31;
		case 2:
			return 30;
		case 3:
			return 29;
		case 4:
			return 28;
		case 5:
			return 27;
		case 6:
			return 32;
		default:
			return 33;
		}
	}

	public final static int[] WEAPON_0 = { 0, 6 };
	public final static int[] WEAPON_1 = { 1, 5 };
	public final static int[] WEAPON_2 = { 2, 4 };
	public final static int[] WEAPON_3 = { 3, 3 };
	public final static int[] WEAPON_4 = { 4, 2 };
	public final static int[] WEAPON_5 = { 5, 1 };
	public final static int[] WEAPON_6 = { 6, 0 };

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

	public final static int[] SHIELD_0 = { 0, 12 };
	public final static int[] SHIELD_1 = { 1, 11 };
	public final static int[] SHIELD_2 = { 2, 10 };
	public final static int[] SHIELD_3 = { 3, 9 };
	public final static int[] SHIELD_4 = { 4, 8 };
	public final static int[] SHIELD_5 = { 5, 7 };

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

	public final static int[] HEAD_0 = { 0, 22 };
	public final static int[] HEAD_1 = { 1, 21 };
	public final static int[] HEAD_2 = { 2, 20 };
	public final static int[] HEAD_3 = { 3, 18 };
	public final static int[] HEAD_4 = { 4, 16 };
	public final static int[] HEAD_5 = { 5, 14 };
	public final static int[] HEAD_2_V2 = { 6, 19 };
	public final static int[] HEAD_3_V2 = { 7, 17 };
	public final static int[] HEAD_4_V2 = { 8, 15 };
	public final static int[] HEAD_5_V2 = { 9, 13 };

	@Override
	public int getOMHead(int i) {
		switch (i) {
		case 0:
			return 22;
		case 1:
			return 21;
		case 2:
			return 20;
		case 3:
			return 18;
		case 4:
			return 16;
		case 5:
			return 14;
		case 6:
			return 19;
		case 7:
			return 17;
		case 8:
			return 15;
		case 9:
			return 13;
		default:
			return 22;
		}

	}

}
