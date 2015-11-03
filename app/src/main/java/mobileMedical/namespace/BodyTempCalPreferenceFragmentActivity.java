package mobileMedical.namespace;


import devDataType.Parameters.FloatParameter;
import mobileMedical.Algorithm.General.QR;
import mobileMedical.Message.BodyTempSensorCalResult;
import mobileMedical.Message.GWInfoResult;
import mobileMedical.Message.MessageData;
import mobileMedical.Message.ParameterDataKeys;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public final class BodyTempCalPreferenceFragmentActivity extends PreferenceFragment implements OnPreferenceChangeListener, OnClickListener{
	 
    private static final int MENU_DO_BODYTEMP_CAL = Menu.FIRST;
    private static final int MENU_DEL_ONE_BODYTEMP_CAL = Menu.FIRST+1;
    private float[] mBodyTempCoeff = new float[3];
    private static Activity mActivity;
    private static BodyTempCalDataPreference[] mBodyTempCalDataPreferenceList = new BodyTempCalDataPreference[50];
	private static int  mBodyTempCalDataPreferenceListIndex = 0;
    private PreferenceGroup mBodyTempCalPreferenceCategory;
	 private PreferenceGroup mBodyTempCalCoeffPreferenceCategory;
	 
	 // BodyTempCalCoeff Category
	 private  static BodyTempCalCoeffsPreference mBodyTempCalCoeffsPreference;

	 private  static ExtendDiagPreference mSendBodyTempCalCoeffsPreference;
	 private  static ExtendDiagPreference mCalcBodyTempCalCoeffsPreference;
	 
	// BodyTempCal Category
	 private   ExtendDiagPreference mClearAllBodyTempCalDataPreference;
	 private   BodyTempCalParmsNamePreference mBodyTempCalNamePreference;
	 
	 private static final String TAG = "BodyTempCalPreferenceFragmentActivity";
	    
	 private static final boolean D = true;
	 private static BodyTempSensorCalResult  bodyTempCalResults = null;
	 
	   @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        mActivity = getActivity();
	        addPreferencesFromResource(R.xml.bodytemp_cal_preference);
	        final PreferenceScreen preferenceScreen = getPreferenceScreen();
	        mBodyTempCalCoeffPreferenceCategory = new PreferenceCategory(mActivity);
	        mBodyTempCalCoeffPreferenceCategory.setTitle(R.string.bodyTempEff);
		    preferenceScreen.addPreference(mBodyTempCalCoeffPreferenceCategory);
		    mBodyTempCalCoeffPreferenceCategory.setEnabled(true);
	        
	        
	        mBodyTempCalPreferenceCategory = new PreferenceCategory(mActivity);
	        mBodyTempCalPreferenceCategory.setTitle(R.string.bodyTempCal_title);
		    preferenceScreen.addPreference(mBodyTempCalPreferenceCategory);
		    mBodyTempCalPreferenceCategory.setEnabled(true);
		    
		    IntentFilter filter = new IntentFilter(ConstDef.BODYTEMP_CAL_RESULTS_BROADCAST_MESSAGE);        
		    mActivity.registerReceiver(mReceiver,filter);  
	        
		    setHasOptionsMenu(true);
	   }

	   @Override
	    public synchronized void onResume() {
	        super.onResume();
	        final PreferenceScreen preferenceScreen = getPreferenceScreen();
	        

	        if(mBodyTempCalCoeffsPreference == null)
	        {
	        mBodyTempCalCoeffsPreference = new BodyTempCalCoeffsPreference(mActivity, new float[]{0.0f,0.0f,0.0f});
	        mBodyTempCalCoeffsPreference.setKey(ConstDef.BODYTEMPCAL_CalCoeff_KEY_STRING);
	        mBodyTempCalCoeffsPreference.setSelectable(true);
	        }
	        
	        
	        if(mCalcBodyTempCalCoeffsPreference == null)
	        {
	        mCalcBodyTempCalCoeffsPreference = new ExtendDiagPreference(mActivity, null);
	        mCalcBodyTempCalCoeffsPreference.setKey(ConstDef.BODYTEMPCAL_CalcCalCoeff_KEY_STRING);
	        mCalcBodyTempCalCoeffsPreference.setTitle(R.string.calcBodyTempEff);
	        mCalcBodyTempCalCoeffsPreference.setSummary(R.string.calcBodyTempEff);
	        mCalcBodyTempCalCoeffsPreference.setDialogTitle(R.string.calcBodyTempEff);
	        mCalcBodyTempCalCoeffsPreference.setDialogMessage(R.string.calcBodyTempEff);
	        mCalcBodyTempCalCoeffsPreference.setOnPreferenceChangeListener(this);
	        mCalcBodyTempCalCoeffsPreference.setOnClickListener(this);
	        }
	        
	        if(mSendBodyTempCalCoeffsPreference == null)
	        {
	        mSendBodyTempCalCoeffsPreference = new ExtendDiagPreference(mActivity, null);
	        mSendBodyTempCalCoeffsPreference.setKey(ConstDef.BODYTEMPCAL_SendCalCoeff_KEY_STRING);
	        mSendBodyTempCalCoeffsPreference.setTitle(R.string.sendBodyTempEff);
	        mSendBodyTempCalCoeffsPreference.setSummary(R.string.sendBodyTempEff);
	        mSendBodyTempCalCoeffsPreference.setDialogTitle(R.string.sendBodyTempEff);
	        mSendBodyTempCalCoeffsPreference.setDialogMessage(R.string.sendBodyTempEff);
	        mSendBodyTempCalCoeffsPreference.setOnPreferenceChangeListener(this);
	        mSendBodyTempCalCoeffsPreference.setOnClickListener(this);
	        }
	        
	        mBodyTempCalCoeffPreferenceCategory.addPreference(mBodyTempCalCoeffsPreference);
	        mBodyTempCalCoeffPreferenceCategory.addPreference(mCalcBodyTempCalCoeffsPreference);
	        mBodyTempCalCoeffPreferenceCategory.addPreference(mSendBodyTempCalCoeffsPreference);
	        
	        
	        if(mClearAllBodyTempCalDataPreference == null)
	        {
	        mClearAllBodyTempCalDataPreference = new ExtendDiagPreference(mActivity, null);
	        mClearAllBodyTempCalDataPreference.setKey(ConstDef.BODYTEMPCAL_ClearAllCalDat_KEY_STRING);
	        mClearAllBodyTempCalDataPreference.setTitle(R.string.clearAllBodyTempData);
	        mClearAllBodyTempCalDataPreference.setSummary(R.string.clearAllBodyTempData);
	        mClearAllBodyTempCalDataPreference.setDialogTitle(R.string.clearAllBodyTempData);
	        mClearAllBodyTempCalDataPreference.setDialogMessage(R.string.clearAllBodyTempDataMessage);
	        mClearAllBodyTempCalDataPreference.setOnPreferenceChangeListener(this);
	        mClearAllBodyTempCalDataPreference.setOnClickListener(this);
	        }
	        
	        if(mBodyTempCalNamePreference == null)
	        {
	        mBodyTempCalNamePreference = new BodyTempCalParmsNamePreference(mActivity, new String[]{getResources().getString(R.string.coldEndTemp),getResources().getString(R.string.heatEndTemp),getResources().getString(R.string.referTemp)});
	        mBodyTempCalNamePreference.setKey(ConstDef.BODYTEMPCAL_CalDataName_KEY_STRING);
	        }
	        
	        mBodyTempCalPreferenceCategory.removeAll();
	        mBodyTempCalPreferenceCategory.addPreference(mClearAllBodyTempCalDataPreference);
	        mBodyTempCalPreferenceCategory.addPreference(mBodyTempCalNamePreference);

	        if(mBodyTempCalDataPreferenceListIndex > 0)
	        {
	        	for(int idx = 0; idx < mBodyTempCalDataPreferenceListIndex; idx++)
	        	{
	        		mBodyTempCalPreferenceCategory.addPreference(mBodyTempCalDataPreferenceList[idx]);
	        	}
	        }
	        
	   }

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		  if(dialog.equals(mCalcBodyTempCalCoeffsPreference.getDialog()))
	        {
	        	
	            if(which ==  DialogInterface.BUTTON_POSITIVE)
	            {
	            	if (mBodyTempCalDataPreferenceListIndex > 0)
	            	{
	            		CalcBodyTempCoeff();
	            		mBodyTempCalCoeffsPreference.setParmsValues(mBodyTempCoeff);
	            		
	            	/*	float temp = (float) mBodyTempCoeff[0];
	            		((FloatParameter) MessageData.parmsDataHashMap
	            				.get(ParameterDataKeys.BodyTempCalCoeffA)).SetValue(temp);
	            		temp = (float) mBodyTempCoeff[1];
	            		((FloatParameter) MessageData.parmsDataHashMap
	            				.get(ParameterDataKeys.BodyTempCalCoeffB)).SetValue(temp);
	            		temp = (float) mBodyTempCoeff[2];
	            		((FloatParameter) MessageData.parmsDataHashMap
	            				.get(ParameterDataKeys.BodyTempCalCoeffC)).SetValue(temp);
	            		
	            		 Intent bodyTempCoeffIntent = new Intent();  				             
	            		 bodyTempCoeffIntent.setAction(ConstDef.BODYTEMP_CAL_COEFF_CONFIG_REQ_BROADCAST_MESSAGE);  
	                     mActivity.sendBroadcast(bodyTempCoeffIntent);*/
	            		
	            	}
	            }
	        }
		  else if (dialog.equals(mClearAllBodyTempCalDataPreference.getDialog()))
		  {
			  if(which ==  DialogInterface.BUTTON_POSITIVE)
	            {
				  if (mBodyTempCalDataPreferenceListIndex > 0)
				  {
	            	for(int idx = 0; idx < mBodyTempCalDataPreferenceListIndex; idx++)
	            	{
	            		mBodyTempCalPreferenceCategory.removePreference(mBodyTempCalDataPreferenceList[idx]);
	            	}
	            	mBodyTempCalDataPreferenceListIndex =0;
				  }
	            }
		  }
		  else if (dialog.equals(mSendBodyTempCalCoeffsPreference.getDialog()))
		  {

	            if(which ==  DialogInterface.BUTTON_POSITIVE)
	            {
	            	 Intent sendBodyTempCalCoeffsIntent = new Intent();  
	            	 float[] coeffs = mBodyTempCalCoeffsPreference.getParmsValues();
	            	 sendBodyTempCalCoeffsIntent.setAction(ConstDef.BODYTEMP_CAL_COEFF_CONFIG_REQ_BROADCAST_MESSAGE); 
	            	 sendBodyTempCalCoeffsIntent.putExtra(ConstDef.BODYTEMP_CAL_COEFFS, coeffs);  
                     mActivity.sendBroadcast(sendBodyTempCalCoeffsIntent);
	            }
		  }
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_DO_BODYTEMP_CAL:
                
                   DoBodyTepmCal();

                return true;
            case MENU_DEL_ONE_BODYTEMP_CAL:
                 DelOneBodyTempCal();
            

            return true;

        }
        return super.onOptionsItemSelected(item);
    }
	
	 public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	        
	        menu.add(Menu.NONE, MENU_DO_BODYTEMP_CAL, 0, R.string.doBodyTemp)
	                .setEnabled(true)
	                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	        
	        menu.add(Menu.NONE, MENU_DEL_ONE_BODYTEMP_CAL, 1, R.string.clearBodyTempData)
	        .setEnabled(true)
	        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

	        super.onCreateOptionsMenu(menu, inflater);
	    	
	    }
	 
	 
	  private void DoBodyTepmCal() {
		  if(mBodyTempCalDataPreferenceListIndex < 49)
		  {
			  
			  
		/*  float a = 1.1f;
		  float b= 2.2f;
		  float c =3.3f;
		  float codTemp = mBodyTempCalDataPreferenceListIndex +1;
		  float heatTemp = mBodyTempCalDataPreferenceListIndex +2;
		  float refTemp = codTemp *a+ heatTemp *b +c; 
		  float[] valuesStrings = new float[]{codTemp,heatTemp,refTemp};
		  mBodyTempCalDataPreferenceList[mBodyTempCalDataPreferenceListIndex] = new BodyTempCalDataPreference(mActivity,valuesStrings);
		  mBodyTempCalDataPreferenceList[mBodyTempCalDataPreferenceListIndex].setKey(ConstDef.BODYTEMPCAL_CalData_KEY_STRING + mBodyTempCalDataPreferenceListIndex);
		  mBodyTempCalPreferenceCategory.addPreference(mBodyTempCalDataPreferenceList[mBodyTempCalDataPreferenceListIndex]);
		  mBodyTempCalDataPreferenceListIndex++;*/
		  
		  Intent bodyTempCalIntent = new Intent();  				             
     	   bodyTempCalIntent.setAction(ConstDef.BODYTEMP_CAL_REQ_BROADCAST_MESSAGE); 
          mActivity.sendBroadcast(bodyTempCalIntent);
		  
		  }
	  }
	  
	  private void DelOneBodyTempCal() {
		  if(mBodyTempCalDataPreferenceListIndex > 0)
		  {
			  mBodyTempCalDataPreferenceListIndex--;
		    mBodyTempCalPreferenceCategory.removePreference(mBodyTempCalDataPreferenceList[mBodyTempCalDataPreferenceListIndex]);
		  }
	  }
	  
	  private void CalcBodyTempCoeff() 
	  {
		  if (mBodyTempCalDataPreferenceListIndex > 0)
		  {
			  int dataArrayLen = mBodyTempCalDataPreferenceListIndex;
			  int coeffNum = 3;
			  float[] tempData;
			  double[] refTempArrray = new double[dataArrayLen];
			  double[][] dataMatrix = new double[dataArrayLen][coeffNum];
			  for (int idx = 0; idx < dataArrayLen; idx++)
			  {
				  tempData = mBodyTempCalDataPreferenceList[idx].getValues();
				  dataMatrix[idx][0] = tempData[0];
				  dataMatrix[idx][1] = tempData[1];
				  dataMatrix[idx][2] = 1;
				  refTempArrray[idx] = tempData[2];	  
			  }
			  
			  QR lsEstimator = new QR(dataMatrix, refTempArrray);
			  
			  double[] tempRets = lsEstimator.compute();
			  mBodyTempCoeff[0] = (float) tempRets[0];
			  mBodyTempCoeff[1] = (float) tempRets[1];
			  mBodyTempCoeff[2] = (float) tempRets[2];
			  
			  
		  }
	  }
	  
	  // The BroadcastReceiver that listens for discovered devices and
	    // changes the title when discovery is finished
	    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            String action = intent.getAction();
	            
	            if(action.equals(ConstDef.BODYTEMP_CAL_RESULTS_BROADCAST_MESSAGE))
	            {
	            	  // Send message to MessageProcess to let it set its state
	              	 Intent resultDislayedInformIntent = new Intent();  
	      		     resultDislayedInformIntent.putExtra(ConstDef.CMD, ConstDef.MEAS_RETS_DISPLAY_STATE);  
	      		     resultDislayedInformIntent.putExtra(ConstDef.RESULTS_DISPLAYED, true); 				             
	      		     resultDislayedInformIntent.setAction(ConstDef.MEAS_RESULTS_DISPLAY_STATE_BROADCAST_MESSAGE);  
	   	             mActivity.sendBroadcast(resultDislayedInformIntent);
	   	             
	   	             bodyTempCalResults = intent.getParcelableExtra(ConstDef.BODYTEMP_CAL_TEMP_RESULTS);
	   	             float[]values = new float[3];
	   	          values[0] = bodyTempCalResults.GetColdTDegree();
	   	          values[1] = bodyTempCalResults.GetHotDegree();
	   	          values[2] = 0.0f;
	   	          if(mBodyTempCalDataPreferenceListIndex < 49)
	   			  {
	   	        	 mBodyTempCalDataPreferenceList[mBodyTempCalDataPreferenceListIndex] = new BodyTempCalDataPreference(mActivity,values);
		   			  mBodyTempCalDataPreferenceList[mBodyTempCalDataPreferenceListIndex].setKey(ConstDef.BODYTEMPCAL_CalData_KEY_STRING + mBodyTempCalDataPreferenceListIndex);
		   			  mBodyTempCalPreferenceCategory.addPreference(mBodyTempCalDataPreferenceList[mBodyTempCalDataPreferenceListIndex]);
		   			  mBodyTempCalDataPreferenceListIndex++;	  
	   				  
	   			/*  float a = 1.1f;
	   			  float b= 2.2f;
	   			  float c =3.3f;
	   			  float codTemp = mBodyTempCalDataPreferenceListIndex +1;
	   			  float heatTemp = mBodyTempCalDataPreferenceListIndex +2;
	   			  float refTemp = codTemp *a+ heatTemp *b +c; 
	   			  float[] valuesStrings = new float[]{codTemp,heatTemp,refTemp};
	   			  mBodyTempCalDataPreferenceList[mBodyTempCalDataPreferenceListIndex] = new BodyTempCalDataPreference(mActivity,valuesStrings);
	   			  mBodyTempCalDataPreferenceList[mBodyTempCalDataPreferenceListIndex].setKey(ConstDef.BODYTEMPCAL_CalData_KEY_STRING + mBodyTempCalDataPreferenceListIndex);
	   			  mBodyTempCalPreferenceCategory.addPreference(mBodyTempCalDataPreferenceList[mBodyTempCalDataPreferenceListIndex]);
	   			  mBodyTempCalDataPreferenceListIndex++;*/
	   			  }
	            }
	            
	        }
	    };
}
