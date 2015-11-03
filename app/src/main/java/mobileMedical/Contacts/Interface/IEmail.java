package mobileMedical.Contacts.Interface;

import java.util.List;

import mobileMedical.Contacts.Model.Email;


public interface IEmail {
	public void insert(Email email);

	public void delete(int emailId);

	public void update(Email email);

	public List<Email> getEmailsByCondition(String condition);
}
