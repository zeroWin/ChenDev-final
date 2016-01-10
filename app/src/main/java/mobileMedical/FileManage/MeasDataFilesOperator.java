package mobileMedical.FileManage;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import devFiles.FileManager.FileOperator;

public class MeasDataFilesOperator extends FileOperator {

	private final String measDataFileName = "measdata.txt";
	private final String measDataInfoFileName = "measdatainfo.txt";
	private final String measDataSyncFileName = "measdatasync.txt";
	private final String filesPath = "/mobileMedical.namespace/files/";
	private String rootDirectory = "/data/data/mobileMedical.namespace/files/";
	private final String FILE_PATH = "/data/data/mobileMedical.namespace/files/";
	private Context context = null;

	public MeasDataFilesOperator(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	/**
	 * 将文件复制到SD卡中或机身存储中
	 * @param sdCard
	 * @return
	 */
	public boolean CopyFilesToSDCardOrApp(boolean sdCard) {

		String[] files;
		try {
			files = context.getResources().getAssets().list("");
		} catch (IOException e1) {
			return false;
		}

		String filePath = null;
		if (sdCard) {
			boolean sacardMounted = HasSDCard();
			if (sacardMounted) {
				filePath = Environment.getExternalStorageDirectory()
						.getAbsolutePath() + filesPath;
			} else {
				return false;
			}
		} else {
			filePath = rootDirectory;
		}

		File workingPath = new File(filePath);
		// if this directory does not exists, make one.
		if (!workingPath.exists()) {
			if (!workingPath.mkdirs()) {

			}
		}

		for (int i = 0; i < files.length; i++) {
			try {
				String fileName = files[i];

				if (fileName.compareTo(measDataFileName) == 0) {

					File outFile = new File(workingPath, fileName);
					if (outFile.exists())
						continue;

					InputStream in = context.getResources().getAssets()
							.open(fileName);
					OutputStream out = new FileOutputStream(outFile);

					// Transfer bytes from in to out
					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}

					in.close();
					out.close();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

		}
		return true;
	}

	/*
	 * public boolean CopyFilesToAppData() { try { String filename = FILE_PATH +
	 * measDataFileName; File dir = new File(rootDirectory);
	 * 
	 * if (!dir.exists()) dir.mkdir();
	 * 
	 * 
	 * InputStream is = context.getResources().openRawResource( R.raw);
	 * FileOutputStream fos = new FileOutputStream(filename); byte[] buffer =
	 * new byte[7168]; int count = 0;
	 * 
	 * while ((count = is.read(buffer)) > 0){ fos.write(buffer, 0, count); }
	 * fos.close(); is.close(); } catch (Exception e){ return false; } return
	 * true; }
	 */

	public String[] ReadMeasDataFromFile(String filename,
			Boolean sdCardFileFirst, int startLine) {
		boolean sdCardFile = false;
		String filePathString = null;
		if (sdCardFileFirst) {
			sdCardFile = HasSDCard();
		}
		if (sdCardFile) {
			filePathString = filesPath + filename;
		} else {

			filePathString = filename;
		}

		OpenFileForRead(filePathString, sdCardFile);
		return (ReadFromFile(startLine));
	}

	public String[] ReadMeasDataFromFile(String filename,
			Boolean sdCardFileFirst, String content) {
		boolean sdCardFile = false;
		String filePathString = null;
		if (sdCardFileFirst) {
			sdCardFile = HasSDCard();
		}
		if (sdCardFile) {
			filePathString = filesPath + filename;
		} else {

			filePathString = filename;
		}

		OpenFileForRead(filePathString, sdCardFile);
		return (ReadFromFileAccordingContent(content));
	}

	// public void ReadMeasDataInfoFromFile(Boolean sdCardFile) {
	//
	// }
	//
	// public void ReadMeasDataSyncFromFile(Boolean sdCardFile) {
	//
	// }

	/**
	 * 将content写入filename中，模式为追加
	 * @param filename
	 * @param sdCardFile
	 * @param content
	 */
	public void WriteMeasDataToFile(String filename, Boolean sdCardFile,
			String content) {
		String filePathString = null;
		String filePath = "";
		if (sdCardFile) {
			filePath = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + filesPath;
			filePathString = filesPath + filename;

			File workingPath = new File(filePath);
			// if this directory does not exists, make one.
			if (!workingPath.exists()) {
				if (!workingPath.mkdirs()) {

				}
			}
		} else {
			filePathString = filename;
		}
		OpenFileForWrite(filePathString, context.MODE_APPEND, sdCardFile);
		WriteToFile(content);
	}

	// public void WriteMeasDataInfoToFile(Boolean sdCardFile) {
	//
	// }
	//
	// public void WriteMeasDataSyncToFile(Boolean sdCardFile) {
	//
	// }
}
