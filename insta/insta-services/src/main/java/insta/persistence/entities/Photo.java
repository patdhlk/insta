package insta.persistence.entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;

@Entity
@NamedQueries({
	@NamedQuery(
			name = Photo.QUERY_GETBYID,
			query= "SELECT c FROM Photo c WHERE c.id=:photo_id"
			)
})
public class Photo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final String QUERY_GETBYID = "Photo.getById";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	private String description;

	@Lob
	private byte[] imageFile;
	
	@Version
	private Timestamp lastChanged;
	
//	@OneToMany(cascade = CascadeType.ALL, orphanRemoval=true)
//	private List<Comment> comments;
	
	/*
	 * GETTERS AND SETTERS
	 */
		
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public byte[] getImageFile() {
		return imageFile;
	}

	public void setImageFile(byte[] imageFile) {
		this.imageFile = imageFile;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Timestamp getLastChanged() {
		return lastChanged;
	}

	public void setLastChanged(Timestamp lastChanged) {
		this.lastChanged = lastChanged;
	}
}
