package mobileMedical.namespace;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.artfulbits.aiCharts.Base.ChartArea;
import com.artfulbits.aiCharts.Base.ChartSeries;
import com.artfulbits.aiCharts.ChartView;
import com.artfulbits.aiCharts.Types.ChartTypes;

import mobileMedical.util.readData;

public class aiChartsDisplay extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_charts_display);
        setTitle("分析结果");

        double[] data1 = readData.readFileByChars(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/mobileMedical.namespace/files/y0.txt");
        double[] data2 = readData.readFileByChars(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/mobileMedical.namespace/files/y1.txt");
        double[] data3 = readData.readFileByChars(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/mobileMedical.namespace/files/y2.txt");
        double[] data4 = readData.readFileByChars(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/mobileMedical.namespace/files/y3.txt");
        double[] data = readData.readFileByChars(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/mobileMedical.namespace/files/measdata.txt");
        //图1
        ChartView chartView1 = (ChartView)findViewById(R.id.chart1);
        ChartSeries series1 = new ChartSeries(ChartTypes.FastLine);
        ChartArea area = new ChartArea();

        series1.getPoints().setData(data1);
//        area.getDefaultXAxis().getScale().setMargin(ChartAxisScale.MARGIN_NONE);

        chartView1.getSeries().add(series1);
        chartView1.getAreas().add(area);
        //图2
        ChartView chartView2 = (ChartView)findViewById(R.id.chart2);
        ChartSeries series2 = new ChartSeries(ChartTypes.FastLine);
        ChartArea area2 = new ChartArea();

        series2.getPoints().setData(data2);

        chartView2.getSeries().add(series2);
        chartView2.getAreas().add(area2);

        //图3
        ChartView chartView3 = (ChartView)findViewById(R.id.chart3);
        ChartSeries series3 = new ChartSeries(ChartTypes.Column);
        ChartArea area3 = new ChartArea();

        series3.getPoints().setData(data3);

        chartView3.getSeries().add(series3);
        chartView3.getAreas().add(area3);

        //图4
        ChartView chartView4 = (ChartView)findViewById(R.id.chart4);
        ChartSeries series4 = new ChartSeries(ChartTypes.FastLine);
        ChartArea area4 = new ChartArea();

        series4.getPoints().setData(data4);

        chartView4.getSeries().add(series4);
        chartView4.getAreas().add(area4);

        //数据
        TextView tv1 = (TextView)findViewById(R.id.tv1);
        TextView tv2 = (TextView)findViewById(R.id.tv2);
        TextView tv3 = (TextView)findViewById(R.id.tv3);
        TextView tv4 = (TextView)findViewById(R.id.tv4);
        TextView tv5 = (TextView)findViewById(R.id.tv5);
        TextView tv6 = (TextView)findViewById(R.id.tv6);
        tv1.setText("SDNN:" + data[0]);
        tv2.setText("MSSD:" + data[1]);
        tv3.setText("HR:" + data[2]);
        tv4.setText("LF:" + data[3]);
        tv5.setText("HF:" + data[4]);
        tv6.setText("TP:" + data[5]);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ai_charts_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
