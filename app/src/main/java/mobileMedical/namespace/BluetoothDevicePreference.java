/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mobileMedical.namespace;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import mobileMedical.namespace.R;


/**
 * BluetoothDevicePreference is the preference type used to display each remote
 * Bluetooth device in the Bluetooth Settings screen.
 */
public final class BluetoothDevicePreference extends Preference implements
	OnClickListener {
    private static final String TAG = "BluetoothDevicePreference";

    private ImageView deviceIcon;
    private ImageView deviceDetail;
    private ImageView divd;
    private TextView  deviceName;
    private boolean isVisiable;

    
 // Key names received from the BluetoothServer Handler

    


    private OnClickListener mOnSettingsClickListener;

    private String mDeviceName;
    private int mDeviceID;

    public BluetoothDevicePreference(Context context, String DeviceName,boolean visiable,int deviceID) {
        super(context);


        isVisiable = visiable;
        mDeviceName = DeviceName;
        mDeviceID = deviceID;
        setWidgetLayoutResource(R.layout.device_prefrence);
        
    }

    

    public void setOnSettingsClickListener(OnClickListener listener) {
        mOnSettingsClickListener = listener;
    }


    @Override
    protected void onBindView(View view) {
    	
	    	deviceIcon = (ImageView) view.findViewById(R.id.routerIcon);
	        

            deviceDetail = (ImageView) view.findViewById(R.id.deviceDetails);
            if (deviceDetail != null) {
                deviceDetail.setOnClickListener(this);
                

            }
            
            deviceName = (TextView) view.findViewById(R.id.routerNameValue);
            if (deviceName != null) {
            	deviceName.setText(mDeviceName);
            	
            	//deviceName.setOnClickListener(this);
            	
            }
            
            divd = (ImageView) view.findViewById(R.id.divider);
        if(isVisiable == false)
        {
        	setInvisible();
        }
            
        super.onBindView(view);
    }

    public void setInvisible()
    {
    	if (deviceIcon != null) {
        	
        	deviceIcon.setVisibility(View.INVISIBLE);

        }
    	
		if (deviceName != null) {
		        	
			deviceName.setVisibility(View.INVISIBLE);
		
		        }
		if (deviceDetail != null) {
		        	
			deviceDetail.setVisibility(View.INVISIBLE);
		
		        }
		if (divd != null) {
            
        	divd.setVisibility(View.INVISIBLE);

        }
    }
    
    public void setName(String str)
    {
    	if (deviceIcon != null) {
        	
        	deviceIcon.setVisibility(View.VISIBLE);

        }
    	
		if (deviceDetail != null) {
		        	
			deviceDetail.setVisibility(View.VISIBLE);
		
		        }
		if (deviceName != null) {
		        	
			deviceName.setVisibility(View.VISIBLE);
			deviceName.setText(str);
		
		        }
		if (divd != null) {
            
        	divd.setVisibility(View.VISIBLE);

        }
    }
    

    public void onClick(View v) {
        // Should never be null by construction
        if (mOnSettingsClickListener != null) {
            mOnSettingsClickListener.onClick(v);
        }
        else
        {
        	Bundle arg = new Bundle();
        	arg.putInt("DEVICE_ID", mDeviceID);
        	switch(mDeviceID)
        	{
        	case ConstDef.DEVICE_ROUTER:
        		((PreferenceActivity) this.getContext()).startPreferencePanel(RouterDetailsPreferenceFragmentActivity.class.getName(),arg, 0, getContext().getResources().getString(R.string.gw_info), null, 0);
        	    break;
        	case ConstDef.DEVICE_SENSOR:
        		((PreferenceActivity) this.getContext()).startPreferencePanel(BodyTempDetailsPreferenceFragmentActivity.class.getName(),arg, 0, getContext().getResources().getString(R.string.sensor_info), null, 0);
        	    break;
        	case ConstDef.DEVICE_BLUETOOTH:
                String[] infos = deviceName.getText().toString().split(new String("\\n"));

                String macAddress = infos[1];   
                String deviceName = infos[0];
                Intent intent = new Intent(this.getContext(),BluetoothServer.class); 
                Bundle bundle = new Bundle();
                bundle.putString(ConstDef.DEVICE_MAC_ADDRESS, macAddress);
                bundle.putString(ConstDef.DEVICE_NAME, deviceName);
                
                intent.putExtras(bundle);
                this.getContext().startService(intent);
        	}
        	}
    }


 
    void onClicked() {
    }

  

}
