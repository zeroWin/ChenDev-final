package mobileMedical.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import mobileMedical.namespace.R;

public class ListViewAdapter extends BaseAdapter {
	private Context context; // 运行上下文
	private List<Map<String, Object>> listItems; // 商品信息集合
	private LayoutInflater listContainer; // 视图容器

	public ListViewAdapter(Context context, List<Map<String, Object>> listItems) {
		this.context = context;
		listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.listItems = listItems;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return listItems.size();
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * ListView Item设置
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final int selectID = position;
		// 自定义视图
		ViewHolder ViewHolder = null;
		if (convertView == null) {
			ViewHolder = new ViewHolder();
			// 获取list_item布局文件的视图
			convertView = listContainer.inflate(R.layout.list_item, null);
			// 获取控件对象
			ViewHolder.title = (TextView) convertView
					.findViewById(R.id.titleItem);
			ViewHolder.info = (TextView) convertView
					.findViewById(R.id.infoItem);
			// 设置控件集到convertView
			convertView.setTag(ViewHolder);
		} else {
			ViewHolder = (ViewHolder) convertView.getTag();
		}


		// 设置文字和图片
		ViewHolder.title.setText((String) listItems.get(position).get("title"));
		ViewHolder.info.setText((String) listItems.get(position).get("info"));

		return convertView;
	}
	
	public final class ViewHolder { // 自定义控件集合
		public TextView title;
		public TextView info;
	}

}
