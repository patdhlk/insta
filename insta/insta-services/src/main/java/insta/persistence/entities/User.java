package insta.persistence.entities;

import insta.persistence.enums.Gender;
import insta.persistence.enums.Role;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@NamedQueries({
	@NamedQuery(
			name = User.QUERY_GETALL,
			query= "SELECT u FROM User u"
			),
	@NamedQuery(
			name = User.QUERY_GETBYEMAIL,
			query= "SELECT c FROM User c WHERE c.eMail=:user_email"
			),
	@NamedQuery(
			name = User.QUERY_GETBYROLE,
			query = "SELECT c FROM User c WHERE c.role=:user_role"
			)	
})
public class User implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public static final String QUERY_GETALL = "User.getAll";
	public static final String QUERY_GETBYEMAIL = "User.getByEMail";
	public static final String QUERY_GETBYROLE = "User.getByRole";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@NotNull
	@Size(min=1, max=50)
	private String firstName;
	
	@NotNull
	@Size(min=1, max=50)
	private String lastName;
	
	@NotNull
	@Size(min=5)
	private String eMail;
	
	@NotNull
	@Size(min=1, max=50)
	private String role;
	
	@NotNull
	@Size(min=1, max=50)
	private String password;
	
	private String displayName;
	
	private int profileImageId;
	
	private Gender gender;

	@Version
	private Timestamp lastChanged;
	
	@OneToMany(cascade={CascadeType.ALL}, orphanRemoval=true)
	@JoinTable(name="User_Photo")
	private List<Photo> photos = new ArrayList<Photo>();
	
	@ManyToMany(cascade={CascadeType.ALL})
	@JoinTable(name="User_Photo_Like")
	private List<Photo> likesPhotos = new ArrayList<Photo>();
	
	@ManyToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(name="User_User")
	private List<User> follower = new ArrayList<User>();
	
	/* *
	 * GETTERS and SETTERS
	 * */

	public String getDisplayName() {
		if(displayName == "")
			setDisplayName(getFirstName() + " " + getLastName());
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setPassword(String password){
		this.password = password;
	}
	
	public String getPassword(){
		return this.password;
	}
	
	public void setRole(Role role){
		this.role = role.name();
	}
	
	public Role getRole(){
		return Role.valueOf(this.role);
	}
	
	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public List<Photo> getPhotos() {
		return photos;
	}

	public void setPhotos(List<Photo> photos) {
		this.photos = photos;
	}

	public String getEMail() {
		return eMail;
	}

	public void setEMail(String eMail) {
		this.eMail = eMail;
	}

	public List<User> getFollower() {
		return follower;
	}

	public void setFollower(List<User> follower) {
		this.follower = follower;
	}

	public List<Photo> getLikesPhotos() {
		return likesPhotos;
	}

	public void setLikesPhotos(List<Photo> likesPhotos) {
		this.likesPhotos = likesPhotos;
	}

	public int getProfileImageId() {
		return profileImageId;
	}

	public void setProfileImageId(int profileImageId) {
		this.profileImageId = profileImageId;
	}
	
	
}
