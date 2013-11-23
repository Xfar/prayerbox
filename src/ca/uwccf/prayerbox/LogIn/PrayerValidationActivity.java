package ca.uwccf.prayerbox.LogIn;

import ca.uwccf.prayerbox.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PrayerValidationActivity extends Activity {
	private String mCode = "uwccf";
	private String mCodeAttempt;
	private EditText codeView;
	private SharedPreferences prefs;

	public void onPause() {
		super.onPause();
		overridePendingTransition(0, 0);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_initial_validation);
		prefs = getSharedPreferences(ACCOUNT_SERVICE, MODE_PRIVATE);
		Button button = (Button) findViewById(R.id.validate_button);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				submitCode();
			}
		});

	}

	public void submitCode() {
		codeView = (EditText) findViewById(R.id.validate_code);
		mCodeAttempt = codeView.getText().toString();
		if (mCodeAttempt.equalsIgnoreCase(mCode)) {
			SharedPreferences.Editor prefEditor = prefs.edit();
			prefEditor.putBoolean("validated", true);
			prefEditor.commit();
			Intent i = new Intent(getApplicationContext(),
					PrayerLoginActivity.class);
			startActivity(i);
			overridePendingTransition(0, 0);
			finish();
		} else {
			codeView.setError(getString(R.string.error_incorrect_code));
		}
	}

}
