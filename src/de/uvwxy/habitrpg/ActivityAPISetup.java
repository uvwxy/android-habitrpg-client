package de.uvwxy.habitrpg;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ActivityAPISetup extends Activity {
	public static final String API_TOKEN = "API_TOKEN";
	public static final String USER_TOKEN = "USER_TOKEN";
	public static final String SERVER_URL = "SERVER_URL";

	private EditText etServerURL;
	private EditText etUserID;
	private EditText etAPIToken;
	private Button btnSave;
	private Button btnScanQRCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apisetup);
		// Show the Up button in the action bar.
		setupActionBar();

		etServerURL = (EditText) findViewById(R.id.etServerURL);
		etUserID = (EditText) findViewById(R.id.etUserID);
		etAPIToken = (EditText) findViewById(R.id.etAPIToken);
		btnSave = (Button) findViewById(R.id.btnSave);
		btnScanQRCode = (Button) findViewById(R.id.btnScanQRCode);

		btnSave.setOnClickListener(btnSaveClick);
		btnScanQRCode.setOnClickListener(btnScanQRCodeClick);

		HabitSettings habitSettings = new HabitSettings(this);

		String loadedServerURL = habitSettings.getURL();
		String loadedUserToken = habitSettings.getUserToken();
		String loadedApiToken = habitSettings.getApiToken();

		if (loadedServerURL != null) {
			etServerURL.setText(loadedServerURL);
		} else {
			etServerURL.setText("https://habitrpg.com");
		}

		if (loadedUserToken != null) {
			etUserID.setText(loadedUserToken);
		}

		if (loadedApiToken != null) {
			etAPIToken.setText(loadedApiToken);
		}

	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_apisetup, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private OnClickListener btnSaveClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent data = new Intent();

			if (etServerURL != null) {

				String fixLastSlash = etServerURL.getText().toString();

				// remove trailing slashes
				while (fixLastSlash.charAt(fixLastSlash.length() - 1) == '/') {
					fixLastSlash = fixLastSlash.substring(0, fixLastSlash.length() - 2);
				}

				etServerURL.setText(fixLastSlash);

				data.putExtra(SERVER_URL, fixLastSlash);
			}
			if (etUserID != null) {
				data.putExtra(USER_TOKEN, etUserID.getText().toString());
			}
			if (etAPIToken != null) {
				data.putExtra(API_TOKEN, etAPIToken.getText().toString());
			}

			if (getParent() == null) {
				setResult(Activity.RESULT_OK, data);
			} else {
				getParent().setResult(Activity.RESULT_OK, data);
			}
			finish();
		}
	};

	private OnClickListener btnScanQRCodeClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivityAPISetup.this);

			alertDialog.setPositiveButton("OK", null);

			alertDialog.setMessage("This feature is not available yet. If we ask Tyler nicely, he might add the option to enter your config via a QR code, =).");
			alertDialog.setTitle("Setup via QR Code");
			alertDialog.show();
		}
	};
}
