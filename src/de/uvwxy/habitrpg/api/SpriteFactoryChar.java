package de.uvwxy.habitrpg.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import de.uvwxy.habitrpg.R;

public class SpriteFactoryChar {

	private static SpriteConverterMale scm = new SpriteConverterMale();
	private static SpriteConverterFemale scf = new SpriteConverterFemale();

	private static Bitmap maleSprites = null;
	private static Bitmap femaleSprites = null;

	private static void checkAndLoad(Context ctx) {
		Options o = new Options();
		o.inScaled = false;

		if (maleSprites == null) {
			maleSprites = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.male_sprites, o);
		}

		if (femaleSprites == null) {
			femaleSprites = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.female_sprites, o);
		}
	}

	public static Bitmap createDefaultMaleChar(Context ctx) {
		checkAndLoad(ctx);

		int w = maleSprites.getHeight();

		Bitmap bm = Bitmap.createBitmap(w, w, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bm);

		drawSprite(maleSprites, scm.getOMSkin("white") * w, w, canvas);
		drawSprite(maleSprites, scm.getOMHair("blond") * w, w, canvas);
		drawSprite(maleSprites, scm.getOMArmor(1) * w, w, canvas);
		drawSprite(maleSprites, scm.getOMShield(1) * w, w, canvas);
		drawSprite(maleSprites, scm.getOMHead(1) * w, w, canvas);
		drawSprite(maleSprites, scm.getOMWeapon(1) * w, w, canvas);

		return bm;
	}

	public static Bitmap createDefaultFemaleChar(Context ctx) {
		checkAndLoad(ctx);

		int w = maleSprites.getHeight();

		Bitmap bm = Bitmap.createBitmap(w, w, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bm);

		drawSprite(femaleSprites, scf.getOMSkin("white") * w, w, canvas);
		drawSprite(femaleSprites, scf.getOMHair("blond") * w, w, canvas);
		drawSprite(femaleSprites, scf.getOMArmor(1) * w, w, canvas);
		drawSprite(femaleSprites, scf.getOMShield(1) * w, w, canvas);
		drawSprite(femaleSprites, scf.getOMHead(1) * w, w, canvas);
		drawSprite(femaleSprites, scf.getOMWeapon(1) * w, w, canvas);

		return bm;
	}

	public static Bitmap createChar(Context ctx, HabitDataV1 habitcon) {
		checkAndLoad(ctx);

		int w = maleSprites.getHeight();

		Bitmap bm = Bitmap.createBitmap(w, w, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bm);

		renderChar(habitcon, w, canvas);

		return bm;
	}

	public static void renderChar(HabitDataV1 habitcon, int w, Canvas canvas) {
		renderCharWithoutWeapon(habitcon, w, canvas);

		boolean male = habitcon.isMale();
		ISpriteConverter isc = male ? scm : scf;
		Bitmap sprites = male ? maleSprites : femaleSprites;
		renderWeapon(isc.getOMWeapon(habitcon.getWeapon()), w, canvas, isc, sprites);
	}

	public static void renderCharWithoutWeapon(HabitDataV1 habitcon, int w, Canvas canvas) {
		boolean male = habitcon.isMale();
		ISpriteConverter isc = male ? scm : scf;
		Bitmap sprites = male ? maleSprites : femaleSprites;

		renderSkin(isc.getOMSkin(habitcon.getSkin()), w, canvas, isc, sprites);
		renderHair(isc.getOMHair(habitcon.getHair()), w, canvas, isc, sprites);

		renderArmor(habitcon.getArmorSet().equals("v1"), habitcon.isMale(), isc.getOMArmor(habitcon.getArmor()), w, canvas, isc, sprites);
		renderShield(isc.getOMShield(habitcon.getShield()), w, canvas, isc, sprites);
		renderHead(habitcon.getArmorSet().equals("v1"), habitcon.showHelm(), habitcon.isMale(), habitcon.getHead(), w, canvas, isc, sprites);
		Paint paint = new Paint();
		int lvl = 0;
		try {
			lvl = (int) habitcon.getLevel();
		} catch (Exception e) {

		}
		canvas.drawText("lvl: " + lvl, 0, canvas.getHeight(), paint);
	}

	public static void renderWeaponOnly(HabitDataV1 habitcon, int w, Canvas canvas, float degrees, int cx, int cy) {
		boolean male = habitcon.isMale();
		ISpriteConverter isc = male ? scm : scf;
		Bitmap sprites = male ? maleSprites : femaleSprites;
		canvas.save();
		canvas.rotate(degrees, cx, cy);
		drawSprite(sprites, isc.getOMWeapon(habitcon.getWeapon()) * w, w, canvas);
		canvas.restore();
	}

	public static void renderSkin(int omSkin, int w, Canvas canvas, ISpriteConverter isc, Bitmap sprites) {
		drawSprite(sprites, omSkin * w, w, canvas);
	}

	public static void renderHair(int omHair, int w, Canvas canvas, ISpriteConverter isc, Bitmap sprites) {
		drawSprite(sprites, omHair * w, w, canvas);
	}

	public static void renderArmor(boolean armorSetV1, boolean isMale, int armorID, int w, Canvas canvas, ISpriteConverter isc, Bitmap sprites) {
		if (!isMale && armorID < 1 && armorSetV1) {
			armorID++;
		}
		drawSprite(sprites, armorID * w, w, canvas);
	}

	public static void renderShield(int omShield, int w, Canvas canvas, ISpriteConverter isc, Bitmap sprites) {
		drawSprite(sprites, omShield * w, w, canvas);
	}

	public static void renderHead(boolean armorSetV1, boolean showHelm, boolean isMale, int headID, int w, Canvas canvas, ISpriteConverter isc, Bitmap sprites) {
		if (showHelm) {
			if (isMale) {
				drawSprite(sprites, isc.getOMHead(headID) * w, w, canvas);
			} else if (headID <= 1 || armorSetV1) {
				drawSprite(sprites, isc.getOMHead(headID) * w, w, canvas);
			} else {
				drawSprite(sprites, (isc.getOMHead(headID) - 1) * w, w, canvas);
			}
		} else {
			drawSprite(sprites, isc.getOMHead(0) * w, w, canvas);
		}
	}

	public static void renderWeapon(int omWeapon, int w, Canvas canvas, ISpriteConverter isc, Bitmap sprites) {
		drawSprite(sprites, omWeapon * w, w, canvas);
	}

	public static void drawSprite(Bitmap bSrc, int offset, int width, Canvas c) {
		if (offset < 0) {
			// dont draw "unset" weapons
			return;
		}
		Rect src = new Rect(offset, 0, offset + width, c.getHeight());
		RectF dst = new RectF(0, 0, c.getWidth(), c.getHeight());
		Paint paint = new Paint();
		paint.setFilterBitmap(false);
		c.drawBitmap(bSrc, src, dst, paint);
	}

	public static Bitmap addColorHPXPBars(Bitmap bmp, HabitDataV1 habitCon) {
		if (bmp == null) {
			throw new RuntimeException("bmp can not be null");
		}
		if (habitCon == null) {
			return bmp;
		}

		int w = bmp.getWidth(), h = bmp.getHeight();
		int barHeight = 6;

		Bitmap bm = Bitmap.createBitmap(w, h + barHeight * 2, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bm);

		Paint paint = new Paint();
		paint.setFilterBitmap(false);
		
		c.drawBitmap(bmp, new Matrix(), paint);
		paint.setColor(HabitColors.colorHP);
		c.drawRect(0, c.getHeight() - barHeight * 2, (float) (c.getWidth() * habitCon.getHP() / habitCon.getMaxHealth()), c.getHeight() - (barHeight + 1),
				paint);
		paint.setColor(HabitColors.colorXP);
		c.drawRect(0, c.getHeight() - barHeight, (float) (c.getWidth() * habitCon.getXP() / habitCon.getToNextLevel()), c.getHeight() - 1, paint);
		return bm;
	}
}
