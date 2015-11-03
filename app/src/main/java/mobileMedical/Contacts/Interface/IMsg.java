package mobileMedical.Contacts.Interface;

import java.util.List;

import mobileMedical.Contacts.Model.Msg;


public interface IMsg {
	public void insert(Msg msg);

	public void deleteAll();

	public List<Msg> getMsgsByCondition(String condition);
}
