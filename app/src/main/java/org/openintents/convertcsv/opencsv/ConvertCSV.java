package org.openintents.convertcsv.opencsv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class ConvertCSV {
	private Context mContext = null;
	private String fileName = null;
	
	/** 联系人名称 **/
	ArrayList<String> mContactsName = new ArrayList<String>();

	/** 联系人电话 **/
	ArrayList<String> mContactsNumber = new ArrayList<String>();


	public  ConvertCSV(Context context, String filename) {
		this.mContext = context;
		this.fileName = filename;
	}

	//开启导出操作
	public void startExport() {
		doExport();
	}

	/**
	 * @param file
	 */
	private void doExport() {
		final File file = new File(fileName);
		try {
			FileWriter writer = new FileWriter(file);
			doExport(writer);
			writer.close();

			//Toast.makeText(mContext, "导出成功！", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			//Toast.makeText(mContext, "导出失败！", Toast.LENGTH_SHORT).show();

		}
	}

	/**
	 * @param writer
	 * @throws IOException
	 */
	private void doExport(FileWriter writer) throws IOException {
		(new ExportCsv(mContext)).export(writer);
	}

	
    public void startImport(ArrayList<String> mContactsName,ArrayList<String> mContactsNumber) {

    	this.mContactsName = mContactsName;
    	this.mContactsNumber = mContactsNumber;
    	File file = new File(fileName);
		if (file.exists()) {
			try{
				FileReader reader = new FileReader(file);
				
				doImport(reader);
				
				reader.close();
				Toast.makeText(mContext, "通讯录成功导入！", Toast.LENGTH_SHORT).show();
				
			} catch (FileNotFoundException e) {
				Toast.makeText(mContext, "文件没有找到，通讯录导入失败！", Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				Toast.makeText(mContext, "文件读取失败，通讯录导入失败！", Toast.LENGTH_SHORT).show();
				
			}
		}
    }
    

   private void doImport(Reader reader) throws IOException {
		
		CSVReader csvreader = new CSVReader(reader);
	    String [] nextLine;
	    while ((nextLine = csvreader.readNext()) != null) {
	        // nextLine[] is an array of values from the line
	    	// We use the first column as note
	    	String contactName = nextLine[0];
	    	String contactPhoneNumber = nextLine[1];
	    	
	    	this.mContactsName.add(contactName);
	    	this.mContactsNumber.add(contactPhoneNumber);
	    	
	    	
	    	Log.i("contactName", contactName);
	    	Log.i("contactPhoneNumber", contactPhoneNumber);
	    }
	}
    
}
