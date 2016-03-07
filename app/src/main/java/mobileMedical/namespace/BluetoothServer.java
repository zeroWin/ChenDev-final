package mobileMedical.namespace;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import mobileMedical.Message.BodyTempSensorCalResult;
import mobileMedical.Message.GWInfoResult;
import mobileMedical.Message.GWStateRetMessage;
import mobileMedical.Message.MeasItemResult;
import mobileMedical.Message.MessageBase;
import mobileMedical.Message.MessageInfo;
import mobileMedical.Message.SensorInfoResult;
  
  
public class BluetoothServer extends Service{  
  
    public boolean threadFlag = true;  
    BluetoothThread mBluetoothThread;  
    CommandReceiver cmdReceiver;//继承自BroadcastReceiver对象，用于得到Activity发送过来的命令  
  
    private MessageProcess  mMessageProcess = null;
    

      
	
	

	private float[] mMeasResultsBuff = new float[1024];
	private int     mMeasResultsLen = -1;
	private boolean mNewMeasResult = false; 

	 private static final String TAG = "BTServer";
	private int mMsgType = -1;
	private int mMsgSensorType = -1;
	private Object mResultsObject = null;
	
     private BluetoothAdapter mBluetoothAdapter = null;  
     private BluetoothSocket btSocket = null;  
     private OutputStream outStream = null;  
     private InputStream  inStream = null;  
     public  boolean bluetoothFlag  = true;  
     private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); 
    // private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
     private static String address = null; // <==要连接的蓝牙设备MAC地址  
     private String deviceName = null;
     private static volatile boolean readByteFlag = true;
     
    @Override  
    public IBinder onBind(Intent intent) {  
        // TODO Auto-generated method stub  
        return null;  
    }  
  
    @Override  
    public void onCreate() {  
        // TODO Auto-generated method stub  
        super.onCreate();  
         
    }  
      
      
      
    //前台Activity调用startService时，该方法自动执行  
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
    	Bundle bunde = intent.getExtras();
    	deviceName = bunde.getString(ConstDef.DEVICE_NAME);	//获取蓝牙设备名
    	
    	address = bunde.getString(ConstDef.DEVICE_MAC_ADDRESS);		//获取蓝牙mac地址
        // TODO Auto-generated method stub  
    	mMessageProcess = new MessageProcess(getApplicationContext(),mHandler);
    	 
        cmdReceiver = new CommandReceiver();  
        IntentFilter filter = new IntentFilter(ConstDef.CMD_BROADCAST_MESSAGE);//创建IntentFilter对象  


        //注册Broadcast Receiver  
        registerReceiver(cmdReceiver, filter);  
        
        filter = new IntentFilter(ConstDef.MEAS_REQ_BROADCAST_MESSAGE);        
        registerReceiver(cmdReceiver,filter);
        
        filter = new IntentFilter(ConstDef.GW_STATE_QUERY_REQ_BROADCAST_MESSAGE);        
        registerReceiver(cmdReceiver,filter);
        
        filter = new IntentFilter(ConstDef.BODYTEMP_CAL_COEFF_CONFIG_REQ_BROADCAST_MESSAGE);        
        registerReceiver(cmdReceiver,filter);
        
        filter = new IntentFilter(ConstDef.BODYTEMP_CAL_REQ_BROADCAST_MESSAGE);        
        registerReceiver(cmdReceiver,filter);
        
        filter = new IntentFilter(ConstDef.SN_CONFIG_REQ_BROADCAST_MESSAGE);        
        registerReceiver(cmdReceiver,filter);
        
        filter = new IntentFilter(ConstDef.MEAS_STOP_REQ_BROADCAST_MESSAGE);        
        registerReceiver(cmdReceiver,filter);
        
        filter = new IntentFilter(ConstDef.MEAS_RESULTS_DISPLAY_STATE_BROADCAST_MESSAGE);        
        registerReceiver(cmdReceiver,filter);
        
        filter = new IntentFilter(ConstDef.ALARM_TIME_REQ_BROADCAST_MESSAGE);        
        registerReceiver(cmdReceiver,filter);
        
        doJob();//调用方法启动线程  
        return super.onStartCommand(intent, flags, startId);  
  
    }  
      
      
  
    @Override  
    public void onDestroy() {  
        // TODO Auto-generated method stub  
         
        this.unregisterReceiver(cmdReceiver);//取消注册的CommandReceiver  
        threadFlag = false;  
        boolean retry = true;  
        while(retry){  
            try{   
                 mBluetoothThread.join();  
                 retry = false;  
            }catch(Exception e){  
                e.printStackTrace();  
            }  
              
        } 
        super.onDestroy(); 
    }  
      
	public class BluetoothThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			
			byte[] buff = new byte[1024];
			byte[] outbuff = new byte[2048];
			byte[] tempbuff = new byte[2048];
			ByteBuffer swepByteBuffer = null;
			byte[] HeaderBytes = {0x2E, 0x1F};

			byte[] EndBytes = {0x3D,0x4B};
			
			byte[] resultsSizeBuff = new byte[2];
			int resultsBuffSize = 0;
			int outbuffIndex = 0;
			int outbuffCount = 0;
			int tempBuffIndex = 0;
			int tempBuffCount = 0;
			int headerIdx = -1;
			int endIdx = -1;
			String TAGString= "HeaderEndError";
			while (threadFlag) {
				if (readByteFlag) {
					int value = readByte(buff);
					if (value != -1) {
						/*if (tempBuffCount ==0)
						{
						tempBuffIndex = 0;
						}
						else
						{
							tempBuffIndex = tempBuffCount -1;
						}*/
						System.arraycopy(buff, 0, tempbuff, tempBuffCount, value);
						if (BuildConfig.DEBUG) {
						Log.i("BYTELEN", String.valueOf(value));
						}
						tempBuffCount += value;
						// find the Header;
						if(headerIdx == -1)
						{
						headerIdx = ArrayFind(tempbuff,tempBuffCount,HeaderBytes, true);
						}
						if (headerIdx != -1)
						{		
							if(headerIdx!=0)
							{
								// Find the wrong Header but its position is not correct
							  Log.e(TAGString, "Wrong Header Posit:" + String.valueOf(headerIdx));	
							}
						}
						else if (outbuffCount >1024)
						{
							// Should has the header but not find
						   Log.e(TAGString, "Not Find Header" + String.valueOf(outbuffCount));
						}
						else {
							// may not enough data, continue read
						}
						
						// find the Ender;
						if(endIdx == -1)
						{
						endIdx = ArrayFind(tempbuff,tempBuffCount,EndBytes, false);
						}
						if (endIdx != -1)
						{
							// Found the End
							if (headerIdx == -1)
							{
								// Should found the header
								Log.e(TAGString, "Ender found, No found header " + String.valueOf(endIdx));
								// abandon the received data
								tempBuffCount = 0;
								headerIdx = -1;
								endIdx = -1;
							}
							else if(endIdx <= headerIdx)
							{
								Log.e(TAGString, "Header >= Ender" + String.valueOf(headerIdx) + String.valueOf(endIdx));
								// abandon the received data
								tempBuffCount = 0;
								headerIdx = -1;
								endIdx = -1;
							}
							else {
								// has one frame result
								outbuffCount = endIdx-headerIdx+1;
								System.arraycopy(tempbuff, headerIdx+2, resultsSizeBuff, 0, resultsSizeBuff.length);
								resultsBuffSize = MessageBase.InvBytesToShort(resultsSizeBuff);
								
								if((outbuffCount  % (resultsBuffSize + 6))== 0)
								{
								System.arraycopy(tempbuff, headerIdx, outbuff, 0, outbuffCount);
								int counter =  tempBuffCount-outbuffCount;
								swepByteBuffer = ByteBuffer.wrap(tempbuff, endIdx+1, counter);
								tempbuff = swepByteBuffer.array();
								tempBuffCount = counter;
								headerIdx = -1;
								endIdx = -1;
								if (BuildConfig.DEBUG) {
								Log.i("SendDataToProcess", String.valueOf(outbuffCount));
								}
								setReadByteFlag(false);
								returnData(outbuffCount, outbuff);
								}
								else {
									Log.e(TAGString, "Header to Ender:" + String.valueOf(outbuffCount) + "Indicated frameSize:" + String.valueOf(resultsBuffSize + 6));
									// The results frame is not correct.
									int counter =  tempBuffCount-outbuffCount;
									swepByteBuffer = ByteBuffer.wrap(tempbuff, endIdx+1, counter);
									tempbuff = swepByteBuffer.array();
									tempBuffCount = counter;
									headerIdx = -1;
									endIdx = -1;
								}
							}
								
						}
						else if (outbuffCount >1024)
						{
							// Should has the ender but not find
						   Log.e(TAGString, "Not Find Ender" + String.valueOf(outbuffCount));
						   if (headerIdx == -1)
						   {
							   // not find the header and ender, abandon the data			   
							   tempBuffCount = 0;
							   headerIdx = -1;
							   endIdx = -1;
						   }
						   else {
							// try Find the last header
							   headerIdx = ArrayFind(tempbuff,tempBuffCount,HeaderBytes, false); 				  
							   headerIdx -=HeaderBytes.length;
							   headerIdx +=1;
							   
							   int counter =  tempBuffCount-headerIdx;
								swepByteBuffer = ByteBuffer.wrap(tempbuff, headerIdx, counter);
								tempbuff = swepByteBuffer.array();
								tempBuffCount = counter;
								headerIdx = -1;
								endIdx = -1;
								   
						}
						}
						else {
							// may not enough data, continue read
						}
						

						
					
						}
				}

			
			}
		}

		/**
		 * 返回targetarray数组在array数组中的位置
		 * @param array
		 * @param counter
		 * @param targetarray
		 * @param fromStart
		 * @return
		 */
	  private int ArrayFind(byte[] array, int counter, byte[] targetarray, boolean fromStart)
	  {
		  int index = -1;
		  if (fromStart)
		  {
			  int targetarrayLen = targetarray.length;
			  byte[] subArray = new byte[targetarrayLen];
			  for (int i = 0; i<counter; i++)
			  {
				  if (i+targetarrayLen <= counter)
				  {
				  System.arraycopy(array, i, subArray, 0, targetarrayLen);
				  if(Arrays.equals(subArray, targetarray))
				  {
					  index = i;
					  break;
				  }
				  }
				  else
				  {
					  break;
				  }
			  }
		  }
		  else
		  {
			  int targetarrayLen = targetarray.length;
			  byte[] subArray = new byte[targetarrayLen];
			  for (int i = counter-1; i>=0; i--)
			  {
					if (i - targetarrayLen +1 >= 0) {
						System.arraycopy(array, i - targetarrayLen +1, subArray,
								0, targetarrayLen);

						if (Arrays.equals(subArray, targetarray)) {
							index = i;
							break;
						}
					} else {
						break;
					}
				}
		  }
		  return index;
	  }
	  
	 /* private  void reverse(byte[] array) {
	      if (array == null) {
	          return;
	      }
	      int i = 0;
	      int j = array.length - 1;
	      byte tmp;
	      while (j > i) {
	          tmp = array[j];
	          array[j] = array[i];
	          array[i] = tmp;
	          j--;
	          i++;
	      }
	  }*/


	}
      
    public synchronized void setReadByteFlag(boolean value)
    {
    	readByteFlag = value;
    }
    
    public void doJob(){      
         mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();  
        if (mBluetoothAdapter == null) {   
        	showToast("蓝牙设备不可用，请打开蓝牙！");  
            bluetoothFlag  = false;  
            return;  
        }  
  
        if (!mBluetoothAdapter.isEnabled()) {  
            DisplayToast("请打开蓝牙并重新运行程序！");  
            bluetoothFlag  = false;  
            stopService();  
            showToast("请打开蓝牙并重新运行程序！");  
            return;  
        }        
        showTitle("准备连接蓝牙设备!"); 
        connectDevice();// 连接蓝牙设备
        
        threadFlag = true;    
        mBluetoothThread = new BluetoothThread();  
        mBluetoothThread.start();  
          
    }  
    public  void connectDevice(){ 
    	
    	showTitle("正在连接蓝牙设备····");  
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);  
        try {  
           btSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID); 
          // btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {  
        	showToast("套接字创建失败！");  
            bluetoothFlag = false;  
        }  
        DisplayToast("成功连接蓝牙设备！");  
        mBluetoothAdapter.cancelDiscovery();  
        try {  
                btSocket.connect();  
                DisplayToast("连接成功建立，可以开始操控了!");  
                showToast("连接成功建立，可以开始操控了!");
                showTitle("连接成功");
                bluetoothFlag = true;
                Intent intent = new Intent();  
                intent.putExtra(ConstDef.CMD, ConstDef.CMD_SET_CONNECTED_DEVICE);  
                intent.putExtra(ConstDef.STRING_INFO, deviceName);
                intent.setAction(ConstDef.BT_CONNECT_BROADCAST_MESSAGE);  
                sendBroadcast(intent);
                
        } catch (IOException e) {  
               try {  
                    btSocket.close();  
                    bluetoothFlag = false; 
                    showToast("无法建立蓝牙连接,请重试！");
                    showTitle("无法建立蓝牙连接,请重试！");
                    DisplayToast("无法建立蓝牙连接,请重试！");  
                } catch (IOException e2) {                          
                   DisplayToast("连接没有建立，无法关闭套接字！");  
                }  
        }     
          
        if(bluetoothFlag){  
            try {  
                inStream = btSocket.getInputStream();  
              } catch (IOException e) {  
                  e.printStackTrace();  
              } //绑定读接口  
                
              try {  
                    outStream = btSocket.getOutputStream();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                } //绑定写接口  
              
        }  
    }

	/**
	 * 串口发送数据
	 * @param messageBuff
	 * @param messageSize
	 */
    public synchronized void sendCmd(byte[] messageBuff, int messageSize)//串口发送数据  
    {     
       /* if(!bluetoothFlag){  
            return;  
        }  */
            
          try {  
            outStream.write(messageBuff, 0, messageSize);  
            outStream.flush();  
          } catch (IOException e) {  
              e.printStackTrace();  
          }           
    }    
      
    public int readByte(byte[] buff){//return -1 if no data  
        int ret = -1;     
        if(!bluetoothFlag){  
            return ret;  
        }  
        try {  
              ret = inStream.read(buff);  
            } catch (IOException e) {  
              e.printStackTrace();  
            }              
        return ret;  
    }  
      
    public void stopService(){//停止服务      
        threadFlag = false;//停止线程  
        try{
           btSocket.close();
          } catch (IOException e2) {                          
             
          }  
          
         // this.unregisterReceiver(cmdReceiver);    
        stopSelf();//停止服务  
    }  
      
    public void showToast(String str){//显示提示信息  
        Intent intent = new Intent();  
        intent.putExtra(ConstDef.CMD, ConstDef.CMD_SHOW_TOAST);  
        intent.putExtra(ConstDef.STRING_INFO, str);  
        intent.setAction(ConstDef.BT_CONNECT_BROADCAST_MESSAGE);  
        sendBroadcast(intent);    
    }  
    
    public void showTitle(String str){//显示提示信息  
        Intent intent = new Intent();  
        intent.putExtra(ConstDef.CMD, ConstDef.CMD_SHOW_TITLE);  
        intent.putExtra(ConstDef.STRING_INFO, str);  
        intent.setAction(ConstDef.BT_CONNECT_BROADCAST_MESSAGE);  
        sendBroadcast(intent);    
    } 
    
    public void returnData(int datasize, byte[] buff){//显示提示信息  
      /*  Intent intent = new Intent();  
        intent.putExtra("cmd", ConstDef.CMD_RECEIVED_DATA);  
        intent.putExtra("ReceivedDataSize", datasize); 
        intent.putExtra("ReceivedDataBuff", buff); 
        intent.setAction(ConstDef.BT_RECEIVED_DATA_BROADCAST_MESSAGE);  
        sendBroadcast(intent);  */
        
     // Process received message
    	mMessageProcess.CopyReceviedMessageBuffer(buff, datasize);   
    } 
    
    public void DisplayToast(String str)  
    {  
    }

	/**
	 *接收Activity传送过来的命令
	 */
    private class CommandReceiver extends BroadcastReceiver{  
        @Override  
        public void onReceive(Context context, Intent intent) {  ;
            if(intent.getAction().equals(ConstDef.CMD_BROADCAST_MESSAGE)){  //获取命令信息
            	 Bundle bundle = intent.getExtras();  
            	 int cmd = bundle.getInt(ConstDef.CMD);  //获取command信息

            	/* if  (cmd == ConstDef.CMD_SET_READBYTE_FLAG){  
            
            		 boolean readByteFlag = bundle.getBoolean("readByte");
            		 setReadByteFlag(readByteFlag);
            	 }*/
                      
                  if(cmd == ConstDef.CMD_STOP_SERVICE){  	//收到停止命令则停止服务
                	  stopService();  
                  }    
                    
                /*  if(cmd == ConstDef.CMD_SEND_DATA)  
                  {  
                	byte[] databuff = bundle.getByteArray("SendData");
                	int datasize = bundle.getInt("SendDataSize");
                      sendCmd(databuff,datasize);  
                  }  */
                          
            } 
            else if (intent.getAction().equals(ConstDef.MEAS_REQ_BROADCAST_MESSAGE))//获取MEAS_REQ信息（测量请求信息？）
            {
            
            	Bundle bundle = intent.getExtras();  
            	int measType = bundle.getInt(ConstDef.MeasType);  //获取测量类型信息
            	mMessageProcess.CreateMeasureCommandMessage(measType);
            	int dataBuffSize= mMessageProcess.GetSendMsgSize();
            	byte[] dataBuff = mMessageProcess.GetSendMsgBuffer();
                sendCmd(dataBuff, dataBuffSize);  
                
            }
            else if (intent.getAction().equals(ConstDef.GW_STATE_QUERY_REQ_BROADCAST_MESSAGE))		//获取GW_STATE_QUERY_REQ信息
            {
            	mMessageProcess.CreateGWStateQueryCommandMessage(1);// 1 is no meaningful
            	int dataBuffSize= mMessageProcess.GetSendMsgSize();
            	byte[] dataBuff = mMessageProcess.GetSendMsgBuffer();
                sendCmd(dataBuff, dataBuffSize);    
            }
            else if (intent.getAction().equals(ConstDef.BODYTEMP_CAL_REQ_BROADCAST_MESSAGE))		//获取BODYTEMP_CAL_REQ信息
            {
            	
            	mMessageProcess.CreateBodyTempCalCommandMessage(1);// 1 is no meaningful
            	int dataBuffSize= mMessageProcess.GetSendMsgSize();
            	byte[] dataBuff = mMessageProcess.GetSendMsgBuffer();
                sendCmd(dataBuff, dataBuffSize);    
            }
            else if (intent.getAction().equals(ConstDef.BODYTEMP_CAL_COEFF_CONFIG_REQ_BROADCAST_MESSAGE))//获取BODYTEMP_CAL_COEFF_CONFIG_REQ信息
            {
            	Bundle bundle = intent.getExtras();   
            	float[] coeffs = bundle.getFloatArray(ConstDef.BODYTEMP_CAL_COEFFS);
            	mMessageProcess.CreateBodyTempCalCoefConfigCommandMessage(coeffs);// 1 is no meaningful
            	int dataBuffSize= mMessageProcess.GetSendMsgSize();
            	byte[] dataBuff = mMessageProcess.GetSendMsgBuffer();
                sendCmd(dataBuff, dataBuffSize);    
            }
            else if (intent.getAction().equals(ConstDef.ALARM_TIME_REQ_BROADCAST_MESSAGE))//获取ALARM_TIME_REQ信息
            {
            	Bundle bundle = intent.getExtras();   
            	float[] coeffs = bundle.getFloatArray(ConstDef.ALARM_TIME_COEFFS);
            	mMessageProcess.CreateAlarmTimeConfigCommandMessage(coeffs);
            	int dataBuffSize= mMessageProcess.GetSendMsgSize();
            	byte[] dataBuff = mMessageProcess.GetSendMsgBuffer();
                sendCmd(dataBuff, dataBuffSize);    
            }
            else if (intent.getAction().equals(ConstDef.SN_CONFIG_REQ_BROADCAST_MESSAGE))//SN_CONFIG_REQ
            {
            	mMessageProcess.CreateSNConfigCommandMessage(1);// 1 is no meaningful
            	int dataBuffSize= mMessageProcess.GetSendMsgSize();
            	byte[] dataBuff = mMessageProcess.GetSendMsgBuffer();
                sendCmd(dataBuff, dataBuffSize);     
            }
            else if (intent.getAction().equals(ConstDef.MEAS_STOP_REQ_BROADCAST_MESSAGE))
            {
            	mMessageProcess.CreateMeasureStopCommandMessage(1);// 1 is no meaningful
            	int dataBuffSize= mMessageProcess.GetSendMsgSize();
            	byte[] dataBuff = mMessageProcess.GetSendMsgBuffer();
                sendCmd(dataBuff, dataBuffSize);     
            }
            else if (intent.getAction().equals(ConstDef.MEAS_RESULTS_DISPLAY_STATE_BROADCAST_MESSAGE))
            {
            	 Bundle bundle = intent.getExtras();  
                 int cmd = bundle.getInt(ConstDef.CMD);  
                 if(cmd == ConstDef.MEAS_RETS_DISPLAY_STATE)
                 {
              	   boolean resultsDisplayState = bundle.getBoolean(ConstDef.RESULTS_DISPLAYED);
              	   if(resultsDisplayState)
              	   {
              		 if (BuildConfig.DEBUG) {
              		 Log.i(TAG, "MEAS_RESULTS_DISPLAY RECEIVED");
              		 }
              	    mMessageProcess.ContinueMesssageProcessing();
              	   }
                 }
                
            }
        }                          
    }  
  
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
         /*   case MESSAGE_STATE_CHANGE:
                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothChatService.STATE_CONNECTED:
                    setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                    mConversationArrayAdapter.clear();
                    break;
                case BluetoothChatService.STATE_CONNECTING:
                    setStatus(R.string.title_connecting);
                    break;
                case BluetoothChatService.STATE_LISTEN:
                case BluetoothChatService.STATE_NONE:
                    setStatus(R.string.title_not_connected);
                    break;
                }
                break;*/
            case ConstDef.MESSAGE_PROCESSED:
            	
                int messageProcessed = msg.arg1;
                if (messageProcessed == 1)
                {	     
            	     setReadByteFlag(true);
                }
                break;
            case ConstDef.MESSAGE_RESULTS:
            {
            	ProcessMeasResults(msg);
            	}
               
                
             
                break;
           
          /*  case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;*/
            }
        }
        
        public void ProcessMeasResults(Message msg)
        {
        	
      	    Intent intent = new Intent();  
      	    
      		mMsgType = msg.arg1;
      		mResultsObject = msg.obj; 
      		switch (mMsgType)
        	{
        	case MessageInfo.MSGTYPE_ST_MEAS_RESULT_IND:
        	{
        		mMsgSensorType =  msg.arg2;
        		            		            	 
        		switch (mMsgSensorType)
            	{
            	case MessageInfo.SENSORTYPE_BLOODOXYGENMETER:
            	{    
            		/*// 
                     intent.putExtra(ConstDef.CMD, ConstDef.BLOODOX_RESULTS);  
                     intent.putExtra(ConstDef.RESULTS_SIZE, mMeasResultsLen); 
                     intent.putExtra(ConstDef.RESULTS, mMeasResultsBuff);
                     intent.putExtra(ConstDef.NEW_RESUTS_INDICATOR, mNewMeasResult);
                     intent.setAction(ConstDef.BLOODOX_MEAS_RESULTS_BROADCAST_MESSAGE);  
                     sendBroadcast(intent); 
                     */
                     ArrayList<MeasItemResult> measItemResultList = new ArrayList<MeasItemResult>();
             		
             		for (int i = 0; i < ((MeasItemResult[])mResultsObject).length; i++) {       				
             			measItemResultList.add(((MeasItemResult[])mResultsObject)[i]);
         			}

             		 intent.putParcelableArrayListExtra(ConstDef.RESULTS, measItemResultList);

                      intent.setAction(ConstDef.BLOODOX_MEAS_RESULTS_BROADCAST_MESSAGE);  
                      sendBroadcast(intent);    
            	}
            		break;
            	case MessageInfo.SENSORTYPE_BLOODPRESSUREMETER:
            	{
            		ArrayList<MeasItemResult> measItemResultList = new ArrayList<MeasItemResult>();
            		
            		for (int i = 0; i < ((MeasItemResult[])mResultsObject).length; i++) {       				
            			measItemResultList.add(((MeasItemResult[])mResultsObject)[i]);
        			}

            		 intent.putParcelableArrayListExtra(ConstDef.RESULTS, measItemResultList);

                     intent.setAction(ConstDef.BLOODPRESS_MEAS_RESULTS_BROADCAST_MESSAGE);  
                     sendBroadcast(intent);  
            	}
            		break;
            	case MessageInfo.SENSORTYPE_BLOODSUGARMETER:
            		break;
            	case MessageInfo.SENSORTYPE_ELECTROCARDIOGRAMMETER:
            	{
            		  ArrayList<MeasItemResult> measItemResultList = new ArrayList<MeasItemResult>();
               		
               		for (int i = 0; i < ((MeasItemResult[])mResultsObject).length; i++) {       				
               			measItemResultList.add(((MeasItemResult[])mResultsObject)[i]);
           			}

               		 intent.putParcelableArrayListExtra(ConstDef.RESULTS, measItemResultList);

                        intent.setAction(ConstDef.ECG_MEAS_RESULTS_BROADCAST_MESSAGE);
                        sendBroadcast(intent);    //心电数据发送
            		break;
            	}
            	case MessageInfo.SENSORTYPE_STETHOSCOPE:
            		break;
            	
            	case MessageInfo.SENSORTYPE_THERMOMETER:

            	{
            		ArrayList<MeasItemResult> measItemResultList = new ArrayList<MeasItemResult>();
            		
            		for (int i = 0; i < ((MeasItemResult[])mResultsObject).length; i++) {       				
            			measItemResultList.add(((MeasItemResult[])mResultsObject)[i]);
        			}

            		 intent.putParcelableArrayListExtra(ConstDef.RESULTS, measItemResultList);

                     intent.setAction(ConstDef.BODYTEMP_MEAS_RESULTS_BROADCAST_MESSAGE);  
                     sendBroadcast(intent);  
            	}
            		break;
            	case MessageInfo.SENSORTYPE_PULMONARYVENTILATION:
            	{    
            		/*// 
                     intent.putExtra(ConstDef.CMD, ConstDef.BLOODOX_RESULTS);  
                     intent.putExtra(ConstDef.RESULTS_SIZE, mMeasResultsLen); 
                     intent.putExtra(ConstDef.RESULTS, mMeasResultsBuff);
                     intent.putExtra(ConstDef.NEW_RESUTS_INDICATOR, mNewMeasResult);
                     intent.setAction(ConstDef.BLOODOX_MEAS_RESULTS_BROADCAST_MESSAGE);  
                     sendBroadcast(intent); 
                     */
                     ArrayList<MeasItemResult> measItemResultList = new ArrayList<MeasItemResult>();
             		
             		for (int i = 0; i < ((MeasItemResult[])mResultsObject).length; i++) {       				
             			measItemResultList.add(((MeasItemResult[])mResultsObject)[i]);
         			}

             		 intent.putParcelableArrayListExtra(ConstDef.RESULTS, measItemResultList);

                      intent.setAction(ConstDef.PULVENT_MEAS_RESULTS_BROADCAST_MESSAGE);  
                      sendBroadcast(intent);    
            	}
            	break;
            	default:
            		 Log.e(TAG, "Wrong SensorType,Reset MEAS_RESULTS_DISPLAY RECEIVED");
               	    mMessageProcess.ContinueMesssageProcessing();
            		break;
            		
            		
            	}
        	}
        		break;
        	case MessageInfo.MSGTYPE_ST_GWZG_STATE_QUERY_IND:
        		ArrayList<SensorInfoResult> sensorInfoItemResultList = new ArrayList<SensorInfoResult>();
        		GWStateRetMessage tmp = (GWStateRetMessage) mResultsObject;
        		GWInfoResult gwInfoResultsGwInfoResult = tmp.GetGWInfoResults();
        		 SensorInfoResult[] sensorInfoResults = tmp.GetSensorInfoResults();
        		for (int i = 0; i < ((SensorInfoResult[])sensorInfoResults).length; i++) {       				
        			sensorInfoItemResultList.add(((SensorInfoResult[])sensorInfoResults)[i]);
    			}
        		
        		 intent.putExtra(ConstDef.CMD, ConstDef.GW_STATE_QUERY_RESULTS);  
        		 intent.putExtra(ConstDef.GW_INFO_RESULTS, gwInfoResultsGwInfoResult);
        		 intent.putParcelableArrayListExtra(ConstDef.SENSOR_INFO_RESULTS, sensorInfoItemResultList);
                 intent.setAction(ConstDef.GW_STATE_QUERY_RESULTS_BROADCAST_MESSAGE);  
                 sendBroadcast(intent); 
                 
        		break;
        	case MessageInfo.MSGTYPE_ST_BODYTEMP_SENSOR_CAL_IND:
        		BodyTempSensorCalResult bodyTempCalRet = (BodyTempSensorCalResult) mResultsObject;
        		intent.putExtra(ConstDef.CMD, ConstDef.BODYTEMP_CAL_RESULTS);  
       		    intent.putExtra(ConstDef.BODYTEMP_CAL_TEMP_RESULTS, bodyTempCalRet);
       		    intent.setAction(ConstDef.BODYTEMP_CAL_RESULTS_BROADCAST_MESSAGE);  
                sendBroadcast(intent); 
        		break;
        	case MessageInfo.MSGTYPE_ST_MEAS_START_CFM:
        		break;
        	case MessageInfo.MSGTYPE_ST_MEAS_STOP_CFM:
        		break;
        	case MessageInfo.MSGTYPE_ST_MEAS_CONFIG_CFM:
        		break;
        	default:
        		break;
        	}
        	
        }
    };
      
}  