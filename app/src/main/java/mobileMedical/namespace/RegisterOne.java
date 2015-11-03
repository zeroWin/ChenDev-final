package mobileMedical.namespace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class RegisterOne extends Activity {

	private Button backButton;
	private Button nextButton;
	private CheckBox agreeeCheckBox;
	public static Activity registerOneActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.register_one);
		initView();
		registerOneActivity = this;
		nextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (agreeeCheckBox.isChecked()) {
					Intent startMainIntent = new Intent();
					startMainIntent.setClass(RegisterOne.this,
							RegisterTwo.class);
					startActivity(startMainIntent);
				} else {
					Toast.makeText(getApplicationContext(), "请勾选同意条款",
							Toast.LENGTH_LONG).show();
				}

			}
		});

		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	private void initView() {
		backButton = (Button) findViewById(R.id.register_one_back);
		nextButton = (Button) findViewById(R.id.register_one_next);
		agreeeCheckBox = (CheckBox) findViewById(R.id.agree_checkbox);
	}
}
