package mobileMedical.Contacts.DataBase;

public interface MSG_DB {
	public static final String DATABASENAME = "msg.db";
	public static final int DATABASE_VERSION = 1;

	public interface TABLES {
		/**
		 * 创建Msg表
		 * 
		 * @author dell
		 * 
		 */
		public interface Msg {
			public static final String TABLENAME = "tb_msg";

			public interface FIELDS {
				public static final String ID = "id";
				public static final String THREADID = "threadId";
				public static final String ADDRESS = "address";
				public static final String PERSON = "person";
				public static final String MSGMARK = "msgMark";
				public static final String MSGDATE = "msgDate";
				public static final String CONTENT = "content";
			}

			public interface SQL {
				// 创建表的SQL语句
				public static final String CREATE = "create table if not exists "
						+ TABLENAME
						+ "("
						+ FIELDS.ID
						+ " integer ,"
						+ FIELDS.THREADID
						+ " integer ,"
						+ FIELDS.ADDRESS
						+ " varchar(20),"
						+ FIELDS.PERSON
						+ " varchar(20),"
						+ FIELDS.MSGMARK
						+ " integer,"
						+ FIELDS.MSGDATE
						+ " varchar(20)," + FIELDS.CONTENT + " varchar(50))";
				// 删除表的SQL语句
				public static final String DROPTABLE = "drop table if exists "
						+ TABLENAME;
				// 插入一条记录的SQL语句
				public static final String INSERT = "insert into " + TABLENAME
						+ "(" + FIELDS.ID + "," + FIELDS.THREADID + ","
						+ FIELDS.ADDRESS + "," + FIELDS.PERSON + ","
						+ FIELDS.MSGMARK + "," + FIELDS.MSGDATE + ","
						+ FIELDS.CONTENT
						+ ") values('%s','%s','%s','%s','%s','%s','%s')";
				// 删除一条记录的SQL语句
				public static final String DELETE = "delete from " + TABLENAME
						+ " where 1=1";
				// 查询的SQL语句
				public static final String SELECT = "select *from " + TABLENAME
						+ " where %s";
			}
		}
	}
}
