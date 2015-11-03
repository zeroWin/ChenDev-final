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
public final class RouterDetailsPreferenceFragmentActivity extends PreferenceFragment {

    
    private static final String TAG = "RouterDetailsPreference";
    private static final boolean D = true;
    

  
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
        DeviceIDPreference.setKey(ConstDef.GWINFO_MOBMEDGWID);
        DeviceIDPreference.setTitle(getString(R.string.gwinfo_mobmedgwid));
        DeviceIDPreference.setSummary(prefs.getString(ConstDef.GWINFO_MOBMEDGWID, ""));
        getPreferenceScreen().addPreference(DeviceIDPreference);
        
        //device sv
        Preference DeviceSVPreference = new Preference(getActivity(),null);
        
        DeviceSVPreference.setSelectable(false);
        DeviceSVPreference.setKey(ConstDef.GWINFO_SYSTEMVERSION);
        DeviceSVPreference.setTitle(getString(R.string.gwinfo_systemversion));
        DeviceSVPreference.setSummary(prefs.getString(ConstDef.GWINFO_SYSTEMVERSION, ""));
        getPreferenceScreen().addPreference(DeviceSVPreference);
        
        //device mac
        Preference DeviceMACPreference = new Preference(getActivity(),null);
        
        DeviceMACPreference.setSelectable(false);
        DeviceMACPreference.setKey(ConstDef.GWINFO_WIRELESSDEVICEMAC);
        DeviceMACPreference.setTitle(getString(R.string.gwinfo_wirelessdevicemac));
        DeviceMACPreference.setSummary(prefs.getString(ConstDef.GWINFO_WIRELESSDEVICEMAC, ""));
        getPreferenceScreen().addPreference(DeviceSVPreference);
        
        //max notes
        Preference DeviceMaxNotesPreference = new Preference(getActivity(),null);
        
        DeviceMaxNotesPreference.setSelectable(false);
        DeviceMaxNotesPreference.setKey(ConstDef.GWINFO_MAXIMUMSENSORNUMBER);
        DeviceMaxNotesPreference.setTitle(getString(R.string.gwinfo_maximumsensornumber));
        DeviceMaxNotesPreference.setSummary(prefs.getString(ConstDef.GWINFO_MAXIMUMSENSORNUMBER, ""));
        getPreferenceScreen().addPreference(DeviceMaxNotesPreference);
        //notes
        Preference DeviceNotesPreference = new Preference(getActivity(),null);
        
        DeviceNotesPreference.setSelectable(false);
        DeviceNotesPreference.setKey(ConstDef.GWINFO_CONNECTEDSENSORNUMBER);
        DeviceNotesPreference.setTitle(getString(R.string.gwinfo_connectedsensornumber));
        DeviceNotesPreference.setSummary(prefs.getString(ConstDef.GWINFO_CONNECTEDSENSORNUMBER, ""));
        getPreferenceScreen().addPreference(DeviceNotesPreference);
        setHasOptionsMenu(true);
        


        
    }
   
   


}
