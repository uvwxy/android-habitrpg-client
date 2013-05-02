package de.uvwxy.habitrpg;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class HabitSettings {

	private static final String PREF_ID = "habitdroid";

	private static final String SET_API_TOKEN = "apitoken";
	private static final String SET_USER_ID = "userid";
	private static final String SET_URL = "serverURL";

	private EditText etServerURL;
	private EditText etUserID;
	private EditText etAPIToken;

	private Context ctx;

	SharedPreferences settings;

	public interface OnSettingsSave {
		public void onSettingsSave();
	}

	public HabitSettings(Context ctx) {
		this.ctx = ctx;
		settings = ctx.getSharedPreferences(PREF_ID, 0);
	}

	Dialog dialog;

	public void show(final OnSettingsSave oss) {

		LayoutInflater inflater = LayoutInflater.from(ctx);
		final View addView;

		addView = inflater.inflate(R.layout.dialog_settings, null);

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx);
		alertDialog.setView(addView);

		TextView tvURL = (TextView) addView.findViewById(R.id.tvURL);
		TextView tvUserID = (TextView) addView.findViewById(R.id.btnUserID);
		TextView tvAPIToken = (TextView) addView.findViewById(R.id.tvAPIToken);
		etServerURL = (EditText) addView.findViewById(R.id.etServerURL);
		etUserID = (EditText) addView.findViewById(R.id.etUserID);
		etAPIToken = (EditText) addView.findViewById(R.id.etAPIToken);

		alertDialog.setPositiveButton(ctx.getString(R.string.save), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				saveDialogInput();
				dialog.dismiss();
				if (oss != null) {
					oss.onSettingsSave();
				}
				dialog.dismiss();

			}
		});

		alertDialog.setNegativeButton(ctx.getString(R.string.cancel), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});

		alertDialog.setTitle("Please enter your config");

		dialog = alertDialog.create();

		loadDialogInput();

		dialog.show();
	}

	public void saveDialogInput() {
		if (etAPIToken != null && etUserID != null && etServerURL != null) {
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(SET_API_TOKEN, etAPIToken.getText().toString().replace(" ", ""));
			editor.putString(SET_USER_ID, etUserID.getText().toString().replace(" ", ""));
			editor.putString(SET_URL, etServerURL.getText().toString().replace(" ", ""));
			editor.commit();
		}

	}

	public void loadDialogInput() {
		etServerURL.setText(settings.getString(SET_URL, "https://habitrpg.com"));
		etUserID.setText(settings.getString(SET_USER_ID, ""));
		etAPIToken.setText(settings.getString(SET_API_TOKEN, ""));
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
