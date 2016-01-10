package mobileMedical.namespace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
//注册第三步，恭喜注册成功~
public class RegisterThree extends Activity {
	private Button finishButton;
	private TextView nameTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.register_three);
		initView();
		Bundle b = getIntent().getExtras();
		// 获取Bundle的信息
		String name = b.getString("name");
		nameTextView.setText(name);
		
		finishButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent startMainIntent = new Intent();
				startMainIntent.setClass(RegisterThree.this, Login.class);
				startActivity(startMainIntent);
				finish();
			}
		});
	}

	private void initView() {
		finishButton = (Button) findViewById(R.id.register_finish);
		nameTextView = (TextView) findViewById(R.id.register_three_name);
	}

}
