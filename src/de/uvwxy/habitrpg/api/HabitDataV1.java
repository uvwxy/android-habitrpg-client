package de.uvwxy.habitrpg.api;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class HabitDataV1 {

	private static final String HABIT_SAV = "habit.sav";

	public static String getTextFromTask(JSONObject o) {
		try {
			return o.getString("text");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	JSONObject root;

	//	JSONArray data_dailies;
	//	JSONArray data_habits;
	//	JSONArray data_rewards;
	//	JSONArray data_todos;
	//	JSONArray data_todos_done;

	public synchronized boolean applyServerResultToData(JSONObject o, String taskID, boolean upOrCompleted) {
		try {
			
			root.getJSONObject("stats").put("gp", o.getDouble("gp"));
			root.getJSONObject("stats").put("hp", o.getDouble("hp"));
			root.getJSONObject("stats").put("lvl", o.getDouble("lvl"));
			root.getJSONObject("stats").put("exp", o.getDouble("exp"));

			JSONObject task = getTask(taskID);
			JSONObject tasks = root.getJSONObject("tasks");
			task.put("value", task.getDouble("value") + o.getDouble("delta"));

			String taskType = task.getString("type");

			if (taskType.equals("habit")) {
				// nothing to do here
			}
			if (taskType.equals("reward")) {
				// nothing to do here
			}
			if (taskType.equals("daily")) {
				task.put("completed", upOrCompleted);
			}
			if (taskType.equals("todo")) {
				task.put("completed", upOrCompleted);
			}

			tasks.put(task.getString("id"), task);
			root.put("tasks", tasks);

			// unused: o.getDouble("delta");
			return true;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return false;
	}

	public int getArmor() {
		try {
			if (root != null) {
				return root.getJSONObject("items").getInt("armor");
			}
		} catch (JSONException e) {
			e.printStackTrace();

		}
		return 0;
	}

	public String getArmorSet() {
		try {
			return root.getJSONObject("preferences").getString("armorSet");

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return "v1";
	}

	public JSONArray getDailies() {
		return getTasksByIdField("dailyIds");
	}

	public JSONArray getDailyIds() {
		try {
			return root.getJSONArray("dailyIds");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public double getXP() {
		try {
			return root.getJSONObject("stats").getDouble("exp");
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public double getGP() {
		try {
			return root.getJSONObject("stats").getDouble("gp");
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public JSONArray getHabitIds() {
		try {
			return root.getJSONArray("habitIds");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public JSONArray getHabits() {
		return getTasksByIdField("habitIds");
	}

	public JSONArray getTasksByIdField(String idField) {

		try {
			JSONArray data_habits = new JSONArray();
			JSONObject tasks = root.getJSONObject("tasks");
			JSONArray ids = root.getJSONArray(idField);
			for (int i = 0; i < ids.length(); i++) {
				data_habits.put(tasks.getJSONObject(ids.getString(i)));
			}
			return data_habits;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public String getHair() {
		try {
			return root.getJSONObject("preferences").getString("hair");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return "blond";
	}

	public int getHead() {
		try {
			if (root != null) {
				return root.getJSONObject("items").getInt("head");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return 0;

	}

	public double getHP() {
		try {
			return root.getJSONObject("stats").getDouble("hp");
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public double getLevel() {
		try {
			return root.getJSONObject("stats").getDouble("lvl");
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public double getMaxHealth() {
		try {
			return root.getJSONObject("stats").getDouble("maxHealth");
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public String getPartyID() {
		try {
			return root.getJSONObject("party").getString("current");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getPartyInvitation() {
		try {
			return root.getJSONObject("party").getString("invitation");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public JSONArray getRewards() {
		return getTasksByIdField("rewardIds");
	}

	public int getShield() {
		try {
			if (root != null) {
				return root.getJSONObject("items").getInt("shield");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return 0;

	}

	public String getSkin() {
		try {
			root.getJSONObject("preferences").getString("skin");
		} catch (JSONException e) {
			e.printStackTrace();
			return "white";
		}

		return "white";
	}

	public JSONObject getTask(String taskID) {
		try {
			JSONObject tasks = root.getJSONObject("tasks");
			return tasks.getJSONObject(taskID);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public JSONArray getTodoIds() {
		try {
			return root.getJSONArray("todoIds");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public JSONArray getTodos() {
		return getTasksByIdField("todoIds");
	}

	public double getToNextLevel() {
		try {
			return root.getJSONObject("stats").getDouble("toNextLevel");
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public String getUserMail() {
		try {
			return root.getJSONObject("auth").getJSONObject("local").getString("email");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getUserName() {
		try {
			return root.getJSONObject("auth").getJSONObject("local").getString("username");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public int getWeapon() {
		try {
			if (root != null) {
				return root.getJSONObject("items").getInt("weapon");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return 0;

	}

	public boolean hasPartyInvitation() {
		return getPartyInvitation() != null;
	}

	public boolean isMale() {
		try {
			return root.getJSONObject("preferences").get("gender").equals("m");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return true;
	}

	public boolean isPartyLeader() {
		try {
			return root.getJSONObject("party").getBoolean("leader");
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean loadLocalData(Context ctx) {
		JSONObject data = null;
		try {
			FileInputStream fin = ctx.openFileInput(HABIT_SAV);
			String dataJSON = HabitConnectionV1.getStringFromInputStream(fin);
			if (dataJSON == null) {
				return false;
			}
			data = new JSONObject(dataJSON);
			return setupData(data);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * This function is used to extract the different task types from the
	 * fetchData chunk.
	 * 
	 * @param data
	 * @return true if successful
	 */
	public boolean setupData(JSONObject data) {

		try {
			root = data;

			JSONObject tasks = data.getJSONObject("tasks");

			return true;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean showHelm() {
		try {
			return root.getJSONObject("preferences").getBoolean("showHelm");
		} catch (JSONException e) {
			e.printStackTrace();
			return true;
		}
	}

	public boolean storeLocalData(Context ctx) {
		if (root == null) {
			return false;
		}

		String dataJSON = root.toString();
		try {
			FileOutputStream fout = ctx.openFileOutput(HABIT_SAV, Context.MODE_PRIVATE);
			fout.write(dataJSON.getBytes());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean clobberTasks(JSONArray source) {
		JSONObject tasks;
		try {
			tasks = root.getJSONObject("tasks");
			fillJSONOject(tasks, source);
			root.put("tasks", tasks);
			return true;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void fillJSONOject(JSONObject target, JSONArray source) {
		for (int i = 0; i < source.length(); i++) {
			JSONObject item;
			try {
				item = source.getJSONObject(i);
				target.put(item.getString("id"), item);
			} catch (JSONException e) {
				e.printStackTrace();
				continue;
			}

		}

	}

	public boolean rootIsNull() {
		return root == null;
	}
}
