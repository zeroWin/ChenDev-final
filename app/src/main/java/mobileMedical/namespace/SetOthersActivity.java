package mobileMedical.namespace;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import mobileMedical.FileManage.MeasDataFilesOperator;
import mobileMedical.Message.MessageInfo;
import mobileMedical.namespace.BluetoothServer.BluetoothThread;
import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

public class SetOthersActivity extends Activity {

private final int updateProgess = 0x11;
private final int updateProgessError = 0x12;
private static int progress = 0;
private boolean threadFlag = true; 
updateProgessThread mUpdateProgessThread;  
boDb boDbHelper = new boDb(this, "bloodox.db", null, 1);

private CheckBox readMeasDataFromSDCard = null; 
private ProgressBar readMeasDataProgessBar = null;
// #region Public methods
/** Called when the activity is first created. */
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.requestWindowFeature(Window.FEATURE_NO_TITLE);  
    setContentView(R.layout.setting_others);
    
    // Initialize the button to read the measData to DataBase
    

    
    readMeasDataFromSDCard = (CheckBox) findViewById(R.id.checkBoxFromSDCard);
    readMeasDataProgessBar = (ProgressBar) findViewById(R.id.progressBarReadMeasData);
    Button readMeasDatButton = (Button) findViewById(R.id.buttonReadMeasData);
    readMeasDatButton.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
         

            threadFlag = true;    
            mUpdateProgessThread = new updateProgessThread();  
            mUpdateProgessThread.start(); 
            
         
        	
        }
    });
   
    
}
private final Handler mHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
    	switch (msg.what)
    	{
    	case updateProgess:
    		readMeasDataProgessBar.setProgress(msg.arg2);
    		break;
    	case updateProgessError:
    		Toast.makeText(SetOthersActivity.this, (String)msg.obj, Toast.LENGTH_LONG).show();
    		break;
    	}
    	
    }
    };
    
    public class updateProgessThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			while (threadFlag) {
				

	        	boolean readFromSDCard = false;
	        	MeasDataFilesOperator measdFilesOperator = new MeasDataFilesOperator(SetOthersActivity.this);
	        	if(readMeasDataFromSDCard.isChecked())
	        	{
	        		readFromSDCard = true;
	        		if (!measdFilesOperator.HasSDCard())
	        		{
	        			
	        			
	        			Message m = new Message();
            			m.what = updateProgessError;
            			m.obj = "Do not has the SD Card";
            			mHandler.sendMessage(m);
	        			threadFlag = false;
	        			break;
	        		}
	        	}
	        	else {
	        		readFromSDCard = false;
				}

	            
	        	    
	            	String[] measDataStrings = measdFilesOperator.ReadMeasDataFromFile("measdata.txt",readFromSDCard,1);
	            	if (measDataStrings != null)
	            	{
	                    String contentString = null; 
	                    String[] measDataInfo = null;
	                    int  patientID = -1;
	                    int measItemID = -1;
	                    String measTimeString = null;
	                    Date time = null;
	            		for (int i = 0; i < measDataStrings.length; i++) {
	            			contentString = measDataStrings[i];
	            			// we can also use the RegularExpressions
	            			if(contentString.split(",").length == 1)
	            			{
	            				// If it includes over one item", it is the measDataInfo, Otherwise, it is the MeasData
	            				double measRet =  Double.parseDouble(contentString);
	            				if(measItemID == MessageInfo.MM_MI_BODY_TEMPERATURE)
	            				{
	            					 SimpleDateFormat format  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
	            					 try {
	        							time =  format.parse(measTimeString);
	        						} catch (ParseException e) {
	        							// TODO Auto-generated catch block
	        							e.printStackTrace();
	        						}
	        						boDbHelper.insertBt(patientID,(float)measRet,time);
	            				}
	            				else if (measItemID == MessageInfo.MM_MI_BODY_BASE_TEMPERATURE)
	            				{
	            					 SimpleDateFormat format  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
	            					 try {
	        							time =  format.parse(measTimeString);
	        						} catch (ParseException e) {
	        							// TODO Auto-generated catch block
	        							e.printStackTrace();
	        						}
	        						boDbHelper.insertBtBase(patientID,(float)measRet,time);
	            				}
	            				
	            			}
	            			else if (contentString.split(",").length == 3)
	            			{
	            				// First string should be measItem
	            				measDataInfo = contentString.split(",");
	            				measItemID = Integer.parseInt(measDataInfo[0]);
	            				patientID = Integer.parseInt(measDataInfo[1]);
	            				measTimeString = measDataInfo[2];
	            			}
	            			else {
	            				
	            				Message m = new Message();
	                			m.what = updateProgessError;
	                			m.obj = Integer.toString(i) + " line is not correct";
	                			mHandler.sendMessage(m);
	                			threadFlag = false;
	                			break;
	            				
	        				}
	            			progress = (int)(((float)(i + 1)/measDataStrings.length) * 100);
	            			if(progress  == 100)
	            			{
	            				threadFlag = false;
	            			}
	            			Message m = new Message();
	            			m.what = updateProgess;
	            			m.arg2 = progress;
	            			mHandler.sendMessage(m);
	            			
	            			try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	            			

	            			
	            		}
	        	}
	        	else
	        	{
	        		Message m = new Message();
        			m.what = updateProgessError;
        			m.obj =  "Do not find the file or file it empty";
        			mHandler.sendMessage(m);
        			threadFlag = false;
        			break;
	        	}
	        
			}
			}
    }
	
    
    public void onDestroy() {
        super.onDestroy();
        threadFlag = false;
    }
}