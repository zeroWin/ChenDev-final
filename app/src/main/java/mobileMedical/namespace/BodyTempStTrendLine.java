package mobileMedical.namespace;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.artfulbits.aiCharts.Base.ChartAxis;
import com.artfulbits.aiCharts.Base.ChartAxisScale;
import com.artfulbits.aiCharts.Base.ChartPoint;
import com.artfulbits.aiCharts.Base.ChartPointCollection;
import com.artfulbits.aiCharts.ChartView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import mobileMedical.Algorithm.EMD.EMD;
import mobileMedical.Algorithm.EMD.Imf;
import mobileMedical.Algorithm.EMD.ImfList;
import mobileMedical.namespace.TableAdapter.TableRow;

public class BodyTempStTrendLine extends Activity {
	private double m_start = 0; 
	private static int duration = 0;//default 1 hour
	private static final boolean D = true;
	private static final String TAG = "BO Statistics";
	private static ChartViewAdv chartViewBodyTempTrendLine1 = null;
	private static ChartViewAdv chartViewBodyTempTrendLine2 = null;
	private static ChartViewAdv chartViewBodyTempTrendLine3 = null;
	private static ChartViewAdv chartViewBodyTempTrendLine4 = null;
	private static ChartViewAdv chartViewBodyTempTrendLiner = null;
	private static ChartViewAdv[] chartViewBodyTempTrendLines = null;
	private final int tableRowNum = 5;
	
	
	private double[] btValue;
	private double[] btTime;
	private double[] btTimeinDate;
	private int imfNumber;
	private int btCount;
	

	private ArrayAdapter<ChartViewAdv> chartViewArrayAdapter;
	private ArrayList<TableRow> table;
	private TableAdapter tableAdapter;
	boDb btDbHelper = new boDb(this, "bloodox.db", null, 1);
	private OnPointClickListener pointClickListener = new OnPointClickListener()
	{
		public void onPointClick(ChartPoint point) 
		{
			Toast.makeText(BodyTempStTrendLine.this, point.toString(), Toast.LENGTH_SHORT).show();
		}
	};
	

    
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
		    	setContentView(R.layout.body_temp_st_trend_line);

	            // Quanwei 2013-03-18
		    	 
	            Cursor cursor = btDbHelper.getAllItemsBt();
	              btValue = new double[cursor.getCount()];
	              btTime =  new double[cursor.getCount()];
	              btTimeinDate = new double[cursor.getCount()];
	              btCount = 0;
	              float bt = 0;
	              String myDate ;
	              boolean noImf = false;
	         
	               double firstDateMs = 0;
	               double tempVal = 0;
	       //       long m_end = System.currentTimeMillis();
	              try {
	      			while(cursor.moveToNext())
	      			{
	      				
	      				bt = Float.valueOf(cursor.getString(cursor.getColumnIndex(ConstDef.DATABASE_FIELD_MEASRESULTS)));
	      				myDate = cursor.getString(cursor.getColumnIndex(ConstDef.DATABASE_FIELD_TIMESTAMP)); 
	        				
	
	        				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	          			Date date = format.parse(myDate);
	          			long dateMs = date.getTime();
	          			
	          			
	          		//	dateMs += DateUtils.HOUR_IN_MILLIS * 8;

	          			
	          			
	          			//only count result in one hour
	          			
	          			btValue[btCount] = bt;
	          			
	          			btTimeinDate[btCount] = dateMs;
	          			firstDateMs = btTimeinDate[0];	
	          			tempVal = (btTimeinDate[btCount] - firstDateMs)/1000;
	          			btTime[btCount++] =	tempVal;

	        			}
	        			cursor.close();
	            
	        	if (btValue == null || btTime == null)
	        	{
	        		noImf = true;
	        	}
	        	else {
									
	            EMD emdAlgEmd = new EMD();
	            ImfList imfs =  emdAlgEmd.Process(btTime, btValue, 10, 0.05, 0.05);
	            int imfsCount = imfs.m;
	            int imfDataLen = imfs.n;
	            if (imfsCount == 0 || imfDataLen == 0)
	            {
	            	noImf = true;
	        	}
	            if(noImf)
	            {
	            	// Using default views;
	            	chartViewBodyTempTrendLine1 = new ChartViewAdv(this, R.xml.chart_body_temp_st_line);
					
	            	
	    			
					chartViewBodyTempTrendLine1.getAreas().get(0).getDefaultXAxis().getScale().setMargin(ChartAxisScale.MARGIN_NONE);
					chartViewBodyTempTrendLine1.getAreas().get(0).getDefaultYAxis().setTitle("IMF1");
					chartViewBodyTempTrendLine1.getAreas().get(0).getDefaultYAxis().getScale().setRange(0, 100);
					
					chartViewBodyTempTrendLine1.getAreas().get(0).getDefaultXAxis().setFormat(DateFormat.getTimeInstance(DateFormat.MEDIUM));
					chartViewBodyTempTrendLine1.getAreas().get(0).getDefaultXAxis().setValueType(ChartAxis.ValueType.Date);
					chartViewBodyTempTrendLine1.getAreas().get(0).getDefaultXAxis().getScale().setRange(0, 100);
					
					chartViewBodyTempTrendLine1.setPanning(ChartView.PANNING_BOTH);
		            chartViewBodyTempTrendLine1.setHitTestEnabled(true);
		            chartViewBodyTempTrendLine1.setOnTouchListener(pointClickListener);
		            
		    		
		    		// chart may contains more that one area.
		            chartViewBodyTempTrendLine1.enableZooming(chartViewBodyTempTrendLine1.getAreas().get(0), true, false);
		            
					chartViewBodyTempTrendLine1.setLayoutParams(new LinearLayout.LayoutParams(  
				            LayoutParams.FILL_PARENT,250, 1));
					
					//chartViewBodyTempTrendLine1.getAreas().get(0).getDefaultXAxis().setLabelAngle(15);
					
					chartViewBodyTempTrendLine2 = new ChartViewAdv(this, R.xml.chart_body_temp_st_line);
					
					
					
					chartViewBodyTempTrendLine2.getAreas().get(0).getDefaultXAxis().getScale().setMargin(ChartAxisScale.MARGIN_NONE);
					chartViewBodyTempTrendLine2.getAreas().get(0).getDefaultYAxis().setTitle("IMF2");
					chartViewBodyTempTrendLine2.getAreas().get(0).getDefaultYAxis().getScale().setRange(0, 100);
					
					chartViewBodyTempTrendLine2.getAreas().get(0).getDefaultXAxis().setFormat(DateFormat.getTimeInstance(DateFormat.MEDIUM));
					chartViewBodyTempTrendLine2.getAreas().get(0).getDefaultXAxis().setValueType(ChartAxis.ValueType.Date);
					chartViewBodyTempTrendLine2.getAreas().get(0).getDefaultXAxis().getScale().setRange(0, 100);
					
					chartViewBodyTempTrendLine2.setPanning(ChartView.PANNING_BOTH);
		            chartViewBodyTempTrendLine2.setHitTestEnabled(true);
		            chartViewBodyTempTrendLine2.setOnTouchListener(pointClickListener);
		         // chart may contains more that one area.
		            chartViewBodyTempTrendLine2.enableZooming(chartViewBodyTempTrendLine2.getAreas().get(0), true, false);
		            
					chartViewBodyTempTrendLine2.setLayoutParams(new LinearLayout.LayoutParams(  
				            LayoutParams.FILL_PARENT,250, 1));
					
					//chartViewBodyTempTrendLine2.getAreas().get(0).getDefaultXAxis().setLabelAngle(15);
					
					chartViewBodyTempTrendLine3 = new ChartViewAdv(this, R.xml.chart_body_temp_st_line);
					
					
					
					chartViewBodyTempTrendLine3.getAreas().get(0).getDefaultXAxis().getScale().setMargin(ChartAxisScale.MARGIN_NONE);
					chartViewBodyTempTrendLine3.getAreas().get(0).getDefaultYAxis().setTitle("IMF3");
					chartViewBodyTempTrendLine3.getAreas().get(0).getDefaultYAxis().getScale().setRange(0, 100);
					
					chartViewBodyTempTrendLine3.getAreas().get(0).getDefaultXAxis().setFormat(DateFormat.getTimeInstance(DateFormat.MEDIUM));
					chartViewBodyTempTrendLine3.getAreas().get(0).getDefaultXAxis().setValueType(ChartAxis.ValueType.Date);
					chartViewBodyTempTrendLine3.getAreas().get(0).getDefaultXAxis().getScale().setRange(0, 100);
					
					chartViewBodyTempTrendLine3.setPanning(ChartView.PANNING_BOTH);
		            chartViewBodyTempTrendLine3.setHitTestEnabled(true);
		            chartViewBodyTempTrendLine3.setOnTouchListener(pointClickListener);
		         // chart may contains more that one area.
		            chartViewBodyTempTrendLine3.enableZooming(chartViewBodyTempTrendLine3.getAreas().get(0), true, false);
		            
					chartViewBodyTempTrendLine3.setLayoutParams(new LinearLayout.LayoutParams(  
				            LayoutParams.FILL_PARENT,250, 1));
					
					//chartViewBodyTempTrendLine3.getAreas().get(0).getDefaultXAxis().setLabelAngle(15);
					
					chartViewBodyTempTrendLine4 = new ChartViewAdv(this, R.xml.chart_body_temp_st_line);
					
					
					
					chartViewBodyTempTrendLine4.getAreas().get(0).getDefaultXAxis().getScale().setMargin(ChartAxisScale.MARGIN_NONE);
					chartViewBodyTempTrendLine4.getAreas().get(0).getDefaultYAxis().setTitle("IMF4");
					chartViewBodyTempTrendLine4.getAreas().get(0).getDefaultYAxis().getScale().setRange(0, 100);
					
					chartViewBodyTempTrendLine4.getAreas().get(0).getDefaultXAxis().setFormat(DateFormat.getTimeInstance(DateFormat.MEDIUM));
					chartViewBodyTempTrendLine4.getAreas().get(0).getDefaultXAxis().setValueType(ChartAxis.ValueType.Date);
					chartViewBodyTempTrendLine4.getAreas().get(0).getDefaultXAxis().getScale().setRange(0, 100);
					
					chartViewBodyTempTrendLine4.setPanning(ChartView.PANNING_BOTH);
		            chartViewBodyTempTrendLine4.setHitTestEnabled(true);
		            chartViewBodyTempTrendLine4.setOnTouchListener(pointClickListener);
		         // chart may contains more that one area.
		            chartViewBodyTempTrendLine4.enableZooming(chartViewBodyTempTrendLine4.getAreas().get(0), true, false);
					chartViewBodyTempTrendLine4.setLayoutParams(new LinearLayout.LayoutParams(  
				            LayoutParams.FILL_PARENT,250, 1));
					
					//chartViewBodyTempTrendLine4.getAreas().get(0).getDefaultXAxis().setLabelAngle(15);
					
					
					//r
					chartViewBodyTempTrendLiner = new ChartViewAdv(this, R.xml.chart_body_temp_st_line);
					chartViewBodyTempTrendLiner.getAreas().get(0).getDefaultXAxis().getScale().setMargin(ChartAxisScale.MARGIN_NONE);
					chartViewBodyTempTrendLiner.getAreas().get(0).getDefaultYAxis().setTitle("R");
					chartViewBodyTempTrendLiner.getAreas().get(0).getDefaultYAxis().getScale().setRange(0, 100);
					
					chartViewBodyTempTrendLiner.getAreas().get(0).getDefaultXAxis().setFormat(DateFormat.getTimeInstance(DateFormat.MEDIUM));
					chartViewBodyTempTrendLiner.getAreas().get(0).getDefaultXAxis().setValueType(ChartAxis.ValueType.Date);
					chartViewBodyTempTrendLiner.getAreas().get(0).getDefaultXAxis().getScale().setRange(0, 100);
					
					chartViewBodyTempTrendLiner.setPanning(ChartView.PANNING_BOTH);
		            chartViewBodyTempTrendLiner.setHitTestEnabled(true);
		            chartViewBodyTempTrendLiner.setOnTouchListener(pointClickListener);
		         // chart may contains more that one area.
		            chartViewBodyTempTrendLiner.enableZooming(chartViewBodyTempTrendLiner.getAreas().get(0), true, false);
					chartViewBodyTempTrendLiner.setLayoutParams(new LinearLayout.LayoutParams(  
				            LayoutParams.FILL_PARENT,250, 1));
					
					//chartViewBodyTempTrendLiner.getAreas().get(0).getDefaultXAxis().setLabelAngle(15);
		            LinearLayout defaultChartLayout = (LinearLayout)findViewById(R.id.linearLayoutBdTpStTrendLine);

		            defaultChartLayout.addView(chartViewBodyTempTrendLine1,0);
		            defaultChartLayout.addView(chartViewBodyTempTrendLine2,1);
		            defaultChartLayout.addView(chartViewBodyTempTrendLine3,2);
		            defaultChartLayout.addView(chartViewBodyTempTrendLine4,3);
		            defaultChartLayout.addView(chartViewBodyTempTrendLiner,4);
		            
	            }
	            else
	            {
	            chartViewBodyTempTrendLines = new ChartViewAdv[imfsCount];
	            String lineTitleString= "";
	        
	            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	            
	          // for(int imfsIdx = 0; imfsIdx < imfsCount; imfsIdx++)
	            int imfsIdx = 0;
	            for (Imf current=imfs.first;current != null;current=current.next) 
	            {
	                double imfXmin = Double.POSITIVE_INFINITY;
		            double imfXmax = Double.NEGATIVE_INFINITY;
		            double imfYmin = Double.POSITIVE_INFINITY;
		            double imfYmax = Double.NEGATIVE_INFINITY;
		            
	            	double[] imfValue = new double[imfDataLen];
	            	for (int j=0; j<imfDataLen;j++) 
	            	{
	            		imfValue[j] =current.pointer[j];
	            		
	            	}
	            	// firstLoop to get the value ranges;
	            	for (int dataIdx = 0; dataIdx < imfDataLen; dataIdx++)
	            	{
	            		if(imfValue[dataIdx] > imfYmax)
	            		{
	            			imfYmax = imfValue[dataIdx];
	            		}
	            		 if (imfValue[dataIdx] < imfYmin)
	            		{
	            			imfYmin = imfValue[dataIdx];
	            		}
	            		
	            		if(btTimeinDate[dataIdx] > imfXmax)
	            		{
	            			imfXmax = btTimeinDate[dataIdx];
	            		}
	            		if (btTimeinDate[dataIdx] < imfXmin)
	            		{
	            			imfXmin = btTimeinDate[dataIdx];
	            		}
	            		
	            	}
	            	

	            	if(imfsIdx < (imfs.m-1))
	            	{
	            	    lineTitleString ="IMF" +  Integer.toString(imfsIdx+1);      
	            	}
	            	else
	            	{
	            		lineTitleString = "R";
	            	}
	            	chartViewBodyTempTrendLines[imfsIdx] = new ChartViewAdv(this, R.xml.chart_body_temp_st_line);
					
	            	
	    			
	            	chartViewBodyTempTrendLines[imfsIdx].getAreas().get(0).getDefaultXAxis().getScale().setMargin(ChartAxisScale.MARGIN_NONE);
	            	chartViewBodyTempTrendLines[imfsIdx].getAreas().get(0).getDefaultYAxis().setTitle(lineTitleString);
	            	chartViewBodyTempTrendLines[imfsIdx].getAreas().get(0).getDefaultYAxis().getScale().setRange(imfYmin, imfYmax);
					
	            	//chartViewBodyTempTrendLines[imfsIdx].getAreas().get(0).getDefaultXAxis().setFormat(DateFormat.getTimeInstance(DateFormat.MEDIUM));
	            	if(imfXmax - imfXmin > (DateUtils.DAY_IN_MILLIS*5))
	            	{
	            		chartViewBodyTempTrendLines[imfsIdx].getAreas().get(0).getDefaultXAxis().getScale().setInterval(1.0,ChartAxisScale.IntervalType.Days);
	            	chartViewBodyTempTrendLines[imfsIdx].getAreas().get(0).getDefaultXAxis().setLabelFormat(new SimpleDateFormat("dd HH:mm:ss"));
	            	}
	            	else if(imfXmax - imfXmin > (DateUtils.HOUR_IN_MILLIS*5))
	            	{
	            		chartViewBodyTempTrendLines[imfsIdx].getAreas().get(0).getDefaultXAxis().getScale().setInterval(10.0,ChartAxisScale.IntervalType.Hours);
		            	chartViewBodyTempTrendLines[imfsIdx].getAreas().get(0).getDefaultXAxis().setLabelFormat(new SimpleDateFormat("HH:mm:ss"));
		            }
	            	else if (imfXmax - imfXmin < (DateUtils.HOUR_IN_MILLIS*2))
	            	{
	            		chartViewBodyTempTrendLines[imfsIdx].getAreas().get(0).getDefaultXAxis().getScale().setInterval(10.0,ChartAxisScale.IntervalType.Minutes);
		            	chartViewBodyTempTrendLines[imfsIdx].getAreas().get(0).getDefaultXAxis().setLabelFormat(new SimpleDateFormat("HH:mm:ss"));
		            }
	            	else
	            	{
	            		chartViewBodyTempTrendLines[imfsIdx].getAreas().get(0).getDefaultXAxis().getScale().setInterval(30.0,ChartAxisScale.IntervalType.Minutes);
		            	chartViewBodyTempTrendLines[imfsIdx].getAreas().get(0).getDefaultXAxis().setLabelFormat(new SimpleDateFormat("HH:mm:ss"));
		            }	
	            	chartViewBodyTempTrendLines[imfsIdx].getAreas().get(0).getDefaultXAxis().setValueType(ChartAxis.ValueType.Date);
	            	
	            	chartViewBodyTempTrendLines[imfsIdx].getAreas().get(0).getDefaultXAxis().getScale().setRange(imfXmin, imfXmax);
					
	            	chartViewBodyTempTrendLines[imfsIdx].setPanning(ChartView.PANNING_BOTH);
	            	chartViewBodyTempTrendLines[imfsIdx].setHitTestEnabled(true);
		            chartViewBodyTempTrendLines[imfsIdx].setOnTouchListener(pointClickListener);
		            
		    		
		    		// chart may contains more that one area.
		            chartViewBodyTempTrendLines[imfsIdx].enableZooming(chartViewBodyTempTrendLines[imfsIdx].getAreas().get(0), true, false);
		            
		            chartViewBodyTempTrendLines[imfsIdx].setLayoutParams(new LinearLayout.LayoutParams(  
				            LayoutParams.FILL_PARENT,250, 1));
		            
		            LinearLayout chartLayout = (LinearLayout)findViewById(R.id.linearLayoutBdTpStTrendLine);
		            chartLayout.addView(chartViewBodyTempTrendLines[imfsIdx],imfsIdx);
		            
		            
		            //chartViewBodyTempTrendLines[imfsIdx].getAreas().get(0).getDefaultXAxis().getScale().setInterval(10.0,ChartAxisScale.IntervalType.Minutes);
		            //chartViewBodyTempTrendLines[imfsIdx].getAreas().get(0).getDefaultXAxis().setLabelFormat(new SimpleDateFormat("HH:mm:ss"));
		            chartViewBodyTempTrendLines[imfsIdx].getAreas().get(0).getDefaultXAxis().getScale().resetZoom();
		            
		            for (int dataIdx = 0; dataIdx < imfDataLen; dataIdx++)
	            	{
		            	ChartPointCollection pointsBt = chartViewBodyTempTrendLines[imfsIdx].getSeries().get(0).getPoints();
    					
		            	chartViewBodyTempTrendLines[imfsIdx].getSeries().get(0).setMarkerDrawable(getResources().getDrawable(R.drawable.diamonds_4));
    					
          				pointsBt.addXY(btTimeinDate[dataIdx], imfValue[dataIdx]);//调整到中国时区,+8

	            	}
		            
		            imfsIdx++;
	            }
	            
	            
	            }
	              }
	              } catch (Exception e) {
	        			// TODO Auto-generated catch block
	        			e.printStackTrace();
	        		};
	             
	            
	           /* chartViewBodyTempTrendLine1 = new ChartViewAdv(this, R.xml.chart_body_temp_st_line);
				
				
				chartViewBodyTempTrendLine1.getAreas().get(0).getDefaultXAxis().getScale().setMargin(ChartAxisScale.MARGIN_NONE);
				chartViewBodyTempTrendLine1.getAreas().get(0).getDefaultYAxis().setTitle(getString(R.string.body_temp_line_axis_title));
				chartViewBodyTempTrendLine1.getAreas().get(0).getDefaultYAxis().getScale().setRange(0, 100);
				
				//chartViewBodyTempTrendLine.getAreas().get(0).getDefaultXAxis().setFormat(DateFormat.getTimeInstance(DateFormat.MEDIUM));
				//chartViewBodyTempTrendLine.getAreas().get(0).getDefaultXAxis().setValueType(ChartAxis.ValueType.Date);
				chartViewBodyTempTrendLine1.setLayoutParams(new LinearLayout.LayoutParams(  
			            LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1));
				
				//chartViewBodyTempTrendLine.getAreas().get(0).getDefaultXAxis().setLabelAngle(15);
				
	            
				
	            

	            chartLayout.addView(chartViewBodyTempTrendLine1,1);
	            //chartLayout.addView(chartViewSpo,1);
	            
	            chartViewBodyTempTrendLine1.setPanning(ChartView.PANNING_BOTH);
	            chartViewBodyTempTrendLine1.setHitTestEnabled(true);
	            chartViewBodyTempTrendLine1.setOnTouchListener(pointClickListener);*/
	            
	    		
	    		// chart may contains more that one area.
	            //chartViewBodyTempTrendLine1.enableZooming(chartViewBodyTempTrendLine1.getAreas().get(0), true, false);
            
    		
    		    //add table
    		    //set up the talbe
	            /*	ListView lv = (ListView) this.findViewById(R.id.listViewBodyTempTableTrend);  
			        table = new ArrayList<TableRow>();   
			        TableCell[] titles = new TableCell[tableRowNum];// ê?è?°?7‰∏??????é    
			        int width = this.getWindowManager().getDefaultDisplay().getWidth()/(titles.length)-4; 
			        int height = this.getWindowManager().getDefaultDisplay().getHeight()/18;//the chart occupy 3/4 of the screen
			        // ???‰πaê?áè￠ò    
			           
			        titles[0] = new TableCell("周期",    
			                                    width ,   
			                                    //LayoutParams.FILL_PARENT,  
			                                    height,
			                                    TableCell.STRING,
			                                    Color.GRAY); 
			        titles[1] = new TableCell("IMF1",    
			                                    width ,   
			                                    //LayoutParams.FILL_PARENT,  
			                                    height,
			                                    TableCell.STRING,
			                                    Color.GRAY); 
			        titles[2] = new TableCell("IMF2",    
			                                    width ,   
			                                    //LayoutParams.FILL_PARENT,  
			                                    height,
			                                    TableCell.STRING,
			                                    Color.GRAY); 
			        titles[3] = new TableCell("IMF3",    
			                                    width ,   
			                                    //LayoutParams.FILL_PARENT,  
			                                    height,
			                                    TableCell.STRING,
			                                    Color.GRAY); 
			        titles[4] = new TableCell("IMF4",    
			                                    width ,   
			                                    //LayoutParams.FILL_PARENT,  
			                                    height,
			                                    TableCell.STRING,
			                                    Color.GRAY);                                                                                     
    		
    				table.add(new TableRow(titles,this));
    				
    				//second row
			        TableCell[] bt = new TableCell[tableRowNum];// ê?è?°?7‰∏??????é    
			           
			        // ???‰πaê?áè￠ò    
			        double bt_temp = 35.5;   
			        bt[0] = new TableCell("体温",    
			                                    width ,   
			                                    //LayoutParams.FILL_PARENT,
			                                    height,
			                                    TableCell.STRING,
			                                    Color.GRAY); 
			        bt[1] = new TableCell(bt_temp,    
		                    width ,   
		                    //LayoutParams.FILL_PARENT,  
		                    height,
		                    TableCell.NUMBER,
		                    Color.BLACK); 
			        
			        bt[2] = new TableCell(bt_temp,    
		                    width  ,   
		                    //LayoutParams.FILL_PARENT,  
		                    height,
		                    TableCell.NUMBER,
		                    Color.BLACK); 
			        
			        bt[3] = new TableCell(bt_temp,    
		                    width ,   
		                    //LayoutParams.FILL_PARENT,  
		                    height,
		                    TableCell.NUMBER,
		                    Color.BLACK); 
			        bt[4] = new TableCell(bt_temp,    
		                    width ,   
		                    //LayoutParams.FILL_PARENT,   
		                    height,
		                    TableCell.NUMBER,
		                    Color.BLACK); 
            
			table.add(new TableRow(bt,this));
			tableAdapter = new TableAdapter(this, table);   
			lv.setAdapter(tableAdapter); 
			
			lv.setLayoutParams(new LinearLayout.LayoutParams(  
					(width+2)*tableRowNum,LayoutParams.FILL_PARENT, 4));*/
			
	 }
	 
	 /*public boolean onTouchEvent(MotionEvent event)
		{
		 chartViewBodyTempTrendLine1.onTouchEvent(event);
		 chartViewBodyTempTrendLine2.onTouchEvent(event);
		 chartViewBodyTempTrendLine3.onTouchEvent(event);
		 boolean rt = chartViewBodyTempTrendLine4.onTouchEvent(event);
		 return rt;
		}*/
	  
	}

