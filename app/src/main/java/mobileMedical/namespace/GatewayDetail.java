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

import mobileMedical.Message.GWInfoResult;

/**
 * This is the main Activity that displays the current chat session.
 */
public class GatewayDetail extends Activity {
	private static final String TAG = "GatewayDetail";

    private static final boolean D = true;

	private ArrayList<HashMap<String, Object>> listItems;   //存放文字、图片信息   
    private SimpleAdapter mConnectedMedicalGateWaysDetailAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        
        // Set up the window layout
        setContentView(R.layout.gatewaydetail);
        
        Bundle bundle = this.getIntent().getExtras();
        
        GWInfoResult gwInfoResult = bundle.getParcelable(ConstDef.GW_INFO_RESULTS);
        
     // Find and set up the ListView for connected devices
        listItems = new ArrayList<HashMap<String, Object>>();   
             
              
            HashMap<String, Object> map = new HashMap<String, Object>();      
            map.put("noteName", "ID");     //文字 
            map.put("noteID", gwInfoResult.GetMobMedGWID());     //文字
            map.put("ItemImage", R.drawable.ic_launcher);//图片  
            map.put("ItemImage2", R.drawable.ic_launcher);//图片
            listItems.add(map);
            
           /* HashMap<String, Object> mapSN = new HashMap<String, Object>();
            mapSN.put("noteName", "序列号");     //文字 
            mapSN.put("noteID", "123456");     //文字
            mapSN.put("ItemImage", R.drawable.ic_launcher);//图片  
            mapSN.put("ItemImage2", R.drawable.ic_launcher);//图片
            listItems.add(mapSN);*/
            
            HashMap<String, Object> mapVersion = new HashMap<String, Object>();
            mapVersion.put("noteName", "系统版本");     //文字 
            mapVersion.put("noteID", gwInfoResult.GetSystemVersion());     //文字
            mapVersion.put("ItemImage", R.drawable.ic_launcher);//图片  
            mapVersion.put("ItemImage2", R.drawable.ic_launcher);//图片
            listItems.add(mapVersion);
            
            HashMap<String, Object> mapMac = new HashMap<String, Object>();
            mapMac.put("noteName", "MAC");     //文字 
            mapMac.put("noteID", gwInfoResult.GetWirelessDeviceMAC());     //文字
            mapMac.put("ItemImage", R.drawable.ic_launcher);//图片  
            mapMac.put("ItemImage2", R.drawable.ic_launcher);//图片
            listItems.add(mapMac);
            
            HashMap<String, Object> mapMaxNotes = new HashMap<String, Object>();
            mapMaxNotes.put("noteName", "最大节点个数");     //文字 
            mapMaxNotes.put("noteID", Integer.toString(gwInfoResult.GetMaximumSensorNumber()));     //文字
            
            mapMaxNotes.put("ItemImage", R.drawable.ic_launcher);//图片  
            mapMaxNotes.put("ItemImage2", R.drawable.ic_launcher);//图片
            listItems.add(mapMaxNotes);
            
            HashMap<String, Object> mapNotes = new HashMap<String, Object>();
            mapNotes.put("noteName", "已连接节点个数");     //文字 
            mapNotes.put("noteID", Integer.toString(gwInfoResult.GetConnectedSensorNumber()));     //文字
            mapNotes.put("ItemImage", R.drawable.ic_launcher);//图片  
            mapNotes.put("ItemImage2", R.drawable.ic_launcher);//图片
            listItems.add(mapNotes);
             
        //生成适配器的Item和动态数组对应的元素      
            mConnectedMedicalGateWaysDetailAdapter = new SimpleAdapter(this,listItems,//数据源       
            R.layout.gateway_detail_item,//ListItem的XML布局实现      
            //动态数组与ImageItem对应的子项              
            new String[] {"ItemImage", "noteName","noteID","ItemImage2"},       
            //ImageItem的XML文件里面的一个ImageView,两个TextView ID      
            new int[] {R.id.ItemImage, R.id.noteName,R.id.noteID,R.id.ItemImage2}); 
        ListView connectedGateWaysListView = (ListView) findViewById(R.id.connected_gateway_detail);
        connectedGateWaysListView.setAdapter(mConnectedMedicalGateWaysDetailAdapter);
}
}