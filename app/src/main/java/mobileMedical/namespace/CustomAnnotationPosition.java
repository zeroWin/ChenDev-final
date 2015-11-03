package mobileMedical.namespace;

import android.graphics.PointF;

import com.artfulbits.aiCharts.Annotations.ChartAnnotationPosition;
import com.artfulbits.aiCharts.Base.ChartArea;
import com.artfulbits.aiCharts.Base.ChartEngine;
import com.artfulbits.aiCharts.Base.ChartTransform;

public class CustomAnnotationPosition extends ChartAnnotationPosition
{
	private final ChartArea m_area;
	private final double m_x;
	private final double m_y;

	public CustomAnnotationPosition(ChartArea area, double x, double y)
	{
		m_area = area;
		m_x = x;
		m_y = y;

	}

//	@Override
/*	public PointF getPin(ChartEngine chart)
	{
		PointF pointF = new PointF();
		ChartTransform.create(m_area).getPoint(m_x, m_y, pointF);
		return pointF;
	}*/
	
	public void getPin(ChartEngine chart,PointF pointF)
	{
		//PointF pointF = new PointF();
		ChartTransform.create(m_area).getPoint(m_x, m_y, pointF);
		//return pointF;
	}
}
