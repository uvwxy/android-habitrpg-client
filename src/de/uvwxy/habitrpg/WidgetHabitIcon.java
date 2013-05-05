package de.uvwxy.habitrpg;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;
import de.uvwxy.habitrpg.api.HabitColors;
import de.uvwxy.habitrpg.api.HabitConnectionV1;
import de.uvwxy.habitrpg.api.SpriteFactoryChar;

public class WidgetHabitIcon extends AppWidgetProvider {

	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		final int N = appWidgetIds.length;

		// Perform this loop procedure for each App Widget that belongs to this provider
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];

			// Create an Intent to launch ExampleActivity
			Intent intent = new Intent(context, ActivityMain.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

			// Get the layout for the App Widget and attach an on-click listener
			// to the button
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_habit_icon);
			views.setOnClickPendingIntent(R.id.ivWidgetIcon, pendingIntent);

			HabitConnectionV1 habitCon = new HabitConnectionV1();

			if (views != null && habitCon.loadLocalData(context)) {
				Log.i("HABITWIDGET", "ID:" + appWidgetId);

				Bitmap bmp = SpriteFactoryChar.createChar(context, habitCon);
				views.setImageViewBitmap(R.id.ivWidgetIcon, SpriteFactoryChar.addColorHPXPBars(bmp, habitCon));
				appWidgetManager.updateAppWidget(appWidgetId, views);
				
				Toast.makeText(context, "Icon refreshed", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, "There was a problem loading local data", Toast.LENGTH_SHORT).show();
			}

			// Tell the AppWidgetManager to perform an update on the current app widget
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}

}
