package mobileMedical.Contacts.Manager;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileManager {
	public static final String COMMAND_BACKUP = "backup";
	public static final String COMMAND_RESTORE = "restore";
	public static final String COMMAND_ISCONTACT = "contact";
	public static final String COMMAND_ISMESSAGE = "message";
	private Context mContext;
	private MsgManager msgMgr;

	public FileManager(Context context) {
		this.mContext = context;
		msgMgr = new MsgManager(context);
	}

	public int dbFileOperation(String... params) {
		// 默认路径是 /data/data/(包名)/databases/*.db
		File dbFile = null;
		File backupDir = null;
		String command1 = params[1];
		if (command1.equals(COMMAND_ISCONTACT)) {
			dbFile = mContext
					.getDatabasePath("/data/data/cn.sqy.contacts/databases/contact.db");
			backupDir = new File(Environment.getExternalStorageDirectory(),
					"contactsBackup");
		} else if (command1.equals(COMMAND_ISMESSAGE)) {
			dbFile = mContext
					.getDatabasePath("/data/data/cn.sqy.contacts/databases/msg.db");
			backupDir = new File(Environment.getExternalStorageDirectory(),
					"msgBackup");
		} else {
			return -1;
		}

		if (!backupDir.exists()) {
			backupDir.mkdirs();
		}
		File backup = new File(backupDir, dbFile.getName());
		String command2 = params[0];
		if (command2.equals(COMMAND_BACKUP)) {
			try {
				if (command1.equals(COMMAND_ISCONTACT)) {
					backup.createNewFile();
					fileCopy(dbFile, backup);
					return 0;
				} else {
					msgMgr.insertMsgsFromSysAllMsgs();// 再将系统短信导入到数据库
					backup.createNewFile();// 最后再把数据库备份到SD卡上
					fileCopy(dbFile, backup);
					return 3;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			}
		} else if (command2.equals(COMMAND_RESTORE)) {
			try {
				if (command1.equals(COMMAND_ISCONTACT)) {
					fileCopy(backup, dbFile);
					return 1;
				} else {
					fileCopy(backup, dbFile);// 先把数据库从SD卡上恢复到我的数据库
					msgMgr.insertIntoSysMsgFromMyMsg();// 再从我的数据库导入到系统数据库中
					return 4;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			}
		} else {
			return -1;
		}
	}

	private void fileCopy(File dbFile, File backup) throws IOException {
		FileChannel inChannel = new FileInputStream(dbFile).getChannel();
		FileChannel outChannel = new FileOutputStream(backup).getChannel();
		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inChannel != null) {
				inChannel.close();
			}
			if (outChannel != null) {
				outChannel.close();
			}
		}
	}
}
