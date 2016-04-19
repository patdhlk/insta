package insta.ejb.interfaces;

import insta.helperobjects.TimelineElement;
import insta.persistence.entities.Photo;
import insta.persistence.entities.User;
import insta.persistence.enums.Role;

import java.util.List;


public interface InstaDAO {

	public User create(User user);
	
	public User update(User user);
	
	public void remove(int id);
	
	public User getUser(int id);
	
	public List<User> getAllUsers();
	
	public List<User> searchUsers(String searchText);

	public User getUserByEMail(String eMail);
	
	public List<User> getUsersByRole(Role role);
	
	public void uploadFile(User user, Photo photo);
	
	public void removeFile(User user, Photo photo);
	
	public byte[] getFile(int photoId);
	
	public void editPhoto(User user, Photo photo);
	
	public void follow(User user, int userId);
		
	public void unfollow(User user, int userId);
	
	public void like(User user, Photo photo);
	
	public void unlike(User user, Photo photo);
	
	public void changePassword(User user, String oldPassword, String newPassword);
	
	public List<TimelineElement> getTimelineElements(int userId, int skip, int count);
}
