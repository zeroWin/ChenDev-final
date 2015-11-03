package mobileMedical.namespace;

import android.content.Context;
import android.preference.Preference;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public final class BodyTempCalCoeffsPreference extends Preference{
	  private Context contextThis;
	  private EditText parmAEditText;
	  private EditText parmBEditText;
	  private EditText parmCEditText;
	  private float[] parmsValues;
	  
	  public BodyTempCalCoeffsPreference(Context context,float[] values) {
	        super(context);
	        parmsValues= new float[3]; 
	       System.arraycopy(values, 0, parmsValues, 0, 3);
	        
	        contextThis = context;
	        setWidgetLayoutResource(R.layout.body_temp_calcoeff_item);
	        
	        
	    }
	  @Override
	    protected void onBindView(View view) {
		  parmAEditText = (EditText) view.findViewById(R.id.coeffA);
		  parmBEditText = (EditText) view.findViewById(R.id.coeffB);
		  parmCEditText = (EditText) view.findViewById(R.id.coeffC);
		 parmAEditText.setText(String.valueOf(parmsValues[0]));
		  parmBEditText.setText(String.valueOf(parmsValues[1]));
		  parmCEditText.setText(String.valueOf(parmsValues[2]));
		  
		  parmCEditText.setOnEditorActionListener(new OnEditorActionListener() {  
	            @Override  
	            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {  
	            	parmsValues[0] = Float.valueOf(parmAEditText.getText().toString());
	            	parmsValues[1] = Float.valueOf(parmBEditText.getText().toString());
	            	parmsValues[2] = Float.valueOf(parmCEditText.getText().toString());
	             
	                return false;  
	            }  
	        });  
		
	  }
	  
	  public void setParmsValues(float[] values)
	  {
	       System.arraycopy(values, 0, parmsValues, 0, 3);

		  notifyChanged();
	  }
	  public float[] getParmsValues()
	  {
		  return parmsValues;
	  }
	  
	

}
