package mobileMedical.namespace;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
public class ExtendDiagPreference  extends DialogPreference {
    private DialogInterface.OnClickListener onClickListener = null;
    public ExtendDiagPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }
    public void setOnClickListener(DialogInterface.OnClickListener listener)
    {
        onClickListener = listener;
    }
 
    @Override
    public void onClick(DialogInterface dialog, int which)
    {
        super.onClick(dialog, which);
        if(onClickListener != null)
            onClickListener.onClick(dialog, which);
    }
    
} 
