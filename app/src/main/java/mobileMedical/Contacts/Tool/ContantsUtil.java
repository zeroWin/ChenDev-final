package mobileMedical.Contacts.Tool;
/*_id：短信序号，如100
thread_id：对话的序号，如100，与同一个手机号互发的短信，其序号是相同的
address：发件人地址，即手机号，如+8613811810000
person：发件人，如果发件人在通讯录中则为具体姓名，陌生人为null
date：日期，long型，如1256539465022，可以对日期显示格式进行设置
protocol：协议0SMS_RPOTO短信，1MMS_PROTO彩信
read：是否阅读0未读，1已读
status：短信状态-1接收，0complete,64pending,128failed
type：短信类型1是接收到的，2是已发出
body：短信具体内容
service_center：短信服务中心号码编号，如+8613800755500*/
public class ContantsUtil {
	
	public static final String ACTION_SMS_SEND = "sms.send";
	public static final String ACTION_SMS_DELIVERY = "sms.delivery";
	public static final String SMS_URI_ALL   = "content://sms/";     
	public static final String SMS_URI_INBOX = "content://sms/inbox";   
	public static final String SMS_URI_SEND  = "content://sms/sent";   
	public static final String SMS_URI_DRAFT = "content://sms/draft";  
	public static final String SMS_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	public static final String SMS_ID="_id";
	public static final String SMS_THREADID="thread_id";
	public static final String SMS_ADDRESS="address";
	public static final String SMS_PERSON="person";
	public static final String SMS_DATE="date";
	public static final String SMS_PROTOCOL="protocol";
	public static final String SMS_READ="read";
	public static final String SMS_STATUS="status";
	public static final String SMS_TYPE="type";
	public static final String SMS_BODY="body";
	public static final String SMS_SERVICE_CENTER="service_center";
	
	
}
