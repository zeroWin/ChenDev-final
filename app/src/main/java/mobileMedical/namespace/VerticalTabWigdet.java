package mobileMedical.namespace;






import java.util.Arrays;

import com.artfulbits.aiCharts.Base.MathUtils;

import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.app.TabActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
import android.view.Window;
import android.view.Gravity;

public class VerticalTabWigdet extends TabWidget {
	   Resources res;



	public VerticalTabWigdet(Context context, AttributeSet attrs) {

	super(context, attrs);

	// TODO Auto-generated constructor stub

	res=context.getResources();

	setOrientation(LinearLayout.VERTICAL);

	}


	@Override

	public void addView(View child) {

	// TODO Auto-generated method stub

	LinearLayout.LayoutParams lp = new LayoutParams(

	LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1.0f);

	lp.setMargins(0, 0, 0, 0);

	child.setLayoutParams(lp);

	super.addView(child);

	//child.setBackgroundDrawable(res.getDrawable(R.drawable.vertical_tab_selector));

	}

	}
