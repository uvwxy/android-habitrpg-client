package de.uvwxy.habitrpg;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import de.uvwxy.habitrpg.R;
import de.uvwxy.habitrpg.ExpandableTask.TaskType;
import de.uvwxy.habitrpg.HabitSettings.OnSettingsSave;
import de.uvwxy.habitrpg.api.HabitConnectionV1;
import de.uvwxy.habitrpg.api.HabitConnectionV1.ServerResultCallback;
import de.uvwxy.habitrpg.sprites.HabitColors;

public class ActivityMain extends Activity {
	private Context ctx = this;

	private RelativeLayout rlMain = null;
	private TextView tvName = null;
	private ProgressBar pbHP = null;
	private ProgressBar pbXP = null;
	private TextView tvGP = null;
	private ExpandableListView elvTasks = null;
	private ExpandableTaskViewAdapter etva = null;

	private HabitSettings habitSet = null;
	private HabitConnectionV1 habitCon = null;
	private ImageView ivChar = null;

	private ArrayList<ExpandableTask> tasksList = new ArrayList<ExpandableTask>();

	private void initGUI() {
		rlMain = (RelativeLayout) findViewById(R.id.rlMain);
		rlMain.setBackgroundColor(HabitColors.colorBackground);
		tvName = (TextView) findViewById(R.id.tvName);
		pbHP = (ProgressBar) findViewById(R.id.pbHP);
		pbHP.setBackgroundColor(HabitColors.colorHP);
		pbXP = (ProgressBar) findViewById(R.id.pbXP);
		pbXP.setBackgroundColor(HabitColors.colorXP);
		tvGP = (TextView) findViewById(R.id.tvGP);
		elvTasks = (ExpandableListView) findViewById(R.id.elvTasks);
		ivChar = (ImageView) findViewById(R.id.ivChar);
		etva = new ExpandableTaskViewAdapter(ctx, tasksList, habitCon, habitResultCallback);
		elvTasks.setAdapter(etva);
	}

	public ServerResultCallback habitResultCallback = new ServerResultCallback() {

		@Override
		public void serverReply(String s) {
			try {
				JSONObject o = new JSONObject(s);

				double oldExp = habitCon.getExp();
				double oldGp = habitCon.getGP();

				// {"exp":147,"gp":39.605614452667595,"hp":50,"lvl":2,"delta":0.9506562257358427}
				updateStats(o.getDouble("exp"), o.getDouble("gp"), o.getDouble("hp"), o.getDouble("lvl"), o.getDouble("delta"));
				//double diffExp = o.getDouble("exp") - oldExp;
				//double diffGp = o.getDouble("gp") - oldGp;
				//String msg = String.format("EXP: " + (diffExp < 0 ? "+" : "") + " %.2f\nGP: " + (diffGp < 0 ? "+" : "") + "%.2f", diffExp, diffGp);
				//updateUiToast(msg);

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	};

	public OnSettingsSave settingsCallback = new OnSettingsSave() {

		@Override
		public void onSettingsSave() {
			updateHabit();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		habitSet = new HabitSettings(this);
		habitCon = new HabitConnectionV1();

		if (!habitSet.isSet()) {
			habitSet.show(null);
		} else {
			String[] set = habitSet.readSettings();
			habitCon.setConfig(set[0], set[1], set[2]);
		}

		initGUI();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onResume() {
		if (!habitSet.isSet()) {
			habitSet.show(settingsCallback);
		} else {
			updateHabit();
		}

		super.onResume();
	}

	private void updateHabit() {
		String[] set = habitSet.readSettings();
		habitCon.setConfig(set[0], set[1], set[2]);
		Thread t = new Thread(backgroundWorker);
		t.start();
	}

	private Runnable backgroundWorker = new Runnable() {

		@Override
		public void run() {
			if (habitCon.isUp()) {
				showWD();
				if (habitCon.fetchData()) {
					wd.setProgress(1);

					updateStats(habitCon.getExp(), habitCon.getGP(), habitCon.getHp(), habitCon.getLevel(), 0);

					ExpandableTask habits = new ExpandableTask();
					ExpandableTask dailies = new ExpandableTask();
					ExpandableTask todos = new ExpandableTask();
					ExpandableTask rewards = new ExpandableTask();

					habits.setTitle("Habits");
					dailies.setTitle("Dailies");
					todos.setTitle("Todos");
					rewards.setTitle("Rewards");

					habits.setType(TaskType.HABIT);
					dailies.setType(TaskType.DAILY);
					todos.setType(TaskType.TODO);
					rewards.setType(TaskType.REWARD);

					habits.setList(habitCon.getHabits(), TaskType.HABIT);
					setWDProgress("Downloaded habits..", 2);

					dailies.setList(habitCon.getDailies(), TaskType.DAILY);
					setWDProgress("Downloaded dailies..", 3);

					todos.setList(habitCon.getTodos(), TaskType.TODO);
					setWDProgress("Downloaded todos..", 4);

					rewards.setList(habitCon.getRewards(), TaskType.REWARD);
					setWDProgress("Downloaded rewards..", 5);

					tasksList.clear();
					tasksList.add(habits);
					tasksList.add(dailies);
					tasksList.add(todos);
					tasksList.add(rewards);

					updateTasksList();
					setWDProgress("Updated UI..", 6);

				} else {

					updateUi(tvName, getText(R.string.name) + "Could not load user data");
				}

				dismissWD();
			} else {
				updateUi(tvName, getText(R.string.name) + "Could not find server");
				updateUiToast("Please take a look at the server settings");
			}

		}
	};

	ProgressDialog wd;

	private void showWD() {
		Runnable uiThread = new Runnable() {
			@Override
			public void run() {
				wd = ProgressDialog.show(ctx, "Downloading data", "Please wait...", true);
				wd.setMax(6);
				wd.setCancelable(false);
				wd.setProgress(0);
				wd.setTitle("Fetching data");
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

	private void updateUi(final ProgressBar pb, final double max, final double value) {
		Runnable uiThread = new Runnable() {
			@Override
			public void run() {
				pb.setMax((int) max);
				pb.setProgress((int) value);
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

	private void updateStats(double exp, double gp, double hp, double lvl, double delta) {
		updateUi(tvName, getText(R.string.name) + habitCon.getUserName() + "(" + ((int) lvl) + ")");
		String format = String.format("" + getText(R.string._gold) + "%.2f", gp);

		updateUi(tvGP, format);
		updateUi(pbHP, habitCon.getMaxHealth(), (int) hp);
		updateUi(pbXP, habitCon.getToNextLevel(), (int) exp);

	}

	public void onMenuSettings(MenuItem m) {
		habitSet.show(settingsCallback);
	}

	public void onMenuRefresh(MenuItem m) {
		updateHabit();
	}
}
