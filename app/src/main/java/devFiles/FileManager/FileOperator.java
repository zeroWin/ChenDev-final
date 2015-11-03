package devFiles.FileManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import android.R.bool;
import android.content.Context;
import android.os.Environment;
import java.io.*;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FileOperator {

	private FileInputStream fInputStream = null;
	private DataInputStream dInputStream = null;
	private FileOutputStream fOutputStream = null;
	private PrintStream prStream = null;
	private File sdCardDir = null;
	private File targetFile = null;
	private RandomAccessFile randAccessFile = null;

	private final ReentrantLock lock = new ReentrantLock();

	public final int OPEN_FROM_LINE_MODE = 1;

	private Context context;
	public static byte[] buffer = new byte[2048];

	public FileOperator(Context context) {
		this.context = context;
	}

	public void DeleteFile() {

	}

	public boolean HasSDCard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	public void OpenFileForRead(String fileName, Boolean sdCardFile) {

		lock.lock();
		try {

			String fileNamePathString = null;
			if (sdCardFile) {

				if (sdCardDir == null) {
					sdCardDir = Environment.getExternalStorageDirectory();
				}

				try {
					/*
					 * fileNamePathString = sdCardDir.getCanonicalPath() + "/" +
					 * fileName;
					 */
					fileNamePathString = sdCardDir.getAbsolutePath() + fileName;
					if (fileNamePathString != null) {
						CloseReadFile();
						fInputStream = new FileInputStream(fileNamePathString);
						targetFile = new File(fileNamePathString);
						dInputStream = new DataInputStream(fInputStream);

					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				fileNamePathString = fileName;
				try {
					if (fileNamePathString != null) {
						CloseReadFile();
						fInputStream = context
								.openFileInput(fileNamePathString);
						targetFile = new File(fileNamePathString);
						dInputStream = new DataInputStream(fInputStream);

					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		} finally {
			lock.unlock();
		}

	}

	public void OpenFileForWrite(String fileName, int mode, Boolean sdCardFile) {
		lock.lock();
		try {
			String fileNamePathString = null;
			if (sdCardFile) {
				if (sdCardDir == null) {
					sdCardDir = Environment.getExternalStorageDirectory();
				}

				try {
					fileNamePathString = sdCardDir.getAbsolutePath() + fileName;
					if (fileNamePathString != null) {
						boolean append = false;
						if (mode == context.MODE_APPEND) {
							append = true;
						}
						CloseWriteFile();
						fOutputStream = new FileOutputStream(
								fileNamePathString, append);

						prStream = new PrintStream(fOutputStream);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				fileNamePathString = fileName;
				if (fileNamePathString != null) {

					try {
						CloseWriteFile();
						fOutputStream = context.openFileOutput(fileName, mode);
						prStream = new PrintStream(fOutputStream);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		} finally {
			lock.unlock();
		}

	}

	public String[] ReadFromFile(int startLine) {
		String[] contentStrings = null;
		lock.lock();
		try {
			String[] totalContentStrings = null;

			StringBuilder contentStringBuilder = new StringBuilder();
			byte[] tempBuffer = null;
			String oneRead = null;
			int readLen = 0;
			do {
				try {
					readLen = fInputStream.read(buffer, 0, buffer.length);
					if (readLen != -1) {
						if (readLen != buffer.length) {
							tempBuffer = new byte[readLen];
							System.arraycopy(buffer, 0, tempBuffer, 0, readLen);
							oneRead = new String(tempBuffer);
							contentStringBuilder.append(oneRead);
						} else {
							oneRead = new String(buffer);
							// contentStringBuilder.append(buffer);
							contentStringBuilder.append(oneRead);
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (readLen > 0);

			if (contentStringBuilder.length() != 0) {
				String contentString = contentStringBuilder.toString();
				totalContentStrings = contentString.split(System.getProperty("line.separator"));
				if (startLine > 0 && startLine <=totalContentStrings.length) {
					contentStrings = Arrays.copyOfRange(totalContentStrings,
							startLine - 1, totalContentStrings.length);
				}
			}
			/*
			 * long fileLength = targetFile.length(); if (fileLength != null) {
			 * 
			 * fInputStream.read }
			 */
		} finally {
			lock.unlock();
		}
		return contentStrings;
	}

	public String[] ReadFromFileAccordingContent(String content) {
		String[] contentStrings = null;
		lock.lock();
		try {
			String[] totalContentStrings = null;

			StringBuilder contentStringBuilder = new StringBuilder();
			byte[] tempBuffer = null;
			String oneRead = null;
			int readLen = 0;
			do {
				try {
					readLen = fInputStream.read(buffer, 0, buffer.length);
					if (readLen != -1) {
						if (readLen != buffer.length) {
							tempBuffer = new byte[readLen];
							System.arraycopy(buffer, 0, tempBuffer, 0, readLen);
							oneRead = new String(tempBuffer);
							contentStringBuilder.append(oneRead);
						} else {
							oneRead = new String(buffer);
							// contentStringBuilder.append(buffer);
							contentStringBuilder.append(oneRead);
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (readLen > 0);

			if (contentStringBuilder.length() != 0) {
				String contentString = contentStringBuilder.toString();
				totalContentStrings = contentString.split(System.getProperty("line.separator"));
				
				for(int i=0;i<totalContentStrings.length;i++){
					if(content.equalsIgnoreCase(totalContentStrings[i])){
						if(i+1<totalContentStrings.length){
							contentStrings=Arrays.copyOfRange(totalContentStrings,i+1, totalContentStrings.length);
							break;
						}
						
					}
				}
			}
			/*
			 * long fileLength = targetFile.length(); if (fileLength != null) {
			 * 
			 * fInputStream.read }
			 */
		} finally {
			lock.unlock();
		}
		return contentStrings;
	}
	
	public Boolean WriteToFile(String content) {
		Boolean successBoolean = false;
		lock.lock();
		try {
			byte[] buff = content.getBytes();
			try {
				fOutputStream.write(buff);
				successBoolean = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			lock.unlock();
		}

		return successBoolean;
	}

	public Boolean CloseReadFile() {
		Boolean closed = false;
		if (fInputStream != null)
			try {
				fInputStream.close();
				closed = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else {
			closed = true;
		}
		return closed;
	}

	public Boolean CloseWriteFile() {
		Boolean closed = false;
		if (fOutputStream != null)
			try {
				fOutputStream.close();
				closed = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else {
			closed = true;
		}
		return closed;
	}

}
