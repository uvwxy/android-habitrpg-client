package de.uvwxy.habitrpg.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
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

	public static Bitmap createChar(Context ctx, HabitConnectionV1 habitcon, boolean male) {
		checkAndLoad(ctx);

		int w = maleSprites.getHeight();

		Bitmap bm = Bitmap.createBitmap(w, w, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bm);

		ISpriteConverter isc = male ? scm : scf;

		drawSprite(maleSprites, isc.getOMSkin(habitcon.getSkin()) * w, w, canvas);
		drawSprite(maleSprites, isc.getOMHair(habitcon.getHair()) * w, w, canvas);
		drawSprite(maleSprites, isc.getOMArmor(habitcon.getArmor()) * w, w, canvas);
		drawSprite(maleSprites, isc.getOMShield(habitcon.getShield()) * w, w, canvas);
		drawSprite(maleSprites, isc.getOMHead(habitcon.getHead()) * w, w, canvas);
		drawSprite(maleSprites, isc.getOMWeapon(habitcon.getWeapon()) * w, w, canvas);

		return bm;
	}

	public static void drawSprite(Bitmap bSrc, int offset, int width, Canvas c) {
		Rect src = new Rect(offset - width, 0, offset, c.getHeight());
		RectF dst = new RectF(0, 0, c.getWidth(), c.getHeight());
		Paint paint = new Paint();
		c.drawBitmap(bSrc, src, dst, paint);
	}
}
