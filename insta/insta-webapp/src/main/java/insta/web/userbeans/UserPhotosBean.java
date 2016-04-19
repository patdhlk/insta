package insta.web.userbeans;

import insta.ejb.interfaces.InstaDAO;
import insta.persistence.entities.Photo;
import insta.persistence.entities.User;
import insta.web.helper.ExceptionUtil;

import java.io.Serializable;
import java.security.Principal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

@ViewScoped
@Named("photos")
public class UserPhotosBean implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private static final Logger log = Logger.getLogger( UserPhotosBean.class.getName() );

	@EJB
	private InstaDAO instaDAO;
	private User user;
	
	public String getUserPrincipalName() {
        FacesContext context = FacesContext.getCurrentInstance();
        Principal principal = context.getExternalContext().getUserPrincipal();
        if(principal == null) {
        	log.log(Level.INFO,"UserPhotosBean.getUserPrincipalName is null");
            return null;
        }
        log.log(Level.INFO,"UserPhotosBean.getUserPrincipalName " + principal.getName());
        return principal.getName();
    }
	
	public void initUser() {
		log.log(Level.INFO,"UserPhotosBean.initUser");
		if(user == null) {
			String userPrincipalName=getUserPrincipalName();
			if(userPrincipalName!=null){
				user = instaDAO.getUserByEMail(userPrincipalName);
				user = instaDAO.getUser(user.getId());
			} else{
				log.log(Level.INFO,"userPrincipalName was null. User not refreshed.");
			}
		}
	}

	public List<Photo> getAllPhotos(){
		log.log(Level.INFO,"UserPhotosBean.getAllPhotos");
		return user.getPhotos();
	}
	
	public String getPath(int id) {
		return "/images/"+id;
	}
	
	private String photoDescription;
	
	public String getPhotoDescription() {
		return photoDescription;
	}

	public void setPhotoDescription(String photoDescription) {
		this.photoDescription = photoDescription;
	}
	
	public void setProfileImageId(int profileImageId){
		user.setProfileImageId(profileImageId);
		try {
			instaDAO.update(user);
		}
		catch (EJBTransactionRolledbackException e) {
			FacesContext.getCurrentInstance().addMessage(null, 
					new FacesMessage("Error during set profile photo: Internal error"));
			log.log(Level.INFO, String.format("UserPhotosBean.setProfileImageId unsuccessful: %s", ExceptionUtil.getRootCause(e).getMessage()));
		}	
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Picture set as profile picture"));
	}

	public String edit(int id) {
		log.log(Level.INFO,"UserPhotosBean.edit " + id + "; " + photoDescription);
		if (user != null) {
			Photo photo = new Photo();
			photo.setId(id);
			photo.setDescription(photoDescription);
			
			try {
				instaDAO.editPhoto(user, photo);
			}
			catch (EJBTransactionRolledbackException e) {
				FacesContext.getCurrentInstance().addMessage(null, 
						new FacesMessage("Error during edit photo: Internal error"));
				log.log(Level.INFO, String.format("UserPhotosBean.edit unsuccessful: %s", ExceptionUtil.getRootCause(e).getMessage()));
			}	
			// trigger reload
			user = null;
			
			return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath()+"?faces-redirect=true";
		}
		return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath()+"?faces-redirect=true";
	}
	
	public String remove(int id) {
		log.log(Level.INFO,"UserPhotosBean.remove");
		if (user != null) {
			Photo photo = new Photo();
			photo.setId(id);
			try {
				instaDAO.removeFile(user, photo);
				
				user.getPhotos().removeIf(x -> x.getId() == id);
			}
			catch (EJBTransactionRolledbackException e) {
				FacesContext.getCurrentInstance().addMessage(null, 
						new FacesMessage("Error during remove photo: Internal error"));
				log.log(Level.INFO, String.format("UserPhotosBean.remove unsuccessful: %s", ExceptionUtil.getRootCause(e).getMessage()));
			}					
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Picture removed successful"));
			return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath()+"?faces-redirect=true";
		}
		return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath()+"?faces-redirect=true";
	}
}
