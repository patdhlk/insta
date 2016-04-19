package insta.helperobjects;

import java.io.Serializable;
import java.util.List;

import insta.persistence.entities.Photo;
import insta.persistence.entities.User;

public class TimelineElement implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private User user;
	private Photo photo;
	private List<User> liker;
	
	public TimelineElement(User user, Photo photo, List<User> liker){
		this.user = user;
		this.photo = photo;
		this.liker = liker;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Photo getPhoto() {
		return photo;
	}

	public void setPhoto(Photo photo) {
		this.photo = photo;
	}

	public List<User> getLiker() {
		return liker;
	}

	public String getLikerAsString() {
		String likerString = "";
		
		for(User u : liker){
			likerString=likerString+u.getEMail();
		}
		
		return likerString;
	}
	
	public void setLiker(List<User> liker) {
		this.liker = liker;
	}
}
