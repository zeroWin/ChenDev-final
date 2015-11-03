package mobileMedical.namespace;

import java.util.List;

import com.artfulbits.aiCharts.ChartView;
import com.artfulbits.aiCharts.Base.ChartLegendItem;
import com.artfulbits.aiCharts.Base.ChartPoint;
import com.artfulbits.aiCharts.Base.ChartSeries;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public abstract class OnPointClickListener implements OnTouchListener
{
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		boolean result = false; 
		ChartView chartView = (ChartView) v;

		int x = (int) event.getX();
		int y = (int) event.getY();

		switch (event.getAction())
		{
		case MotionEvent.ACTION_UP:
			for (Object obj : chartView.hitTest(x, y))
			{
				if (obj instanceof ChartPoint)
				{
					onPointClick((ChartPoint) obj);
					result =  true;
					break;
				}
				else if (obj instanceof ChartSeries)
				{
					onSeriesClick((ChartSeries) obj);
					result =  true;
					break;
				}
				else if (obj instanceof ChartLegendItem)
				{
					onLegendItemClick((ChartLegendItem) obj);
					result =  true;
					break;
				}
			}
			break;
		}

		return result;
	}

	public void onPointClick(ChartPoint point)
	{
		onSeriesClick(point.getSeries());
	}

	public void onSeriesClick(ChartSeries point)
	{

	}

	public void onLegendItemClick(ChartLegendItem item)
	{

	}
}
