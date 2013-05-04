package de.uvwxy.habitrpg.api;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class HabitConnectionV1 {
	private static final String HABIT_SAV = "habit.sav";

	private String mUrl;

	private String userId;
	private String apiToken;
	private JSONObject data_main;
	private JSONObject data_auth;
	private JSONObject data_preferences;
	private JSONArray data_habits;
	private JSONArray data_dailies;
	private JSONArray data_todos;
	private JSONArray data_todos_done;
	private JSONArray data_rewards;

	private double exp = Double.MIN_VALUE;
	private double gp = Double.MIN_VALUE;
	private double hp = Double.MIN_VALUE;
	private double lvl = Double.MIN_VALUE;

	public HabitConnectionV1() {
		super();
	}

	public void setConfig(String url, String userId, String apiToken) {
		this.mUrl = url;
		this.userId = userId;
		this.apiToken = apiToken;
	}

	public boolean isUp() {
		try {
			return new JSONObject(getJSONResponse("/api/v1/status")).getString("status").equals("up");
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

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
			String dataJSON = getStringFromInputStream(fin);
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

	public boolean loadRemoteData() {
		JSONObject data;
		try {
			data = new JSONObject(getJSONResponse("/api/v1/user"));
			data_main = data;
			return setupData(data);
		} catch (ClientProtocolException e) {
			Log.e("HABIT", "Error: " + e.getMessage());
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
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

	public JSONArray fetchHabits() {
		data_habits = fetchTasks("habit");
		return data_habits;
	}

	public JSONArray fetchDailies() {
		data_dailies = fetchTasks("daily");
		return data_dailies;
	}

	public JSONArray fetchTodos() {
		data_todos = fetchTasks("todo");
		return data_todos;
	}

	public JSONArray fetchRewards() {
		data_rewards = fetchTasks("reward");
		return data_rewards;
	}

	private JSONArray fetchTasks(String type) {
		try {
			JSONArray t = new JSONArray(getJSONResponse("/api/v1/user/tasks?type=" + type));
			return t;
		} catch (Exception e) {
			return null;
		}
	}

	public String updateTask(String taskId, boolean upOrCompleted) throws ClientProtocolException, IOException {
		String url = mUrl + "/api/v1/user/tasks/" + taskId + "/" + (upOrCompleted ? "up" : "down");

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		post.addHeader(new BasicHeader("x-api-user", userId));
		post.addHeader(new BasicHeader("x-api-key", apiToken));

		((AbstractHttpClient) client).addRequestInterceptor(new HttpRequestInterceptor() {

			public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
				if (!request.containsHeader("Accept-Encoding")) {
					request.addHeader("Accept-Encoding", "gzip");
				}
			}

		});

		((AbstractHttpClient) client).addResponseInterceptor(new HttpResponseInterceptor() {

			public void process(final HttpResponse response, final HttpContext context) throws HttpException, IOException {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					Header ceheader = entity.getContentEncoding();
					if (ceheader != null) {
						HeaderElement[] codecs = ceheader.getElements();
						for (int i = 0; i < codecs.length; i++) {
							if (codecs[i].getName().equalsIgnoreCase("gzip")) {
								response.setEntity(new GzipDecompressingEntity(response.getEntity()));
								return;
							}
						}
					}
				}
			}

		});

		HttpResponse response = client.execute(post);
		//		StringWriter writer = new StringWriter();
		//
		//		IOUtils.copy(response.getEntity().getContent(), writer, Charset.forName("UTF-8"));
		return getStringFromInputStream(response.getEntity().getContent());
	}

	private String getJSONResponse(String action) throws ClientProtocolException, IOException {

		String url = mUrl + action;

		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);

		((AbstractHttpClient) client).addRequestInterceptor(new HttpRequestInterceptor() {

			public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
				if (!request.containsHeader("Accept-Encoding")) {
					request.addHeader("Accept-Encoding", "gzip");
				}
			}

		});

		((AbstractHttpClient) client).addResponseInterceptor(new HttpResponseInterceptor() {

			public void process(final HttpResponse response, final HttpContext context) throws HttpException, IOException {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					Header ceheader = entity.getContentEncoding();
					if (ceheader != null) {
						HeaderElement[] codecs = ceheader.getElements();
						for (int i = 0; i < codecs.length; i++) {
							if (codecs[i].getName().equalsIgnoreCase("gzip")) {
								response.setEntity(new GzipDecompressingEntity(response.getEntity()));
								return;
							}
						}
					}
				}
			}

		});

		get.addHeader(new BasicHeader("x-api-user", userId));
		get.addHeader(new BasicHeader("x-api-key", apiToken));

		HttpResponse response = client.execute(get);
		//		StringWriter writer = new StringWriter();
		//
		//		IOUtils.copy(response.getEntity().getContent(), writer, Charset.forName("UTF-8"));
		return getStringFromInputStream(response.getEntity().getContent());

	}

	public interface ServerResultCallback {
		public void serverReply(String s);
	}

	private static String getStringFromInputStream(InputStream is) {

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();

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
