package mobileMedical.namespace;


import android.os.Bundle;

import android.preference.Preference;

import android.preference.PreferenceFragment;

import android.preference.PreferenceManager;

import android.util.Log;


import java.util.ArrayList;

import java.util.List;











import android.content.SharedPreferences;





/**
 * This fragment shows the preferences for the first header.
 */
public final class BodyTempDetailsPreferenceFragmentActivity extends PreferenceFragment {

    
    private static final String TAG = "BodyTempDetailsPreference";
  
    private static final boolean D = true;
    
   
    
    // Message types sent from the BluetoothServer Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothServer Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String DEVICE_MAC_ADDRESS = "device_mac_address";
    public static final String TOAST = "toast";

   

    


    
    // Layout Views
    //private ListView mConversationView;
    //private EditText mOutEditText;
    //private Button mSendButton;

  
   
    List<String> lstDevices = new ArrayList<String>();
    List<String> lstConnectedDevices = new ArrayList<String>();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make sure default values are applied.  In a real app, you would
        // want this in a shared function that is used to retrieve the
        // SharedPreferences wherever they are needed.
        //PreferenceManager.setDefaultValues(getActivity(),
          //      R.xml.advanced_preferences, false);

        // Load the preferences from an XML resource

        SharedPreferences prefs =PreferenceManager.getDefaultSharedPreferences(getActivity()) ;

        
        addPreferencesFromResource(R.xml.router_details_preference);
        //device id
        Preference DeviceIDPreference = new Preference(getActivity(),null);
	        
        DeviceIDPreference.setSelectable(false);
        DeviceIDPreference.setKey(ConstDef.SENSORINFO_SENSORID);
        DeviceIDPreference.setTitle(getString(R.string.sensorinfo_sensorid));
        DeviceIDPreference.setSummary(prefs.getString(ConstDef.SENSORINFO_SENSORID, ""));
        getPreferenceScreen().addPreference(DeviceIDPreference);
       
        //device sv
        Preference DeviceSVPreference = new Preference(getActivity(),null);
        
        DeviceSVPreference.setSelectable(false);
        DeviceSVPreference.setKey(ConstDef.SENSORINFO_SYSTEMVERSION);
        DeviceSVPreference.setTitle(getString(R.string.sensorinfo_systemversion));
        DeviceSVPreference.setSummary(prefs.getString(ConstDef.SENSORINFO_SYSTEMVERSION, ""));
        getPreferenceScreen().addPreference(DeviceSVPreference);
        
        //device mac
        Preference DeviceMACPreference = new Preference(getActivity(),null);
        
        DeviceMACPreference.setSelectable(false);
        DeviceMACPreference.setKey(ConstDef.SENSORINFO_WIRELESSDEVICEMAC);
        DeviceMACPreference.setTitle(getString(R.string.sensorinfo_wirelessdevicemac));
        DeviceMACPreference.setSummary(prefs.getString(ConstDef.SENSORINFO_WIRELESSDEVICEMAC, ""));
        getPreferenceScreen().addPreference(DeviceMACPreference);

        setHasOptionsMenu(true);
        


        
    }
   
   


}
