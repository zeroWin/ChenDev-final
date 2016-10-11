package mobileMedical.namespace;






import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

public class settingActivity extends TabActivity {
	
	private TabHost tabHost = null;
	private int mCurrentTabIndex = -1;

	private static final boolean D = true;
	
	private String mConnectedDeviceStr = "没有蓝牙连接";
	private final String mNoConnection = "没有蓝牙连接";
	
	public static final int MESSAGE_RESULTS = 1;
	public static final int MESSAGE_PROCESSED = 2;
	public static final int BLOODOX_RESULTS = 3;
	public static final int BLOODOX_STATE = 4;
    // Meas Related
	
	private byte[]  mMeasCmdBuff = null;
	private float[] mMeasResultsBuff = new float[1024];
	private int     mMeasResultsLen = -1;
	private boolean mNewMeasResult = false; 
	private static int mMeasResultMessageCounter = 0;
	
    // Measure CMD Parameters
/*    private int mDocID     = 0;
    private int mpatientID = 0;
    private final int mMessageHeader = 0x7C; 
    private int MessageBufferSize = 0;
    private int SingleMeasComm = 0x6D;
    private int ShortAddr = 0x0;
    private int CommID = 0x0;*/

    private MessageProcess  mMessageProcess = null;
    private boolean mMeasResultsReady = false;
    private boolean mMeasFinished = false;
    
    
    private boolean mSimulatedResults = false;
    private int mSimulatedBloodOxDataSize = SimulationData.BloodOxSimulationDataLen;
    private static int mSimulatedBloodOxDataidx = 0;
    

    private static final String TAG = "MobileMedicalActivity";
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE); 
        setContentView(R.layout.setting);

        
 
        tabHost=getTabHost();  
        //TabView view = null;
        //view = new TabView(this, R.drawable.ic_launcher, R.drawable.ic_launcher);  
        //view.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.sync_launcher));
        TextView view = null;
        view = new TextView(this);
        view.setText("网关设置");
        
        TabSpec blueToothConfigSpec =tabHost.newTabSpec("router");  
        //BloodOxSpec.setIndicator(view); 
        blueToothConfigSpec.setIndicator("网关设置"); 
        
        
        Intent bluetoothConfigIntent = new Intent(this, BluetoothConfig.class);  
        Bundle bundle = new Bundle();
    	bundle.putString("dev",mConnectedDeviceStr);  
    	bluetoothConfigIntent.putExtras(bundle); 
    	blueToothConfigSpec.setContent(bluetoothConfigIntent);
        
              
        tabHost.addTab(blueToothConfigSpec);  
        tabHost.addTab(tabHost.newTabSpec("timing")  
               .setIndicator("时间设置")  
                .setContent(new Intent(this, setTimingActivity.class)));  


        
        tabHost.addTab(tabHost.newTabSpec("OtherSetting")  
                .setIndicator("其他设置")  
                 .setContent(new Intent(this, SetOthersActivity.class)));
        
        TabWidget tabWidget = tabHost.getTabWidget();
        int height = this.getWindowManager().getDefaultDisplay().getHeight()/5; 
        
        for (int i =0; i < tabWidget.getChildCount(); i++) { 
      //–?∏?Tabhost∏???∫??ì??
        	tabWidget.getChildAt(i).getLayoutParams().height = height;
        	tabWidget.getChildAt(i).getLayoutParams().width = LayoutParams.FILL_PARENT;
        	
       //–?∏??‘???÷??￥?–°
        	TextView tv = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);
        	//tv.setTextSize(20);
        	//RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width,LayoutParams.FILL_PARENT);  
        	//此处相当于布局文件中的Android:layout_gravity属性  
        	//lp.gravity = Gravity.CENTER; 
        	//tv.setLayoutParams(lp);
        	tv.setGravity(Gravity.CENTER);//bottom
        	
        	
        	
        	
        }
        setTitle("设置");

  
    }
    
    @Override
    public void onStart() {
        super.onStart();

       
    }

    @Override
    public synchronized void onResume() {
        super.onResume();

      

    }
    @Override   protected void onActivityResult(int requestCode, int resultCode,  Intent data)
    {    
    
    	 switch (requestCode) { 
    	 case RESULT_OK: 
    		 
     		Bundle bunde = data.getExtras();
     		mConnectedDeviceStr = bunde.getString(ConstDef.BT_DEVICE); 
     		          break;  
    	 default: 
             break; 

         } 
      	}  
  /*  private void setupSendDirectMessage() {
        Log.d(TAG, "setupSendDirectMessage()");

        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
        mConversationView = (ListView) findViewById(R.id.in);
        mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the compose field with a listener for the return key
        mOutEditText = (EditText) findViewById(R.id.edit_text_out);
        mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mSendButton = (Button) findViewById(R.id.button_send);
        mSendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                TextView view = (TextView) findViewById(R.id.edit_text_out);
                String message = view.getText().toString();
                sendMessage(message);
            }
        });

        // Initialize the BluetoothServer to perform bluetooth connections
        mBluetoothService = new BluetoothServer(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }
*/
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

        //stop bt if connected
        if(!mConnectedDeviceStr.equals(mNoConnection))
        {
        Intent intent = new Intent();
        intent.setAction(ConstDef.CMD_BROADCAST_MESSAGE);  
        intent.putExtra(ConstDef.CMD, ConstDef.CMD_STOP_SERVICE);  
          
        sendBroadcast(intent);
        }
       
    }
    
    
    private  class TabView extends LinearLayout {  
	    ImageView imageView ;  
	public TabView(Context c, int drawable, int drawableselec) {  
	    super(c);  
	    imageView = new ImageView(c);  
	    StateListDrawable listDrawable = new StateListDrawable();  
	    listDrawable.addState(SELECTED_STATE_SET, this.getResources()  
	            .getDrawable(drawableselec));  
	    listDrawable.addState(ENABLED_STATE_SET, this.getResources()  
	            .getDrawable(drawable));  
	    imageView.setImageDrawable(listDrawable);  
	    imageView.setBackgroundColor(Color.TRANSPARENT);  
	    setGravity(Gravity.CENTER);  
	    addView(imageView);  
	}  
    }
    
    


}