package mobileMedical.Contacts.Model;

import java.util.Arrays;

public class Contact implements Comparable<Contact> {
	private int id; // 联系人ID（主键）
	private String name;// 联系人姓名
	private String namePinyin;// 联系人首字母
	private String nickName;// 联系人昵称
	private String address;// 联系人地址
	private String company;// 联系人公司
	private String birthday;// 联系人生日
	private String note;// 联系人备注信息
	private byte[] image;// 联系人头像
	private int groupId;// 联系人分组号
	private String idNo; // id card no
	private String describe; // describe
	private String hospital; // hospital
	private String gender; // gender
	private String fileNo; // file number
	private String type; // yibao type
	private String relationName;// name of the relation
	private String relation;// relation
	private String relationTele;// telephone number
	private String relationEmail; // email address
	private int doctorID;// doctor id

	/**
	 * 联系人类构造函数
	 */
	public Contact() {

	}

	/**
	 * 联系人类构造函数(+1)
	 * 
	 * @param name
	 *            联系人姓名
	 * @param nickName
	 *            联系人昵称
	 * @param address
	 *            联系人地址
	 * @param company
	 *            联系人公司
	 * @param birthday
	 *            联系人生日
	 * @param note
	 *            联系人备注
	 * @param image
	 *            联系人头像
	 * @param groupId
	 *            联系人分组号
	 * @param idNo
	 *            身份证号
	 * @param relationName
	 *            联系人信息
	 * @param relation
	 *            联系人关系
	 * @param relationTele
	 *            联系人电话
	 * @param relationEmail
	 *            联系人电子邮件
	 * @param doctorID
	 *            医生编号
	 */
	public Contact(String name, String namePinyin, String nickName,
			String address, String company, String birthday, String note,
			byte[] image, int groupId, String idNo, String describe,
			String hospital, String type, String fileNo, String gender,
			String relationName, String relation, String relationTele,
			String relationEmail, int doctorID) {
		super();
		this.name = name;
		this.namePinyin = namePinyin;
		this.nickName = nickName;
		this.address = address;
		this.company = company;
		this.birthday = birthday;
		this.note = note;
		this.image = image;
		this.groupId = groupId;
		this.idNo = idNo;
		this.describe = describe;
		this.gender = gender;
		this.hospital = hospital;
		this.type = type;
		this.fileNo = fileNo;
		this.relationName = relationName;
		this.relation = relation;
		this.relationTele = relationTele;
		this.relationEmail = relationEmail;
		this.doctorID = doctorID;
	}

	/**
	 * 联系人类构造函数(+2)
	 * 
	 * @param id
	 *            联系人ID号
	 * @param name
	 *            联系人姓名
	 * @param nickName
	 *            联系人昵称
	 * @param address
	 *            联系人地址
	 * @param company
	 *            联系人公司
	 * @param birthday
	 *            联系人生日
	 * @param note
	 *            联系人备注
	 * @param image
	 *            联系人头像
	 * @param groupId
	 *            联系人分组号
	 * @param idNo
	 *            身份证号
	 * @param describe
	 *            describe
	 * @param hospital
	 *            which hospital
	 * @param type
	 *            yibao type
	 * @param gender
	 *            gender
	 * @param fileno
	 *            file number
	 * @param relationName
	 *            联系人信息
	 * @param relation
	 *            联系人关系
	 * @param relationTele
	 *            联系人电话
	 * @param relationEmail
	 *            联系人电子邮件
	 * @param doctorID
	 *            医生编号
	 */
	public Contact(int id, String name, String namePinyin, String nickName,
			String address, String company, String birthday, String note,
			byte[] image, int groupId, String idNo, String describe,
			String hospital, String type, String fileNo, String gender,
			String relationName, String relation, String relationTele,
			String relationEmail,int doctorID) {
		super();
		this.id = id;
		this.name = name;
		this.namePinyin = namePinyin;
		this.nickName = nickName;
		this.address = address;
		this.company = company;
		this.birthday = birthday;
		this.note = note;
		this.image = image;
		this.groupId = groupId;
		this.idNo = idNo;
		this.describe = describe;
		this.gender = gender;
		this.hospital = hospital;
		this.type = type;
		this.fileNo = fileNo;
		this.relationName = relationName;
		this.relation = relation;
		this.relationTele = relationTele;
		this.relationEmail = relationEmail;
		this.doctorID=doctorID;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNamePinyin() {
		return namePinyin;
	}

	public void setNamePinyin(String namePinyin) {
		this.namePinyin = namePinyin;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public String getRelationEmail() {
		return relationEmail;
	}

	public void setRelationEmail(String relationEmail) {
		this.relationEmail = relationEmail;
	}

	public String getRelationName() {
		return relationName;
	}

	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}

	public String getRelationTele() {
		return relationTele;
	}

	public void setRelationTele(String relationTele) {
		this.relationTele = relationTele;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getHospital() {
		return hospital;
	}

	public void setHospital(String hospital) {
		this.hospital = hospital;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFileNo() {
		return fileNo;
	}

	public void setFileNo(String fileNo) {
		this.fileNo = fileNo;
	}
	
	public int getDoctorID() {
		return doctorID;
	}

	public void setDoctorID(int doctorID) {
		this.doctorID = doctorID;
	}

	@Override
	public String toString() {
		return "Contact [id=" + id + ", name=" + name + ", namePinyin="
				+ namePinyin + ", nickName=" + nickName + ", address="
				+ address + ", company=" + company + ", birthday=" + birthday
				+ ", note=" + note + ", image=" + Arrays.toString(image)
				+ ", groupId=" + groupId + "]";
	}

	@Override
	public int compareTo(Contact another) {
		return this.namePinyin.compareToIgnoreCase(another.getNamePinyin());
	}

}
