package mobileMedical.Contacts.Model;

public class IM {
	private int imId;// 即时通讯ID号（主键）
	private int id;// 联系人ID号
	private String imName;// 即时通讯类型名称
	private String imAcount;// 即时通讯账号

	/**
	 * 
	 */
	public IM() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param id
	 * @param imName
	 * @param imAcount
	 */
	public IM(int id, String imName, String imAcount) {
		super();
		this.id = id;
		this.imName = imName;
		this.imAcount = imAcount;
	}

	/**
	 * @param imId
	 * @param id
	 * @param imName
	 * @param imAcount
	 */
	public IM(int imId, int id, String imName, String imAcount) {
		super();
		this.imId = imId;
		this.id = id;
		this.imName = imName;
		this.imAcount = imAcount;
	}

	public int getImId() {
		return imId;
	}

	public void setImId(int imId) {
		this.imId = imId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getImName() {
		return imName;
	}

	public void setImName(String imName) {
		this.imName = imName;
	}

	public String getImAcount() {
		return imAcount;
	}

	public void setImAcount(String imAcount) {
		this.imAcount = imAcount;
	}

	@Override
	public String toString() {
		return "IM [imId=" + imId + ", id=" + id + ", imName=" + imName
				+ ", imAcount=" + imAcount + "]";
	}

}
