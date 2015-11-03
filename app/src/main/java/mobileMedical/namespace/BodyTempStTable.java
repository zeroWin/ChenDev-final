package mobileMedical.namespace;
import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import mobileMedical.namespace.TableAdapter.TableCell;
import mobileMedical.namespace.TableAdapter.TableRow;

public class BodyTempStTable extends Activity {
	private double m_start = 0; 
	private int duration = 0;//default 1 hour
	private final boolean D = true;
	private final String TAG = "BodyTemp Statistics Table";
	//def for time setting
	public static final String SETTING_INFOS = "SETTINGInfos"; 
	private boolean currentTime;
    private int statisticYear;
    private int statisticMonth;
    private int statisticDate;
    private int statisticHour;
    public static final String STATISTIC_TIME_CHECK_BOX = "STATISTICTIMECHECKBOX";
	public static final String STATISTIC_TIME_YEAR = "STATISTICTIMEYEAR";
	public static final String STATISTIC_TIME_MONTH = "STATISTICTIMEMONTH";
	public static final String STATISTIC_TIME_DATE = "STATISTICTIMEDATE";
	public static final String STATISTIC_TIME_HOUR = "STATISTICTIMEHOUR";  

	
    public static final String MORNING_HOUR_FROM = "MORNINGHOURFROM";
	public static final String MORNING_MIN_FROM = "MORNINGMINFROM";
	public static final String MORNING_HOUR_TO = "MORNINGHOURTO";
	public static final String MORNING_MIN_TO = "MORNINGMINTO";
	
	public static final String NOON_HOUR_FROM = "NOONHOURFROM";
	public static final String NOON_MIN_FROM = "NOONMINFROM";
	public static final String NOON_HOUR_TO = "NOONHOURTO";
	public static final String NOON_MIN_TO = "NOONMINTO";
	
	public static final String EVENING_HOUR_FROM = "EVENINGHOURFROM";
	public static final String EVENING_MIN_FROM = "EVENINGMINFROM";
	public static final String EVENING_HOUR_TO = "EVENINGHOURTO";
	public static final String EVENING_MIN_TO = "EVENINGMINTO";
	
	public static final String NIGHT_HOUR_FROM = "NIGHTHOURFROM";
	public static final String NIGHT_MIN_FROM = "NIGHTMINFROM";
	public static final String NIGHT_HOUR_TO = "NIGHTHOURTO";
	public static final String NIGHT_MIN_TO = "NIGHTMINTO";
	
	public static final String ANALYSIS_HOUR_FROM = "ANALYSISHOURFROM";
	public static final String ANALYSIS_MIN_FROM = "ANALYSISMINFROM";
	public static final String ANALYSIS_HOUR_TO = "ANALYSISHOURTO";
	public static final String ANALYSIS_MIN_TO = "ANALYSISMINTO";
	private  float[] btMorningHour;
	private  float[] btNoonHour;
	private  float[] btEveningHour;
	private  float[] btNightHour;
	private  float[] btTotalHour;
	private  float[] btBaseHour;
	
	//day
	private  float[] btMorningDay;
	private  float[] btNoonDay;
	private  float[] btEveningDay;
	private  float[] btNightDay;
	private  float[] btTotalDay;
	private  float[] btBaseDay;
	
	//week
	private  float[] btMorningWeek;
	private  float[] btNoonWeek;
	private  float[] btEveningWeek;
	private  float[] btNightWeek;
	private  float[] btTotalWeek;
	private  float[] btBaseWeek;
	
	//Month
	private  float[] btMorningMonth;
	private  float[] btNoonMonth;
	private  float[] btEveningMonth;
	private  float[] btNightMonth;
	private  float[] btTotalMonth;
	private  float[] btBaseMonth;
	
    
    
    float morningAveHour = Float.NaN;
    float noonAveHour = Float.NaN;
    float eveningAveHour = Float.NaN;
    float nightAveHour = Float.NaN;
    float totalAveHour = Float.NaN;
    float baseAveHour = Float.NaN;
    float morningSqHour = Float.NaN;
    float noonSqHour = Float.NaN;
    float eveningSqHour = Float.NaN;
    float nightSqHour = Float.NaN;
    float totalSqHour = Float.NaN;
    float baseSqHour = Float.NaN;
    
    //day
    
    float morningAveDay = Float.NaN;
    float noonAveDay = Float.NaN;
    float eveningAveDay = Float.NaN;
    float nightAveDay = Float.NaN;
    float totalAveDay = Float.NaN;
    float baseAveDay = Float.NaN;
    float morningSqDay = Float.NaN;
    float noonSqDay = Float.NaN;
    float eveningSqDay = Float.NaN;
    float nightSqDay = Float.NaN;
    float totalSqDay = Float.NaN;
    float baseSqDay = Float.NaN;
    
    //week
    
    float morningAveWeek = Float.NaN;
    float noonAveWeek = Float.NaN;
    float eveningAveWeek = Float.NaN;
    float nightAveWeek = Float.NaN;
    float totalAveWeek = Float.NaN;
    float baseAveWeek = Float.NaN;
    float morningSqWeek = Float.NaN;
    float noonSqWeek = Float.NaN;
    float eveningSqWeek = Float.NaN;
    float nightSqWeek = Float.NaN;
    float totalSqWeek = Float.NaN;
    float baseSqWeek = Float.NaN;
    
    //Month
    
    float morningAveMonth = Float.NaN;
    float noonAveMonth = Float.NaN;
    float eveningAveMonth = Float.NaN;
    float nightAveMonth = Float.NaN;
    float totalAveMonth = Float.NaN;
    float baseAveMonth = Float.NaN;
    float morningSqMonth = Float.NaN;
    float noonSqMonth = Float.NaN;
    float eveningSqMonth = Float.NaN;
    float nightSqMonth = Float.NaN;
    float totalSqMonth = Float.NaN;
    float baseSqMonth = Float.NaN;
	
	private ArrayList<TableRow> table;
	private TableAdapter tableAdapter;
	
	boDb btDbHelper = new boDb(this, "bloodox.db", null, 1);
	
	

    
    @Override
    public void onStart() {
        super.onStart();

        
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
     

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
        
       
    }
    

	 @Override
	    public void onCreate(Bundle savedInstanceState) {
		 /** Called when the activity is first created. */
		       
		    	super.onCreate(savedInstanceState);
		    	setContentView(R.layout.body_temp_st_table);
		    	
		    	//
		        int morningCountHour =0;
		        int noonCountHour =0;
		        int eveningCountHour =0;
		        int nightCountHour =0;
		        int totalCountHour =0;
		        int baseCountHour = 0;
		        
		        int morningCountWeek =0;
		        int noonCountWeek =0;
		        int eveningCountWeek =0;
		        int nightCountWeek =0;
		        int totalCountWeek =0;
		        int baseCountWeek = 0;
		        
		        int morningCountDay =0;
		        int noonCountDay =0;
		        int eveningCountDay =0;
		        int nightCountDay =0;
		        int totalCountDay =0;
		        int baseCountDay = 0;
		        
		        int morningCountMonth =0;
		        int noonCountMonth =0;
		        int eveningCountMonth =0;
		        int nightCountMonth =0;
		        int totalCountMonth =0;
		        int baseCountMonth = 0;
		        
		        Cursor cursor = btDbHelper.getAllItemsBt();
		        
		        
		        SharedPreferences settings = getSharedPreferences(SETTING_INFOS, 0);
		        
		        int morningFromHour = settings.getInt(MORNING_HOUR_FROM,6);
		        int morningFromMin = settings.getInt(MORNING_MIN_FROM,0);
		        int morningToHour = settings.getInt(MORNING_HOUR_TO,11);
		        int morningToMin = settings.getInt(MORNING_MIN_TO,0);
		        int noonFromHour = settings.getInt(NOON_HOUR_FROM,11);
		        int noonFromMin = settings.getInt(NOON_MIN_FROM,0);
		        int noonToHour = settings.getInt(NOON_HOUR_TO,18);
		        int noonToMin = settings.getInt(NOON_MIN_TO,0);
		        int eveningFromHour = settings.getInt(EVENING_HOUR_FROM,18);
		        int eveningFromMin = settings.getInt(EVENING_MIN_FROM,0);
		        int eveningToHour = settings.getInt(EVENING_HOUR_TO,20);
		        int eveningToMin = settings.getInt(EVENING_MIN_TO,0);
		        int nightFromHour = settings.getInt(NIGHT_HOUR_FROM,20);
		        int nightFromMin = settings.getInt(NIGHT_MIN_FROM,0);
		        int nightToHour = settings.getInt(NIGHT_HOUR_TO,24);
		        int nightToMin = settings.getInt(NIGHT_MIN_TO,0);
		        int analyFromHour = settings.getInt(ANALYSIS_HOUR_FROM,11);
		        int analyFromMin = settings.getInt(ANALYSIS_MIN_FROM,0);
		        int analyToHour = settings.getInt(ANALYSIS_MIN_FROM,18);
		        int analyToMin = settings.getInt(ANALYSIS_MIN_TO,0);

		        
		        float bt = 0;
		 
		        long morningFrom = DateUtils.HOUR_IN_MILLIS * morningFromHour + DateUtils.MINUTE_IN_MILLIS * morningFromMin;
		        long morningTo = DateUtils.HOUR_IN_MILLIS * morningToHour + DateUtils.MINUTE_IN_MILLIS * morningToMin;
		        long noonFrom = DateUtils.HOUR_IN_MILLIS * noonFromHour + DateUtils.MINUTE_IN_MILLIS * noonFromMin;
		        long noonTo = DateUtils.HOUR_IN_MILLIS * noonToHour + DateUtils.MINUTE_IN_MILLIS * noonToMin;
		        long eveningFrom = DateUtils.HOUR_IN_MILLIS * eveningFromHour + DateUtils.MINUTE_IN_MILLIS * eveningFromMin;
		        long eveningTo = DateUtils.HOUR_IN_MILLIS * eveningToHour + DateUtils.MINUTE_IN_MILLIS * eveningToMin;
		        long nightFrom = DateUtils.HOUR_IN_MILLIS * nightFromHour + DateUtils.MINUTE_IN_MILLIS * nightFromMin;
		        long nightTo = DateUtils.HOUR_IN_MILLIS * nightToHour + DateUtils.MINUTE_IN_MILLIS * nightToMin;

		        

		        
		        String myDate ;
		        
		        
		        btMorningHour = new float [cursor.getCount()];
		        btNoonHour = new float [cursor.getCount()];
		        btEveningHour = new float [cursor.getCount()];
		        btNightHour = new float [cursor.getCount()];
		        btTotalHour = new float [cursor.getCount()];
		        
		        btMorningDay = new float [cursor.getCount()];
		        btNoonDay = new float [cursor.getCount()];
		        btEveningDay = new float [cursor.getCount()];
		        btNightDay = new float [cursor.getCount()];
		        btTotalDay = new float [cursor.getCount()];
		        
		        btMorningWeek = new float [cursor.getCount()];
		        btNoonWeek = new float [cursor.getCount()];
		        btEveningWeek = new float [cursor.getCount()];
		        btNightWeek = new float [cursor.getCount()];
		        btTotalWeek = new float [cursor.getCount()];
		        
		        btMorningMonth = new float [cursor.getCount()];
		        btNoonMonth = new float [cursor.getCount()];
		        btEveningMonth = new float [cursor.getCount()];
		        btNightMonth = new float [cursor.getCount()];
		        btTotalMonth = new float [cursor.getCount()];

		        
	              
	              currentTime = settings.getBoolean(STATISTIC_TIME_CHECK_BOX, true);
	              statisticYear = settings.getInt(STATISTIC_TIME_YEAR, 2013);
	              statisticMonth = settings.getInt(STATISTIC_TIME_MONTH, 1);
	              statisticDate = settings.getInt(STATISTIC_TIME_DATE, 1);
	              statisticHour = settings.getInt(STATISTIC_TIME_HOUR, 1);
	              String Year;
	              String Month;
	              String Day;
	              String Hour;
	              SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	              long m_end;
	              if(currentTime == true)
	              {
	            	  m_end = System.currentTimeMillis();
	              }
	              else
	              {
		              Year = String.valueOf(statisticYear);
		              if(statisticMonth > 10)
		      	    	{
		            	  Month = String.valueOf(statisticMonth);
		      	    	}
		      	      else
		      	    	{
		      	    		Month  = "0"+String.valueOf(statisticMonth);
		      	    	}
		              
		              if(statisticDate > 10)
		    	    	{
		            	  Day = String.valueOf(statisticDate);
		    	    	}
		    	      else
		    	    	{
		    	    		Day  = "0"+String.valueOf(statisticDate);
		    	    	}
		              if(statisticHour > 10)
		  	    		{
		            	  Hour = String.valueOf(statisticHour);
		  	    		}
		              else
		  	    		{
		  	    		  Hour  = "0"+String.valueOf(statisticHour);
		  	    		}
		              Date dateEnd=null;
		              try{
		              dateEnd = format.parse( Year+"-"+Month+"-"+Day+" "+Hour+":"+"00" ); 
		              }catch (Exception e) {
		        			// TODO Auto-generated catch block
		        			e.printStackTrace();
		        		};
		              
		              m_end = dateEnd.getTime();
	              }
		        try {
		            
		            
		            while(cursor.moveToNext())
		    		{
		    			
		    			bt = Float.valueOf(cursor.getString(cursor.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS)));
		    			myDate = cursor.getString(cursor.getColumnIndex(ConstDef.DATABASE_FIELD_TIMESTAMP));  
		    			
		    			Date date = format.parse(myDate);
		    			long dateMs = date.getTime();
		    			
		    			dateMs += DateUtils.HOUR_IN_MILLIS * 8;
		    			
		    			Date dateTZ = new Date(dateMs);

		    			
		    			
		    			//only count result in one hour
		    			if(dateMs > (m_end - DateUtils.HOUR_IN_MILLIS) && m_end>=dateMs){
			    			if((dateTZ.getHours()*DateUtils.HOUR_IN_MILLIS+dateTZ.getMinutes()*DateUtils.MINUTE_IN_MILLIS > morningFrom) &&
			    					(dateTZ.getHours()*DateUtils.HOUR_IN_MILLIS+dateTZ.getMinutes()*DateUtils.MINUTE_IN_MILLIS < morningTo))
			    			{
			    				btMorningHour[morningCountHour++] = bt;
			    				if(Float.isNaN(morningAveHour))
			    					morningAveHour = 0;
			    				morningAveHour+=bt;
			    				
			    				btMorningDay[morningCountDay++] = bt;
			    				if(Float.isNaN(morningAveDay))
			    					morningAveDay = 0;
			    				morningAveDay+=bt;
			    				
			    				btMorningWeek[morningCountWeek++] = bt;
			    				if(Float.isNaN(morningAveWeek))
			    					morningAveWeek = 0;
			    				morningAveWeek+=bt;
			    				
			    				btMorningMonth[morningCountMonth++] = bt;
			    				if(Float.isNaN(morningAveMonth))
			    					morningAveMonth = 0;
			    				morningAveMonth+=bt;
			    			}
			    			else if((dateTZ.getHours()*DateUtils.HOUR_IN_MILLIS+dateTZ.getMinutes()*DateUtils.MINUTE_IN_MILLIS > noonFrom) &&
			    					(dateTZ.getHours()*DateUtils.HOUR_IN_MILLIS+dateTZ.getMinutes()*DateUtils.MINUTE_IN_MILLIS < noonTo))
			    			{
			    				btNoonHour[noonCountHour++] = bt;
			    				if(Float.isNaN(noonAveHour))
			    					noonAveHour = 0;
			    				noonAveHour+=bt;
			    				
			    				btNoonDay[noonCountDay++] = bt;
			    				if(Float.isNaN(noonAveDay))
			    					noonAveDay = 0;
			    				noonAveDay+=bt;
			    				
			    				btNoonWeek[noonCountWeek++] = bt;
			    				if(Float.isNaN(noonAveWeek))
			    					noonAveWeek = 0;
			    				noonAveWeek+=bt;
			    				
			    				btNoonMonth[noonCountMonth++] = bt;
			    				if(Float.isNaN(noonAveMonth))
			    					noonAveMonth = 0;
			    				noonAveMonth+=bt;
			    				
			    			}
			    			else if((dateTZ.getHours()*DateUtils.HOUR_IN_MILLIS+dateTZ.getMinutes()*DateUtils.MINUTE_IN_MILLIS > eveningFrom) &&
			    					(dateTZ.getHours()*DateUtils.HOUR_IN_MILLIS+dateTZ.getMinutes()*DateUtils.MINUTE_IN_MILLIS < eveningTo))
			    			{
			    				btEveningHour[eveningCountHour++] = bt;
			    				if(Float.isNaN(eveningAveHour))
			    					eveningAveHour = 0;
			    				eveningAveHour+=bt;
			    				
			    				btEveningDay[eveningCountDay++] = bt;
			    				if(Float.isNaN(eveningAveDay))
			    					eveningAveDay = 0;
			    				eveningAveDay+=bt;
			    				
			    				btEveningWeek[eveningCountWeek++] = bt;
			    				if(Float.isNaN(eveningAveWeek))
			    					eveningAveWeek = 0;
			    				eveningAveWeek+=bt;
			    				
			    				btEveningMonth[eveningCountMonth++] = bt;
			    				if(Float.isNaN(eveningAveMonth))
			    					eveningAveMonth = 0;
			    				eveningAveMonth+=bt;
			    				
			    			}
			    			else if((dateTZ.getHours()*DateUtils.HOUR_IN_MILLIS+dateTZ.getMinutes()*DateUtils.MINUTE_IN_MILLIS > nightFrom) &&
			    					(dateTZ.getHours()*DateUtils.HOUR_IN_MILLIS+dateTZ.getMinutes()*DateUtils.MINUTE_IN_MILLIS < nightTo))
			    			{
			    				btNightHour[nightCountHour++] = bt;
			    				if(Float.isNaN(nightAveHour))
			    					nightAveHour = 0;
			    				nightAveHour+=bt;
			    				
			    				btNightDay[nightCountDay++] = bt;
			    				if(Float.isNaN(nightAveDay))
			    					nightAveDay = 0;
			    				nightAveDay+=bt;
			    				
			    				btNightWeek[nightCountWeek++] = bt;
			    				if(Float.isNaN(nightAveWeek))
			    					nightAveWeek = 0;
			    				nightAveWeek+=bt;
			    				
			    				btNightMonth[nightCountMonth++] = bt;
			    				if(Float.isNaN(nightAveMonth))
			    					nightAveMonth = 0;
			    				nightAveMonth+=bt;
			    			}
			    			if(Float.isNaN(totalAveHour))
			    				totalAveHour = 0;
			    			totalAveHour+=bt;
			    			btTotalHour[totalCountHour++] = bt;
			    			
			    			if(Float.isNaN(totalAveDay))
		    					totalAveDay = 0;
			    			totalAveDay+=bt;
			    			btTotalDay[totalCountDay++] = bt;
			    			
			    			if(Float.isNaN(totalAveWeek))
		    					totalAveWeek = 0;
			    			totalAveWeek+=bt;
			    			btTotalWeek[totalCountWeek++] = bt;
			    			
			    			if(Float.isNaN(totalAveMonth))
		    					totalAveMonth = 0;
			    			totalAveMonth+=bt;
			    			btTotalMonth[totalCountMonth++] = bt;
			    			
		    			}
		    			else if(dateMs > (m_end - DateUtils.DAY_IN_MILLIS)&& m_end>=dateMs)
		    			{
		    				if((dateTZ.getHours()*DateUtils.HOUR_IN_MILLIS+dateTZ.getMinutes()*DateUtils.MINUTE_IN_MILLIS > morningFrom) &&
			    					(dateTZ.getHours()*DateUtils.HOUR_IN_MILLIS+dateTZ.getMinutes()*DateUtils.MINUTE_IN_MILLIS < morningTo))
			    			{
			    				btMorningDay[morningCountDay++] = bt;
			    				if(Float.isNaN(morningAveDay))
			    					morningAveDay = 0;
			    				morningAveDay+=bt;
			    				
			    				btMorningWeek[morningCountWeek++] = bt;
			    				if(Float.isNaN(morningAveWeek))
			    					morningAveWeek = 0;
			    				morningAveWeek+=bt;
			    				
			    				btMorningMonth[morningCountMonth++] = bt;
			    				if(Float.isNaN(morningAveMonth))
			    					morningAveMonth = 0;
			    				morningAveMonth+=bt;
			    				
			    			}
			    			else if((dateTZ.getHours()*DateUtils.HOUR_IN_MILLIS+dateTZ.getMinutes()*DateUtils.MINUTE_IN_MILLIS > noonFrom) &&
			    					(dateTZ.getHours()*DateUtils.HOUR_IN_MILLIS+dateTZ.getMinutes()*DateUtils.MINUTE_IN_MILLIS < noonTo))
			    			{
			    				btNoonDay[noonCountDay++] = bt;
			    				if(Float.isNaN(noonAveDay))
			    					noonAveDay = 0;
			    				noonAveDay+=bt;
			    				
			    				btNoonWeek[noonCountWeek++] = bt;
			    				if(Float.isNaN(noonAveWeek))
			    					noonAveWeek = 0;
			    				noonAveWeek+=bt;
			    				
			    				btNoonMonth[noonCountMonth++] = bt;
			    				if(Float.isNaN(noonAveMonth))
			    					noonAveMonth = 0;
			    				noonAveMonth+=bt;
			    			}
			    			else if((dateTZ.getHours()*DateUtils.HOUR_IN_MILLIS+dateTZ.getMinutes()*DateUtils.MINUTE_IN_MILLIS > eveningFrom) &&
			    					(dateTZ.getHours()*DateUtils.HOUR_IN_MILLIS+dateTZ.getMinutes()*DateUtils.MINUTE_IN_MILLIS < eveningTo))
			    			{
			    				btEveningDay[eveningCountDay++] = bt;
			    				if(Float.isNaN(eveningAveDay))
			    					eveningAveDay = 0;
			    				eveningAveDay+=bt;
			    				
			    				btEveningWeek[eveningCountWeek++] = bt;
			    				if(Float.isNaN(eveningAveWeek))
			    					eveningAveWeek = 0;
			    				eveningAveWeek+=bt;
			    				
			    				btEveningMonth[eveningCountMonth++] = bt;
			    				if(Float.isNaN(eveningAveMonth))
			    					eveningAveMonth = 0;
			    				eveningAveMonth+=bt;
			    				
			    			}
			    			else if((dateTZ.getHours()*DateUtils.HOUR_IN_MILLIS+dateTZ.getMinutes()*DateUtils.MINUTE_IN_MILLIS > nightFrom) &&
			    					(dateTZ.getHours()*DateUtils.HOUR_IN_MILLIS+dateTZ.getMinutes()*DateUtils.MINUTE_IN_MILLIS < nightTo))
			    			{
			    				btNightDay[nightCountDay++] = bt;
			    				if(Float.isNaN(nightAveDay))
			    					nightAveDay = 0;
			    				nightAveDay+=bt;
			    				
			    				btNightWeek[nightCountWeek++] = bt;
			    				if(Float.isNaN(nightAveWeek))
			    					nightAveWeek = 0;
			    				nightAveWeek+=bt;
			    				
			    				btNightMonth[nightCountMonth++] = bt;
			    				if(Float.isNaN(nightAveMonth))
			    					nightAveMonth = 0;
			    				nightAveMonth+=bt;
			    			}
		    				if(Float.isNaN(totalAveDay))
		    					totalAveDay = 0;
			    			totalAveDay+=bt;
			    			btTotalDay[totalCountDay++] = bt;
			    			
			    			if(Float.isNaN(totalAveWeek))
		    					totalAveWeek = 0;
			    			totalAveWeek+=bt;
			    			btTotalWeek[totalCountWeek++] = bt;
			    			
			    			if(Float.isNaN(totalAveMonth))
		    					totalAveMonth = 0;
			    			totalAveMonth+=bt;
			    			btTotalMonth[totalCountMonth++] = bt;
		    			}
		    			else if(dateMs > (m_end - DateUtils.WEEK_IN_MILLIS)&& m_end>=dateMs)
		    			{
		    				if((dateTZ.getHours()*DateUtils.HOUR_IN_MILLIS+dateTZ.getMinutes()*DateUtils.MINUTE_IN_MILLIS > morningFrom) &&
			    					(dateTZ.getHours()*DateUtils.HOUR_IN_MILLIS+dateTZ.getMinutes()*DateUtils.MINUTE_IN_MILLIS < morningTo))
			    			{
			    				btMorningWeek[morningCountWeek++] = bt;
			    				if(Float.isNaN(morningAveWeek))
			    					morningAveWeek = 0;
			    				morningAveWeek+=bt;
			    				
			    				btMorningMonth[morningCountMonth++] = bt;
			    				if(Float.isNaN(morningAveMonth))
			    					morningAveMonth = 0;
			    				morningAveMonth+=bt;
			    				
			    			}
			    			else if((dateTZ.getHours()*DateUtils.HOUR_IN_MILLIS+dateTZ.getMinutes()*DateUtils.MINUTE_IN_MILLIS > noonFrom) &&
			    					(dateTZ.getHours()*DateUtils.HOUR_IN_MILLIS+dateTZ.getMinutes()*DateUtils.MINUTE_IN_MILLIS < noonTo))
			    			{
			    				btNoonWeek[noonCountWeek++] = bt;
			    				if(Float.isNaN(noonAveWeek))
			    					noonAveWeek = 0;
			    				noonAveWeek+=bt;
			    				
			    				btNoonMonth[noonCountMonth++] = bt;
			    				if(Float.isNaN(noonAveMonth))
			    					noonAveMonth = 0;
			    				noonAveMonth+=bt;
			    			}
			    			else if((dateTZ.getHours()*DateUtils.HOUR_IN_MILLIS+dateTZ.getMinutes()*DateUtils.MINUTE_IN_MILLIS > eveningFrom) &&
			    					(dateTZ.getHours()*DateUtils.HOUR_IN_MILLIS+dateTZ.getMinutes()*DateUtils.MINUTE_IN_MILLIS < eveningTo))
			    			{
			    				btEveningWeek[eveningCountWeek++] = bt;
			    				if(Float.isNaN(eveningAveWeek))
			    					eveningAveWeek = 0;
			    				eveningAveWeek+=bt;
			    				
			    				btEveningMonth[eveningCountMonth++] = bt;
			    				if(Float.isNaN(eveningAveMonth))
			    					eveningAveMonth = 0;
			    				eveningAveMonth+=bt;
			    				
			    			}
			    			else if((dateTZ.getHours()*DateUtils.HOUR_IN_MILLIS+dateTZ.getMinutes()*DateUtils.MINUTE_IN_MILLIS > nightFrom) &&
			    					(dateTZ.getHours()*DateUtils.HOUR_IN_MILLIS+dateTZ.getMinutes()*DateUtils.MINUTE_IN_MILLIS < nightTo))
			    			{
			    				btNightWeek[nightCountWeek++] = bt;
			    				if(Float.isNaN(nightAveWeek))
			    					nightAveWeek = 0;
			    				nightAveWeek+=bt;
			    				
			    				btNightMonth[nightCountMonth++] = bt;
			    				if(Float.isNaN(nightAveMonth))
			    					nightAveMonth = 0;
			    				nightAveMonth+=bt;
			    			}
		    				if(Float.isNaN(totalAveWeek))
		    					totalAveWeek = 0;
			    			totalAveWeek+=bt;
			    			btTotalWeek[totalCountWeek++] = bt;
			    			
			    			if(Float.isNaN(totalAveMonth))
		    					totalAveMonth = 0;
			    			totalAveMonth+=bt;
			    			btTotalMonth[totalCountMonth++] = bt;
		    			}
		    			else if(dateMs > (m_end - DateUtils.DAY_IN_MILLIS * 30)&& m_end>=dateMs)
		    			{
		    				if((dateTZ.getHours()*DateUtils.HOUR_IN_MILLIS+dateTZ.getMinutes()*DateUtils.MINUTE_IN_MILLIS > morningFrom) &&
			    					(dateTZ.getHours()*DateUtils.HOUR_IN_MILLIS+dateTZ.getMinutes()*DateUtils.MINUTE_IN_MILLIS < morningTo))
			    			{
			    				btMorningMonth[morningCountMonth++] = bt;
			    				if(Float.isNaN(morningAveMonth))
			    					morningAveMonth = 0;
			    				morningAveMonth+=bt;
			    			}
			    			else if((dateTZ.getHours()*DateUtils.HOUR_IN_MILLIS+dateTZ.getMinutes()*DateUtils.MINUTE_IN_MILLIS > noonFrom) &&
			    					(dateTZ.getHours()*DateUtils.HOUR_IN_MILLIS+dateTZ.getMinutes()*DateUtils.MINUTE_IN_MILLIS < noonTo))
			    			{
			    				btNoonMonth[noonCountMonth++] = bt;
			    				if(Float.isNaN(noonAveMonth))
			    					noonAveMonth = 0;
			    				noonAveMonth+=bt;
			    			}
			    			else if((dateTZ.getHours()*DateUtils.HOUR_IN_MILLIS+dateTZ.getMinutes()*DateUtils.MINUTE_IN_MILLIS > eveningFrom) &&
			    					(dateTZ.getHours()*DateUtils.HOUR_IN_MILLIS+dateTZ.getMinutes()*DateUtils.MINUTE_IN_MILLIS < eveningTo))
			    			{
			    				btEveningMonth[eveningCountMonth++] = bt;
			    				if(Float.isNaN(eveningAveMonth))
			    					eveningAveMonth = 0;
			    				eveningAveMonth+=bt;
			    				
			    			}
			    			else if((dateTZ.getHours()*DateUtils.HOUR_IN_MILLIS+dateTZ.getMinutes()*DateUtils.MINUTE_IN_MILLIS > nightFrom) &&
			    					(dateTZ.getHours()*DateUtils.HOUR_IN_MILLIS+dateTZ.getMinutes()*DateUtils.MINUTE_IN_MILLIS < nightTo))
			    			{
			    				btNightMonth[nightCountMonth++] = bt;
			    				if(Float.isNaN(nightAveMonth))
			    					nightAveMonth = 0;
			    				nightAveMonth+=bt;
			    			}
		    				if(Float.isNaN(totalAveMonth))
		    					totalAveMonth = 0;
			    			totalAveMonth+=bt;
			    			btTotalMonth[totalCountMonth++] = bt;
		    			}
		    			
		    			
		    		}
		        }catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
		    		cursor.close();
		    		//cursorTiming.close();
		    		if(morningCountHour > 0)
		    		{
		    			morningAveHour = morningAveHour/morningCountHour;
		    			morningSqHour = calculateSq(btMorningHour,morningCountHour,morningAveHour);
		    		}
		    		if(noonCountHour > 0)
		    		{
		    			noonAveHour = noonAveHour/noonCountHour;
		    			noonSqHour = calculateSq(btNoonHour,noonCountHour,noonAveHour);
		    		}
		    		if(eveningCountHour > 0)
		    		{
		    			eveningAveHour = eveningAveHour/eveningCountHour;
		    			eveningSqHour = calculateSq(btEveningHour,eveningCountHour,eveningAveHour);
		    		}
		    		if(nightCountHour > 0)
		    		{
		    			nightAveHour = nightAveHour/nightCountHour;
		    			nightSqHour = calculateSq(btNightHour,nightCountHour,nightAveHour);
		    		}
		    		if(totalCountHour > 0)
		    		{
		    			totalAveHour = totalAveHour/totalCountHour;
		    			totalSqHour = calculateSq(btTotalHour,totalCountHour,totalAveHour);
		    		}
		    		
		     	    //day
		    		if(morningCountDay > 0)
		    		{
		    			morningAveDay = morningAveDay/morningCountDay;
		    			morningSqDay = calculateSq(btMorningDay,morningCountDay,morningAveDay);
		    		}
		    		if(noonCountDay > 0)
		    		{
		    			noonAveDay = noonAveDay/noonCountDay;
		    			noonSqDay = calculateSq(btNoonDay,noonCountDay,noonAveDay);
		    		}
		    		if(eveningCountDay > 0)
		    		{
		    			eveningAveDay = eveningAveDay/eveningCountDay;
		    			eveningSqDay = calculateSq(btEveningDay,eveningCountDay,eveningAveDay);
		    		}
		    		if(nightCountDay > 0)
		    		{
		    			nightAveDay = nightAveDay/nightCountDay;
		    			nightSqDay = calculateSq(btNightDay,nightCountDay,nightAveDay);
		    		}
		    		if(totalCountDay > 0)
		    		{
		    			totalAveDay = totalAveDay/totalCountDay;
		    			totalSqDay = calculateSq(btTotalDay,totalCountDay,totalAveDay);
		    		}
		    		
		    	    
		    	    //week
		    		if(morningCountWeek > 0)
		    		{
		    			morningAveWeek = morningAveWeek/morningCountWeek;
		    			morningSqWeek = calculateSq(btMorningWeek,morningCountWeek,morningAveWeek);
		    		}
		    		if(noonCountWeek > 0)
		    		{
		    			noonAveWeek = noonAveWeek/noonCountWeek;
		    			noonSqWeek = calculateSq(btNoonWeek,noonCountWeek,noonAveWeek);
		    		}
		    		if(eveningCountWeek > 0)
		    		{
		    			eveningAveWeek = eveningAveWeek/eveningCountWeek;
		    			eveningSqWeek = calculateSq(btEveningWeek,eveningCountWeek,eveningAveWeek);
		    		}
		    		if(nightCountWeek > 0)
		    		{
		    			nightAveWeek = nightAveWeek/nightCountWeek;
		    			nightSqWeek = calculateSq(btNightWeek,nightCountWeek,nightAveWeek);
		    		}
		    		if(totalCountWeek > 0)
		    		{
		    			totalAveWeek = totalAveWeek/totalCountWeek;
		    			totalSqWeek = calculateSq(btTotalWeek,totalCountWeek,totalAveWeek);
		    		}
		    		
		   	    //month
		    		if(morningCountMonth > 0)
		    		{
		    			morningAveMonth = morningAveMonth/morningCountMonth;
		    			morningSqMonth = calculateSq(btMorningMonth,morningCountMonth,morningAveMonth);
		    		}
		    		if(noonCountMonth > 0)
		    		{
		    			noonAveMonth = noonAveMonth/noonCountMonth;
		    			noonSqMonth = calculateSq(btNoonMonth,noonCountMonth,noonAveMonth);
		    		}
		    		if(eveningCountMonth > 0)
		    		{
		    			eveningAveMonth = eveningAveMonth/eveningCountMonth;
		    			eveningSqMonth = calculateSq(btEveningMonth,eveningCountMonth,eveningAveMonth);
		    		}
		    		if(nightCountMonth > 0)
		    		{
		    			nightAveMonth = nightAveMonth/nightCountMonth;
		    			nightSqMonth = calculateSq(btNightMonth,nightCountMonth,nightAveMonth);
		    		}
		    		if(totalCountMonth > 0)
		    		{
		    			totalAveMonth = totalAveMonth/totalCountMonth;
		    			totalSqMonth = calculateSq(btTotalMonth,totalCountMonth,totalAveMonth);
		    		}
		    		
		    	    //base body temp
		    	    Cursor cursorBase = btDbHelper.getAllItemsBtBase();
		    	    btBaseHour = new float[cursorBase.getCount()];
		    	    btBaseDay = new float[cursorBase.getCount()];
		    	    btBaseWeek = new float[cursorBase.getCount()];
		    	    btBaseMonth = new float[cursorBase.getCount()];
		    	    try {
		                
		                
		                while(cursorBase.moveToNext())
		        		{
		        			
		        			
		        			myDate = cursorBase.getString(cursorBase.getColumnIndex("timestamp")); 
		        			
		        			Date date = format.parse(myDate);
		        			long dateMs = date.getTime();
		        			
		        			dateMs += DateUtils.HOUR_IN_MILLIS * 8;
		        			
		        			Date dateTZ = new Date(dateMs);

		        			
		        			
		        			//only count result in one hour
		        			if(dateMs > (m_end - DateUtils.HOUR_IN_MILLIS)&& m_end>=dateMs)
		        			{
		        				btBaseHour[baseCountHour ++] = Float.valueOf(cursorBase.getString(cursorBase.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS)));
		        				if(Float.isNaN(baseAveHour))
		        					baseAveHour = 0;
		        				baseAveHour += Float.valueOf(cursorBase.getString(cursorBase.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS)));
		        				
		        				btBaseDay[baseCountDay ++] = Float.valueOf(cursorBase.getString(cursorBase.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS)));
		        				if(Float.isNaN(baseAveDay))
		        					baseAveDay = 0;
		        				baseAveDay += Float.valueOf(cursorBase.getString(cursorBase.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS)));
		        				
		        				btBaseWeek[baseCountWeek ++] = Float.valueOf(cursorBase.getString(cursorBase.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS)));
		        				if(Float.isNaN(baseAveWeek))
		        					baseAveWeek = 0;
		        				baseAveWeek += Float.valueOf(cursorBase.getString(cursorBase.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS)));
		        				
		        				btBaseMonth[baseCountMonth ++] = Float.valueOf(cursorBase.getString(cursorBase.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS)));
		        				if(Float.isNaN(baseAveMonth))
		        					baseAveMonth = 0;
		        				baseAveMonth += Float.valueOf(cursorBase.getString(cursorBase.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS)));
		        			}
		        			else if(dateMs > (m_end - DateUtils.DAY_IN_MILLIS)&& m_end>=dateMs)
		        			{
		        				btBaseDay[baseCountDay ++] = Float.valueOf(cursorBase.getString(cursorBase.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS)));
		        				if(Float.isNaN(baseAveDay))
		        					baseAveDay = 0;
		        				baseAveDay += Float.valueOf(cursorBase.getString(cursorBase.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS)));
		        				
		        				btBaseWeek[baseCountWeek ++] = Float.valueOf(cursorBase.getString(cursorBase.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS)));
		        				if(Float.isNaN(baseAveWeek))
		        					baseAveWeek = 0;
		        				baseAveWeek += Float.valueOf(cursorBase.getString(cursorBase.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS)));
		        				
		        				btBaseMonth[baseCountMonth ++] = Float.valueOf(cursorBase.getString(cursorBase.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS)));
		        				if(Float.isNaN(baseAveMonth))
		        					baseAveMonth = 0;
		        				baseAveMonth += Float.valueOf(cursorBase.getString(cursorBase.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS)));
		        			}
		        			else if(dateMs > (m_end - DateUtils.WEEK_IN_MILLIS)&& m_end>=dateMs)
		        			{
		        				btBaseWeek[baseCountWeek ++] = Float.valueOf(cursorBase.getString(cursorBase.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS)));
		        				if(Float.isNaN(baseAveWeek))
		        					baseAveWeek = 0;
		        				baseAveWeek += Float.valueOf(cursorBase.getString(cursorBase.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS)));
		        				
		        				btBaseMonth[baseCountMonth ++] = Float.valueOf(cursorBase.getString(cursorBase.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS)));
		        				if(Float.isNaN(baseAveMonth))
		        					baseAveMonth = 0;
		        				baseAveMonth += Float.valueOf(cursorBase.getString(cursorBase.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS)));
		        			}
		        			else if(dateMs > (m_end - DateUtils.DAY_IN_MILLIS * 30)&& m_end>=dateMs)
		        			{
		        				btBaseMonth[baseCountMonth ++] = Float.valueOf(cursorBase.getString(cursorBase.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS)));
		        				if(Float.isNaN(baseAveMonth))
		        					baseAveMonth = 0;
		        				baseAveMonth += Float.valueOf(cursorBase.getString(cursorBase.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS)));
		        			}
		        			
		        		}
		        			
		    	    
		    }catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
			cursorBase.close();
					if(baseCountHour > 0)
					{
						baseAveHour = baseAveHour/baseCountHour;
				
						baseSqHour = calculateSq(btBaseHour,baseCountHour,baseAveHour);
					}
					if(baseCountDay > 0)
					{
						baseAveDay = baseAveDay/baseCountDay;
				
						baseSqDay = calculateSq(btBaseDay,baseCountDay,baseAveDay);
					}
					if(baseCountWeek > 0)
					{
						baseAveWeek = baseAveWeek/baseCountWeek;
				
						baseSqWeek = calculateSq(btBaseWeek,baseCountWeek,baseAveWeek);
					}
					if(baseCountMonth > 0)
					{
						baseAveMonth = baseAveMonth/baseCountMonth;
				
						baseSqMonth = calculateSq(btBaseMonth,baseCountMonth,baseAveMonth);
					}
		        			
		    	    DecimalFormat df = new DecimalFormat("###.00");  

		    	    //set up the table
		    	    ListView lv = (ListView) this.findViewById(R.id.listViewBodyTempTable);   
			        table = new ArrayList<TableRow>();   
			        TableCell[] titles = new TableCell[7];// ê?è?°?7‰∏??????é    
			        int width = this.getWindowManager().getDefaultDisplay().getWidth()/(titles.length)-2; 
			        int height = this.getWindowManager().getDefaultDisplay().getHeight()/9;//the chart occupy 3/4 of the screen
			        // ???‰πaê?áè￠ò    
			           
			        titles[0] = new TableCell("统计数据",    
			                                    width ,   
			                                    //LayoutParams.FILL_PARENT,  
			                                    height,
			                                    TableCell.STRING,
			                                    Color.GRAY); 
			        titles[1] = new TableCell("整体体温",    
		                    width ,   
		                    //LayoutParams.FILL_PARENT,  
		                    height,
		                    TableCell.STRING,
		                    Color.GRAY); 
			        
			        titles[2] = new TableCell("基础体温",    
		                    width  ,   
		                    //LayoutParams.FILL_PARENT, 
		                    height,
		                    TableCell.STRING,
		                    Color.GRAY); 
			        
			        titles[3] = new TableCell("早晨体温",    
		                    width ,   
		                    //LayoutParams.FILL_PARENT,
		                    height,
		                    TableCell.STRING,
		                    Color.GRAY); 
			        titles[4] = new TableCell("中午体温",    
		                    width ,   
		                    //LayoutParams.FILL_PARENT, 
		                    height,
		                    TableCell.STRING,
		                    Color.GRAY); 
			        titles[5] = new TableCell("晚上体温",    
		                    width ,   
		                    //LayoutParams.FILL_PARENT,   
		                    height,
		                    TableCell.STRING,
		                    Color.GRAY); 
			        titles[6] = new TableCell("夜间体温",    
		                    width ,   
		                    //LayoutParams.FILL_PARENT,   
		                    height,
		                    TableCell.STRING,
		                    Color.GRAY); 
			         
			        table.add(new TableRow(titles,this)); 
			        
			        //second row
			        TableCell[] average = new TableCell[7];// ê?è?°?7‰∏??????é    
			           
			        // ???‰πaê?áè￠ò    
			           
			        average[0] = new TableCell("均值(℃)",    
			                                    width ,   
			                                    //LayoutParams.FILL_PARENT,
			                                    height,
			                                    TableCell.STRING,
			                                    Color.GRAY); 
			        average[1] = new TableCell(totalAveHour,    
		                    width ,   
		                    //LayoutParams.FILL_PARENT,  
		                    height,
		                    TableCell.NUMBER,
		                    Color.BLACK); 
			        
			        average[2] = new TableCell(baseAveHour,    
		                    width  ,   
		                    //LayoutParams.FILL_PARENT,  
		                    height,
		                    TableCell.NUMBER,
		                    Color.BLACK); 
			        
			        average[3] = new TableCell(morningAveHour,    
		                    width ,   
		                    //LayoutParams.FILL_PARENT,  
		                    height,
		                    TableCell.NUMBER,
		                    Color.BLACK); 
			        average[4] = new TableCell(noonAveHour,    
		                    width ,   
		                    //LayoutParams.FILL_PARENT,   
		                    height,
		                    TableCell.NUMBER,
		                    Color.BLACK); 
			        average[5] = new TableCell(eveningAveHour,    
		                    width ,   
		                    //LayoutParams.FILL_PARENT,  
		                    height,
		                    TableCell.NUMBER,
		                    Color.BLACK); 
			        average[6] = new TableCell(nightAveHour,    
		                    width ,   
		                    //LayoutParams.FILL_PARENT, 
		                    height,
		                    TableCell.NUMBER,
		                    Color.BLACK); 
			         
			        table.add(new TableRow(average,this));
			        
			        TableCell[] meanSquare = new TableCell[7];// ê?è?°?7‰∏??????é    
			           
			        // ???‰πaê?áè￠ò    
			           
			        meanSquare[0] = new TableCell("方差(℃)",    
			                                    width ,   
			                                    //LayoutParams.FILL_PARENT,  
			                                    height,
			                                    TableCell.STRING,
			                                    Color.GRAY); 
			        meanSquare[1] = new TableCell(totalSqHour,    
		                    width ,   
		                    //LayoutParams.FILL_PARENT, 
		                    height,
		                    TableCell.NUMBER,
		                    Color.BLACK); 
			        
			        meanSquare[2] = new TableCell(baseSqHour,    
		                    width  ,   
		                    //LayoutParams.FILL_PARENT,    
		                    height,
		                    TableCell.NUMBER,
		                    Color.BLACK); 
			        
			        
			        meanSquare[3] = new TableCell(morningSqHour,    
		                    width ,   
		                    //LayoutParams.FILL_PARENT,
		                    height,
		                    TableCell.NUMBER,
		                    Color.BLACK); 
			        meanSquare[4] = new TableCell(noonSqHour,    
		                    width ,   
		                    //LayoutParams.FILL_PARENT, 
		                    height,
		                    TableCell.NUMBER,
		                    Color.BLACK); 
			        meanSquare[5] = new TableCell(eveningSqHour,    
		                    width ,   
		                    //LayoutParams.FILL_PARENT,    
		                    height,
		                    TableCell.NUMBER,
		                    Color.BLACK); 
			        meanSquare[6] = new TableCell(nightSqHour,    
		                    width ,   
		                    //LayoutParams.FILL_PARENT, 
		                    height,
		                    TableCell.NUMBER,
		                    Color.BLACK); 
			         
			        table.add(new TableRow(meanSquare,this));
			        
			        //third row
			        
		/*		        // ê?è?°?á??ê?∞ê??    
			        TableCell[] cells = new TableCell[5];// ê?è?°?5‰∏??????é    
			        for (int i = 0; i < cells.length - 1; i++) {   
			            cells[i] = new TableCell("No." + df.format(i),   
			                                    titles[i].width,    
			                                    LayoutParams.FILL_PARENT,    
			                                    TableCell.STRING);   
			        }   
			        cells[cells.length - 1] = new TableCell(R.drawable.ic_launcher,   
			                                                titles[cells.length - 1].width,    
			                                                LayoutParams.WRAP_CONTENT,   
			                                                TableCell.IMAGE);   
			        // ê???°?ê?oá???°?ê∑a????à∞?°?ê?o    
			        for (int i = 0; i < 12; i++)   
			        {
			            table.add(new TableRow(cells));   
			        }
			        */
			        tableAdapter = new TableAdapter(this, table);   
			        lv.setAdapter(tableAdapter); 
			        lv.setLayoutParams(new LinearLayout.LayoutParams(  
							(width+2)*7,LayoutParams.FILL_PARENT, 1));
			        LinearLayout chartLayout = (LinearLayout)findViewById(R.id.linearLayoutBodyStTable);
			        //chartLayout.setLayoutParams(new LinearLayout.LayoutParams(  
						//	(width+2)*7,LayoutParams.FILL_PARENT, 5));
			        
		    	//set listener for Hour button
		        Button btnBldHour=(Button)findViewById(R.id.bldHourBodtTempTable);  
		        btnBldHour.setOnClickListener(new OnClickListener()  
		        {  
		  
		            @Override  
		            public void onClick(View arg0) { 
		            	
		            	DecimalFormat df = new DecimalFormat("###.00");  
		            	tableAdapter.updateTextView(1, 1, totalAveHour);
		            	tableAdapter.updateTextView(1, 2, baseAveHour);
		            	tableAdapter.updateTextView(1, 3, morningAveHour);
		            	tableAdapter.updateTextView(1, 4, noonAveHour);
		            	tableAdapter.updateTextView(1, 5, eveningAveHour);
		            	tableAdapter.updateTextView(1, 6, nightAveHour);
		            	
		            	tableAdapter.updateTextView(2, 1, totalSqHour);
		            	tableAdapter.updateTextView(2, 2, baseSqHour);
		            	tableAdapter.updateTextView(2, 3, morningSqHour);
		            	tableAdapter.updateTextView(2, 4, noonSqHour);
		            	tableAdapter.updateTextView(2, 5, eveningSqHour);
		            	tableAdapter.updateTextView(2, 6, nightSqHour);
		            	
		            	
		            }
		        });
		        
		        
		    	//set listener for day button
		        Button btnBldDay=(Button)findViewById(R.id.bldDayBodtTempTable);  
		        btnBldDay.setOnClickListener(new OnClickListener()  
		        {  
		  
		            @Override  
		            public void onClick(View arg0) { 
		            	DecimalFormat df = new DecimalFormat("###.00");  
		            	tableAdapter.updateTextView(1, 1, totalAveDay);
		            	tableAdapter.updateTextView(1, 2, baseAveDay);
		            	tableAdapter.updateTextView(1, 3, morningAveDay);
		            	tableAdapter.updateTextView(1, 4, noonAveDay);
		            	tableAdapter.updateTextView(1, 5, eveningAveDay);
		            	tableAdapter.updateTextView(1, 6, nightAveDay);
		            	
		            	tableAdapter.updateTextView(2, 1, totalSqDay);
		            	tableAdapter.updateTextView(2, 2, baseSqDay);
		            	tableAdapter.updateTextView(2, 3, morningSqDay);
		            	tableAdapter.updateTextView(2, 4, noonSqDay);
		            	tableAdapter.updateTextView(2, 5, (eveningSqDay));
		            	tableAdapter.updateTextView(2, 6, (nightSqDay));
		            	
		            	
		            }
		        });
		        
		      //set listener for week button
		        Button btnBldWeek=(Button)findViewById(R.id.bldWeekBodtTempTable);  
		        btnBldWeek.setOnClickListener(new OnClickListener()  
		        {  
		  
		            @Override  
		            public void onClick(View arg0) { 
		            	DecimalFormat df = new DecimalFormat("###.00");  
		            	tableAdapter.updateTextView(1, 1, (totalAveWeek));
		            	tableAdapter.updateTextView(1, 2, (baseAveWeek));
		            	tableAdapter.updateTextView(1, 3, (morningAveWeek));
		            	tableAdapter.updateTextView(1, 4, (noonAveWeek));
		            	tableAdapter.updateTextView(1, 5, (eveningAveWeek));
		            	tableAdapter.updateTextView(1, 6, (nightAveWeek));
		            	
		            	tableAdapter.updateTextView(2, 1, (totalSqWeek));
		            	tableAdapter.updateTextView(2, 2, (baseSqWeek));
		            	tableAdapter.updateTextView(2, 3, (morningSqWeek));
		            	tableAdapter.updateTextView(2, 4, (noonSqWeek));
		            	tableAdapter.updateTextView(2, 5, (eveningSqWeek));
		            	tableAdapter.updateTextView(2, 6, (nightSqWeek));
		            	
		            	
		            	
		            }
		        });
		        
		      //set listener for Month button
		        Button btnBldMonth=(Button)findViewById(R.id.bldMonthBodtTempTable);  
		        btnBldMonth.setOnClickListener(new OnClickListener()  
		        {  
		  
		            @Override  
		            public void onClick(View arg0) { 
		            	DecimalFormat df = new DecimalFormat("###.00");  
		            	tableAdapter.updateTextView(1, 1, (totalAveMonth));
		            	tableAdapter.updateTextView(1, 2, (baseAveMonth));
		            	tableAdapter.updateTextView(1, 3, (morningAveMonth));
		            	tableAdapter.updateTextView(1, 4, (noonAveMonth));
		            	tableAdapter.updateTextView(1, 5, (eveningAveMonth));
		            	tableAdapter.updateTextView(1, 6, (nightAveMonth));
		            	
		            	tableAdapter.updateTextView(2, 1, (totalSqMonth));
		            	tableAdapter.updateTextView(2, 2, (baseSqMonth));
		            	tableAdapter.updateTextView(2, 3, (morningSqMonth));
		            	tableAdapter.updateTextView(2, 4, (noonSqMonth));
		            	tableAdapter.updateTextView(2, 5, (eveningSqMonth));
		            	tableAdapter.updateTextView(2, 6, (nightSqMonth));
		            	
		            	
		            	
		            }
		        });
  
 
		    }   

	 

	private float calculateSq(float[]array,int count,float ave)
	{
		float sum=0;
		if(count == 0)
			return sum;
		for(int i = 0;i<count;i++)
            sum += (array[i] - ave)  * (array[i] - ave) ;
        sum /= count;
        return sum;
	}
	}

