package mobileMedical.namespace;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.view.Window;
import android.os.Bundle;

import java.util.Random;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.view.View;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;


import android.content.ContentValues;  
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;  
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.text.DateFormat;
import android.graphics.Color;

import com.artfulbits.aiCharts.ChartView;
import com.artfulbits.aiCharts.Base.ChartArea;
import com.artfulbits.aiCharts.Base.ChartAxisScale;
import com.artfulbits.aiCharts.Base.ChartEngine;
import com.artfulbits.aiCharts.Base.ChartPoint;
import com.artfulbits.aiCharts.Base.ChartPointCollection;
import com.artfulbits.aiCharts.Base.ChartSeries;
import com.artfulbits.aiCharts.Types.ChartTypes;

import com.artfulbits.aiCharts.Base.ChartAxis;

public class BodyTempStColum extends Activity {
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
		
		private long m_end;	
	private double m_start = 0; 
	private  int duration = 0;//default 1 hour
	private  final boolean D = true;
	private  final String TAG = "BodyTempStColum";
	private  ChartView chartViewBodyTempColum = null;
	private  int[] tempArrayHour;
	private  int[] tempArrayDay;
	private  int[] tempArrayWeek;
	private  int[] tempArrayMonth;
	private int pointCount = 15;
	private int tempUpLimite = 37;
	private int tempLowLimite = 36;

	
	
	boDb btDbHelper = new boDb(this, "bloodox.db", null, 1);
	private OnPointClickListener pointClickListener = new OnPointClickListener()
	{
		public void onPointClick(ChartPoint point) 
		{
			Toast.makeText(BodyTempStColum.this, point.toString(), Toast.LENGTH_SHORT).show();
		}
	};
	

    
    @Override
    public void onStart() {
        super.onStart();

        
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        Cursor cursor = btDbHelper.getAllItemsBt();
        float bt = 0;
        String myDate ;
        tempArrayHour = new int[pointCount];
        tempArrayDay = new int[pointCount];
        tempArrayWeek = new int[pointCount];
        tempArrayMonth = new int[pointCount];
        
        SharedPreferences settings = getSharedPreferences(SETTING_INFOS, 0);
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
			if((bt < 35)||(bt > 42))
				continue;
			myDate = cursor.getString(cursor.getColumnIndex(ConstDef.DATABASE_FIELD_TIMESTAMP));
			
			
			Date date = format.parse(myDate);
			long dateMs = date.getTime();
			//only count result in one hour
			if((DateUtils.WEEK_IN_MILLIS < (m_end - (dateMs + 28800000))) &&
					((m_end - (dateMs + 28800000)) < 30*DateUtils.DAY_IN_MILLIS)&& m_end>=(dateMs + 28800000))
			{
				tempArrayMonth[(int)((bt-35)*10/5)]++;
			}
			else if((DateUtils.DAY_IN_MILLIS < (m_end - (dateMs + 28800000))) &&
					((m_end - (dateMs + 28800000)) < DateUtils.WEEK_IN_MILLIS)&& m_end>=(dateMs + 28800000))
			{
				tempArrayMonth[(int)((bt-35)*10/5)]++;
				tempArrayWeek[(int)((bt-35)*10/5)]++;
			}
			else if((DateUtils.HOUR_IN_MILLIS < (m_end - (dateMs + 28800000))) &&
					((m_end - (dateMs + 28800000)) < DateUtils.DAY_IN_MILLIS) && m_end>=(dateMs + 28800000))
			{
				tempArrayMonth[(int)((bt-35)*10/5)]++;
				tempArrayWeek[(int)((bt-35)*10/5)]++;
				tempArrayDay[(int)((bt-35)*10/5)]++;
				
			}
			else if((m_end - (dateMs + 28800000)) < DateUtils.HOUR_IN_MILLIS && m_end>=(dateMs + 28800000))
			{
				tempArrayMonth[(int)((bt-35)*10/5)]++;
				tempArrayWeek[(int)((bt-35)*10/5)]++;
				tempArrayDay[(int)((bt-35)*10/5)]++;
				tempArrayHour[(int)((bt-35)*10/5)]++;
			}
								
		}
		cursor.close();
		
	    for(int i=0;i<pointCount;i++)
	    {
	    	//if(tempArrayHour[i] != 0)
	    	//{
	    	 ChartPointCollection pointsBt = chartViewBodyTempColum.getSeries().get(0).getPoints();
	    	 ChartPoint point = pointsBt.addXY(35+0.5*i, tempArrayHour[i]);
	    	 if(35+i*0.5< tempLowLimite)
	    		 point.setBackColor(0xff0099cc);
	    	 else if(35+i*0.5 > tempUpLimite)
	    		 point.setBackColor(0xffcc0000);
	    	 else
	    		 point.setBackColor(0xff669900);    	
        } 
        }catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
      

      

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
		    	setContentView(R.layout.body_temp_st_colum);
		       chartViewBodyTempColum = (ChartView)findViewById(R.id.chartViewBodyTempSt);
				
			
	      //chartViewBodyTempColum.getAreas().get(0).getDefaultXAxis().getScale().setMargin(ChartAxisScale.MARGIN_NONE);
			
		  chartViewBodyTempColum.getAreas().get(0).getDefaultXAxis().getScale().setRange(34, 44);
		  chartViewBodyTempColum.getAreas().get(0).getDefaultXAxis().getScale().setInterval(0.5);
			
			
            
            chartViewBodyTempColum.setPanning(ChartView.PANNING_BOTH);
            chartViewBodyTempColum.setHitTestEnabled(true);
            chartViewBodyTempColum.setOnTouchListener(pointClickListener);
            
            Button btnBldHour=(Button)findViewById(R.id.bldHour);  
	        btnBldHour.setOnClickListener(new OnClickListener()  
	        {  
	  
	            @Override  
	            public void onClick(View arg0) {  
	            	chartViewBodyTempColum.getSeries().get(0).getPoints().removeAll(chartViewBodyTempColum.getSeries().get(0).getPoints());
	            	//get time for now
	            	//double m_now = System.currentTimeMillis();
	                // TODO Auto-generated method stub  
	            	
	            	
	            	for(int i=0;i<pointCount;i++)
	        	    {
	        	    	//if(tempArrayHour[i] != 0)
	        	    	{
	        	    	 ChartPointCollection pointsBt = chartViewBodyTempColum.getSeries().get(0).getPoints();
	        	    	 ChartPoint point = pointsBt.addXY(35+i*0.5, tempArrayHour[i]);
	        	    	 if(35+i*0.5< tempLowLimite)
	        	    		 point.setBackColor(0xff0099cc);
	        	    	 else if(35+i*0.5 > tempUpLimite)
	        	    		 point.setBackColor(0xffcc0000);
	        	    	 else
	        	    		 point.setBackColor(0xff669900);
	        	    	 
	        	    	}
	        	    	
	                } 
	            	

	            	
	            	
	            }  
	              
	        }); 
	        
	      //set listener for day button
	        Button btnBldDay=(Button)findViewById(R.id.bldDay);  
	        btnBldDay.setOnClickListener(new OnClickListener()  
	        {  
	  
	            @Override  
	            public void onClick(View arg0) { 
	            	chartViewBodyTempColum.getSeries().get(0).getPoints().removeAll(chartViewBodyTempColum.getSeries().get(0).getPoints());
	            	for(int i=0;i<pointCount;i++)
	        	    {
	        	    	//if(tempArrayDay[i] != 0)
	        	    	{
	        	    	 ChartPointCollection pointsBt = chartViewBodyTempColum.getSeries().get(0).getPoints();
	        	    	 ChartPoint point = pointsBt.addXY(35+i*0.5, tempArrayDay[i]);
	        	    	 if(35+i*0.5< tempLowLimite)
	        	    		 point.setBackColor(0xff0099cc);
	        	    	 else if(35+i*0.5> tempUpLimite)
	        	    		 point.setBackColor(0xffcc0000);
	        	    	 else
	        	    		 point.setBackColor(0xff669900);
	        	    	 
	        	    	}
	        	    	
	                } 
	            	
	            }  
	              
	        }); 
	        
	      //set listener for week button
	        Button btnBldWeek=(Button)findViewById(R.id.bldWeek);  
	        btnBldWeek.setOnClickListener(new OnClickListener()  
	        {  
	  
	            @Override  
	            public void onClick(View arg0) {  
	            	
	            	chartViewBodyTempColum.getSeries().get(0).getPoints().removeAll(chartViewBodyTempColum.getSeries().get(0).getPoints());
	            	for(int i=0;i<pointCount;i++)
	        	    {
	        	    	//if(tempArrayWeek[i] != 0)
	        	    	{
	        	    	 ChartPointCollection pointsBt = chartViewBodyTempColum.getSeries().get(0).getPoints();
	        	    	 ChartPoint point = pointsBt.addXY(35+i*0.5, tempArrayWeek[i]);

	        	    	 if(35+i*0.5< tempLowLimite)
	        	    		 point.setBackColor(0xff0099cc);
	        	    	 else if(35+i*0.5 > tempUpLimite)
	        	    		 point.setBackColor(0xffcc0000);
	        	    	 else
	        	    		 point.setBackColor(0xff669900);
	        	    	 
	        	    	}
	        	    	
	                } 
	            	
	            }  
	            
	            //set gesture listener for two chart.
	            
	              
	        }); 
	        
	        
	      //set listener for week button
	        Button btnBldMonth=(Button)findViewById(R.id.bldMonth);  
	        btnBldMonth.setOnClickListener(new OnClickListener()  
	        {  
	  
	            @Override  
	            public void onClick(View arg0) {  
	            	chartViewBodyTempColum.getSeries().get(0).getPoints().removeAll(chartViewBodyTempColum.getSeries().get(0).getPoints());
	            	for(int i=0;i<pointCount;i++)
	        	    {
	        	    	//if(tempArrayMonth[i] != 0)
	        	    	//{
	        	    	 ChartPointCollection pointsBt = chartViewBodyTempColum.getSeries().get(0).getPoints();
	        	    	 ChartPoint point = pointsBt.addXY(35+i*0.5, tempArrayMonth[i]);
	        	    	 if(35+i*0.5< tempLowLimite)
	        	    		 point.setBackColor(0xff0099cc);
	        	    	 else if(35+i*0.5 > tempUpLimite)
	        	    		 point.setBackColor(0xffcc0000);
	        	    	 else
	        	    		 point.setBackColor(0xff669900);
	        	    	 
	        	    	//}
	        	    	
	                } 
	            	
	            }  
	              
	        }); 	
    		
    		
    		
            
			
			
	 }
	 

	  
	}

