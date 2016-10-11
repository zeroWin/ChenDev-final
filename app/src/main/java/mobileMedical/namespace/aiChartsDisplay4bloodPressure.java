package mobileMedical.namespace;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.artfulbits.aiCharts.Base.ChartArea;
import com.artfulbits.aiCharts.Base.ChartSeries;
import com.artfulbits.aiCharts.ChartView;
import com.artfulbits.aiCharts.Types.ChartTypes;

import mobileMedical.util.readData;

public class aiChartsDisplay4bloodPressure extends Activity {
    private TextView tv ;
    private LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_charts_display4blood_pressure);

        WindowManager wm = this.getWindowManager();
        int height = wm.getDefaultDisplay().getHeight();

        init();

        //更改控件的高度
        LinearLayout.LayoutParams tvParams = (LinearLayout.LayoutParams) tv.getLayoutParams(); // 取控件tv当前的布局参数
        tvParams.height = height/10;
        tv.setLayoutParams(tvParams);
        LinearLayout.LayoutParams llParams = (LinearLayout.LayoutParams) ll.getLayoutParams(); // 取控件ll当前的布局参数
        llParams.height = 5*height/10;
        ll.setLayoutParams(llParams);

        try {
//        double[] bpdata1 = readData.readFileByChars( "data/data/mobileMedical.namespace/files/ac.txt");
//        double[] bpdata2 = readData.readFileByChars( "data/data/mobileMedical.namespace/files/dc.txt");
            double[] bpdata1 = readData.readFileByChars(Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/mobileMedical.namespace/files/ac.txt");
            double[] bpdata2 = readData.readFileByChars(Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/mobileMedical.namespace/files/dc.txt");

            ChartView bpcv1 = (ChartView) findViewById(R.id.bloodPressurechart1);
            ChartView bpcv2 = (ChartView) findViewById(R.id.bloodPressurechart2);

            //图1
            ChartSeries bpcs1 = new ChartSeries(ChartTypes.FastLine);
            ChartSeries bpcs12 = new ChartSeries(ChartTypes.FastLine);
            ChartArea bpca1 = new ChartArea();
            bpca1.getDefaultYAxis().setTitle(getString(R.string.bloodpressDC));
            bpcs1.getPoints().setData(bpdata1);
            bpcs12.getPoints().setData(bpdata2);
            bpcv1.getSeries().add(bpcs1);
            bpcv1.getSeries().add(bpcs12);
            bpcv1.getAreas().add(bpca1);
//图2
            ChartSeries bpcs2 = new ChartSeries(ChartTypes.FastLine);
            ChartArea bpca2 = new ChartArea();
            bpca2.getDefaultYAxis().setTitle(getString(R.string.bloodpressAC));
            bpcs2.getPoints().setData(bpdata2);
            bpcv2.getSeries().add(bpcs2);
            bpcv2.getAreas().add(bpca2);
        }catch (Exception e){
            Toast.makeText(this,"没找到文件",Toast.LENGTH_SHORT).show();
        }
    }

    private void init() {
        tv = (TextView)findViewById(R.id.bloodPressuretv);
        ll = (LinearLayout)findViewById(R.id.bloodPressurell);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ai_charts_display4blood_pressure, menu);
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
