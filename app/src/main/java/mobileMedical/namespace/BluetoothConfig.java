package mobileMedical.namespace;
//Copy form the Android bluetooth samples code: BluetoothChat.java
/*
 * Copyright (C) 2009 The Android Open Source Project
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


//import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mobileMedical.Message.GWInfoResult;
import mobileMedical.Message.SensorInfoResult;

/**
 * This is the main Activity that displays the current chat session.
 */
public class BluetoothConfig extends Activity {
    // Debugging
    private static final String TAG = "BluetoothVitalSignsMeas";
    private static final String  noconnected = "没有蓝牙连接";
    private static final boolean D = true;
    
    private static boolean devicesNotFound = true;
    private static boolean deviceConnected = false;
    private static boolean gwStateQuried = false;
    
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

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    

    
    // Layout Views
    //private ListView mConversationView;
    //private EditText mOutEditText;
    //private Button mSendButton;
	public static final int MESSAGE_RESULTS = 1;
	public static final int MESSAGE_PROCESSED = 2;
	
    // Connection Type
    private boolean mSecure = true;    
    // Name of the connected device
    private String mConnectedDeviceStr = "已连接的蓝牙设备";
    // Array adapter for the conversation thread
    //private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    //private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    //private BluetoothServer mBluetoothService = null;

    private ArrayAdapter<String> mPairedMedicalGateWaysArrayAdapter;
    private ArrayAdapter<String> mNewMedicalGateWaysArrayAdapter;
    private ArrayList<HashMap<String, Object>> listItems;   //存放文字、图片信息   
    private SimpleAdapter mConnectedMedicalGateWaysArrayAdapter;
    List<String> lstDevices = new ArrayList<String>();
    List<String> lstConnectedDevices = new ArrayList<String>();
    
    private static SensorInfoResult[]  sensorInfoItemResults = null;
    private static GWInfoResult gwInfoResult = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        
        // Set up the window layout
        setContentView(R.layout.bt_config);
        int height = this.getWindowManager().getDefaultDisplay().getHeight()/12;
        
        LinearLayout mLayout = (LinearLayout)findViewById(R.id.btConfigConnectedTitleLayout);
        mLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, height));
        
        mLayout = (LinearLayout)findViewById(R.id.btConfigConnectedLayout);
        mLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, height));
        
        mLayout = (LinearLayout)findViewById(R.id.btConfigConnectedNotesTitleLayout);
        mLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, height));
        
        mLayout = (LinearLayout)findViewById(R.id.btConfigConnectedNotesLayout);
        mLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, height*3));

        mLayout = (LinearLayout)findViewById(R.id.linearLayoutNewGWTitle);
        mLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, height));
        
        mLayout = (LinearLayout)findViewById(R.id.linearLayoutNewGW);
        mLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, height*3));
        
        mLayout = (LinearLayout)findViewById(R.id.btConfigSearchLayout);
        mLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, height));
        
        // Initialize the button to perform device discovery
        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
                //v.setVisibility(View.GONE);
            }
        });
        
     // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        mPairedMedicalGateWaysArrayAdapter = new ArrayAdapter<String>(this, R.layout.medicalgw_name);
        mNewMedicalGateWaysArrayAdapter = new ArrayAdapter<String>(this, R.layout.medicalgw_name);
        //mConnectedMedicalGateWaysArrayAdapter = new ArrayAdapter<String>(this, R.layout.notes_name);

        

        // Find and set up the ListView for newly discovered devices
        ListView newMedicalGateWaysListView = (ListView) findViewById(R.id.new_MedicalGateWays);
        newMedicalGateWaysListView.setAdapter(mNewMedicalGateWaysArrayAdapter);
        newMedicalGateWaysListView.setOnItemClickListener(mDeviceClickListener);
        //findViewById(R.id.title_new_MedicalGateWays).setVisibility(View.VISIBLE);
        listItems = new ArrayList<HashMap<String, Object>>(); 
     
        //生成适配器的Item和动态数组对应的元素      
            mConnectedMedicalGateWaysArrayAdapter = new SimpleAdapter(this,listItems,//数据源       
            R.layout.notes_name,//ListItem的XML布局实现      
            //动态数组与ImageItem对应的子项              
            new String[] {"ItemImage", "noteName","noteID","ItemImage2"},       
            //ImageItem的XML文件里面的一个ImageView,两个TextView ID      
            new int[] {R.id.ItemImage, R.id.noteName,R.id.noteID,R.id.ItemImage2});      
        ListView connectedNotesListView = (ListView) findViewById(R.id.connected_notes_list);
        connectedNotesListView.setAdapter(mConnectedMedicalGateWaysArrayAdapter);
        connectedNotesListView.setOnItemClickListener(mDeviceDisconnectClickListener);
        findViewById(R.id.title_connected_MedicalGateWays).setVisibility(View.VISIBLE);
        
        connectedNotesListView.setOnItemClickListener(new OnItemClickListener(){    
            @Override    
            public void onItemClick(AdapterView<?> parent, View view,    
                    int position, long id) {
            	Intent noteDetailIntent = new Intent();
            	noteDetailIntent.setClass(BluetoothConfig.this, NotesDetail.class);
            	 
            	Bundle bundle = new Bundle();

            	bundle.putParcelable(ConstDef.SINGLE_SENSOR_INFO_RESULTS, sensorInfoItemResults[position]); 
            	noteDetailIntent.putExtras(bundle);  
            	 startActivity(noteDetailIntent);    
            	
            	
            	/* Intent newIntent = new Intent();  	
            	 newIntent.putExtra(ConstDef.CMD, "dev");  
   				 newIntent.setAction("android.intent.action.gwStateQueryRequest");  
   	             sendBroadcast(newIntent);*/
            }    
        });
        /* 取得Intent中的Bundle对象 */
        Intent intent = this.getIntent();
        Bundle bunde = intent.getExtras();

     // Initialize the button to perform bluetooth connection 
        ImageButton gatewayDetailButton = (ImageButton) findViewById(R.id.imageButtonRouterDetail);
        gatewayDetailButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent gatewayDetailIntent = new Intent();
            	gatewayDetailIntent.setClass(BluetoothConfig.this, GatewayDetail.class);
            	 
            	Bundle bundle = new Bundle();
            	bundle.putParcelable(ConstDef.GW_INFO_RESULTS, gwInfoResult);            	
            	gatewayDetailIntent.putExtras(bundle);  
            	 startActivity(gatewayDetailIntent);
            	 //startActivityForResult(bluetoothConfigIntent,0);  
            }
        });
        
        gatewayDetailButton.setVisibility(View.INVISIBLE);
        TextView gatewayConnected = (TextView) findViewById(R.id.title_connected_MedicalGateWaysNameString);
        gatewayConnected.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent();//创建Intent对象  
                intent.setAction(ConstDef.CMD_BROADCAST_MESSAGE);  
                intent.putExtra(ConstDef.CMD, ConstDef.CMD_STOP_SERVICE);  
                sendBroadcast(intent);  
            }
        });
        gatewayConnected.setVisibility(View.INVISIBLE);
        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
        
     // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter);

        filter = new IntentFilter(ConstDef.BT_CONNECT_BROADCAST_MESSAGE);        
        this.registerReceiver(mReceiver,filter);  
        
        
        filter = new IntentFilter(ConstDef.GW_STATE_QUERY_RESULTS_BROADCAST_MESSAGE);        
        this.registerReceiver(mReceiver,filter);  
        
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        
    }
    
    private void doDiscovery() {

        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.bt_state_searching);

        // Turn on sub-title for new devices
        findViewById(R.id.title_new_MedicalGateWays).setVisibility(View.VISIBLE);

        // If we're already discovering, stop it
        if (mBluetoothAdapter.isDiscovering()) {
        	mBluetoothAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBluetoothAdapter.startDiscovery();
    }
    
    @Override
    public void onStart() {
        super.onStart();

        // If BT is not on, request that it be enabled.
        // setupBluetooth() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } 
    }

    @Override
    public synchronized void onResume() {
        super.onResume();

      

    }
  
 
    public boolean onKeyDown(int kCode, KeyEvent kEvent) {
        switch (kCode) {
        
       
        case KeyEvent.KEYCODE_BACK: {
        	Intent data = new Intent();
    		data.putExtra("dev",mConnectedDeviceStr);
    		
    		 
    		//BluetoothConfig.this.setResult(RESULT_OK, data);
    		BluetoothConfig.this.getParent().setResult(RESULT_OK, data);
    		
    		BluetoothConfig.this.finish();
            return true;
        }
        }
        return super.onKeyDown(kCode, kEvent);
    }
    @Override
    public synchronized void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
    	
        super.onDestroy();
        
        // Make sure we're not doing discovery anymore
        if (mBluetoothAdapter != null) {
        	mBluetoothAdapter.cancelDiscovery();
        }
        
        
        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);

    }

    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        /*case REQUEST_CONNECT_DEVICE_SECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, true);
            }
            break;
        case REQUEST_CONNECT_DEVICE_INSECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, false);
            }
            break;
            */
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
            } else {
                // User did not enable Bluetooth or an error occurred
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent serverIntent = null;
        switch (item.getItemId()) {
        case R.id.secure_connect_scan:
        	mSecure = true;
            // Launch the DeviceListActivity to see devices and do scan
            //serverIntent = new Intent(this, DeviceListActivity.class);
            //startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
            return true;
        case R.id.insecure_connect_scan:
            // Launch the DeviceListActivity to see devices and do scan
            //serverIntent = new Intent(this, DeviceListActivity.class);
            //startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
        	mSecure = false;
            return true;
        case R.id.discoverable:
            // Ensure this device is discoverable by others
            ensureDiscoverable();
            return true;
        }
        return false;
    }
    
 // The on-click listener for all devices in the ListViews
    private OnItemClickListener mPairedDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
        	if(deviceConnected == true)
        	{
        		Toast.makeText(getApplicationContext(), "已有蓝牙连接,无法建立新蓝牙连接！", Toast.LENGTH_SHORT).show();
        		return;
        	}
            // Cancel discovery because it's costly and we're about to connect
        	mBluetoothAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            mConnectedDeviceStr = info;
            String macAddress = info.substring(info.length() - 17);   
            Intent intent = new Intent(BluetoothConfig.this,BluetoothServer.class); 
            Bundle bundle = new Bundle();
            bundle.putString(DEVICE_MAC_ADDRESS, macAddress);
            intent.putExtras(bundle);
            startService(intent);  
        }
    };
    
 // The on-click listener for all devices in the ListViews
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
        	if(devicesNotFound == true)
        	{
        		if(deviceConnected == true)
            	{
            		Toast.makeText(getApplicationContext(), "已有蓝牙连接,无法建立新蓝牙连接！", Toast.LENGTH_SHORT).show();
            		return;
            	}
            // Cancel discovery because it's costly and we're about to connect
        	mBluetoothAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            mConnectedDeviceStr = info;
            String macAddress = info.substring(info.length() - 17);   
            Intent intent = new Intent(BluetoothConfig.this,BluetoothServer.class); 
            Bundle bundle = new Bundle();
            bundle.putString(DEVICE_MAC_ADDRESS, macAddress);
            intent.putExtras(bundle);
            startService(intent);  
        	}
        	else
        	{
        		//String noDevices = getResources().getText(R.string.bt_title_no_device).toString();
                //mNewMedicalGateWaysArrayAdapter.remove(noDevices);
        		//doDiscovery();
                //v.setVisibility(View.GONE);
        	}
        }
    };
 // The on-click listener for all devices in the ListViews
    private OnItemClickListener mDeviceDisconnectClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
        	Intent stopServiceintent = new Intent();//创建Intent对象  
        	stopServiceintent.setAction(ConstDef.CMD_BROADCAST_MESSAGE);  
        	stopServiceintent.putExtra(ConstDef.CMD, ConstDef.CMD_STOP_SERVICE);  
              
            sendBroadcast(stopServiceintent);//发送广播     
            /*deviceConnected = false;
            mConnectedMedicalGateWaysArrayAdapter.remove(mConnectedDeviceStr);
            
            mConnectedMedicalGateWaysArrayAdapter.add(noconnected); */ 
            
        }
        	
        };
        
      
    
 // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            	devicesNotFound = true;
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                	String str = device.getName() + "\n" + device.getAddress(); 
                    if (lstDevices.indexOf(str) == -1)// 防止重复添加 
                    {
                       mNewMedicalGateWaysArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                       lstDevices.add(str); 
                    }
                }
            // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.bt_state_searched);
                if (mNewMedicalGateWaysArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.bt_title_no_device).toString();
                    mNewMedicalGateWaysArrayAdapter.add(noDevices);
                    devicesNotFound = false;
                }
                
            }
            else if(BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)){ 
            	Intent intentss = new Intent();//创建Intent对象  
                intentss.setAction(ConstDef.CMD_BROADCAST_MESSAGE);  
                intentss.putExtra(ConstDef.CMD, ConstDef.CMD_STOP_SERVICE);  
                  
                sendBroadcast(intentss);//发送广播     
                deviceConnected = false;
                
                	//mConnectedMedicalGateWaysArrayAdapter.remove(mConnectedDeviceStr);
                    //mConnectedMedicalGateWaysArrayAdapter.add(noconnected); 
                    lstConnectedDevices.remove(mConnectedDeviceStr);
                    lstConnectedDevices.add(noconnected);
                    TextView gatewayConnected = (TextView) findViewById(R.id.title_connected_MedicalGateWaysNameString);
                    
                    gatewayConnected.setVisibility(View.INVISIBLE);
                    ImageButton gatewayDetailButton = (ImageButton) findViewById(R.id.imageButtonRouterDetail);
                    gatewayDetailButton.setVisibility(View.INVISIBLE);
                mConnectedDeviceStr = noconnected;
                
                } 
            else if(action.equals(ConstDef.GW_STATE_QUERY_RESULTS_BROADCAST_MESSAGE))
            {

            	
//            	ArrayList<SensorInfoResult>  sensorInfoItemResults = new ArrayList<SensorInfoResult>();
            	gwInfoResult = intent.getParcelableExtra(ConstDef.GW_INFO_RESULTS);
            	ArrayList<SensorInfoResult> sensorInfoItemResultsList =   intent.getParcelableArrayListExtra(ConstDef.SENSOR_INFO_RESULTS);
            	if(sensorInfoItemResultsList != null && !sensorInfoItemResultsList.isEmpty())
           	   {
           		//   Object[] tmpObjects  = (measItemResultsList.toArray());
           		   int listSize = sensorInfoItemResultsList.size(); 
           		   sensorInfoItemResults =  (SensorInfoResult[])sensorInfoItemResultsList.toArray(new SensorInfoResult[listSize] );
           	   }
                
            	 Intent resultDislayedInformIntent = new Intent();  
    		     resultDislayedInformIntent.putExtra(ConstDef.CMD, ConstDef.MEAS_RETS_DISPLAY_STATE);  
    		     resultDislayedInformIntent.putExtra(ConstDef.RESULTS_DISPLAYED, true); 				             
    		     resultDislayedInformIntent.setAction(ConstDef.MEAS_RESULTS_DISPLAY_STATE_BROADCAST_MESSAGE);  
	             sendBroadcast(resultDislayedInformIntent);
	             
               /* HashMap<String, Object> map = new HashMap<String, Object>();      
                map.put("noteName", "体温");     //文字 
                map.put("noteID", "BTM0604C2P");     //文字
                map.put("ItemImage", R.drawable.ic_launcher);//图片  
                map.put("ItemImage2", R.drawable.ic_launcher);//图片
                listItems.add(map);
                ListView connectedGateWaysListView = (ListView) findViewById(R.id.connected_notes_list);
                connectedGateWaysListView.setAdapter(mConnectedMedicalGateWaysArrayAdapter);
                
                TextView gatewayConnected = (TextView) findViewById(R.id.title_connected_MedicalGateWaysNameString);
                gatewayConnected.setText("BTM0604C2P");
                gatewayConnected.setVisibility(View.VISIBLE);
                ImageButton gatewayDetailButton = (ImageButton) findViewById(R.id.imageButtonRouterDetail);
                gatewayDetailButton.setVisibility(View.VISIBLE);*/
                
            }
            else if(action.equals(ConstDef.BT_CONNECT_BROADCAST_MESSAGE)){  
                Bundle bundle = intent.getExtras();  
                int cmd = bundle.getInt(ConstDef.CMD);  
                  
                if(cmd == ConstDef.CMD_SHOW_TOAST){  
                    String str = bundle.getString(ConstDef.STRING_INFO);  
                    //Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();      
                }
                else if(cmd == ConstDef.CMD_SHOW_TITLE){
                	 String str = bundle.getString(ConstDef.STRING_INFO);  
                	 setTitle(str);
                }
                else if(cmd == ConstDef.CMD_RECEIVED_DATA){
                	int datasize = bundle.getInt(ConstDef.RESULTS_SIZE);
                	byte[] buff = bundle.getByteArray(ConstDef.RESULTS);
                }
                  
                else if(cmd == ConstDef.CMD_SYSTEM_EXIT){  
                    System.exit(0);  
                } 
                else if (cmd == ConstDef.CMD_SET_CONNECTED_DEVICE)
                {
                 // Send GW Query to MobileMedicalActivity
                 Intent gwQueryIntent = new Intent();  				             
                 gwQueryIntent.setAction(ConstDef.GW_STATE_QUERY_REQ_BROADCAST_MESSAGE);  
   	             sendBroadcast(gwQueryIntent);
   	             //
   	             
   	          HashMap<String, Object> map = new HashMap<String, Object>();      
              map.put("noteName", "体温");     //文字 
              map.put("noteID", "BTM0604C2P");     //文字
              map.put("ItemImage", R.drawable.ic_launcher);//图片  
              map.put("ItemImage2", R.drawable.ic_launcher);//图片
              listItems.add(map);
              ListView connectedGateWaysListView = (ListView) findViewById(R.id.connected_notes_list);
              connectedGateWaysListView.setAdapter(mConnectedMedicalGateWaysArrayAdapter);
              
              TextView gatewayConnected = (TextView) findViewById(R.id.title_connected_MedicalGateWaysNameString);
              gatewayConnected.setText("BTM0604C2P");
              gatewayConnected.setVisibility(View.VISIBLE);
              ImageButton gatewayDetailButton = (ImageButton) findViewById(R.id.imageButtonRouterDetail);
              gatewayDetailButton.setVisibility(View.VISIBLE);
   	             //
   	             
   	             
                	//mConnectedMedicalGateWaysArrayAdapter.remove(noconnected);
                	//mConnectedMedicalGateWaysArrayAdapter.add(mConnectedDeviceStr);
                	lstConnectedDevices.remove(noconnected);
                    lstConnectedDevices.add(mConnectedDeviceStr);
                	deviceConnected = true;
                	// Find and set up the ListView for connected devices
                      
                     
                        
                	
                	
                }
            }
        }
    };

    
  
}
     
