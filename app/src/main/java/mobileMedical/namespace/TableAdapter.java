package mobileMedical.namespace;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;
/**
 * ListView自适应实现Table的类TableAdapter.java代码如下：

	PS：TableCell是格单元的类，TableRow是表格行的类，TableRowView是实现表格行的组件。
	实现步骤：TableCell --> TableRow（TableRowView）-->ListView

 * @author longgangbai
 *
 */
public class TableAdapter extends BaseAdapter {   
    private Context context;   
    private List<TableRow> table;   
    public TableAdapter(Context context, List<TableRow> table) {   
        this.context = context;   
        this.table = table;   
    }   
    @Override   
    public int getCount() {   
        return table.size();   
    }   
    @Override   
    public long getItemId(int position) {   
        return position;   
    }   
    public TableRow getItem(int position) {   
        return table.get(position);   
    }   
    public LinearLayout getView(int position, View convertView, ViewGroup parent) {   
        TableRow tableRow = table.get(position);   
        return new TableRowView(this.context, tableRow);   
    } 

    public void updateTextView(int rowIndex,int cellIndex,Object value)
    {
    	DecimalFormat df = new DecimalFormat("###.00"); 
    	TableRow row = getItem(rowIndex);
    	TextView textView = row.getTextView(cellIndex);
    	if(value.equals(Float.NaN))
    		textView.setText("--");
    	else
    		textView.setText(String.valueOf(df.format(Float.parseFloat(value.toString()))));
    }
    /** 
     * TableRowView 实现表格行的样式 
     * @author hellogv 
     */   
    class TableRowView extends LinearLayout {   
        public TableRowView(Context context, TableRow tableRow) {   
            super(context);   
           // if (parent != null) { 
           //     parent.removeAllViewsInLayout();
            //} 
            
            this.setOrientation(LinearLayout.HORIZONTAL);   
            for (int i = 0; i < tableRow.getSize(); i++) {//逐个格单元添加到行    
                TableCell tableCell = tableRow.getCellValue(i);   
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(   
                        tableCell.width, tableCell.height);//按照格单元指定的大小设置空间 
                
                layoutParams.setMargins(1, 1, 1, 1);//预留空隙制造边框
               // this.removeAllViewsInLayout();
                     
                addView(tableRow.getTextView(i), layoutParams);
                
                  
            }   
            this.setBackgroundColor(Color.WHITE);//背景白色，利用空隙来实现边框    
        }   
    }   
    /** 
     * TableRow 实现表格的行 
     * @author hellogv 
     */   
    static public class TableRow {  
    	private Context context;  
        private TableCell[] cell; 
        public TextView[] textViewTable;
        public TableRow(TableCell[] cell,Context context) {   
            this.cell = cell; 
            makeTextView(context);
        }   
        public int getSize() {   
            return cell.length;   
        }   
        public TableCell getCellValue(int index) {   
            if (index >= cell.length)   
                return null;   
            return cell[index];   
        }  
        public TextView getTextView(int index)
        {
        	return textViewTable[index];
        }
        public void makeTextView(Context context)
        {
        	textViewTable = new TextView[cell.length];
        	DecimalFormat df = new DecimalFormat("###.00"); 
        	for(int i=0;i<cell.length;i++)
        	{
        		textViewTable[i] = new TextView(context);
        		textViewTable[i].setLines(1);   
        		textViewTable[i].setGravity(Gravity.CENTER);   
        		textViewTable[i].setBackgroundColor(cell[i].color);//背景黑色   
        		if(cell[i].type == TableCell.NUMBER)
        		{
	        		if(cell[i].value.equals(Float.NaN))
	        			textViewTable[i].setText("--");
	        		else
	        			textViewTable[i].setText(String.valueOf(df.format(Float.parseFloat(cell[i].value.toString()))));
        		}
        		else
        			textViewTable[i].setText(cell[i].value.toString());
        		
        		textViewTable[i].setTextAppearance(context, android .R.style.TextAppearance_Large);
        	}
        }
    }   
    /** 
     * TableCell 实现表格的格单元 
     * @author hellogv 
     */   
    static public class TableCell {   
        static public final int STRING = 0;   
        static public final int NUMBER = 1;   
        public Object value;   
        public int width;   
        public int height;   
        private int type;   
        private int color;
        public TableCell(Object value, int width, int height, int type, int color) {   
            this.value = value;   
            this.width = width;   
            this.height = height;   
            this.type = type; 
            this.color = color;
        }   
        public Object getValue(){
        	return this.value;
        }
        public void setValue(Object value){
        	this.value=value;
        }
    }   
}  
 

