package mobileMedical.namespace;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import mobileMedical.database.DBManager;

public class Login extends Activity {

	private Button loginButton;
	private Button cancelButton;
	private Button registerButton;

	private EditText usernameEditText;
	private EditText passwordEditText;
	private DBManager dbManager;

	private CheckBox rememberCheckBox;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//这句啥意思？window和flag是啥。？？？？？？？？？？？？？？？？？？？？
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.login);
		initView();
		dbManager = new DBManager(Login.this);
		Cursor cursor = dbManager.querySetting();
		if (cursor.moveToNext()) {
			int remember = cursor.getInt(cursor.getColumnIndex("remember"));
			String username = cursor.getString(cursor
					.getColumnIndex("username"));
			String password = cursor.getString(cursor
					.getColumnIndex("password"));
			if (remember == 1) {
				rememberCheckBox.setChecked(true);
				usernameEditText.setText(username);
				passwordEditText.setText(password);
			}
		}
		cursor.close();
		dbManager.closeDB();
		//用能否找到对应id来判断用户名和密码是否正确。
		loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int id = dbManager.query(usernameEditText.getText().toString(),
						passwordEditText.getText().toString());
				if (id != -1) {
					//本if-else是为了更新记住密码相关设置。
					if (rememberCheckBox.isChecked()) {
						Cursor cursor = dbManager.querySetting();
						if (cursor.getCount() == 0) {
							dbManager.addSetting(1, usernameEditText.getText()
									.toString(), passwordEditText.getText()
									.toString());
						} else {
							dbManager.updateSetting(1, usernameEditText
									.getText().toString(), passwordEditText
									.getText().toString());
						}
						cursor.close();
						dbManager.closeDB();
					} else {
						Cursor cursor = dbManager.querySetting();
						if (cursor.getCount() == 0) {
							dbManager.addSetting(0, "", "");
						} else {
							dbManager.updateSetting(0, "", "");
						}
						cursor.close();
						dbManager.closeDB();
					}

					Intent startMainIntent = new Intent();
					startMainIntent.setClass(Login.this, MemberManage.class);
					startActivity(startMainIntent);
					MemberManage.doctorID = id;
					finish();
				} else {
					Toast.makeText(Login.this, "用户名或者密码错误", Toast.LENGTH_LONG)
							.show();
				}

			}
		});

		registerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent startMainIntent = new Intent();
				startMainIntent.setClass(Login.this, RegisterOne.class);
				startActivity(startMainIntent);
			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(Login.this)
						.setIcon(R.drawable.diamonds_4)
						.setTitle(R.string.app_name)
						.setMessage(R.string.hint_quit)
						.setNegativeButton(R.string.cancel,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}
								})
						.setPositiveButton(R.string.sure,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
														int whichButton) {
										finish();
										System.exit(0); // exitApp
									}
								}).show();
			}
		});
	}

	private void initView() {
		loginButton = (Button) findViewById(R.id.login);
		cancelButton = (Button) findViewById(R.id.cancel);
		registerButton = (Button) findViewById(R.id.register);

		usernameEditText = (EditText) findViewById(R.id.login_username);
		passwordEditText = (EditText) findViewById(R.id.login_passwrod);
		rememberCheckBox = (CheckBox) findViewById(R.id.login_checkBox1);
	}

	/**
	 * 监听系统按键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(this)
					.setIcon(R.drawable.notice)
					.setTitle(R.string.app_name)
					.setMessage(R.string.hint_quit)
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							})
					.setPositiveButton(R.string.sure,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									System.exit(0); // exitApp
								}
							}).show();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

}
