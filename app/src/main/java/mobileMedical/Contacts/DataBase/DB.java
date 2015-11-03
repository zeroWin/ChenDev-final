package mobileMedical.Contacts.DataBase;

public interface DB {
	public static final String DATABASENAME = "contact.db";
	public static final int DATABASE_VERSION = 1;

	public interface TABLES {
		/**
		 * 创建Contact表
		 * 
		 * @author dell
		 * 
		 */
		public interface CONTACT {
			public static final String TABLENAME = "tbl_contact";

			public interface FIELDS {
				public static final String ID = "id";
				public static final String NAME = "name";
				public static final String NAMEPINYIN = "namePinyin";
				public static final String NICKNAME = "nickName";
				public static final String ADDRESS = "address";
				public static final String COMPANY = "company";
				public static final String BIRTHDAY = "birthday";
				public static final String NOTE = "note";
				public static final String IMAGE = "image";
				public static final String GROUPID = "groupId";
				public static final String IDNO = "idNo";
				public static final String DESCRIBE = "describe";
				public static final String GENDER = "gender";
				public static final String TYPE = "type";
				public static final String FILENO = "fileno";
				public static final String HOSPITAL = "hospital";
				public static final String RELATIONNAME = "relationName";
				public static final String RELATION = "relation";
				public static final String RELATIONTELE = "relationTele";
				public static final String RELATIONEMAIL = "relationEmail";
				public static final String DOCTORID = "doctorID";
			}

			public interface SQL {
				// 创建联系人表的SQL语句
				public static final String CREATE = "create table if not exists "
						+ TABLENAME
						+ "("
						+ FIELDS.ID
						+ " integer primary key autoincrement,"
						+ FIELDS.NAME
						+ " varchar(20),"
						+ FIELDS.NAMEPINYIN
						+ " varchar(20),"
						+ FIELDS.NICKNAME
						+ " varchar(20),"
						+ FIELDS.ADDRESS
						+ " varchar(30),"
						+ FIELDS.COMPANY
						+ " varchar(20),"
						+ FIELDS.BIRTHDAY
						+ " varchar(20),"
						+ FIELDS.NOTE
						+ " varchar(40),"
						+ FIELDS.IMAGE
						+ " integer,"
						+ FIELDS.GROUPID
						+ " integer,"
						+ FIELDS.IDNO
						+ " varchar(20),"
						+ FIELDS.DESCRIBE
						+ " varchar(30),"
						+ FIELDS.GENDER
						+ " varchar(10),"
						+ FIELDS.TYPE
						+ " varchar(20),"
						+ FIELDS.FILENO
						+ " varchar(20),"
						+ FIELDS.HOSPITAL
						+ " varchar(20),"
						+ FIELDS.RELATIONNAME
						+ " varchar(20),"
						+ FIELDS.RELATION
						+ " varchar(10),"
						+ FIELDS.RELATIONTELE
						+ " varchar(20),"
						+ FIELDS.RELATIONEMAIL 
						+ " varchar(50)," 
						+ FIELDS.DOCTORID
						+ " varchar(12)" + ")";
				// 删除表的SQL语句
				public static final String DROPTABLE = "drop table if exists "
						+ TABLENAME;
				// 插入一条记录的SQL语句
				public static final String INSERT = "insert into "
						+ TABLENAME
						+ "("
						+ FIELDS.NAME
						+ ","
						+ FIELDS.NAMEPINYIN
						+ ","
						+ FIELDS.NICKNAME
						+ ","
						+ FIELDS.ADDRESS
						+ ","
						+ FIELDS.COMPANY
						+ ","
						+ FIELDS.BIRTHDAY
						+ ","
						+ FIELDS.NOTE
						+ ","
						+ FIELDS.IMAGE
						+ ","
						+ FIELDS.GROUPID
						+ ","
						+ FIELDS.IDNO
						+ ","
						+ FIELDS.DESCRIBE
						+ ","
						+ FIELDS.GENDER
						+ ","
						+ FIELDS.TYPE
						+ ","
						+ FIELDS.FILENO
						+ ","
						+ FIELDS.HOSPITAL
						+ ","
						+ FIELDS.RELATIONNAME
						+ ","
						+ FIELDS.RELATION
						+ ","
						+ FIELDS.RELATIONTELE
						+ ","
						+ FIELDS.RELATIONEMAIL
						+ ","
						+ FIELDS.DOCTORID
						+ ") values('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s')";
				// 根据主键id号修改一条记录的SQL语句
				public static final String UPDATE = "update " + TABLENAME
						+ " set " + FIELDS.NAME + "='%s'," + FIELDS.NAMEPINYIN
						+ "='%s'," + FIELDS.NICKNAME + "='%s',"
						+ FIELDS.ADDRESS + "='%s'," + FIELDS.COMPANY + "='%s',"
						+ FIELDS.BIRTHDAY + "='%s'," + FIELDS.NOTE + "='%s',"
						+ FIELDS.IMAGE + "=%s," + FIELDS.GROUPID + "=%s,"
						+ FIELDS.IDNO + "=%s,"+ FIELDS.DESCRIBE + "=%s,"+ FIELDS.GENDER + "=%s,"+ FIELDS.TYPE + "=%s,"+ FIELDS.FILENO + "=%s,"+ FIELDS.HOSPITAL + "=%s," + FIELDS.RELATIONNAME + "=%s,"
						+ FIELDS.RELATION + "=%s," + FIELDS.RELATIONTELE
						+ "=%s," + FIELDS.RELATIONEMAIL + "=%s," + FIELDS.DOCTORID + "=%s where "
						+ FIELDS.ID + "=%s";
				// 删除一条记录的SQL语句
				public static final String DELETE = "delete from " + TABLENAME
						+ " where id=%s";
				// 查询的SQL语句
				public static final String SELECT = "select *from " + TABLENAME
						+ " where %s";
				// 改变分组
				public static final String CHANGEGROUP = "update " + TABLENAME
						+ " set " + FIELDS.GROUPID + "=%s where %s";
			}
		}

		/**
		 * 创建Email表
		 * 
		 * @author dell
		 * 
		 */
		public interface EMAIL {
			public static final String TABLENAME = "tbl_email";

			public interface FIELDS {
				public static final String EMAILID = "emailId";
				public static final String ID = "id";
				public static final String EMAILNAME = "emailName";
				public static final String EMAILACOUNT = "emailAcount";
			}

			public interface SQL {
				// 创建表的SQL语句
				public static final String CREATE = "create table if not exists "
						+ TABLENAME
						+ "("
						+ FIELDS.EMAILID
						+ " integer primary key autoincrement,"
						+ FIELDS.ID
						+ " integer,"
						+ FIELDS.EMAILNAME
						+ " varchar(20),"
						+ FIELDS.EMAILACOUNT + " varchar(30))";
				// 删除表的SQL语句
				public static final String DROPTABLE = "drop table if exists "
						+ TABLENAME;
				// 插入一条记录的SQL语句
				public static final String INSERT = "insert into " + TABLENAME
						+ "(" + FIELDS.ID + "," + FIELDS.EMAILNAME + ","
						+ FIELDS.EMAILACOUNT + ") values('%s','%s','%s')";
				// 根据主键emailId号修改一条记录的SQL语句
				public static final String UPDATE = "update " + TABLENAME
						+ " set " + FIELDS.ID + "=%s," + FIELDS.EMAILNAME
						+ "='%s'," + FIELDS.EMAILACOUNT + "='%s' where "
						+ FIELDS.EMAILID + "=%s";
				// 删除一条记录的SQL语句
				public static final String DELETE = "delete from " + TABLENAME
						+ " where %s";
				// 查询的SQL语句
				public static final String SELECT = "select *from " + TABLENAME
						+ " where %s";
			}
		}

		/**
		 * 创建Group表
		 * 
		 * @author dell
		 * 
		 */
		public interface GROUP {
			public static final String TABLENAME = "tbl_group";

			public interface FIELDS {
				public static final String GROUPID = "groupId";
				public static final String GROUPNAME = "groupName";
			}

			public interface SQL {
				// 创建表的SQL语句
				public static final String CREATE = "create table if not exists "
						+ TABLENAME
						+ "("
						+ FIELDS.GROUPID
						+ " integer primary key autoincrement,"
						+ FIELDS.GROUPNAME + " varchar(20))";
				// 删除表的SQL语句
				public static final String DROPTABLE = "drop table if exists "
						+ TABLENAME;
				// 插入一条记录的SQL语句
				public static final String INSERT = "insert into " + TABLENAME
						+ "(" + FIELDS.GROUPNAME + ") values('%s')";
				// 根据主键emailId号修改一条记录的SQL语句
				public static final String UPDATE = "update " + TABLENAME
						+ " set " + FIELDS.GROUPNAME + " = '%s' where "
						+ FIELDS.GROUPID + " = '%s' ";
				// 删除一条记录的SQL语句
				public static final String DELETE = "delete from " + TABLENAME
						+ " where groupId=%s";
				// 查询的SQL语句
				public static final String SELECT = "select *from " + TABLENAME
						+ " where %s";
			}
		}

		/**
		 * 创建IM表
		 * 
		 * @author dell
		 * 
		 */
		public interface IM {
			public static final String TABLENAME = "tbl_im";

			public interface FIELDS {
				public static final String IMID = "imId";
				public static final String ID = "id";
				public static final String IMNAME = "imName";
				public static final String IMACOUNT = "imAcount";
			}

			public interface SQL {
				// 创建表的SQL语句
				public static final String CREATE = "create table if not exists "
						+ TABLENAME
						+ "("
						+ FIELDS.IMID
						+ " integer primary key autoincrement,"
						+ FIELDS.ID
						+ " integer,"
						+ FIELDS.IMNAME
						+ " varchar(20),"
						+ FIELDS.IMACOUNT + " varchar(30))";
				// 删除表的SQL语句
				public static final String DROPTABLE = "drop table if exists "
						+ TABLENAME;
				// 插入一条记录的SQL语句
				public static final String INSERT = "insert into " + TABLENAME
						+ "(" + FIELDS.ID + "," + FIELDS.IMNAME + ","
						+ FIELDS.IMACOUNT + ") values('%s','%s','%s')";
				// 根据主键emailId号修改一条记录的SQL语句
				public static final String UPDATE = "update " + TABLENAME
						+ " set " + FIELDS.ID + "=%s," + FIELDS.IMNAME
						+ "='%s'," + FIELDS.IMACOUNT + "='%s' where "
						+ FIELDS.IMID + "=%s";
				// 删除一条记录的SQL语句
				public static final String DELETE = "delete from " + TABLENAME
						+ " where imId=%s";
				// 查询的SQL语句
				public static final String SELECT = "select *from " + TABLENAME
						+ " where %s";
			}
		}

		/**
		 * 创建Tel表
		 * 
		 * @author dell
		 * 
		 */
		public interface TEL {
			public static final String TABLENAME = "tbl_tel";

			public interface FIELDS {
				public static final String TELID = "telId";
				public static final String ID = "id";
				public static final String TELNAME = "telName";
				public static final String TELNUMBER = "telNumber";
			}

			public interface SQL {
				// 创建表的SQL语句
				public static final String CREATE = "create table if not exists "
						+ TABLENAME
						+ "("
						+ FIELDS.TELID
						+ " integer primary key autoincrement,"
						+ FIELDS.ID
						+ " integer,"
						+ FIELDS.TELNAME
						+ " varchar(20),"
						+ FIELDS.TELNUMBER + " varchar(30))";
				// 删除表的SQL语句
				public static final String DROPTABLE = "drop table if exists "
						+ TABLENAME;
				// 插入一条记录的SQL语句
				public static final String INSERT = "insert into " + TABLENAME
						+ "(" + FIELDS.ID + "," + FIELDS.TELNAME + ","
						+ FIELDS.TELNUMBER + ") values('%s','%s','%s')";
				// 根据主键emailId号修改一条记录的SQL语句
				public static final String UPDATE = "update " + TABLENAME
						+ " set " + FIELDS.ID + "=%s," + FIELDS.TELNAME
						+ "='%s'," + FIELDS.TELNUMBER + "='%s' where "
						+ FIELDS.TELID + "=%s";
				// 删除一条记录的SQL语句
				public static final String DELETE = "delete from " + TABLENAME
						+ " where telId=%s";
				// 查询的SQL语句
				public static final String SELECT = "select *from " + TABLENAME
						+ " where %s";
			}
		}
	}
}
