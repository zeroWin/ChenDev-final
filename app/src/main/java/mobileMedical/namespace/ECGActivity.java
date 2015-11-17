package mobileMedical.namespace;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import com.artfulbits.aiCharts.Base.ChartArea;
import com.artfulbits.aiCharts.Base.ChartAxisScale;
import com.artfulbits.aiCharts.Base.ChartPointCollection;
import com.artfulbits.aiCharts.Base.ChartSeries;
import com.artfulbits.aiCharts.ChartView;
import com.artfulbits.aiCharts.Types.ChartTypes;

import java.util.ArrayList;

import devDataType.Parameters.IntParameter;
import mobileMedical.Message.MeasItemResult;
import mobileMedical.Message.MessageData;
import mobileMedical.Message.MessageInfo;
import mobileMedical.Message.ParameterDataKeys;



public class ECGActivity extends Activity {


boDb boDbHelper = new boDb(this, "bloodox.db", null, 1);
private final int MaxPoints = 200;

private final float mSampleRate = 200.0f;
private int m_ResultsPackageIndex = 0;
private boolean m_NewResults = true;

private int m_ViewMaxPoints = 100000;
private int m_ViewMaxPointsRepeat = 0;

private int mResultsIdx = 0;
private char[] mResults = null;
private char[] mMeasItemCharResults = null;
private final int mResultsParameters = 3;
private int mResultsItemNum = 0;

private final int GET_RESULTS = 1;

private static final String TAG = "EGCActivity";


		private int m_PointsCounter=0;
		private int meas_index = 2;
	
		private ChartPointCollection m_TargetCollection;
		private ChartView m_ChartView;
		private ChartSeries m_ChartSeries;
		private ChartArea m_ChartArea;
		
		private MeasItemResult[] m_MeasItemResults;
		private TextView m_HeartRate;
		private ArrayList<MeasItemResult>  m_MeasItemResultsList;
		private int m_ListSize;
		private int m_ModeTimes;
		private int m_MultipleTimes;
		
		private int m_MesaItemsResultLen;
		private int m_Idx;
		private	int m_MeasItemIdex;
		private	int m_ECGWaveResult;
		private	int m_HeartRateResult;
		private int m_MaxPoints = 200;
		private int m_YAxisMax = 200;
		private int m_YAxisMin = 0;
		private int m_XAxisMin = 0;
	
		private int m_TransID;
		private int m_SensorType;
		private int m_MeasItem;
		private int m_DoctorID;
		private int m_PatientID;

		
		private SQLiteDatabase m_DataBaseSQL;
		private StringBuilder m_TotalDataPackage;
		private ContentValues m_ContentValues;
		
	// #region Public methods
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);  
        setContentView(R.layout.ecg);
        
        
        IntentFilter filter = new IntentFilter(ConstDef.ECG_MEAS_RESULTS_BROADCAST_MESSAGE);        
        this.registerReceiver(mReceiver,filter); 
        m_HeartRate = (TextView) findViewById(R.id.textViewECGHeartRateValue);
    	
       m_ChartView = (ChartView)findViewById(R.id.chartViewECG);
       
       m_DataBaseSQL = boDbHelper.getWritableDb();
       m_TotalDataPackage = new StringBuilder();
		m_ContentValues = new ContentValues();

		
		m_ChartSeries = new ChartSeries("FastLineSeries", ChartTypes.FastLine);
		m_ChartView.setPanning(ChartView.PANNING_BOTH);
		m_ChartView.getSeries().add(m_ChartSeries);
		m_ChartArea = new ChartArea("ecg");
		m_ChartArea.getDefaultXAxis().getScale().setMargin(ChartAxisScale.MARGIN_NONE);
		m_ChartArea.getDefaultXAxis().setShowLabels(false);
		m_ChartArea.getDefaultYAxis().setTitle(getString(R.string.egc_axis_title));
		
		m_ChartView.getAreas().add(m_ChartArea);				
		m_ChartArea.getDefaultYAxis().getScale().setRange(m_YAxisMin, m_YAxisMax);
		
		m_ChartArea.getDefaultXAxis().getScale().setRange(m_XAxisMin, m_MaxPoints);	
	
		SharedPreferences settings = getSharedPreferences(ConstDef.SHAREPRE_SETTING_INFOS, 0); //首先获取一个 SharedPreferences 对象  
        float ecgDisplFramLen = settings.getFloat(ConstDef.SHAREPRE_ECG_DISPLAY_FRAME_LEN,0);
        m_MaxPoints =   Math.round(ecgDisplFramLen * mSampleRate/MessageInfo.ECG_WAVEFORM_SAMPLES_NUM_PER_PACKET)
        *MessageInfo.ECG_WAVEFORM_SAMPLES_NUM_PER_PACKET;;
        if (m_MaxPoints < MaxPoints)
        {
        	m_MaxPoints = MaxPoints;
        }
        
        
		m_TargetCollection = m_ChartSeries.getPoints();		
}
    
    @Override
    public synchronized void onResume() {
        super.onResume();
		SharedPreferences settings = getSharedPreferences(ConstDef.SHAREPRE_SETTING_INFOS, 0); //首先获取一个 SharedPreferences 对象  
        float ecgDisplFramLen = settings.getFloat(ConstDef.SHAREPRE_ECG_DISPLAY_FRAME_LEN,0);
        m_MaxPoints =   Math.round(ecgDisplFramLen * mSampleRate/MessageInfo.ECG_WAVEFORM_SAMPLES_NUM_PER_PACKET)
        *MessageInfo.ECG_WAVEFORM_SAMPLES_NUM_PER_PACKET;;
        if (m_MaxPoints < MaxPoints)
        {
        	m_MaxPoints = MaxPoints;
        }
    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
    	boolean initPhase = true;
		

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(ConstDef.MEAS_INIT_BROADCAST_MESSAGE))
            {
            	 Bundle bundle = intent.getExtras();  
          	   int measType = bundle.getInt(ConstDef.MeasType);
              if (measType != ConstDef.ElectrocardiogramTabIndex)
     	     {
     		   return;
     	       }
              
          

              m_HeartRate.setText(R.string.nullValue);
			
			// Clear the pulse waveform trace		
				m_TargetCollection.clear();
				
				// Reset the m_PointsCounter
				m_PointsCounter = 0;
				
				m_NewResults = true;
				
				m_ResultsPackageIndex = 0;

            }
            else if(action.equals(ConstDef.ECG_MEAS_RESULTS_BROADCAST_MESSAGE))
             {  
            	 
            	if(m_NewResults)
           	 {
           	  // It means that it is initiated measure by node.

           	    m_NewResults = false;
           	    m_TransID = ((IntParameter) MessageData.parmsDataHashMap
    						.get(ParameterDataKeys.TRANSID)).GetValue();
           	    // Should increase TransId by 1 to keep it is monotonic increasing.
//           	    m_TransID +=1;
  				   ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.TRANSID)).SetValue(m_TransID);			 
    				m_SensorType = MessageInfo.SENSORTYPE_ELECTROCARDIOGRAMMETER;
    				m_MeasItem = MessageInfo.MM_MI_ELECTRO_CARDIOGRAM;
    				m_DoctorID = ((IntParameter) MessageData.parmsDataHashMap
    						.get(ParameterDataKeys.DOCTORID)).GetValue();
    				m_PatientID = ((IntParameter) MessageData.parmsDataHashMap
    						.get(ParameterDataKeys.PATIENTID)).GetValue();
    				
    				m_ResultsPackageIndex = 0;
    				m_PointsCounter = 0;
           	 }
            	
            	m_MeasItemResultsList = new ArrayList<MeasItemResult>();
           	  
            	m_MeasItemResultsList =   intent.getParcelableArrayListExtra(ConstDef.RESULTS);
         	 
         	   if(m_MeasItemResultsList != null && !m_MeasItemResultsList.isEmpty())
         	   {
         		   m_ListSize = m_MeasItemResultsList.size(); 
         		   m_MeasItemResults =  (MeasItemResult[])m_MeasItemResultsList.toArray(new MeasItemResult[m_ListSize] );
         	   }

         	  if(m_MeasItemResults!= null)
         	  {
         		 mResultsItemNum = m_MeasItemResults.length;       	  
         	  }
         	 
                   
              m_ModeTimes = m_PointsCounter % m_MaxPoints;
           	  m_MultipleTimes = m_PointsCounter / m_MaxPoints;
           	 if (m_PointsCounter > m_ViewMaxPoints)
        	  {
        		  m_TargetCollection.clear();
        		  m_PointsCounter = 0;
        		  m_ViewMaxPointsRepeat++;
        		  if (BuildConfig.DEBUG) {
        			  Log.i("m_ViewMaxPoints", String.valueOf(m_ViewMaxPointsRepeat));
        		  }
        	  }
           	 else if(m_ModeTimes == 0 && m_PointsCounter !=0)
           	 {
           		// m_TargetCollection.clear();
           		m_ChartArea.getDefaultXAxis()
						.getScale().setRange(m_XAxisMin, (m_MultipleTimes +1)*m_MaxPoints/mSampleRate);
				
								
           		m_ChartArea.getDefaultXAxis()
					.getScale().setZoom(m_MultipleTimes*m_MaxPoints/mSampleRate , m_MaxPoints/mSampleRate);
           	 }
           	 
                	//test
                  m_MesaItemsResultLen = mResultsItemNum * MessageInfo.ECG_HYBRID_MEASITEM_RESULT_LEN;
                  m_Idx = 0;
                  mResults = new char[m_MesaItemsResultLen];
                	m_MeasItemIdex = 0;
                	
                	 if(m_TotalDataPackage.length() >0)
                	 {
                	 m_TotalDataPackage.delete(0, m_TotalDataPackage.length()-1);
                	 }
   
                	do {

						mResultsItemNum -= 1;
						mMeasItemCharResults =  m_MeasItemResults[m_MeasItemIdex].GetCharResults();
						System.arraycopy(mMeasItemCharResults, 0, mResults, 0, MessageInfo.ECG_HYBRID_MEASITEM_RESULT_LEN);	
						m_Idx += MessageInfo.ECG_HYBRID_MEASITEM_RESULT_LEN;
		
						m_HeartRateResult = mMeasItemCharResults[MessageInfo.ECG_HEARTRATE_RESULT_IDX];
						m_HeartRate.setText(String.valueOf(m_HeartRateResult));
						m_TotalDataPackage.append(m_HeartRateResult + " ");

						for (int index = 0; index < MessageInfo.ECG_WAVEFORM_SAMPLES_NUM_PER_PACKET; index++)
						{
						m_ECGWaveResult = mResults[MessageInfo.ECG_WAVEFORM_RESULT_STARTIDX + index];
						m_TargetCollection.addXY(m_PointsCounter/mSampleRate,m_ECGWaveResult);
						m_TotalDataPackage.append(m_ECGWaveResult + " ");

						m_PointsCounter++;
						}
					
						
					}while(mResultsItemNum > 0);
                	
                	
                	m_DataBaseSQL.beginTransaction();

    				
    				m_ContentValues.clear();
    				m_ContentValues.put(ConstDef.DATABASE_FIELD_TRANSID, m_TransID);
    				m_ContentValues.put(ConstDef.DATABASE_FIELD_SENSORTYPE, m_SensorType);
    				m_ContentValues.put(ConstDef.DATABASE_FIELD_MEASITEM, m_MeasItem);
    				m_ContentValues.put(ConstDef.DATABASE_FIELD_DOCTORID, m_DoctorID);
    				m_ContentValues.put(ConstDef.DATABASE_FIELD_PATIENTID, m_PatientID);
    				m_ContentValues.put(ConstDef.DATABASE_FIELD_MEASRESULTS, m_TotalDataPackage
    						.toString().trim());
    				m_ContentValues.put(ConstDef.DATABASE_FIELD_MEASRESULTSIDX,
    						m_ResultsPackageIndex);
    				m_DataBaseSQL.insert(ConstDef.DATABASE_TABLE_NAME_DATA, null, m_ContentValues);

    				m_ResultsPackageIndex++;

    				m_DataBaseSQL.setTransactionSuccessful();
        					// 在setTransactionSuccessful和endTransaction之间不进行任何数据库操作
        			
            
        				// 当所有操作执行完成后结束一个事务
                        m_DataBaseSQL.endTransaction();
                        //m_DataBaseSQL.close();


                	m_Idx = 0;
					
					 // Fist Time, do not send it. Maybe we can also do it like the MessageProcessThread.
					 // To start this thread only when received the broadcast from MobileMedical Activity.
                	 Intent resultDislayedInformIntent = new Intent();  
        		     resultDislayedInformIntent.putExtra(ConstDef.CMD, ConstDef.MEAS_RETS_DISPLAY_STATE);  
        		     resultDislayedInformIntent.putExtra(ConstDef.RESULTS_DISPLAYED, true); 				             
        		     resultDislayedInformIntent.setAction(ConstDef.MEAS_RESULTS_DISPLAY_STATE_BROADCAST_MESSAGE);  
    	             sendBroadcast(resultDislayedInformIntent);
    	             
                }
           
             // Can add some other code here to trigger the new statistical view.
        }
    };

   
        }
