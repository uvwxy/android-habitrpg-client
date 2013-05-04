package de.uvwxy.habitrpg;

import android.content.Context;
import android.content.SharedPreferences;

public class HabitSettings {
	private Context ctx;

	private static final String PREF_ID = "habitdroid";

	private static final String SET_API_TOKEN = "apitoken";
	private static final String SET_USER_ID = "userid";
	private static final String SET_URL = "serverURL";

	private SharedPreferences settings;

	public HabitSettings(Context ctx) {
		this.ctx = ctx;
		settings = ctx.getSharedPreferences(PREF_ID, 0);
	}

	public void saveConfig(String url, String userToken, String apiToken) {
		if (url != null && userToken != null && apiToken != null) {
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(SET_API_TOKEN, apiToken);
			editor.putString(SET_USER_ID, userToken);
			editor.putString(SET_URL, url);
			editor.commit();
		}

	}

	public String getURL() {
		return settings.getString(SET_URL, null);
	}

	public String getUserToken() {
		return settings.getString(SET_USER_ID, null);
	}

	public String getApiToken() {
		return settings.getString(SET_API_TOKEN, null);
	}

	public boolean isSet() {
		SharedPreferences settings = ctx.getSharedPreferences(PREF_ID, 0);
		String apiToken = settings.getString(SET_API_TOKEN, null);
		String userId = settings.getString(SET_USER_ID, null);
		String serverURL = settings.getString(SET_URL, null);

		return serverURL != null && userId != null && apiToken != null;
	}
}
