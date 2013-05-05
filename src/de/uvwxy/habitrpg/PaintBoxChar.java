package de.uvwxy.habitrpg;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import de.uvwxy.habitrpg.api.HabitDataV1;
import de.uvwxy.habitrpg.api.SpriteFactoryChar;
import de.uvwxy.paintbox.PaintBox;

public class PaintBoxChar extends PaintBox {

	private int numWorkingThreads = 0;
	private HabitDataV1 habitData;

	Bitmap defaultChar = null;

	private Paint paint = new Paint();
	private long degrees;

	private long animSpeed = 500;
	private long lastCheck = System.currentTimeMillis();
	private long counter = System.currentTimeMillis();

	public PaintBoxChar(Context context) {
		super(context);
		defaultChar = SpriteFactoryChar.createDefaultMaleChar(getContext());
	}

	public PaintBoxChar(Context context, AttributeSet attrs) {
		super(context, attrs);
		defaultChar = SpriteFactoryChar.createDefaultMaleChar(getContext());
	}

	public PaintBoxChar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		defaultChar = SpriteFactoryChar.createDefaultMaleChar(getContext());
	}

	@Override
	protected void onDraw(Canvas canvas) {

		if (habitData == null) {
			canvas.drawBitmap(defaultChar, new Matrix(), paint);
			canvas.drawText("Data is null", 0, getHeight() / 2, paint);
			return;
		}

		int origWidth = defaultChar.getWidth();
		int origHeight = defaultChar.getHeight();

		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

		SpriteFactoryChar.renderCharWithoutWeapon(habitData, origWidth, canvas);
		int cx = origWidth / 2 + origWidth / 6;
		int cy = origHeight / 2 + origHeight / 8;

		if (numWorkingThreads > 0) {

			counter += (System.currentTimeMillis() - lastCheck) * numWorkingThreads;
			degrees = counter;
			
			degrees %= animSpeed;

			if (degrees < animSpeed / 2) {
				degrees = -animSpeed / 4 + degrees;
			} else {
				degrees = animSpeed / 4 - (degrees - animSpeed / 2);
			}

			degrees /= 4;

		} else {
			degrees = 0;
		}

		lastCheck = System.currentTimeMillis();
		SpriteFactoryChar.renderWeaponOnly(habitData, origWidth, canvas, degrees, cx, cy);
	}

	public void setHabitData(HabitDataV1 habitData) {
		this.habitData = habitData;
	}

	public synchronized void addHabitWorkingThreadCount() {
		numWorkingThreads++;
	}

	public synchronized void subtractHabitWorkingThreadCount() {
		numWorkingThreads--;
	}

}
