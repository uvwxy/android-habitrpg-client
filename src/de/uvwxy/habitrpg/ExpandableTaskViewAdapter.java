package de.uvwxy.habitrpg;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import de.uvwxy.habitrpg.api.HabitColors;
import de.uvwxy.habitrpg.api.HabitConnectionV1;
import de.uvwxy.habitrpg.api.HabitConnectionV1.ServerResultCallback;

public class ExpandableTaskViewAdapter extends BaseExpandableListAdapter {
	private Context ctx;
	private ArrayList<ExpandableTask> listOfAllTasks = null;
	private LayoutInflater inf;
	private HabitConnectionV1 habitCon = null;

	private static final boolean BUY = true;
	private static final boolean UP = true;
	private static final boolean DOWN = false;
	private static final boolean COMPLETED = true;
	private static final boolean UNCOMPLETED = false;
	// this is never used
	private static final boolean ISCHECKBOX = true;

	private ServerResultCallback serverResultCallback = null;

	private void habitClick(View v, final String taskId, final boolean upOrCompleted) {

		if (v instanceof Button) {
			v.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					final ProgressDialog waitingDialog = ProgressDialog.show(ctx, "Communicating", "Please wait...", true);
					waitingDialog.setProgress(10);
					waitingDialog.show();

					Thread t = new Thread(new Runnable() {

						@Override
						public void run() {
							try {

								String result = habitCon.updateTask(taskId, upOrCompleted);
								serverResultCallback.serverReply(result);
								waitingDialog.dismiss();
							} catch (ClientProtocolException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					});
					t.start();
				}
			});
		}

		if (v instanceof CheckBox) {
			CheckBox cb = (CheckBox) v;
			cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

					boolean direction = false;
					if (isChecked) {
						direction = COMPLETED;
					} else {
						direction = UNCOMPLETED;
					}
					final boolean fDirection = direction;

					Thread t = new Thread(new Runnable() {

						@Override
						public void run() {
							try {
								String result = habitCon.updateTask(taskId, fDirection);
								serverResultCallback.serverReply(result);
							} catch (ClientProtocolException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					});
					t.start();
				}
			});
		}

	}

	public ExpandableTaskViewAdapter(Context ctx, ArrayList<ExpandableTask> list, HabitConnectionV1 habitCon, ServerResultCallback src) {
		if (ctx == null || list == null || habitCon == null || src == null) {
			throw new RuntimeException("Context or list or habitCon was null. uh oh..");
		}
		this.ctx = ctx;
		this.listOfAllTasks = list;
		this.habitCon = habitCon;
		this.serverResultCallback = src;
		this.inf = LayoutInflater.from(ctx);

	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		try {
			return listOfAllTasks.get(groupPosition).getList().getJSONObject(childPosition);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

		Object o = listOfAllTasks.get(groupPosition);
		if (!(o instanceof ExpandableTask)) {
			return null;
		}

		ExpandableTask e = ((ExpandableTask) o);

		switch (e.getType()) {
		case DAILY:
			return getDailyView(groupPosition, childPosition, isLastChild, convertView, parent);

		case HABIT:
			return getHabitView(groupPosition, childPosition, isLastChild, convertView, parent);

		case REWARD:
			return getRewardView(groupPosition, childPosition, isLastChild, convertView, parent);

		case TODO:
			return getTodoView(groupPosition, childPosition, isLastChild, convertView, parent);

		default:
			return null;

		}

	}

	private View getHabitView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

		convertView = inf.inflate(R.layout.expandable_habit, parent, false);

		final RelativeLayout rlHabitInner = (RelativeLayout) convertView.findViewById(R.id.rlHabitInner);
		final LinearLayout llHabitOuter = (LinearLayout) convertView.findViewById(R.id.llHabitOuter);
		final TextView tvHabit = (TextView) convertView.findViewById(R.id.tvHabit);
		final Button btnPlus = (Button) convertView.findViewById(R.id.btnPlus);
		final Button btnMinus = (Button) convertView.findViewById(R.id.btnMinus);

		ExpandableTask e = listOfAllTasks.get(groupPosition);

		if (e != null) {
			JSONArray list = e.getList();

			try {
				JSONObject h = list.getJSONObject(childPosition);
				
				int color = HabitColors.colorFromValue(h.getDouble("value"));
				int dColor = darkenColor(color);
				int lColor = lightenColor(color);

				if (!h.getBoolean("up")) {
					btnPlus.setVisibility(View.GONE);
				} else {
					habitClick(btnPlus, h.getString("id"), UP);
					btnPlus.setBackgroundColor(lColor);
				}

				if (!h.getBoolean("down")) {
					btnMinus.setVisibility(View.GONE);
				} else {
					habitClick(btnMinus, h.getString("id"), DOWN);
					btnMinus.setBackgroundColor(lColor);
				}

				tvHabit.setText(h.getString("text"));
				tvHabit.setBackgroundColor(color);
				rlHabitInner.setBackgroundColor(dColor);
				
				if (isLastChild) {
					// TODO: add switch between apis
					llHabitOuter.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.layout_boder_no_top));
				}
			} catch (JSONException e1) {
				tvHabit.setText("[Error]}\n" + e1.getMessage());
				tvHabit.setOnClickListener(GUIHelpers.mkToastListener(ctx, e1.getMessage()));
				e1.printStackTrace();
			}
		}

		return convertView;
	}

	private int darkenColor(int color) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[2] *= .8f; // value component
		color = Color.HSVToColor(hsv);
		return color;
	}

	private int lightenColor(int color) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[1] *= 1.2f; // value component
		color = Color.HSVToColor(hsv);
		return color;
	}

	private View getDailyView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

		convertView = inf.inflate(R.layout.expandable_daily, parent, false);

		final RelativeLayout rlDaily = (RelativeLayout) convertView.findViewById(R.id.rlDaily);
		final LinearLayout llDaily = (LinearLayout) convertView.findViewById(R.id.llDaily);
		final LinearLayout llDailyOuter = (LinearLayout) convertView.findViewById(R.id.llDailyOuter);
		final CheckBox cbDaily = (CheckBox) convertView.findViewById(R.id.cbDaily);
		final TextView tvDaily = (TextView) convertView.findViewById(R.id.tvDaily);
		final ExpandableTask e = listOfAllTasks.get(groupPosition);

		if (e != null && cbDaily != null) {
			JSONArray list = e.getList();

			try {
				JSONObject h = list.getJSONObject(childPosition);

				cbDaily.setChecked(h.getBoolean("completed"));

				//				if (cbDaily.isChecked()){
				//					// we can not uncheck things with the api?
				//					cbDaily.setEnabled(false);
				//				}

				tvDaily.setText(h.getString("text"));
				habitClick(cbDaily, h.getString("id"), ISCHECKBOX);
				int color = HabitColors.colorFromValue(h.getDouble("value"));
				int dColor = darkenColor(color);
				int lColor = lightenColor(color);
				tvDaily.setBackgroundColor(HabitColors.colorFromValue(h.getDouble("value")));
				cbDaily.setBackgroundColor(lColor);
				if (cbDaily.isChecked()) {
					tvDaily.setBackgroundColor(Color.LTGRAY);
					tvDaily.setTextColor(Color.GRAY);
					cbDaily.setBackgroundColor(Color.GRAY);
				}
				llDaily.setBackgroundColor(dColor);
				if (isLastChild) {
					// TODO: add switch between apis
					llDailyOuter.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.layout_boder_no_top));
				}
			} catch (JSONException e1) {
				cbDaily.setText("[Error]}\n" + e1.getMessage());
				cbDaily.setOnClickListener(GUIHelpers.mkToastListener(ctx, e1.getMessage()));
				e1.printStackTrace();
			}
		}

		return convertView;
	}

	private View getTodoView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

		convertView = inf.inflate(R.layout.expandable_todo, parent, false);

		final RelativeLayout rlTodo = (RelativeLayout) convertView.findViewById(R.id.rlTodo);
		final LinearLayout llTodo = (LinearLayout) convertView.findViewById(R.id.llTodo);
		final LinearLayout llTodoOuter = (LinearLayout) convertView.findViewById(R.id.llTodoOuter);
		final CheckBox cbTodo = (CheckBox) convertView.findViewById(R.id.cbTodo);
		final TextView tvTodo = (TextView) convertView.findViewById(R.id.tvTodo);

		ExpandableTask e = listOfAllTasks.get(groupPosition);

		if (e != null && cbTodo != null) {
			JSONArray list = e.getList();

			try {
				JSONObject h = list.getJSONObject(childPosition);

				cbTodo.setChecked(h.getBoolean("completed"));

				tvTodo.setText(h.getString("text"));
				int color = HabitColors.colorFromValue(h.getDouble("value"));
				int dColor = darkenColor(color);
				int lColor = lightenColor(color);
				tvTodo.setBackgroundColor(HabitColors.colorFromValue(h.getDouble("value")));
				cbTodo.setBackgroundColor(lColor);
				if (cbTodo.isChecked()) {
					tvTodo.setBackgroundColor(Color.LTGRAY);
					tvTodo.setTextColor(Color.GRAY);
					cbTodo.setBackgroundColor(Color.GRAY);
				}
				llTodo.setBackgroundColor(dColor);
				if (isLastChild) {
					// TODO: add switch between apis
					llTodoOuter.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.layout_boder_no_top));
				}

			} catch (JSONException e1) {
				cbTodo.setText("[Error]}\n" + e1.getMessage());
				cbTodo.setOnClickListener(GUIHelpers.mkToastListener(ctx, e1.getMessage()));
				e1.printStackTrace();
			}
		}

		return convertView;
	}

	private View getRewardView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

		convertView = inf.inflate(R.layout.expandable_reward, parent, false);

		final Button btnRewardBuy = (Button) convertView.findViewById(R.id.btnRewardBuy);
		final ImageView ivRewardIcon = (ImageView) convertView.findViewById(R.id.ivRewardIcon);
		final TextView tvRewardDescription = (TextView) convertView.findViewById(R.id.tvRewardDescription);
		final TextView tvRewardPrice = (TextView) convertView.findViewById(R.id.tvRewardPrice);

		ExpandableTask e = listOfAllTasks.get(groupPosition);

		if (e != null && tvRewardPrice != null) {
			JSONArray list = e.getList();

			try {
				JSONObject h = list.getJSONObject(childPosition);

				tvRewardPrice.setText("" + h.getInt("value") + "G");
				// TODO: COIN: btnRewardBuy.setBackgroundResource(R.drawable.)

				tvRewardDescription.setText(h.getString("text"));
				habitClick(btnRewardBuy, h.getString("id"), BUY);

			} catch (JSONException e1) {
				tvRewardDescription.setText("[Error]}\n" + e1.getMessage());
				tvRewardDescription.setOnClickListener(GUIHelpers.mkToastListener(ctx, e1.getMessage()));
				e1.printStackTrace();
			}
		}

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (listOfAllTasks.get(groupPosition).getList() != null) {
			return listOfAllTasks.get(groupPosition).getList().length();
		} else {
			return 0;
		}
	}

	@Override
	public Object getGroup(int groupPosition) {
		return listOfAllTasks.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return listOfAllTasks.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		convertView = inf.inflate(R.layout.expandable_group, parent, false);

		LinearLayout llGroup = (LinearLayout) convertView.findViewById(R.id.llGroup);
		ImageView ivExpanderArrow = (ImageView) convertView.findViewById(R.id.ivExpanderArrow);

		if (isExpanded) {
			llGroup.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.layout_border_no_bottom));
			ivExpanderArrow.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.arrow_up));
		} else {
			llGroup.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.layout_border));
			ivExpanderArrow.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.arrow_down));
		}

		TextView tvGroupTitle = (TextView) convertView.findViewById(R.id.tvGroupTitle);

		tvGroupTitle.setText(listOfAllTasks.get(groupPosition).getTitle());

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO: proper implementation for this?
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
