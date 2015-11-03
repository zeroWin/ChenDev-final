package mobileMedical.Contacts.UI;

import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import mobileMedical.namespace.R;

public class MainActivity extends ActivityGroup {
	private GridView bottomMenu;// 应用的底部菜单
	private LinearLayout container;// 子Activity
	private Intent intent;

	/** gridViewBar图片 **/
	private int[] gridViewBar_image_array = { R.drawable.contact_bg_tab_dial_normal,
			R.drawable.contact_bg_tab_contact_normal, R.drawable.contact_bg_tab_sms_normal,
			R.drawable.contact_bg_tab_setting_normal };
	/** gridViewBar文字 **/
	private String[] gridViewBar_name_array = { "拨 号", "联系人", "信 息", "个人中心" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_activity_main);

		bottomMenu = (GridView) findViewById(R.id.main_gridViewBar);
		container = (LinearLayout) findViewById(R.id.llContainer);

		bottomMenu.setNumColumns(4);// 设置每行列数
		bottomMenu.setGravity(Gravity.CENTER);// 设置位置居中
		bottomMenu.setVerticalSpacing(10);// 垂直间隔
		bottomMenu.setHorizontalSpacing(10);// 水平间隔
		bottomMenu.setAdapter(new SimpleAdapter_1(this));// 设置菜单Adapter

		switchActivity(1);// 默认第一次进入联系人界面

		bottomMenu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 设置当前选中项的下划线可见
				View view1 = parent.getChildAt(0).findViewById(R.id.select);
				View view2 = parent.getChildAt(1).findViewById(R.id.select);
				View view3 = parent.getChildAt(2).findViewById(R.id.select);
				View view4 = parent.getChildAt(3).findViewById(R.id.select);
				switch (position) {
				case 0:
					view1.setVisibility(View.VISIBLE);
					view2.setVisibility(View.GONE);
					view3.setVisibility(View.GONE);
					view4.setVisibility(View.GONE);
					break;
				case 1:
					view1.setVisibility(View.GONE);
					view2.setVisibility(View.VISIBLE);
					view3.setVisibility(View.GONE);
					view4.setVisibility(View.GONE);
					break;
				case 2:
					view1.setVisibility(View.GONE);
					view2.setVisibility(View.GONE);
					view3.setVisibility(View.VISIBLE);
					view4.setVisibility(View.GONE);
					break;
				case 3:
					view1.setVisibility(View.GONE);
					view2.setVisibility(View.GONE);
					view3.setVisibility(View.GONE);
					view4.setVisibility(View.VISIBLE);
					break;
				}
				// 转换子Activity
				switchActivity(position);
			}
		});
	}

	/**
	 * Activity之间的转换
	 * 
	 * @param position
	 *            当前点击的项
	 */
	private void switchActivity(int position) {
		container.removeAllViews();
		switch (position) {
		case 0:
			intent = new Intent(this, DialActivity.class);
			break;
		case 1:
			intent = new Intent(this, ContactActivity.class);
			break;
		case 2:
			intent = new Intent(this, MessageActivity.class);
			break;
		case 3:
			intent = new Intent(this, CenterActivity.class);
			break;
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// 子Activity切换时不保存状态
		// Activity 转为 View
		Window subActivity = getLocalActivityManager().startActivity(
				"subActivity", intent);
		container.addView(subActivity.getDecorView(),
				new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.FILL_PARENT));

	}

	/**
	 * 当子Activity转换后,释放菜单键,使点击"Menu"时可以显示菜单
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			this.getLocalActivityManager().getCurrentActivity()
					.openOptionsMenu();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 自定义适配器（用于GridView生成主界面底部菜单栏）
	 * 
	 * @author dell
	 * 
	 */
	public class SimpleAdapter_1 extends BaseAdapter {

		private LayoutInflater inflater;

		public SimpleAdapter_1(Context context) {
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return gridViewBar_name_array.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = inflater.inflate(R.layout.contact_item_menu, null);
			ImageView item_img = (ImageView) convertView
					.findViewById(R.id.item_image);
			TextView item_txt = (TextView) convertView
					.findViewById(R.id.item_text);
			View view = convertView.findViewById(R.id.select);

			item_img.setImageResource(gridViewBar_image_array[position]);
			item_txt.setText(gridViewBar_name_array[position]);
			if (position == 1)// 默认启动程序时显示"联系人界面"
				view.setVisibility(View.VISIBLE);

			return convertView;
		}
	}
}
