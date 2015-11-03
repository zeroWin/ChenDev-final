package mobileMedical.Contacts.Interface;

import java.util.List;

import mobileMedical.Contacts.Model.IM;


public interface IIM {
	public void insert(IM im);

	public void delete(int imId);

	public void update(IM im);

	public List<IM> getIMsByCondition(String condition);
}
