package mobileMedical.namespace;

import mobileMedical.namespace.R.string;
import android.content.Context;
import android.preference.Preference;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public final class BodyTempCalDataPreference extends Preference{
	  private Context contextThis;
	  private TextView coldEndTempValue;
	  private TextView heatEndTempValue;
	  private EditText referTempValue;
	  private float[] parmsValues;

	  
	  public BodyTempCalDataPreference(Context context, float[] values) {
	        super(context);
	        parmsValues = new float[3];
	        System.arraycopy(values, 0, parmsValues, 0, 3);
	        contextThis = context;
	        setWidgetLayoutResource(R.layout.body_temp_cal_item);
	        
	        
	    }
	  @Override
	    protected void onBindView(View view) {
		  coldEndTempValue = (TextView) view.findViewById(R.id.coldEndTempValue);
		  heatEndTempValue = (TextView) view.findViewById(R.id.heatEndTempValue);
		  referTempValue = (EditText)view.findViewById(R.id.referTempValue);
		  coldEndTempValue.setText(String.valueOf(parmsValues[0]));
		  heatEndTempValue.setText(String.valueOf(parmsValues[1]));
		  referTempValue.setText(String.valueOf(parmsValues[2]));
		  referTempValue.setOnKeyListener(onKey);
		  referTempValue.setOnEditorActionListener(new OnEditorActionListener() {  
	            @Override  
	            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {  
	            	
	            	parmsValues[2] = Float.valueOf(referTempValue.getText().toString());
	             
	                return false;  
	            }  
	        });  
	  }
	  
	  public float[] getValues()
	  {
		  return parmsValues;
	  }
	  
	  OnKeyListener onKey=new OnKeyListener() {

		  @Override

		  public boolean onKey(View v, int keyCode, KeyEvent event) {

		  // TODO Auto-generated method stub

		 // if(keyCode == KeyEvent.KEYCODE_ENTER){
			  if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&event.getAction() == KeyEvent.ACTION_UP)
			  {
		  InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

		  if(imm.isActive()){

		  imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0 );

		  }

		  return true;

		  }

		  return false;

		  }

		  };
}
