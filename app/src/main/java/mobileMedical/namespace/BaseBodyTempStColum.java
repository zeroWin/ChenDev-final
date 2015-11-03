package mobileMedical.namespace;
import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

import com.artfulbits.aiCharts.Annotations.ChartTextAnnotation;
import com.artfulbits.aiCharts.Base.ChartAxis;
import com.artfulbits.aiCharts.Base.ChartAxisScale;
import com.artfulbits.aiCharts.Base.ChartPoint;
import com.artfulbits.aiCharts.Base.ChartPointCollection;
import com.artfulbits.aiCharts.ChartView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseBodyTempStColum extends Activity {
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
	private int duration = 0;//default 1 hour
	private float btHour[];
	private float btDay[];
	private float btWeek[];
	private float btMonth[];
	
	private long btHourTime[];
	private long btDayTime[];
	private long btWeekTime[];
	private long btMonthTime[];
	
	private int btHourCount = 0;
	private int btDayCount = 0;
	private int btWeekCount = 0;
	private int btMonthCount = 0;
	
	private final boolean D = true;
	private final String TAG = "BaseBodyTempStLine";
	private ChartViewAdv chartViewBaseBodyTempLine = null;
	
	boDb btDbHelper = new boDb(this, "bloodox.db", null, 1);
	private OnPointClickListener pointClickListener = new OnPointClickListener()
	{
		public void onPointClick(ChartPoint point) 
		{
			chartViewBaseBodyTempLine.getChart().getAnnotations().clear();
			Drawable anBackground = getResources().getDrawable(R.drawable.annotation);
			
			ChartTextAnnotation annotation1 = new ChartTextAnnotation(anBackground, point.toString());
			ChartTextAnnotation annotation2 = new ChartTextAnnotation(anBackground, "|");
			ChartTextAnnotation annotation3 = new ChartTextAnnotation(anBackground, "|");
			
			
			annotation1.setPosition(new CustomAnnotationPosition(chartViewBaseBodyTempLine.getAreas().get(0), point.getX(), point.getY()[0]+5));
			annotation2.setPosition(new CustomAnnotationPosition(chartViewBaseBodyTempLine.getAreas().get(0), point.getX(), point.getY()[0]+1));
			annotation3.setPosition(new CustomAnnotationPosition(chartViewBaseBodyTempLine.getAreas().get(0), point.getX(), point.getY()[0]+3));
			
			
			chartViewBaseBodyTempLine.getChart().getAnnotations().add(annotation1);
			chartViewBaseBodyTempLine.getChart().getAnnotations().add(annotation2);
			chartViewBaseBodyTempLine.getChart().getAnnotations().add(annotation3);
		}
	};
	

    
    @Override
    public void onStart() {
        super.onStart();

        
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        
      //读出当前用户的每次测试的脉搏最大值最小值平均值和SPO的最小值
      


      

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
		    	setContentView(R.layout.base_body_temp_st_colum);
				chartViewBaseBodyTempLine = new ChartViewAdv(this, R.xml.chart_body_temp_st_line);
				
			
			//chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().getScale().setMargin(ChartAxisScale.MARGIN_NONE);
			chartViewBaseBodyTempLine.getAreas().get(0).getDefaultYAxis().setTitle(getString(R.string.body_temp_line_axis_title));
			chartViewBaseBodyTempLine.getAreas().get(0).getDefaultYAxis().getScale().setRange(20, 42);
			
			chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().setFormat(DateFormat.getTimeInstance(DateFormat.MEDIUM));
			chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().setValueType(ChartAxis.ValueType.Date);
			chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().getScale().setRange(0, 100);
			
			chartViewBaseBodyTempLine.setLayoutParams(new LinearLayout.LayoutParams(  
		            LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1));
			
			//chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().setLabelAngle(15);
			
            
			
            LinearLayout chartLayout = (LinearLayout)findViewById(R.id.linearLayoutBsBdTpStChart);

            chartLayout.addView(chartViewBaseBodyTempLine,0);
            //chartLayout.addView(chartViewSpo,1);
            
            chartViewBaseBodyTempLine.setPanning(ChartView.PANNING_BOTH);
            chartViewBaseBodyTempLine.setHitTestEnabled(true);
            chartViewBaseBodyTempLine.setOnTouchListener(pointClickListener);
            
    		
    		// chart may contains more that one area.
            chartViewBaseBodyTempLine.enableZooming(chartViewBaseBodyTempLine.getAreas().get(0), true, false);
            //chartViewBaseBodyTempLine.enableZooming(chartViewBaseBodyTempLine.getAreas().get(1), true, false);
            
    		
            Cursor cursor = btDbHelper.getAllItemsBtBase();
            btHour = new float[cursor.getCount()];
            btDay = new float[cursor.getCount()];
            btWeek = new float[cursor.getCount()];
            btMonth = new float[cursor.getCount()];
            
            btHourTime = new long[cursor.getCount()];
            btDayTime = new long[cursor.getCount()];
            btWeekTime = new long[cursor.getCount()];
            btMonthTime = new long[cursor.getCount()];
            
            float bt = 0;
            String myDate ;
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
	            	  Month = String.valueOf(statisticMonth+1);
	      	    	}
	      	      else
	      	    	{
	      	    		Month  = "0"+String.valueOf(statisticMonth+1);
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
      				
        			
        			
        			//only count result in one hour
        			if(dateMs > (m_end - DateUtils.HOUR_IN_MILLIS) && m_end>=dateMs)
        			{
        				btHour[btHourCount] = bt;
        				btHourTime[btHourCount++] = dateMs;
        				
        				btDay[btDayCount] = bt;
        				btDayTime[btDayCount++] = dateMs;
        				
        				btWeek[btWeekCount] = bt;
        				btWeekTime[btWeekCount++] = dateMs;
        				
        				btMonth[btMonthCount] = bt;
        				btMonthTime[btMonthCount++] = dateMs;
        			}
        			else if(dateMs > (m_end - DateUtils.DAY_IN_MILLIS) && m_end>=dateMs)
        			{
        				btDay[btDayCount] = bt;
        				btDayTime[btDayCount++] = dateMs;
        				
        				btWeek[btWeekCount] = bt;
        				btWeekTime[btWeekCount++] = dateMs;
        				
        				btMonth[btMonthCount] = bt;
        				btMonthTime[btMonthCount++] = dateMs;
        			}
        			else if(dateMs > (m_end - DateUtils.WEEK_IN_MILLIS) && m_end>=dateMs)
        			{
        				btWeek[btWeekCount] = bt;
        				btWeekTime[btWeekCount++] = dateMs;
        				
        				btMonth[btMonthCount] = bt;
        				btMonthTime[btMonthCount++] = dateMs;
        			}
        			else if(dateMs > (m_end - DateUtils.DAY_IN_MILLIS * 30) && m_end>=dateMs)
        			{
        				btMonth[btMonthCount] = bt;
        				btMonthTime[btMonthCount++] = dateMs;
        			}

      			}
      			cursor.close();
      			
    			switch(duration)
    			{
    			case 0:
    			 //set 1 hour	
    			 chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().getScale().setRange(m_end - 3600*1000, m_end);
    			 chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().getScale().setInterval(10.0,ChartAxisScale.IntervalType.Minutes);
    			 chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().setLabelFormat(new SimpleDateFormat("HH:mm:ss"));
    			 chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().getScale().resetZoom();
    			    		
    			 for(int i=0;i < btHourCount;i++)
       			{
       				ChartPointCollection pointsBt = chartViewBaseBodyTempLine.getSeries().get(0).getPoints();
 					
       				chartViewBaseBodyTempLine.getSeries().get(0).setMarkerDrawable(getResources().getDrawable(R.drawable.diamonds_4));
 					
       				pointsBt.addXY(btHourTime[i], btHour[i]);//调整到中国时区,+8
       				Date date = format.parse(String.valueOf(btHourTime[i]));
       			} 
    			break;
    			case 1:
    			 //set 1 day
    			 chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().getScale().setRange(m_end - 24*3600*1000, m_end);
    			 chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().getScale().setInterval(6.0,ChartAxisScale.IntervalType.Hours); 
    			 chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().setLabelFormat(new SimpleDateFormat("HH:mm:ss"));
    			 chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().getScale().resetZoom();
    	    	    		
    			 for(int i=0;i < btDayCount;i++)
       			{
       				ChartPointCollection pointsBt = chartViewBaseBodyTempLine.getSeries().get(0).getPoints();
 					
       				chartViewBaseBodyTempLine.getSeries().get(0).setMarkerDrawable(getResources().getDrawable(R.drawable.diamonds_4));
 					
       				pointsBt.addXY(btDayTime[i], btDay[i]);//调整到中国时区,+8
       				Date date = format.parse(String.valueOf(btDayTime[i]));
       			}  
    	    	break;
    			case 2:
    				 //set 1 week
    				 chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().getScale().setRange(m_end - 7*DateUtils.DAY_IN_MILLIS, m_end);
    				 chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().getScale().setInterval(1.0,ChartAxisScale.IntervalType.Days); 
    				 chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().setLabelFormat(new SimpleDateFormat("dd HH:mm:ss"));
    				 chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().getScale().resetZoom();
    		    	    		
    				 for(int i=0;i < btWeekCount;i++)
           			{
           				ChartPointCollection pointsBt = chartViewBaseBodyTempLine.getSeries().get(0).getPoints();
     					
           				chartViewBaseBodyTempLine.getSeries().get(0).setMarkerDrawable(getResources().getDrawable(R.drawable.diamonds_4));
     					
           				pointsBt.addXY(btWeekTime[i], btWeek[i]);//调整到中国时区,+8
           				Date date = format.parse(String.valueOf(btWeekTime[i]));
           			}
    		    	break;
    			case 3:
    				 //set 1 month
    				 chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().getScale().setRange(m_end - 30 * DateUtils.DAY_IN_MILLIS, m_end);
    				 chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().getScale().setInterval(6.0,ChartAxisScale.IntervalType.Days); 
    				 chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().setLabelFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    				 chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().getScale().resetZoom();
    		    	    		
    				 for(int i=0;i < btMonthCount;i++)
           			{
           				ChartPointCollection pointsBt = chartViewBaseBodyTempLine.getSeries().get(0).getPoints();
     					
           				chartViewBaseBodyTempLine.getSeries().get(0).setMarkerDrawable(getResources().getDrawable(R.drawable.diamonds_4));
     					
           				pointsBt.addXY(btMonthTime[i], btMonth[i]);//调整到中国时区,+8
           				Date date = format.parse(String.valueOf(btMonthTime[i]));
           			}
    		    	break;
    			
    			}
    		
            } catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		};
           //define the button
			//set listener for hour button
	        Button btnBldHour=(Button)findViewById(R.id.bldHour);  
	        btnBldHour.setOnClickListener(new OnClickListener()  
	        {  
	  
	            @Override  
	            public void onClick(View arg0) {  
	            	//get time for now
	            	//double m_now = System.currentTimeMillis();
	                // TODO Auto-generated method stub  
	            	
	            	chartViewBaseBodyTempLine.getSeries().get(0).getPoints().removeAll(chartViewBaseBodyTempLine.getSeries().get(0).getPoints());
	            	chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().getScale().setRange(m_end - 3600*1000, m_end);
	            	chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().getScale().setInterval(10.0,ChartAxisScale.IntervalType.Minutes);
	            	chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().setLabelFormat(new SimpleDateFormat("HH:mm:ss"));
	            	chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().getScale().resetZoom();
	            	
	            	SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
	            	for(int i=0;i < btHourCount;i++)
	      			{
	      				ChartPointCollection pointsBt = chartViewBaseBodyTempLine.getSeries().get(0).getPoints();
						
	      				chartViewBaseBodyTempLine.getSeries().get(0).setMarkerDrawable(getResources().getDrawable(R.drawable.diamonds_4));
						
	      				pointsBt.addXY(btHourTime[i], btHour[i]);//调整到中国时区,+8
	      				try {
	      	              
	      				Date date = format.parse(String.valueOf(btHourTime[i]));
	      				} catch (Exception e) {
	      	      			// TODO Auto-generated catch block
	      	      			e.printStackTrace();
	      	      		};
	      			} 
	            	
	            	
	            }  
	              
	        }); 
	        
	      //set listener for day button
	        Button btnBldDay=(Button)findViewById(R.id.bldDay);  
	        btnBldDay.setOnClickListener(new OnClickListener()  
	        {  
	  
	            @Override  
	            public void onClick(View arg0) {  
	            	//get time for now
	            	//double m_now = System.currentTimeMillis();  
	            	chartViewBaseBodyTempLine.getSeries().get(0).getPoints().removeAll(chartViewBaseBodyTempLine.getSeries().get(0).getPoints());
	            	chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().getScale().setRange(m_end - 24*3600*1000, m_end);
	            	chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().getScale().setInterval(6.0,ChartAxisScale.IntervalType.Hours);
	            	chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().setLabelFormat(new SimpleDateFormat("HH:mm:ss"));
	            	chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().getScale().resetZoom();
	            	
	            	SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
	            	for(int i=0;i < btDayCount;i++)
	      			{
	      				ChartPointCollection pointsBt = chartViewBaseBodyTempLine.getSeries().get(0).getPoints();
						
	      				chartViewBaseBodyTempLine.getSeries().get(0).setMarkerDrawable(getResources().getDrawable(R.drawable.diamonds_4));
						
	      				pointsBt.addXY(btDayTime[i], btDay[i]);//调整到中国时区,+8
	      				try {
		      	              
		      				Date date = format.parse(String.valueOf(btDayTime[i]));
		      				} catch (Exception e) {
		      	      			// TODO Auto-generated catch block
		      	      			e.printStackTrace();
		      	      		};
	      			}

	            	
	            	
	            }  
	              
	        }); 
	        
	      //set listener for week button
	        Button btnBldWeek=(Button)findViewById(R.id.bldWeek);  
	        btnBldWeek.setOnClickListener(new OnClickListener()  
	        {  
	  
	            @Override  
	            public void onClick(View arg0) {  
	            	//get time for now
	            	//double m_now = System.currentTimeMillis();  
	            	chartViewBaseBodyTempLine.getSeries().get(0).getPoints().removeAll(chartViewBaseBodyTempLine.getSeries().get(0).getPoints()); 
	            	chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().getScale().setRange(m_end - 7*24*3600*1000, m_end);
	            	chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().getScale().setInterval(1.0,ChartAxisScale.IntervalType.Days);
	            	chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().setLabelFormat(new SimpleDateFormat("dd HH:mm:ss"));
	            	chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().getScale().resetZoom();
	            	
	            	SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
	            	for(int i=0;i < btWeekCount;i++)
	      			{
	      				ChartPointCollection pointsBt = chartViewBaseBodyTempLine.getSeries().get(0).getPoints();
						
	      				chartViewBaseBodyTempLine.getSeries().get(0).setMarkerDrawable(getResources().getDrawable(R.drawable.diamonds_4));
						
	      				pointsBt.addXY(btWeekTime[i], btWeek[i]);//调整到中国时区,+8
	      				try {
		      	              
		      				Date date = format.parse(String.valueOf(btWeekTime[i]));
		      				} catch (Exception e) {
		      	      			// TODO Auto-generated catch block
		      	      			e.printStackTrace();
		      	      		};
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
	            	//get time for now
	            	//long m_now = System.currentTimeMillis();  
	            	chartViewBaseBodyTempLine.getSeries().get(0).getPoints().removeAll(chartViewBaseBodyTempLine.getSeries().get(0).getPoints());
	            	chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().getScale().setRange(m_end - 30 * DateUtils.DAY_IN_MILLIS, m_end);
	            	chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().getScale().setInterval(6.0,ChartAxisScale.IntervalType.Days);
	            	chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().setLabelFormat(new SimpleDateFormat("MM-dd HH:mm:ss"));
	            	chartViewBaseBodyTempLine.getAreas().get(0).getDefaultXAxis().getScale().resetZoom();
	            	
	            	SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
	            	for(int i=0;i < btMonthCount;i++)
	      			{
	      				ChartPointCollection pointsBt = chartViewBaseBodyTempLine.getSeries().get(0).getPoints();
						
	      				chartViewBaseBodyTempLine.getSeries().get(0).setMarkerDrawable(getResources().getDrawable(R.drawable.diamonds_4));
						
	      				pointsBt.addXY(btMonthTime[i], btMonth[i]);//调整到中国时区,+8
	      				try {
		      	              
		      				Date date = format.parse(String.valueOf(btMonthTime[i]));
		      				} catch (Exception e) {
		      	      			// TODO Auto-generated catch block
		      	      			e.printStackTrace();
		      	      		};
	      			}
	            	
	            	
	            	
	            }  
	              
	        }); 
            
			
			
	 }
	 

	  
	}

