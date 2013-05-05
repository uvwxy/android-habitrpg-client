package de.uvwxy.habitrpg;

import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import de.uvwxy.habitrpg.ExpandableTask.TaskType;
import de.uvwxy.habitrpg.api.HabitColors;
import de.uvwxy.habitrpg.api.HabitConnectionV1;
import de.uvwxy.habitrpg.api.HabitConnectionV1.ServerResultCallback;
import de.uvwxy.habitrpg.api.HabitDataV1;
import de.uvwxy.habitrpg.api.SpriteFactoryChar;

public class ActivityMain extends Activity {
	private Context ctx = this;

	private static final int REQUEST_HABIT_CONFIG = 2;

	private RelativeLayout rlMain = null;
	private TextView tvName = null;
	private TextView tvGP = null;

	private TextView tvHP = null;
	private TextView tvXP = null;
	private TextView tvHPString = null;
	private TextView tvXPString = null;
	//	private ImageView ivChar = null;

	public static PaintBoxChar pbChar = null;

	private MenuItem mnuRefresh;
	private MenuItem mnuReportIssue;
	private MenuItem mnuOpenHRPG;
	private MenuItem mnuConfig;
	private MenuItem mnuAbout;

	private ExpandableTask habits = new ExpandableTask("idHabits");
	private ExpandableTask dailies = new ExpandableTask("idDailies");
	private ExpandableTask todos = new ExpandableTask("idTodos");
	private ExpandableTask rewards = new ExpandableTask("idRewards");

	private ExpandableListView elvTasks = null;
	private ExpandableTaskViewAdapter etva = null;

	private ProgressDialog wd;

	private HabitSettings habitSet = null;
	private HabitConnectionV1 habitCon = null;
	private HabitDataV1 habitData = null;
	private Runnable backgroundWorker = new Runnable() {

		@Override
		public void run() {
			pullDataFromHabit();
		}
	};
	private Thread backGroundThread;

	private ArrayList<ExpandableTask> tasksList = new ArrayList<ExpandableTask>();

	private void initGUI() {
		rlMain = (RelativeLayout) findViewById(R.id.rlMain);

		tvName = (TextView) findViewById(R.id.tvName);
		// tvGP = (TextView) findViewById(R.id.tvGP);
		tvHP = (TextView) findViewById(R.id.tvHP);
		tvXP = (TextView) findViewById(R.id.tvXP);
		tvHPString = (TextView) findViewById(R.id.tvHPString);
		tvXPString = (TextView) findViewById(R.id.tvXPString);

		//		ivChar = (ImageView) findViewById(R.id.ivChar);
		elvTasks = (ExpandableListView) findViewById(R.id.elvTasks);

		rlMain.setBackgroundColor(HabitColors.colorBackground);
		tvHP.setBackgroundColor(HabitColors.colorHP);
		tvXP.setBackgroundColor(HabitColors.colorXP);

		etva = new ExpandableTaskViewAdapter(ctx, tasksList, habitCon, habitData, habitResultCallback);
		elvTasks.setAdapter(etva);

		pbChar = (PaintBoxChar) findViewById(R.id.pbChar);
	}

	public ServerResultCallback habitResultCallback = new ServerResultCallback() {

		@Override
		public void serverReply(String s, String taskID, boolean upOrCompleted) {
			pbChar.subtractHabitWorkingThreadCount();
			try {
				JSONObject o = new JSONObject(s);
				double oldExp = habitData.getXP();
				double oldGp = habitData.getGP();
				double oldHp = habitData.getHP();
				double oldLvl = habitData.getLevel();

				// TODO: display diff
				habitData.applyServerResultToData(o, taskID, upOrCompleted);
				refreshExpandableTasks(habitData);
				updateTasksList();

				updateStats(o.getDouble("exp"), o.getDouble("gp"), o.getDouble("hp"), o.getDouble("lvl"), o.getDouble("delta"));
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	};

	private void initGuiFromHabit(final HabitConnectionV1 habitCon) {

		habits.setTitle("Habits");
		dailies.setTitle("Dailies");
		todos.setTitle("Todos");
		rewards.setTitle("Rewards");

		habits.setType(TaskType.HABIT);
		dailies.setType(TaskType.DAILY);
		todos.setType(TaskType.TODO);
		rewards.setType(TaskType.REWARD);

		updateStats(habitData.getXP(), habitData.getGP(), habitData.getHP(), habitData.getLevel(), 0);

		refreshExpandableTasks(habitData);

		pbChar.setHabitData(habitData);
		pbChar.setNewMode();
		pbChar.setTransparentTop();

		tasksList.clear();
		tasksList.add(habits);
		tasksList.add(dailies);
		tasksList.add(todos);
		tasksList.add(rewards);

		ExpandableTask dummy = new ExpandableTask("idDummy");
		dummy.setTitle("dummy");
		tasksList.add(dummy);

		updateTasksList();

		updateWidget();
	}

	private void refreshExpandableTasks(HabitDataV1 habitData) {
		habits.setList(habitData.getHabits(), TaskType.HABIT);
		dailies.setList(habitData.getDailies(), TaskType.DAILY);
		todos.setList(habitData.getTodos(), TaskType.TODO);
		rewards.setList(habitData.getRewards(), TaskType.REWARD);

	}

	private void updateWidget() {
		if (habitCon == null || habitData == null || habitData.rootIsNull()) {
			return;
		}

		RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.widget_habit_icon);
		ComponentName thisWidget = new ComponentName(getApplicationContext(), WidgetHabitIcon.class);

		Bitmap bitmap = SpriteFactoryChar.createChar(getApplicationContext(), habitData);
		remoteViews.setImageViewBitmap(R.id.ivWidgetIcon, SpriteFactoryChar.addColorHPXPBars(bitmap, habitData));
		AppWidgetManager.getInstance(getApplicationContext()).updateAppWidget(thisWidget, remoteViews);
	}

	private void pullDataFromHabit() {
		// do this on a thread
		if (habitCon.isUp()) {
			showWD();
			if (habitCon.loadRemoteData(habitData)) {
				wd.setProgress(1);

				initGuiFromHabit(habitCon);
				wd.setProgress(2);

				habitData.storeLocalData(ctx);
			} else {

				updateUi(tvHPString, "Could not load user data, setup correct?");
				updateUi(tvXPString, "(Menu -> \"Change Config\")");
			}

			dismissWD();
		} else {
			updateUi(tvHPString, "Could not find server");
			updateUiToast("Please take a look at the server settings");
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		habitSet = new HabitSettings(this);
		habitCon = new HabitConnectionV1();
		habitData = new HabitDataV1();

		if (habitSet.isSet()) {
			habitCon.setConfig(habitSet.getURL(), habitSet.getUserToken(), habitSet.getApiToken());
		}

		initGUI();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}

		switch (requestCode) {
		case REQUEST_HABIT_CONFIG:
			if (data != null) {
				habitSet.saveConfig(data.getStringExtra(ActivityAPISetup.SERVER_URL), data.getStringExtra(ActivityAPISetup.USER_TOKEN),
						data.getStringExtra(ActivityAPISetup.API_TOKEN));
			}
		default:
		}
	}

	@Override
	protected void onResume() {
		if (habitSet.isSet()) {
			if (habitData.loadLocalData(ctx)) {
				initGuiFromHabit(habitCon);
				tvHP.post(new Runnable() {

					@Override
					public void run() {
						if (habitCon == null) {
							return;
						}
						updateUi(tvHP, habitData.getMaxHealth(), habitData.getHP());
						updateUi(tvXP, habitData.getToNextLevel(), habitData.getXP());
					}
				});

				elvTasks.post(new Runnable() {

					@Override
					public void run() {
						elvTasks.setIndicatorBounds(0, 0);
					}
				});

				elvTasks.setSelector(android.R.color.transparent);

				updateUiCharIcon();
				Toast.makeText(ctx, "Loaded offline data, refresh to update", Toast.LENGTH_SHORT).show();
			} else {
				startBackgroundPullDataThread();
			}
		} else {
			updateUi(tvHPString, "Please setup your HabitRPG.");
			updateUi(tvXPString, "(Menu -> \"Change Config\")");
		}

		super.onResume();
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (mnuRefresh.equals(item)) {
			startBackgroundPullDataThread();
		}
		if (mnuReportIssue.equals(item)) {
			openUrl("https://bitbucket.org/uvwxy/android-habitrpg-client/issues?status=new&status=open");
		}
		if (mnuOpenHRPG.equals(item)) {
			openUrl("https://habitrpg.com");

		}
		if (mnuConfig.equals(item)) {
			Intent intent = new Intent(this, ActivityAPISetup.class);
			startActivityForResult(intent, REQUEST_HABIT_CONFIG);
		}
		if (mnuAbout.equals(item)) {
			Intent intent = new Intent(this, ActivityAbout.class);
			startActivity(intent);
		}

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		mnuRefresh = menu.findItem(R.id.itemRefresh);
		mnuReportIssue = menu.findItem(R.id.itemReportIssue);
		mnuOpenHRPG = menu.findItem(R.id.itemOpenHRPG);
		mnuConfig = menu.findItem(R.id.itemSettings);
		mnuAbout = menu.findItem(R.id.itemAbout);
		return super.onPrepareOptionsMenu(menu);
	}

	private void startBackgroundPullDataThread() {
		habitCon.setConfig(habitSet.getURL(), habitSet.getUserToken(), habitSet.getApiToken());
		killBackgroundThread();
		startBackgroundThread();
	}

	private void startBackgroundThread() {
		backGroundThread = new Thread(backgroundWorker);
		backGroundThread.start();
	}

	private void killBackgroundThread() {
		if (backGroundThread != null && backGroundThread.isAlive()) {
			backGroundThread.interrupt();
		}
	}

	@Override
	protected void onPause() {
		if (habitData != null) {
			habitData.storeLocalData(getApplicationContext());
			updateWidget();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		killBackgroundThread();
		super.onDestroy();
	}

	private void showWD() {
		Runnable uiThread = new Runnable() {
			@Override
			public void run() {
				wd = ProgressDialog.show(ctx, "Downloading data", "Please wait...", true);
				wd.setMax(2);
				wd.setCancelable(false);
				wd.setProgress(0);
				wd.setTitle("Connecting to " + habitSet.getURL());
				wd.setMessage("Downloading data");
				wd.show();
			}
		};

		this.runOnUiThread(uiThread);

	}

	private void setWDProgress(final String msg, final int progress) {
		Runnable uiThread = new Runnable() {
			@Override
			public void run() {
				wd.setMessage(msg);
				wd.setProgress(progress);
			}
		};

		this.runOnUiThread(uiThread);
	}

	private void dismissWD() {
		Runnable uiThread = new Runnable() {
			@Override
			public void run() {
				wd.dismiss();
			}
		};

		this.runOnUiThread(uiThread);
	}

	private void updateTasksList() {
		Runnable uiThread = new Runnable() {
			@Override
			public void run() {
				etva.notifyDataSetChanged();
				int count = etva.getGroupCount();
				for (int i = 0; i < count; i++) {
					elvTasks.expandGroup(i);
				}

			}
		};

		this.runOnUiThread(uiThread);

	}

	private void updateUi(final TextView tvBar, final double max, final double value) {
		Runnable uiThread = new Runnable() {
			@Override
			public void run() {
				tvBar.setWidth((int) (tvHPString.getWidth() * (value / max)));
			}
		};

		this.runOnUiThread(uiThread);
	}

	private void updateUi(final TextView tv, final String s) {
		Runnable uiThread = new Runnable() {
			@Override
			public void run() {
				tv.setText(s);
			}
		};

		this.runOnUiThread(uiThread);
	}

	private void updateUiToast(final String s) {
		Runnable uiThread = new Runnable() {
			@Override
			public void run() {
				Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
			}
		};

		this.runOnUiThread(uiThread);
	}

	private void updateUiCharIcon() {
		Runnable uiThread = new Runnable() {

			@Override
			public void run() {

				//				Bitmap b;
				//				if (habitCon != null) {
				//					b = SpriteFactoryChar.createChar(ctx, habitData);
				//				} else {
				//					b = SpriteFactoryChar.createDefaultMaleChar(ctx);
				//				}
				//				ivChar.setImageBitmap(b);
				pbChar.setHabitData(habitData);
			}
		};
		this.runOnUiThread(uiThread);
	}

	private void updateStats(double exp, double gp, double hp, double lvl, double delta) {
		updateUi(tvName, habitData.getUserName());
		String format = String.format("" + getText(R.string._gold) + "%.2f", gp);

		// TODO: update this in review Section...
		// updateUi(tvGP, format);

		updateUi(tvHPString, String.format(Locale.US, "%d/%d", (int) hp, (int) habitData.getMaxHealth()));
		updateUi(tvXPString, String.format(Locale.US, "%d/%d", (int) exp, (int) habitData.getToNextLevel()));
		updateUiCharIcon();

		updateUi(tvHP, habitData.getMaxHealth(), hp);
		updateUi(tvXP, habitData.getToNextLevel(), exp);
	}

	private void openUrl(String url) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
	}
}
