package mobileMedical.Contacts.UI;

/* import相关class */
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mobileMedical.Contacts.Tool.CommonUtil;
import mobileMedical.Contacts.View.FileAdapter;
import mobileMedical.namespace.R;

/**
 * Demo2资源管理器进化
 * 
 * @author dell
 * 
 */
public class FileSelectActivity extends ListActivity {
	/*
	 * 对象声明 items：存放显示的名称 paths：存放文件路径 rootPath：起始目录
	 */
	private List<String> items = null;
	private List<String> paths = null;
	private String rootPath = "/";
	private TextView mPath;
	private String returnImagePath = null;

	public static final int RESULT_FILE_SELECTED = 1;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		/* 加载main.xml Layout */

		setContentView(R.layout.contact_activity_file_select);
		/* 初始化mPath，用以显示目前路径 */
		mPath = (TextView) findViewById(R.id.mPath);
		getFileDir(rootPath);
	}

	/* 取得文件架构的method */
	private void getFileDir(String filePath) {
		/* 设定目前所存路径 */
		mPath.setText(filePath);
		items = new ArrayList<String>();
		paths = new ArrayList<String>();

		File f = new File(filePath);
		File[] files = f.listFiles();

		if (!filePath.equals(rootPath)) {
			/* 第一笔设定为[并到根目录] */
			items.add("b1");
			paths.add(rootPath);
			/* 第二笔设定为[并勺层] */
			items.add("b2");
			paths.add(f.getParent());
		}
		/* 将所有文件放入ArrayList中 */
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			items.add(file.getName());
			paths.add(file.getPath());
		}

		/* 使用自定义的MyAdapter来将数据传入ListActivity */
		setListAdapter(new FileAdapter(this, items, paths));
	}

	/* 设定ListItem被按下时要做的操作 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		File file = new File(paths.get(position));
		returnImagePath = paths.get(position);
		if (file.canRead()) {
			if (file.isDirectory()) {
				/* 如果是文件夹就运行getFileDir() */
				getFileDir(paths.get(position));
			} else {
				/* 如果是文件调用fileHandle() */
				fileHandle(file);
			}
		} else {
			/* 弹出AlertDialog显示权限不足 */
			new AlertDialog.Builder(this)
					.setTitle("Message")
					.setMessage("权限不足!")
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).show();
		}
	}

	/* 处理文件的method */
	private void fileHandle(final File file) {
		/* 按下文件时的OnClickListener */
		OnClickListener listener1 = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					/* 选择的item为打开文件 */
					openFile(file);
				} else if (which == 1) {
					String type = getMIMEType(file);
					//CommonUtil.Toast(FileSelectActivity.this, type);
					if (type.equals("image/*")) {
						CommonUtil.Log("sqy", "openFile", returnImagePath, 'i');
						Intent data = new Intent();
						data.putExtra("path", returnImagePath);
						setResult(RESULT_FILE_SELECTED, data);
						finish();
					} else
						CommonUtil
								.Toast(FileSelectActivity.this, "选择的文件不是图片!!");
				} else {
					/* 选择的item为删除文件 */
					new AlertDialog.Builder(FileSelectActivity.this)
							.setTitle("注意!")
							.setMessage("确定要删除文件吗?")
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											/* 删除文件 */
											file.delete();
											getFileDir(file.getParent());
										}
									})
							.setNegativeButton("取消",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
										}
									}).show();
				}
			}
		};

		/* 选择几个文件时，弹出要处理文件的ListDialog */
		String[] menu = { "打开文件", "获取图片", "删除文件" };
		new AlertDialog.Builder(FileSelectActivity.this).setTitle("你要做甚么?")
				.setItems(menu, listener1)
				.setPositiveButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
	}

	/* 手机打开文件的method */
	private void openFile(File f) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);

		// 调用getMIMEType()来取得MimeType
		String type = getMIMEType(f);
		// 设定intent的file与MimeType
		intent.setDataAndType(Uri.fromFile(f), type);
		startActivity(intent);
	}

	/* 判断文件MimeType的method */
	private String getMIMEType(File f) {
		String type = "";
		String fName = f.getName();
		/* 取得扩展名 */
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();
		Log.i("sqy", end);

		/* 按扩展名的类型决定MimeType */
		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			type = "audio";
		} else if (end.equals("3gp") || end.equals("mp4")) {
			type = "video";
		} else if (end.equalsIgnoreCase("jpg") || end.equalsIgnoreCase("gif")
				|| end.equalsIgnoreCase("png") || end.equalsIgnoreCase("jpeg")
				|| end.equalsIgnoreCase("bmp")) {
			type = "image";
		} else {
			type = "*";
		}
		/* 如果无法直接打开，就弹出软件列表给用户选择 */
	  type += "/*";
		return type;
	}
}
