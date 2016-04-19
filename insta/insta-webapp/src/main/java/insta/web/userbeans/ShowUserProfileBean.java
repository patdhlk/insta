package insta.web.userbeans;

import insta.ejb.interfaces.InstaDAO;
import insta.persistence.entities.User;
import insta.persistence.enums.Gender;

import java.io.Serializable;
import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@ViewScoped
@Named("showprofile")
public class ShowUserProfileBean implements Serializable {
 
	private static final long serialVersionUID = 1L;
	
	private static final Logger log = Logger.getLogger( ShowUserProfileBean.class.getName() );

	@EJB
	private InstaDAO instaDAO;
	
	private String firstName;
	private String lastName;
	private String eMail;
	private String gender;
	private User user;
	private User userToShow;
	private String userEmailToShow = "";
 
	public ShowUserProfileBean() {
	}
	
	public String getUserPrincipalName() {
        FacesContext context = FacesContext.getCurrentInstance();
        Principal principal = context.getExternalContext().getUserPrincipal();
        if(principal == null) {
        	log.log(Level.INFO,"ShowUserProfileBean.getUserPrincipalName is null");
            return null;
        }
        log.log(Level.INFO,"ShowUserProfileBean.getUserPrincipalName " + principal.getName());
        return principal.getName();
    }
	
	public void initUser() {
		log.log(Level.INFO,"ShowUserProfileBean.initUser");
		
		if(user == null) {
			String userPrincipalName=getUserPrincipalName();
			if(userPrincipalName!=null){
				user = instaDAO.getUserByEMail(getUserPrincipalName());
				user = instaDAO.getUser(user.getId());
			}else{
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
			
			this.eMail = userToShow.getEMail();
			this.firstName = userToShow.getFirstName();
			this.lastName = userToShow.getLastName();
			try {
				this.gender = userToShow.getGender().toString();
			}
			catch (Exception e) {
				this.gender = Gender.Unknown.toString();
			}
		} else{
			this.eMail = "";
			this.firstName = "";
			this.lastName = "";
			this.gender = Gender.Unknown.toString();
		}
	}

	public String showProfile(String email){
		log.log(Level.INFO,"ShowUserProfileBean.showProfile");
		userEmailToShow=email;
		
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest)context.getExternalContext().getRequest();
		HttpSession httpSession = request.getSession(false);
		httpSession.setAttribute("userEmailToShow", userEmailToShow);
		
		if(userEmailToShow!=""){
			userToShow = instaDAO.getUserByEMail(userEmailToShow);
			userToShow = instaDAO.getUser(userToShow.getId());
		} else{
			log.log(Level.INFO,"userEmailToShow was empty.");
		}
		return "/users/showprofile.xhtml?faces-redirect=true";
	}
	
	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String geteMail() {
		return eMail;
	}

	public String getGender() {
		return gender;
	}
	
	public int getProfileImageId(){
		return userToShow.getProfileImageId();
	}
}
