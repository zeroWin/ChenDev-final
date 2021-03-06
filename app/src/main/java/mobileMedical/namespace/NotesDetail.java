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
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import mobileMedical.Message.SensorInfoResult;

/**
 * This is the main Activity that displays the current chat session.
 */
public class NotesDetail extends Activity {
	private static final String TAG = "NotesDetail";

    private static final boolean D = true;

	private ArrayList<HashMap<String, Object>> listItems;   //存放文字、图片信息   
    private SimpleAdapter mConnectedMedicalNotesDetailAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getIntent().getExtras();
        
        SensorInfoResult sensorInfoResult = bundle.getParcelable(ConstDef.SINGLE_SENSOR_INFO_RESULTS);
        
        // Set up the window layout
        setContentView(R.layout.notesdetail);
     // Find and set up the ListView for connected devices
        listItems = new ArrayList<HashMap<String, Object>>();   
             
              
            HashMap<String, Object> map = new HashMap<String, Object>();      
            map.put("noteName", "ID");     //文字 
     /*       map.put("noteID", "BTM0604C2P");     //文字
*/          map.put("noteID",sensorInfoResult.GetSensorID());
            map.put("ItemImage", R.drawable.ic_launcher);//图片  
            map.put("ItemImage2", R.drawable.ic_launcher);//图片
            listItems.add(map);
            
            HashMap<String, Object> mapSN = new HashMap<String, Object>();
            mapSN.put("noteName", "无线设备协议版本");     //文字 
//            mapSN.put("noteID", "123456");     //文字
            mapSN.put("noteID", sensorInfoResult.GetWirelessDeviceProtVers()); 
            mapSN.put("ItemImage", R.drawable.ic_launcher);//图片  
            mapSN.put("ItemImage2", R.drawable.ic_launcher);//图片
            listItems.add(mapSN);
            
            HashMap<String, Object> mapVersion = new HashMap<String, Object>();
            mapVersion.put("noteName", "系统版本");     //文字 
            mapVersion.put("noteID", sensorInfoResult.GetSystemVersion());     //文字
            mapVersion.put("ItemImage", R.drawable.ic_launcher);//图片  
            mapVersion.put("ItemImage2", R.drawable.ic_launcher);//图片
            listItems.add(mapVersion);
            
            HashMap<String, Object> mapMac = new HashMap<String, Object>();
            mapMac.put("noteName", "MAC");     //文字 
            mapMac.put("noteID", sensorInfoResult.GetWirelessDeviceMAC());     //文字
            mapMac.put("ItemImage", R.drawable.ic_launcher);//图片  
            mapMac.put("ItemImage2", R.drawable.ic_launcher);//图片
            listItems.add(mapMac);
            

             
        //生成适配器的Item和动态数组对应的元素      
            mConnectedMedicalNotesDetailAdapter = new SimpleAdapter(this,listItems,//数据源       
            R.layout.gateway_detail_item,//ListItem的XML布局实现      
            //动态数组与ImageItem对应的子项              
            new String[] {"ItemImage", "noteName","noteID","ItemImage2"},       
            //ImageItem的XML文件里面的一个ImageView,两个TextView ID      
            new int[] {R.id.ItemImage, R.id.noteName,R.id.noteID,R.id.ItemImage2}); 
        ListView connectedGateWaysListView = (ListView) findViewById(R.id.connected_notes_detail);
        connectedGateWaysListView.setAdapter(mConnectedMedicalNotesDetailAdapter);
}
}