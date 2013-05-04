package de.uvwxy.habitrpg;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;
import de.uvwxy.habitrpg.api.HabitConnectionV1;
import de.uvwxy.habitrpg.api.SpriteFactoryChar;

public class ActivityWidgetAction extends Activity {
	private HabitConnectionV1 habitCon;
	private int mAppWidgetId;

	private RemoteViews views;
	private AppWidgetManager appWidgetManager;

	private Button btnRefreshIcon;

	private OnClickListener updateIconListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			updateIcon();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_widget_action);
		btnRefreshIcon = (Button) findViewById(R.id.btnWidgetActionRefresh);
		btnRefreshIcon.setOnClickListener(updateIconListener);

		habitCon = new HabitConnectionV1();

		Intent intent = getIntent();
		mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		Log.i("HABITWIDGET", "Widet id: "+ mAppWidgetId);
		if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
			appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
			views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.widget_habit_icon);
		}
	}

	private void updateIcon() {

		if (views != null && habitCon.loadLocalData(getApplicationContext())) {
			Bitmap bmp = SpriteFactoryChar.createChar(getApplicationContext(), habitCon, habitCon.isMale());
			views.setImageViewBitmap(R.id.ivWidgetIcon, bmp);
			appWidgetManager.updateAppWidget(mAppWidgetId, views);
			Toast.makeText(getApplicationContext(), "Icon refreshed", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(getApplicationContext(), "There was a problem loading local data", Toast.LENGTH_LONG).show();
		}
	}
}
