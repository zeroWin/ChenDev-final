package mobileMedical.namespace;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

import java.util.List;

public class SettingPreferenceActivity extends PreferenceActivity {
	private static final String TAG = "SettingPreference";
    private static final String  noconnected = "没有蓝牙连接";
    private static final boolean D = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("系统设置");
        
    }

    /**
     * Populate the activity with the top-level headers.
     */
    @Override
    public void onBuildHeaders(List<Header> target) {
    	if (BuildConfig.DEBUG) {
    		Log.i(TAG, "++ ON Build Header ++");
    	}
        loadHeadersFromResource(R.xml.setting_preference, target);
        
    }


 
    
}
