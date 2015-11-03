package mobileMedical.Contacts.Model;

public class Msg {
	private int id;// 存储其_id号
	private int threadId;// 对话ID号
	private int msgCount;// 短信数量
	private int unreadCount;// 未读短信数量
	private String address;// 信息号码
	private String person;// 联系人姓名
	private int msgMark;// 信息的标记(判断是发送的还是接收的)1:接收;2:发送
	private int read;// 标记信息是否已阅读,0未读，1已读;
	private String msgDate;// 信息的日期
	private String content;// 信息的内容

	/**
	 * 无参构造函数
	 */
	public Msg() {
	}

	/**
	 * 有参构造函数(+1)
	 * 
	 * @param contactId
	 *            联系人ID号
	 * @param msgMark
	 *            信息的标记(判断是发送的还是接收的)
	 * @param msgDate
	 *            信息的日期
	 * @param content
	 *            信息的内容
	 */
	public Msg(int id, int threadId, int msgCount, int unReadCount,
			String address, String person, int msgMark, int read,
			String msgDate, String content) {
		this.id = id;
		this.threadId = threadId;
		this.msgCount = msgCount;
		this.unreadCount = unReadCount;
		this.address = address;
		this.person = person;
		this.msgMark = msgMark;
		this.read = read;
		this.msgDate = msgDate;
		this.content = content;
	}

	/**
	 * 有参构造函数(+2)
	 * 
	 * @param threadId
	 *            对话ID号
	 * @param address
	 *            信息号码
	 * @param person
	 *            信息联系人
	 * @param msgMark
	 *            信息的标记(判断是发送的还是接收的)
	 * @param msgDate
	 *            信息的日期
	 * @param content
	 *            信息的内容
	 */
	public Msg(int threadId, int msgCount, int unReadCount, String address,
			String person, int msgMark, int read, String msgDate, String content) {
		super();
		this.threadId = threadId;
		this.msgCount = msgCount;
		this.unreadCount = unReadCount;
		this.address = address;
		this.person = person;
		this.msgMark = msgMark;
		this.read = read;
		this.msgDate = msgDate;
		this.content = content;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getThreadId() {
		return threadId;
	}

	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}

	public int getMsgCount() {
		return msgCount;
	}

	public void setMsgCount(int msgCount) {
		this.msgCount = msgCount;
	}

	public int getUnreadCount() {
		return unreadCount;
	}

	public void setUnreadCount(int unreadCount) {
		this.unreadCount = unreadCount;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public int getMsgMark() {
		return msgMark;
	}

	public void setMsgMark(int msgMark) {
		this.msgMark = msgMark;
	}

	public int getRead() {
		return read;
	}

	public void setRead(int read) {
		this.read = read;
	}

	public String getMsgDate() {
		return msgDate;
	}

	public void setMsgDate(String msgDate) {
		this.msgDate = msgDate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "Msg [id=" + id + ", threadId=" + threadId + ", msgCount="
				+ msgCount + ", unreadCount=" + unreadCount + ", address="
				+ address + ", person=" + person + ", msgMark=" + msgMark
				+ ", read=" + read + ", msgDate=" + msgDate + ", content="
				+ content + "]";
	}

}
