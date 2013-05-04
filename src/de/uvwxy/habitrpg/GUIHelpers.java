package de.uvwxy.habitrpg;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class GUIHelpers {
	public static OnClickListener mkToastListener(final Context ctx, final String txt) {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(ctx, txt, Toast.LENGTH_LONG).show();
			}
		};
	}
	
	 
    public static int getDipsFromPixel(Context ctx, float pixels)
    {
     // Get the screen's density scale
     final float scale = ctx.getResources().getDisplayMetrics().density;
     // Convert the dps to pixels, based on density scale
     return (int) (pixels * scale + 0.5f);
    }
}
