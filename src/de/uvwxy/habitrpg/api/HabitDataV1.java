package de.uvwxy.habitrpg.api;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class HabitDataV1 {

	private static final String HABIT_SAV = "habit.sav";

	JSONObject data_main;
	JSONObject data_auth;
	JSONObject data_preferences;
	JSONArray data_habits;
	JSONArray data_dailies;
	JSONArray data_todos;
	JSONArray data_todos_done;
	JSONArray data_rewards;

	public boolean storeLocalData(Context ctx) {
		if (data_main == null) {
			return false;
		}

		String dataJSON = data_main.toString();
		try {
			FileOutputStream fout = ctx.openFileOutput(HABIT_SAV, Context.MODE_PRIVATE);
			fout.write(dataJSON.getBytes());
		} catch (FileNotFoundException e) {
			Log.e("HABIT", "Error: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("HABIT", "Error: " + e.getMessage());
			e.printStackTrace();
		}

		return false;
	}

	public boolean loadLocalData(Context ctx) {
		JSONObject data = null;
		try {
			FileInputStream fin = ctx.openFileInput(HABIT_SAV);
			String dataJSON = HabitConnectionV1.getStringFromInputStream(fin);
			if (dataJSON == null) {
				Log.e("HABIT", "Error: dataJSON was null");
				return false;
			}
			data = new JSONObject(dataJSON);
			data_main = data;
			return setupData(data);
		} catch (FileNotFoundException e) {
			Log.e("HABIT", "Error: " + e.getMessage());
			e.printStackTrace();
		} catch (JSONException e) {
			Log.e("HABIT", "Error: " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	public boolean setupData(JSONObject data) {

		try {
			data_auth = data.getJSONObject("auth");
			data_preferences = data.getJSONObject("preferences");

			JSONObject tasks = data.getJSONObject("tasks");

			data_habits = new JSONArray();
			data_dailies = new JSONArray();
			data_todos = new JSONArray();
			data_todos_done = new JSONArray();
			data_rewards = new JSONArray();

			JSONArray ja = data.getJSONArray("habitIds");
			for (int i = 0; i < ja.length(); i++) {
				data_habits.put(tasks.getJSONObject(ja.getString(i)));
			}

			ja = data.getJSONArray("dailyIds");
			for (int i = 0; i < ja.length(); i++) {
				data_dailies.put(tasks.getJSONObject(ja.getString(i)));
			}
			ja = data.getJSONArray("todoIds");
			for (int i = 0; i < ja.length(); i++) {
				JSONObject o = tasks.getJSONObject(ja.getString(i));
				if (!o.getBoolean("completed")) {
					data_todos.put(o);
				} else {
					data_todos_done.put(o);
				}
			}
			ja = data.getJSONArray("rewardIds");
			for (int i = 0; i < ja.length(); i++) {
				data_rewards.put(tasks.getJSONObject(ja.getString(i)));
			}

			return true;
		} catch (JSONException e) {
			Log.e("HABIT", "Error: " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	public String getUserName() {
		try {
			return data_auth.getJSONObject("local").getString("username");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getUserMail() {
		try {
			return data_auth.getJSONObject("local").getString("email");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public double getExp() {
		try {
			return data_main.getJSONObject("stats").getDouble("exp");
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public double getHp() {
		try {
			return data_main.getJSONObject("stats").getDouble("hp");
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public double getLevel() {
		try {
			return data_main.getJSONObject("stats").getDouble("lvl");
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public double getToNextLevel() {
		try {
			return data_main.getJSONObject("stats").getDouble("toNextLevel");
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public double getMaxHealth() {
		try {
			return data_main.getJSONObject("stats").getDouble("maxHealth");
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public double getGP() {
		try {
			return data_main.getJSONObject("stats").getDouble("gp");
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public boolean isPartyLeader() {
		try {
			return data_main.getJSONObject("party").getBoolean("leader");
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}

	public String getPartyID() {
		try {
			return data_main.getJSONObject("party").getString("current");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean hasPartyInvitation() {
		return getPartyInvitation() != null;
	}

	public String getPartyInvitation() {
		try {
			return data_main.getJSONObject("party").getString("invitation");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public int getWeapon() {
		try {
			if (data_main != null) {
				return data_main.getJSONObject("items").getInt("weapon");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return 0;

	}

	public int getArmor() {
		try {
			if (data_main != null) {
				return data_main.getJSONObject("items").getInt("armor");
			}
		} catch (JSONException e) {
			e.printStackTrace();

		}
		return 0;
	}

	public int getShield() {
		try {
			if (data_main != null) {
				return data_main.getJSONObject("items").getInt("shield");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return 0;

	}

	public int getHead() {
		try {
			if (data_main != null) {
				return data_main.getJSONObject("items").getInt("head");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return 0;

	}

	public JSONArray getHabitIds() {
		try {
			return data_main.getJSONArray("habitIds");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public JSONArray getDailyIds() {
		try {
			return data_main.getJSONArray("dailyIds");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public JSONArray getTodoIds() {
		try {
			return data_main.getJSONArray("todoIds");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getTextFromTask(JSONObject o) {
		try {
			return o.getString("text");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public JSONArray getHabits() {
		return data_habits;
	}

	public JSONArray getDailies() {
		return data_dailies;
	}

	public JSONArray getTodos() {
		return data_todos;
	}

	public JSONArray getRewards() {
		return data_rewards;
	}

	public boolean isMale() {
		try {
			if (data_preferences != null && data_preferences.get("gender") != null) {
				return data_preferences.get("gender").equals("m");
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return true;
		}

		return true;
	}

	public boolean showHelm() {
		try {
			if (data_preferences != null && data_preferences.getBoolean("showHelm")) {
				return true;
			} else {
				return false;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return true;
		}
	}

	public String getHair() {
		try {
			if (data_preferences != null && data_preferences.get("hair") != null) {
				return data_preferences.getString("hair");
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return "blond";
		}

		return "blond";
	}

	public String getSkin() {
		try {
			if (data_preferences != null && data_preferences.get("skin") != null) {
				return data_preferences.getString("skin");

			}
		} catch (JSONException e) {
			e.printStackTrace();
			return "white";
		}

		return "white";
	}

	public String getArmorSet() {
		try {
			if (data_preferences != null && data_preferences.get("armorSet") != null) {
				return data_preferences.getString("armorSet");

			}
		} catch (JSONException e) {
			e.printStackTrace();
			return "v1";
		}

		return "v1";
	}
}
