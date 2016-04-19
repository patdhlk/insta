package insta.ejb.beans;

import insta.ejb.interfaces.InstaDAO;
import insta.helperobjects.TimelineElement;
import insta.persistence.entities.Photo;
import insta.persistence.entities.User;
import insta.persistence.enums.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJBException;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Stateless
@Remote(InstaDAO.class)
public class EntityManagerBean implements InstaDAO{
	
	private static final Logger log = Logger.getLogger( EntityManagerBean.class.getName() );
	
	@PersistenceContext
	private EntityManager em;

	@Override
	public User create(User user) {
		log.log(Level.INFO,"UserBean.create");
		em.persist(user);
		return user;
	}

	@Override
	public User update(User user) {
		return em.merge(user);
	}

	@Override
	public void remove(int id) {
		log.log(Level.INFO,"EntityManagerBean.remove - id:" + id);
		List<User> users = em.createQuery("SELECT c FROM User c", User.class).getResultList();
		
		log.log(Level.INFO,"EntityManagerBean.remove - user size: " + users.size());
		for (User user : users) {
			log.log(Level.INFO,"EntityManagerBean.remove - user id: " + user.getId());
			user.getFollower().removeIf(x -> x.getId() == id);
			user.getLikesPhotos().removeIf(y -> this.getUser(id).getPhotos().contains(y));
			em.merge(user);
		}
		log.log(Level.INFO,"EntityManagerBean.remove - remove id: " + id);
		User toDelete = getUser(id);
		toDelete.getLikesPhotos().clear();
		
		em.remove(toDelete);
	}

	@Override
	public User getUser(int id) {
		return em.find(User.class, id);
	}
	
	@Override
	public User getUserByEMail(String eMail) {
		List<User> users = (List<User>)em.createNamedQuery(User.QUERY_GETBYEMAIL, User.class)
                .setParameter("user_email", eMail).getResultList();
		if(users==null) return null;
		else {
			if(users.size()==0) return null;
			else return users.get(0);
		}
	}
	
	@Override
	public List<User> getUsersByRole(Role role) {
		List<User> users = (List<User>)em.createNamedQuery(User.QUERY_GETBYROLE, User.class)
                .setParameter("user_role", role.name()).getResultList();
		if(users==null) return null;
		else {
			if(users.size()==0) return null;
			else return users;
		}
	}
	
	@Override
	public List<User> getAllUsers() {
		return em.createNamedQuery(User.QUERY_GETALL, User.class).getResultList();
	}
	
	public List<User> searchUsers(String searchText) {
		log.log(Level.INFO,"EntityManagerBean.searchUsers: " + searchText);
		String[] splitted = searchText.split(" ");
		
		List<User> users = null;
		if (splitted.length == 1) {
			users = em.createQuery("SELECT c FROM User c WHERE (c.firstName LIKE :user_firstName1) OR (c.lastName LIKE :user_lastName1) ", User.class)
	                .setParameter("user_firstName1", "%" + splitted[0] + "%")
	                .setParameter("user_lastName1", "%" + splitted[0] + "%")
	                .getResultList();	
		}
		else {
			users = em.createQuery("SELECT c FROM User c WHERE (c.firstName LIKE :user_firstName1) OR (c.firstName LIKE :user_firstName2) OR (c.lastName LIKE :user_lastName1) OR (c.lastName LIKE :user_lastName2)", User.class)
	                .setParameter("user_firstName1", "%" + splitted[0] + "%")
	                .setParameter("user_firstName2", "%" + splitted[1] + "%")
	                .setParameter("user_lastName1", "%" + splitted[0] + "%")
	                .setParameter("user_lastName2", "%" + splitted[1] + "%")
	                .getResultList();	
		}
		
		return users;
	}

	public void uploadFile(User user, Photo photo) {
		log.log(Level.INFO,"EntityManagerBean.uploadFile");

		User _user = null;
		try {
			_user = this.getUser(user.getId());
		}
		catch (Exception e) {

		}
		if (_user != null) {
			_user.getPhotos().add(photo);
			this.update(_user);
		}
	}
	
	public void changePassword(User user, String oldPassword, String newPassword) {
		log.log(Level.INFO,"EntityManagerBean.changePassword");
		User _user = null;
		try {
			_user = this.getUser(user.getId());
		}
		catch (Exception e) {
			//_user = this.getAllUsers().get(0);
		}
		if (_user != null) {
			if (_user.getPassword().equals(oldPassword)) {
				_user.setPassword(newPassword);
				em.merge(_user);
			}
			else
				throw new EJBException("Old password wrong");
		}
		else
			throw new EJBException("User not found");
	}
	
	public void editPhoto(User user, Photo photo) {
		log.log(Level.INFO,"EntityManagerBean.editPhoto");
		User _user = null;
		try {
			_user = this.getUser(user.getId());
		}
		catch (Exception e) {
			//_user = this.getAllUsers().get(0);
		}
		if (_user != null) {
			Photo _photo = (Photo)em.createNamedQuery(Photo.QUERY_GETBYID, Photo.class)
		            .setParameter("photo_id", photo.getId()).getSingleResult();
				
			if(_photo!=null){
				_photo.setDescription(photo.getDescription());
				em.merge(_photo);
			}
		}		
	}
	
	public void removeFile(User user, Photo photo) {
		log.log(Level.INFO,"EntityManagerBean.removeFile");
		User _user = null;
		try {
			_user = this.getUser(user.getId());
		}
		catch (Exception e) {
			//_user = this.getAllUsers().get(0);
		}
		if (_user != null) {
			int id = _user.getId();
			
			//Processes all insta users, works but is ineffective
			List<User> users = em.createQuery("SELECT c FROM User c", User.class).getResultList(); 
			
			//This would be better but the users list is empty, dont know why
			//List<User> users = (List<User>)em.createQuery("SELECT c FROM User c JOIN c.likesPhotos d WHERE d.userId = :uploaderId")
			//		.setParameter("uploaderId", id).getResultList();
			
			log.log(Level.INFO,"EntityManagerBean.removeFile - user size: " + users.size());
			
			for (User singleUser : users) {
				log.log(Level.INFO,"EntityManagerBean.removeFile - user id: " + singleUser.getId());
				singleUser.getLikesPhotos().removeIf(y -> this.getUser(id).getPhotos().contains(y));
				em.merge(singleUser);
			}
			
			_user.getPhotos().removeIf(x -> x.getId() == photo.getId());
			if(_user.getProfileImageId()==photo.getId()) _user.setProfileImageId(0);
			this.update(_user);
		}		
	}
	
	public byte[] getFile(int photoId) {
		log.log(Level.INFO,"EntityManagerBean.getFile");
		Photo photo;
		try{
		photo = (Photo)em.createNamedQuery(Photo.QUERY_GETBYID, Photo.class)
	            .setParameter("photo_id", photoId).getSingleResult();
		}
		catch (NoResultException e){
			return null;
		}
		return photo.getImageFile();
	}
	
	public void follow(User user, int userId) {
		log.log(Level.INFO,"EntityManagerBean.follow");
		User _user = null;
		try {
			_user = this.getUser(user.getId());
		}
		catch (Exception e) {
			//_user = this.getAllUsers().get(0);
		}
		if (_user != null) {
			User userFollow = this.getUser(userId);
			if (userFollow != null) {
				if(! _user.getFollower().contains(userFollow)){
					_user.getFollower().add(userFollow);
					this.update(_user);
				}
			}
		}
	}
	
	public void unfollow(User user, int userId) {
		log.log(Level.INFO,"EntityManagerBean.unfollow");
		User _user = null;
		try {
			_user = this.getUser(user.getId());
		}
		catch (Exception e) {
			//_user = this.getAllUsers().get(0);
		}
		if (_user != null) {	
			User userFollow = this.getUser(userId);
			if (userFollow != null) {
				if(_user.getFollower().contains(userFollow)){
					_user.getFollower().remove(userFollow);
					this.update(_user);
				}
			}
		}
	}
	
	public void like(User user, Photo photo) {
		log.log(Level.INFO,"EntityManagerBean.like");
		
		User _user = null;
		_user = this.getUser(user.getId());

		if (_user != null) {	
			photo = (Photo)em.createNamedQuery(Photo.QUERY_GETBYID, Photo.class)
            .setParameter("photo_id", photo.getId()).getSingleResult();
		
			if(photo!=null){
				if(!_user.getLikesPhotos().contains(photo)){
					_user.getLikesPhotos().add(photo);
					this.update(_user);
				}
			}
		}
	}
	
	public void unlike(User user, Photo photo) {
		log.log(Level.INFO,"EntityManagerBean.unlike");
		
		User _user = null;
		_user = this.getUser(user.getId());

		if (_user != null) {	
			photo = (Photo)em.createNamedQuery(Photo.QUERY_GETBYID, Photo.class)
            .setParameter("photo_id", photo.getId()).getSingleResult();
		
			if(photo!=null){
				if(_user.getLikesPhotos().contains(photo)){
					_user.getLikesPhotos().remove(photo);
					this.update(_user);
				}
			}
		}
	}

	/**
	 * 
	 * @param userId User of timeline
	 * @param skip Number of elemenets to skip
	 * @param count Number of elements to get
	 * @return List of TimelineElement
	 */
	public List<TimelineElement> getTimelineElements(int userId, int skip, int count){
		log.log(Level.INFO,"EntityManagerBean.getTimelineElements");

		List<TimelineElement> elements = new ArrayList<TimelineElement>();
		@SuppressWarnings("unchecked")
		List<Photo> timelinePhotos = 
				em.createNativeQuery("select Photo.* from (select * from User, User_User where User.id = User_User.follower_Id and User_User.user_Id = "+userId+") as followeruser, Photo, User_Photo where User_Photo.photos_Id = Photo.id and User_Photo.User_id=followeruser.id order by Photo.lastChanged desc limit "+skip+","+count+";", Photo.class)
				.getResultList();

		User timelinePhotoPoster = new User();
		for (Photo p : timelinePhotos){
			timelinePhotoPoster = em.createQuery("SELECT u FROM User u JOIN u.photos p WHERE p in (:pho)", User.class)
	                .setParameter("pho", p).getSingleResult();
			
			List<User> liker = em.createQuery("SELECT u FROM User u JOIN u.likesPhotos p WHERE p in (:pho)", User.class)
	                .setParameter("pho", p).getResultList();
			
			elements.add(new TimelineElement(timelinePhotoPoster,p, liker));
		}
		return elements;
	}
}
