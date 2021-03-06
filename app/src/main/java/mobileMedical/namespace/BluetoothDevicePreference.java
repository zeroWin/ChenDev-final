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


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;


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

    /**
     * 返回蓝牙设备的设置，默认可见
     * @param context
     * @param DeviceName
     * @param visiable
     * @param deviceID
     */
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

    /**
     * 初始化关联layout
     * @param view
     */
    @Override
    protected void onBindView(View view) {
    	    //初始化蓝牙设备的图标
	    	deviceIcon = (ImageView) view.findViewById(R.id.routerIcon);
	        
            //初始化蓝牙设备后面的连接按钮
            deviceDetail = (ImageView) view.findViewById(R.id.deviceDetails);
            if (deviceDetail != null) {
                deviceDetail.setOnClickListener(this);
                

            }
            //初始化蓝牙设备名称
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

    /**
     * 设为不可见
     */
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

    /**
     * 设置蓝牙名称
     * @param str
     */
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

    /**
     * v的点击事件
     * @param v
     */
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
        	case ConstDef.DEVICE_ROUTER:    //获取网关信息
        		((PreferenceActivity) this.getContext()).startPreferencePanel(RouterDetailsPreferenceFragmentActivity.class.getName(),arg, 0, getContext().getResources().getString(R.string.gw_info), null, 0);
        	    break;
        	case ConstDef.DEVICE_SENSOR:        //获取节点信息
        		((PreferenceActivity) this.getContext()).startPreferencePanel(BodyTempDetailsPreferenceFragmentActivity.class.getName(),arg, 0, getContext().getResources().getString(R.string.sensor_info), null, 0);
        	    break;
        	case ConstDef.DEVICE_BLUETOOTH:     //获取蓝牙信息
                String[] infos = deviceName.getText().toString().split(new String("\\n"));

                String macAddress = infos[1];   
                String deviceName = infos[0];
                Intent intent = new Intent(this.getContext(),BluetoothServer.class); 
                Bundle bundle = new Bundle();
                bundle.putString(ConstDef.DEVICE_MAC_ADDRESS, macAddress);  //蓝牙地址
                bundle.putString(ConstDef.DEVICE_NAME, deviceName);         //蓝牙名称
                
                intent.putExtras(bundle);
                this.getContext().startService(intent);
        	}
        	}
    }


 
    void onClicked() {
    }

  

}
