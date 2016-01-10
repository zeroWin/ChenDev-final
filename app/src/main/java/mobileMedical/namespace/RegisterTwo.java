package mobileMedical.namespace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import mobileMedical.database.DBManager;
//注册第二步，必须要填写用户名密码和id,savetoDB中用到了事务。
public class RegisterTwo extends Activity {
	private Button backButton;
	private Button submitButton;
	private EditText nameEditText;
	private EditText passwordEditText;
	private EditText idEditText;
	private EditText workEditText;
	private EditText roomEditText;
	private EditText levelEditText;
	private EditText teleEditText;
	private EditText emailEditText;
	private DBManager dbManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.register_two);
		initView();
		dbManager = new DBManager(RegisterTwo.this);

		submitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!nameEditText.getText().toString().equalsIgnoreCase("")
						&& !passwordEditText.getText().toString()
								.equalsIgnoreCase("")
						&& !idEditText.getText().toString()
								.equalsIgnoreCase("")) {
					if (checkId(idEditText.getText().toString())) {
						saveToDB();

						Intent startMainIntent = new Intent();
						startMainIntent.setClass(RegisterTwo.this,
								RegisterThree.class);
						Bundle bundle = new Bundle();
						bundle.putString("name", nameEditText.getText()
								.toString());
						startMainIntent.putExtras(bundle);
						startActivity(startMainIntent);
						finish();
						RegisterOne.registerOneActivity.finish();
					} else {
						Toast.makeText(getApplicationContext(), "编号已存在,请重新输入",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"请正确输入用户名,密码和数字组成的编号", Toast.LENGTH_SHORT).show();
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

	private boolean checkId(String id) {
		return dbManager.identifyId(id);
	}

	private void saveToDB() {
		dbManager.add(nameEditText.getText().toString(), passwordEditText
				.getText().toString(), idEditText.getText().toString(),
				workEditText.getText().toString(), roomEditText.getText()
						.toString(), levelEditText.getText().toString(),
				teleEditText.getText().toString(), emailEditText.getText()
						.toString());
	}

	private void initView() {
		backButton = (Button) findViewById(R.id.register_two_back);
		submitButton = (Button) findViewById(R.id.register_two_submit);
		nameEditText = (EditText) findViewById(R.id.register_two_editText1);
		passwordEditText = (EditText) findViewById(R.id.register_two_password);
		idEditText = (EditText) findViewById(R.id.register_two_editText2);
		workEditText = (EditText) findViewById(R.id.register_two_editText3);
		roomEditText = (EditText) findViewById(R.id.register_two_editText4);
		levelEditText = (EditText) findViewById(R.id.register_two_editText5);
		teleEditText = (EditText) findViewById(R.id.register_two_editText6);
		emailEditText = (EditText) findViewById(R.id.register_two_editText7);
	}

}
