package de.uvwxy.habitrpg.api;

import java.io.BufferedReader;
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


public class HabitConnectionV1 {

	private String mUrl;

	private String userId;
	private String apiToken;

	public interface ServerResultCallback {
		public void serverReply(String s, String taskId, boolean upOrCompleted);
	}

	public HabitConnectionV1() {
		super();
	}

	public void setConfig(String url, String userId, String apiToken) {
		this.mUrl = url;
		this.userId = userId;
		this.apiToken = apiToken;
	}

	public boolean isUp() {
		if (mUrl == null || userId == null || apiToken == null) {
			return false;
		}

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

	public boolean loadRemoteData(HabitDataV1 habitData) {
		JSONObject data;
		try {
			data = new JSONObject(getJSONResponse("/api/v1/user"));
			habitData.root = data;
			return habitData.setupData(data);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void fetchHabits(HabitDataV1 target) {
		target.clobberTasks(fetchTasks("habit"));
	}

	public void fetchDailies(HabitDataV1 target) {
		target.clobberTasks(fetchTasks("daily"));
	}

	public void fetchTodos(HabitDataV1 target) {
		target.clobberTasks(fetchTasks("todo"));
	}

	public void fetchRewards(HabitDataV1 target) {
		target.clobberTasks(fetchTasks("reward"));
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

	static String getStringFromInputStream(InputStream is) {

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

}
