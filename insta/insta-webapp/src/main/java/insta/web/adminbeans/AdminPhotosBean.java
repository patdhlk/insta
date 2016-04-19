package insta.web.adminbeans;

import insta.ejb.interfaces.InstaDAO;
import insta.persistence.entities.Photo;
import insta.persistence.entities.User;

import java.io.Serializable;
import java.security.Principal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@ViewScoped
@Named("adminphotos")
public class AdminPhotosBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private static final Logger log = Logger.getLogger( AdminPhotosBean.class.getName() );

	@EJB
	private InstaDAO instaDAO;
	private User user;
	private User userToShow;
	private String userEmailToShow = "";
	
	public String getUserPrincipalName() {
        FacesContext context = FacesContext.getCurrentInstance();
        Principal principal = context.getExternalContext().getUserPrincipal();
        if(principal == null) {
        	log.log(Level.INFO,"AdminPhotosBean.getUserPrincipalName is null");
            return null;
        }
        log.log(Level.INFO,"AdminPhotosBean.getUserPrincipalName " + principal.getName());
        return principal.getName();
    }
	
	public void initUser() {
		log.log(Level.INFO,"AdminPhotosBean.initUser");
		if(user == null) {
			String userPrincipalName=getUserPrincipalName();
			if(userPrincipalName!=null){
				user = instaDAO.getUserByEMail(userPrincipalName);
				user = instaDAO.getUser(user.getId());
			} else{
				log.log(Level.INFO,"userPrincipalName was null. User not refreshed.");
			}
		}
		
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest)context.getExternalContext().getRequest();
		HttpSession httpSession = request.getSession(false);
		userEmailToShow = (String) httpSession.getAttribute("userEmailToShow");
		
		if(userEmailToShow!=""){
			userToShow = instaDAO.getUserByEMail(userEmailToShow);
			userToShow = instaDAO.getUser(userToShow.getId());
		}
	}

	public String showUser(String email){
		
		userEmailToShow=email;
		
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest)context.getExternalContext().getRequest();
		HttpSession httpSession = request.getSession(false);
		httpSession.setAttribute("userEmailToShow", userEmailToShow);
		
		if(userEmailToShow!=""){
			userToShow = instaDAO.getUserByEMail(userEmailToShow);
			userToShow = instaDAO.getUser(userToShow.getId());
		}
		return "/admin/photos.xhtml?faces-redirect=true";
	}
	
	public List<Photo> getAllPhotos(){
		log.log(Level.INFO,"AdminPhotosBean.getAllPhotos");
		return userToShow.getPhotos();
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
	
	public String remove(int id) {
		log.log(Level.INFO,"AdminPhotosBean.remove");
		if (userToShow != null) {
			Photo photo = new Photo();
			photo.setId(id);
			
			instaDAO.removeFile(userToShow, photo);
			return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath()+"?faces-redirect=true";
		}
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Picture removed successful"));
		return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath()+"?faces-redirect=true";
	}
	
	public String getUserEmailToShow() {
		return userEmailToShow;
	}

	public void setUserEmailToShow(String userEmailToShow) {
		this.userEmailToShow = userEmailToShow;
	}
}
