package mobileMedical.Contacts.Model;

public class Tel {
	private int telId;// 电话ID号（主键）
	private int id;// 联系人ID号
	private String telName;// 电话类型名称
	private String telNumber;// 电话号码

	/**
	 * 构造函数
	 */
	public Tel() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 构造函数(+1)
	 * 
	 * @param id
	 *            联系人ID号
	 * @param telName
	 *            电话类型名称
	 * @param telNumber
	 *            电话号码
	 */
	public Tel(int id, String telName, String telNumber) {
		super();
		this.id = id;
		this.telName = telName;
		this.telNumber = telNumber;
	}

	/**
	 * 构造函数(+2)
	 * 
	 * @param telId
	 *            电话ID号（主键）
	 * @param id
	 *            联系人ID号
	 * @param telName
	 *            电话类型名称
	 * @param telNumber
	 *            电话号码
	 */
	public Tel(int telId, int id, String telName, String telNumber) {
		super();
		this.telId = telId;
		this.id = id;
		this.telName = telName;
		this.telNumber = telNumber;
	}

	public int getTelId() {
		return telId;
	}

	public void setTelId(int telId) {
		this.telId = telId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTelName() {
		return telName;
	}

	public void setTelName(String telName) {
		this.telName = telName;
	}

	public String getTelNumber() {
		return telNumber;
	}

	public void setTelNumber(String telNumber) {
		this.telNumber = telNumber;
	}

	@Override
	public String toString() {
		return "Tel [telId=" + telId + ", id=" + id + ", telName=" + telName
				+ ", telNumber=" + telNumber + "]";
	}

}
