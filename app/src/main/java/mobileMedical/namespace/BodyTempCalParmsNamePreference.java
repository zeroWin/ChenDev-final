package mobileMedical.namespace;

import android.content.Context;
import android.preference.Preference;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BodyTempCalParmsNamePreference extends Preference{
	  private Context contextThis;
	  private TextView parmANameTextView;
	  private TextView parmBNameTextView;
	  private TextView parmCNameTextView;
	  private String[] parmsName;
	  
	  public BodyTempCalParmsNamePreference(Context context,String[] names) {
	        super(context);

	        parmsName = names;
	        contextThis = context;
	        setWidgetLayoutResource(R.layout.body_temp_cal_item_title);
	        
	        
	    }
	  @Override
	    protected void onBindView(View view) {
		  parmANameTextView = (TextView) view.findViewById(R.id.parmAName);
		  parmBNameTextView = (TextView) view.findViewById(R.id.parmBName);
		  parmCNameTextView = (TextView) view.findViewById(R.id.parmCName);
		  parmANameTextView.setText(parmsName[0]);
		  parmBNameTextView.setText(parmsName[1]);
		  parmCNameTextView.setText(parmsName[2]);
	  
	  }
}
