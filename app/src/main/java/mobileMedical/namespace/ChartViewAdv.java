// #region Copyright
// Copyright ArtfulBits Inc. 2005 - 2011. All rights reserved.
//
// Use of this code is subject to the terms of our license.
// A copy of the current license can be obtained at any time by e-mailing
// info@artfulbits.com. Re-distribution in any form is strictly
// prohibited. Any infringement will be prosecuted under applicable laws.
// #endregion
package mobileMedical.namespace;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.artfulbits.aiCharts.ChartView;
import com.artfulbits.aiCharts.Base.ChartArea;
import com.artfulbits.aiCharts.Base.ChartAxisScale;

public class ChartViewAdv extends ChartView
{
	private double m_touchDistance1 = Double.NaN;
	
	private double m_touchDistance2 = Double.NaN;
	
	private double x = Double.NaN;
	private double x_new = Double.NaN;
	
	private double m_zoomFactor = 1;
	private double scale_factor = Double.NaN;
	
	private ChartArea m_zoomArea = null;
	
	private boolean m_zoomXAxis = false;
	
	private boolean m_zoomYAxis = false;
	private int numAera = 1;
	
	//for multi chart panning
	private int initiater = 0;
	
	/**
	 * @param context
	 * @param chartId
	 */
	public ChartViewAdv(Context context, int chartId)
	{
		super(context, chartId);
	}
	
	public void enableZooming(ChartArea area, boolean xAxis, boolean yAxis)
	{
		m_zoomArea = area;
		
		m_zoomXAxis = xAxis;
		m_zoomYAxis = yAxis;
	}
	
	public void disableZooming()
	{
		m_zoomArea = null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.artfulbits.aiCharts.ChartView#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(m_zoomArea != null && event.getPointerCount() > 1)
		{
			if(event.getAction() == MotionEvent.ACTION_MOVE)
			{
				double dx = event.getX(0) - event.getX(1);
				double dy = event.getY(0) - event.getY(1);
				
				// compute distance between pointers
				m_touchDistance2 = Math.sqrt(dx * dx + dy * dy);
				
				if(Double.isNaN(m_touchDistance1))
				{
					m_touchDistance1 = m_touchDistance2;
				}
				else
				{
					// compute current zoom factor
					double currentZoomFactor = m_zoomFactor * m_touchDistance1
							/ m_touchDistance2;
					zoomToFactor(currentZoomFactor);
				}
			}
			
			//return true;
		}
		else if( !Double.isNaN(m_touchDistance1))
		{
			// save current zoom factor
			m_zoomFactor *= m_touchDistance1 / m_touchDistance2;
			
			// zoom factor can't be more that one
			m_zoomFactor = validateFactor(m_zoomFactor);
			
			// reset distances
			m_touchDistance1 = Double.NaN;
			m_touchDistance2 = Double.NaN;
			
			//return true;
		}
		
		if((event.getPointerCount() == 1)&&(event.getAction() == MotionEvent.ACTION_DOWN))
		{
			x = event.getX(0);
			return true;
		}
		if((event.getPointerCount() == 1)&&(event.getAction() == MotionEvent.ACTION_UP))
		{
			x = Double.NaN;
			return true;
		}
		if((event.getPointerCount() == 1)&&(event.getAction() == MotionEvent.ACTION_MOVE))
		{
			x_new = event.getX(0);
			
			for(int i=0;i<numAera;i++)
			{
			ChartAxisScale xScale = getAreas().get(i)
			.getDefaultXAxis().getScale();
			scale_factor = (xScale.getVisibleMaximum() - xScale.getVisibleMinimum())/1000;
			double new_value = xScale.getVisibleMinimum()+(x_new-x)*scale_factor;
			//if(new_value < xScale.get)
			xScale.setZoomPosition(xScale.getVisibleMinimum()-(x_new-x)*scale_factor );
			
			}
			x = x_new;
			return true;
		}
		return super.onTouchEvent(event);
	}
	
	private double validateFactor(double factor)
	{
		factor = Math.min(factor, 1d);
		factor = Math.max(factor, 0.01d);
		
		return factor;
	}
	
	private void zoomToFactor(double scale)
	{
		scale = validateFactor(scale);
		for(int i=0;i<numAera;i++)
		{
			if(m_zoomXAxis)
				this.getAreas().get(i).getDefaultXAxis().getScale().zoomToFactor(scale);
			
			if(m_zoomYAxis)
				this.getAreas().get(i).getDefaultYAxis().getScale().zoomToFactor(scale);
			
		}
		
	}
	
	public void increaseAreaNum()
	{
		numAera++;
	}
}
