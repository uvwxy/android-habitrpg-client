package de.uvwxy.habitrpg;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import de.uvwxy.habitrpg.R;

public class HabitSettings {

	private static final String PREF_ID = "habitdroid";

	private static final String SET_API_TOKEN = "apitoken";
	private static final String SET_USER_ID = "userid";
	private static final String SET_URL = "serverURL";

	private Context ctx;

	public interface OnSettingsSave {
		public void onSettingsSave();
	}

	public HabitSettings(Context ctx) {
		this.ctx = ctx;
	}

	Dialog dialog;

	public void show(final OnSettingsSave oss) {
		final SharedPreferences settings = ctx.getSharedPreferences(PREF_ID, 0);

		dialog = new Dialog(ctx);

		dialog.setContentView(R.layout.dialog_settings);
		dialog.setTitle("Please enter your config");

		TextView tvURL = (TextView) dialog.findViewById(R.id.tvURL);
		TextView tvUserID = (TextView) dialog.findViewById(R.id.btnUserID);
		TextView tvAPIToken = (TextView) dialog.findViewById(R.id.tvAPIToken);
		final EditText etServerURL = (EditText) dialog.findViewById(R.id.etServerURL);
		final EditText etUserID = (EditText) dialog.findViewById(R.id.etUserID);
		final EditText etAPIToken = (EditText) dialog.findViewById(R.id.etAPIToken);

		etServerURL.setText(settings.getString(SET_URL, "https://habitrpg.com"));
		etUserID.setText(settings.getString(SET_USER_ID, ""));
		etAPIToken.setText(settings.getString(SET_API_TOKEN, ""));

		Button btnSave = (Button) dialog.findViewById(R.id.btnSave);
		Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);

		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SharedPreferences.Editor editor = settings.edit();
				editor.putString(SET_API_TOKEN, etAPIToken.getText().toString().replace(" ", ""));
				editor.putString(SET_USER_ID, etUserID.getText().toString().replace(" ", ""));
				editor.putString(SET_URL, etServerURL.getText().toString().replace(" ", ""));
				editor.commit();
				if (oss != null) {
					oss.onSettingsSave();
				}
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	public String[] readSettings() {
		SharedPreferences settings = ctx.getSharedPreferences(PREF_ID, 0);
		String apiToken = settings.getString(SET_API_TOKEN, null);
		String userId = settings.getString(SET_USER_ID, null);
		String serverURL = settings.getString(SET_URL, null);
		return new String[] { userId, apiToken, serverURL };
	}

	public boolean isSet() {
		SharedPreferences settings = ctx.getSharedPreferences(PREF_ID, 0);
		String apiToken = settings.getString(SET_API_TOKEN, null);
		String userId = settings.getString(SET_USER_ID, null);
		String serverURL = settings.getString(SET_URL, null);

		return serverURL != null && userId != null && apiToken != null;
	}
}
