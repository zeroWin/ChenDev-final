
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



public class BloodPressureActivity extends Activity {


boDb boDbHelper = new boDb(this, "bloodox.db", null, 1);

//private final int MaxPoints = 1000;
private final int MaxPoints = 200;
private final float mSampleRate = 200.0f;
private int m_ResultsPackageIndex = 0;
private boolean m_NewResults = true;

private int mResultsIdx = 0;
private char[] mResults = null;
private char[] mMeasItemIntResults = null;
private final int mResultsParameters = 3;
private int mResultsItemNum = 0;

private int m_ViewMaxPoints = 100000;
private int m_ViewMaxPointsRepeat = 0;

private final int GET_RESULTS = 1;

private static final String TAG = "BloodPressActivity";

		
		private int m_PointsCounter=0;

	
		
		private MeasItemResult[] m_MeasItemResults;
		private ArrayList<MeasItemResult>  m_MeasItemResultsList;
		private int m_ListSize;
		private ChartView m_BloodPressDCChartView;
		private ChartView m_BloodPressACChartView;
		private ChartSeries m_BloodPressDCChartSeries;
		private ChartSeries m_BloodPressACChartSeries;
		private ChartArea m_BloodPressACChartArea;
		private ChartArea m_BloodPressDCChartArea;
		private ChartPointCollection m_ACTargetCollection;
		private ChartPointCollection m_DCTargetCollection;
		
		private TextView m_SP;
		private TextView m_DP;
		private int m_ModeTimes;
		private int m_MultipleTimes;
		
		private int m_MesaItemsResultLen;
		private int m_Idx;
		private	int m_MeasItemIdex;
		private	int m_DCWaveResult;
		private	int m_ACWaveResult;
		private	int m_SPResult;
		private	int m_DPResult;
		private	int m_ACCountResult;
		private int m_MaxPoints = 200;
		private int m_YAxisMax = 3000;
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
        setContentView(R.layout.bloodpress);
        
        
        IntentFilter filter = new IntentFilter(ConstDef.BLOODPRESS_MEAS_RESULTS_BROADCAST_MESSAGE);        
        this.registerReceiver(mReceiver,filter); 
        
        m_SP = (TextView) findViewById(R.id.textViewBloodPressSPValue);
      	
      	m_DP = (TextView) findViewById(R.id.textViewBloodPressDPValue);
      	

        m_DataBaseSQL = boDbHelper.getWritableDb();
        m_TotalDataPackage = new StringBuilder();
		m_ContentValues = new ContentValues();
		
        m_BloodPressDCChartView = (ChartView)findViewById(R.id.BloodPressDCchartView);
		m_BloodPressDCChartSeries = new ChartSeries("FastLineSeriesDC", ChartTypes.FastLine);
		m_BloodPressDCChartView.setPanning(ChartView.PANNING_BOTH);
		m_BloodPressDCChartView.getSeries().add(m_BloodPressDCChartSeries);
		m_BloodPressDCChartArea = new ChartArea(getString(R.string.bloodpressDC));
		m_BloodPressDCChartArea.getDefaultXAxis().getScale().setMargin(ChartAxisScale.MARGIN_NONE);
		m_BloodPressDCChartArea.getDefaultXAxis().setShowLabels(false);
		m_BloodPressDCChartArea.getDefaultYAxis().setTitle(getString(R.string.bloodpressDC));
		m_BloodPressDCChartView.getAreas().add(m_BloodPressDCChartArea);
		m_BloodPressDCChartArea.getDefaultYAxis().getScale().setRange(500,3000);
		m_BloodPressDCChartArea.getDefaultXAxis().getScale().setRange(m_XAxisMin, m_MaxPoints);
	
		m_BloodPressACChartView = (ChartView)findViewById(R.id.BloodPressACchartView);		
		m_BloodPressACChartSeries = new ChartSeries("FastLineSeriesAC", ChartTypes.FastLine);
		 m_BloodPressACChartView.setPanning(ChartView.PANNING_BOTH);
		 m_BloodPressACChartView.getSeries().add(m_BloodPressACChartSeries);
		 m_BloodPressACChartArea = new ChartArea(getString(R.string.bloodpressAC));
		 m_BloodPressACChartArea.getDefaultXAxis().getScale().setMargin(ChartAxisScale.MARGIN_NONE);
		 m_BloodPressACChartArea.getDefaultXAxis().setShowLabels(false);
		 m_BloodPressACChartArea.getDefaultYAxis().setTitle(getString(R.string.bloodpressAC));
		 m_BloodPressACChartView.getAreas().add(m_BloodPressACChartArea);		
		 m_BloodPressACChartArea.getDefaultYAxis().getScale().setRange(1000, 2500);
		 m_BloodPressACChartArea.getDefaultXAxis().getScale().setRange(m_XAxisMin, m_MaxPoints);
        
		
		
		/*SharedPreferences settings = getSharedPreferences("spo_meas", MODE_PRIVATE);
		meas_index = settings.getInt("measIndex", 0);*/
		 SharedPreferences settings = getSharedPreferences(ConstDef.SHAREPRE_SETTING_INFOS, 0); //首先获取一个 SharedPreferences 对象  
	        float bloodPressDisplFramLen = settings.getFloat(ConstDef.SHAREPRE_BLOODPRESS_DISPLAY_FRAME_LEN,0);
	        m_MaxPoints =   Math.round(bloodPressDisplFramLen * mSampleRate/MessageInfo.BLOODPRESS_WAVEFORM_SAMPLES_NUM_PER_PACKET)
	        *MessageInfo.BLOODPRESS_WAVEFORM_SAMPLES_NUM_PER_PACKET;;
	        if (m_MaxPoints < MaxPoints)
	        {
	        	m_MaxPoints = MaxPoints;
	        }
	        
	  
	  m_DCTargetCollection = m_BloodPressDCChartSeries.getPoints();
	  m_ACTargetCollection = m_BloodPressACChartSeries.getPoints();
}
    @Override
    public synchronized void onResume() {
        super.onResume();
		SharedPreferences settings = getSharedPreferences(ConstDef.SHAREPRE_SETTING_INFOS, 0); //首先获取一个 SharedPreferences 对象  
        float bloodPressDisplFramLen = settings.getFloat(ConstDef.SHAREPRE_BLOODPRESS_DISPLAY_FRAME_LEN,0);
        m_MaxPoints =   Math.round(bloodPressDisplFramLen * mSampleRate/MessageInfo.BLOODPRESS_WAVEFORM_SAMPLES_NUM_PER_PACKET)
        *MessageInfo.BLOODPRESS_WAVEFORM_SAMPLES_NUM_PER_PACKET;;
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
              if (measType != ConstDef.BloodPresureTabIndex)
     	     {
     		   return;
     	       }
              
          	m_SP.setText(R.string.nullValue);         	
          	m_DP.setText(R.string.nullValue);
			
							
				m_DCTargetCollection.clear();
				m_ACTargetCollection.clear();
				
				// Reset the m_PointsCounter
				m_PointsCounter = 0;
				
				m_NewResults = true;
				
				m_ResultsPackageIndex = 0;

            }
            else if(action.equals(ConstDef.BLOODPRESS_MEAS_RESULTS_BROADCAST_MESSAGE))
             {  
            	 if(m_NewResults)
            	 {
            	  // It means that it is initiated measure by node.

            	    m_NewResults = false;
            	    m_TransID = ((IntParameter) MessageData.parmsDataHashMap
     						.get(ParameterDataKeys.TRANSID)).GetValue();
            	    // Should increase TransId by 1 to keep it is monotonic increasing.
//            	    m_TransID +=1;
   				   ((IntParameter)MessageData.parmsDataHashMap.get(ParameterDataKeys.TRANSID)).SetValue(m_TransID);			 
     				m_SensorType = MessageInfo.SENSORTYPE_BLOODPRESSUREMETER;
     				m_MeasItem = MessageInfo.MM_MI_BLOOD_PRESSURE;
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
             		m_DCTargetCollection.clear();
             		m_ACTargetCollection.clear();
           		  m_PointsCounter = 0;
           		  m_ViewMaxPointsRepeat++;
           		  if (BuildConfig.DEBUG) {
           			  Log.i("m_ViewMaxPoints", String.valueOf(m_ViewMaxPointsRepeat));
           		  }
           	  }
           	  else if(m_ModeTimes == 0 && m_PointsCounter !=0)
           	   {
         		m_BloodPressACChartArea.getDefaultXAxis()
						.getScale().setRange(m_XAxisMin, (m_MultipleTimes + 1) * m_MaxPoints / mSampleRate);
         		m_BloodPressACChartArea.getDefaultXAxis()
//					.getScale().setZoom(m_MultipleTimes*m_MaxPoints/mSampleRate , m_MaxPoints/mSampleRate);
				   .getScale().setZoom(m_MultipleTimes*m_MaxPoints/mSampleRate , 5);
         		
         		m_BloodPressDCChartArea.getDefaultXAxis()
				.getScale().setRange(m_XAxisMin, (m_MultipleTimes +1)*m_MaxPoints/mSampleRate);							
 		       m_BloodPressDCChartArea.getDefaultXAxis()
//			     .getScale().setZoom(m_MultipleTimes*m_MaxPoints/mSampleRate , m_MaxPoints/mSampleRate);
					   .getScale().setZoom(m_MultipleTimes*m_MaxPoints/mSampleRate , 5);
            	 }
           	 
           	m_MesaItemsResultLen = mResultsItemNum * MessageInfo.BLOODPRESS_HYBRID_MEASITEM_RESULT_LEN;
                 m_Idx = 0;
                  mResults = new char[m_MesaItemsResultLen];
                   m_DCWaveResult = 0;
					m_ACWaveResult = 0;
					m_MeasItemIdex = 0;
					
					 if(m_TotalDataPackage.length() >0)
                	 {
                	 m_TotalDataPackage.delete(0, m_TotalDataPackage.length()-1);
                	 }
   
					 
                	do {

						mResultsItemNum -= 1;
						mMeasItemIntResults =  m_MeasItemResults[m_MeasItemIdex].GetCharResults();
//						System.arraycopy(mMeasItemIntResults, 0, mResults, m_Idx, MessageInfo.BLOODPRESS_HYBRID_MEASITEM_RESULT_LEN);
						System.arraycopy(mMeasItemIntResults, 0, mResults, 0, MessageInfo.BLOODPRESS_HYBRID_MEASITEM_RESULT_LEN);//edit by xy
						m_Idx += MessageInfo.BLOODPRESS_HYBRID_MEASITEM_RESULT_LEN;
						/*
						 * Message m = updateHandler .obtainMessage(1, Spo2,
						 * hR, pulseWave);
						 * 
						 * Bundle bundle = new Bundle();
						 * bundle.putFloat("value", obj);
						 * 
						 * m.setData(bundle); updateHandler .sendMessage(m);
						 */
					
						 m_SPResult = mMeasItemIntResults[MessageInfo.BLOODPRESS_RESULT_IDX];
						 m_DPResult = mMeasItemIntResults[MessageInfo.BLOODPRESS_RESULT_IDX+1];
//						 m_ACCountResult = mMeasItemIntResults[MessageInfo.BLOODPRESS_RESULT_IDX+2];
						 
							m_TotalDataPackage.append(m_SPResult + " ");
							m_TotalDataPackage.append(m_DPResult + " ");
//							m_TotalDataPackage.append(m_ACCountResult + " ");
						if(m_SPResult != 65535)
						{
						
						m_SP.setText(String.valueOf(m_SPResult));
						}
						if(m_DPResult != 65535)
						{
						
						m_DP.setText(String.valueOf(m_DPResult));
						}		
						
						for (int index = 0; index < MessageInfo.BLOODPRESS_WAVEFORM_SAMPLES_NUM_PER_PACKET/2; index++)
						{
						 m_DCWaveResult = mResults[MessageInfo.BLOODPRESS_WAVEFORM_RESULT_STARTIDX + index];
						 m_ACWaveResult = mResults[MessageInfo.BLOODPRESS_WAVEFORM_RESULT_STARTIDX +
						                            MessageInfo.BLOODPRESS_WAVEFORM_SAMPLES_NUM_PER_DATA +index];

							// 这个m_TotalDataPackage是写入用的。所以是一个AC一个DC
							m_TotalDataPackage.append(m_ACWaveResult + " ");
							m_TotalDataPackage.append(m_DCWaveResult + " ");

//
//							if(index  == 0)
//							{
//								m_DCTargetCollection.addXY(m_PointsCounter / mSampleRate, m_DCWaveResult);
//								m_ACTargetCollection.addXY(m_PointsCounter / mSampleRate, m_ACWaveResult);
//								m_PointsCounter++;
//							}
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
                        
                	m_Idx = 0;
					
					 // Fist Time, do not send it. Maybe we can also do it like the MessageProcessThread.
					 // To start this thread only when received the broadcast from MobileMedical Activity.
                	 Intent resultDislayedInformIntent = new Intent();  
        		     resultDislayedInformIntent.putExtra(ConstDef.CMD, ConstDef.MEAS_RETS_DISPLAY_STATE);  
        		     resultDislayedInformIntent.putExtra(ConstDef.RESULTS_DISPLAYED, true); 				             
        		     resultDislayedInformIntent.setAction(ConstDef.MEAS_RESULTS_DISPLAY_STATE_BROADCAST_MESSAGE);  
    	             sendBroadcast(resultDislayedInformIntent);
    	             if (BuildConfig.DEBUG) {
    	             Log.i(TAG, "MEAS_RESULTS_DISPLAY SEND");
    	             }
                }
           
             // Can add some other code here to trigger the new statistical view.
        }
    };

   
        }
