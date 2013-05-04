package de.uvwxy.habitrpg.api;

import android.util.Log;

public class SpriteConverterMale implements ISpriteConverter {

	private final int OFFSET_WEAPON = 6;
	private final int OFFSET_ARMOR = 27;
	private final int OFFSET_SHIELD = 11;
	private final int OFFSET_HAIR = 21;
	private final int OFFSET_SKIN = 32;
	private final int OFFSET_HEAD = 17;

	public int getOMHair(String s) {

		if (s.equals("blond")) {
			return OFFSET_HAIR - 3;
		} else if (s.equals("black")) {
			return OFFSET_HAIR - 2;
		} else if (s.equals("brown")) {
			return OFFSET_HAIR - 1;
		} else if (s.equals("white")) {
			return OFFSET_HAIR;
		}

		// default: "blond"
		return OFFSET_HAIR - 3;
	}

	@Override
	public int getOMSkin(String s) {
		Log.i("CVR", "SKIN: " + s);

		if (s.equals("dead")) {
			return OFFSET_SKIN - 4;
		} else if (s.equals("orc")) {
			return OFFSET_SKIN - 3;
		} else if (s.equals("asian")) {
			return OFFSET_SKIN - 2;
		} else if (s.equals("black")) {
			return OFFSET_SKIN - 1;
		} else if (s.equals("white")) {
			return OFFSET_SKIN;
		}

		// default: "white"
		return OFFSET_SKIN;
	}

	@Override
	public int getOMArmor(int i) {
		switch (i) {
		case 0:
			return OFFSET_ARMOR;
		case 1:
			return OFFSET_ARMOR - 1;
		case 2:
			return OFFSET_ARMOR - 2;
		case 3:
			return OFFSET_ARMOR - 3;
		case 4:
			return OFFSET_ARMOR - 4;
		case 5:
			return OFFSET_ARMOR - 5;
		default:
			return OFFSET_ARMOR;
		}
	}

	@Override
	public int getOMWeapon(int i) {
		switch (i) {
		case 0:
			return OFFSET_WEAPON;
		case 1:
			return OFFSET_WEAPON - 1;
		case 2:
			return OFFSET_WEAPON - 2;
		case 3:
			return OFFSET_WEAPON - 3;
		case 4:
			return OFFSET_WEAPON - 4;
		case 5:
			return OFFSET_WEAPON - 5;
		case 6:
			return OFFSET_WEAPON - 6;
		default:
			return OFFSET_WEAPON;
		}
	}

	@Override
	public int getOMShield(int i) {
		switch (i) {
		case 0:
			return -1;
		case 1:
			return OFFSET_SHIELD;
		case 2:
			return OFFSET_SHIELD - 1;
		case 3:
			return OFFSET_SHIELD - 2;
		case 4:
			return OFFSET_SHIELD - 3;
		case 5:
			return OFFSET_SHIELD - 4;
		default:
			return -1;
		}
	}

	@Override
	public int getOMHead(int i) {
		switch (i) {
		case 0:
			return OFFSET_HEAD;
		case 1:
			return OFFSET_HEAD - 1;
		case 2:
			return OFFSET_HEAD - 2;
		case 3:
			return OFFSET_HEAD - 3;
		case 4:
			return OFFSET_HEAD - 4;
		case 5:
			return OFFSET_HEAD - 5;
		default:
			return OFFSET_HEAD;
		}
	}

}
