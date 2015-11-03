package mobileMedical.Contacts.Interface;

import java.util.List;

import mobileMedical.Contacts.Model.Tel;


public interface ITel {

	public void insert(Tel tel);

	public void delete(int telId);

	public void update(Tel tel);

	public List<Tel> getTelsByCondition(String condition);
}
