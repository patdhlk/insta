package insta.web.adminbeans;

import insta.ejb.interfaces.InstaDAO;
import insta.persistence.entities.User;

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

@ViewScoped
@Named("adminbean")
public class AdminUserBean implements Serializable {
 
	private static final long serialVersionUID = 1L;
	
	private static final Logger log = Logger.getLogger( AdminUserBean.class.getName() );

	@EJB
	private InstaDAO instaDAO;
	
	private String firstName;
	private String lastName;
	private String eMail;
	private String gender;	
	private String oldPassword;
	private String newPassword;
	private String confirmNewPassword;
	private User user;
 
	public AdminUserBean() {
	}
	
	public String getUserPrincipalName() {
        FacesContext context = FacesContext.getCurrentInstance();
        Principal principal = context.getExternalContext().getUserPrincipal();
        if(principal == null) {
        	log.log(Level.INFO,"AdminBean.getUserPrincipalName is null");
            return null;
        }
        log.log(Level.INFO,"AdminBean.getUserPrincipalName " + principal.getName());
        return principal.getName();
    }
	
	public void initUser() {
		log.log(Level.INFO,"AdminBean.initUser");
		if(user == null) {
			String userPrincipalName=getUserPrincipalName();
			if(userPrincipalName!=null){
				user = instaDAO.getUserByEMail(userPrincipalName);
				user = instaDAO.getUser(user.getId());
			} else{
				log.log(Level.INFO,"userPrincipalName was null. User not refreshed.");
			}
			
			this.eMail = user.getEMail();
			this.firstName = user.getFirstName();
			this.lastName = user.getLastName();
			try {
				this.gender = user.getGender().toString();
			}
			catch (Exception e) {
				this.gender = "Unknown";
			}
		}
	}
 
	public List<User> getAllUsers(){
		List<User> allUser = new ArrayList<User>();
		allUser = instaDAO.getAllUsers();
		return allUser.stream().filter(a -> a.getId()!=user.getId()).collect(Collectors.toList());//Remove itself from list. Admin cant delete himself this way.
	}
	
	public String remove(int userId) {
		log.log(Level.INFO,"AdminBean.remove");
    	instaDAO.remove(userId);
    	
    	return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath()+"?faces-redirect=true";
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

	public String geteMail() {
		return eMail;
	}

	public void seteMail(String eMail) {
		this.eMail = eMail;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getConfirmNewPassword() {
		return confirmNewPassword;
	}

	public void setConfirmNewPassword(String confirmNewPassword) {
		this.confirmNewPassword = confirmNewPassword;
	}
	
	public String changePassword() {
		log.log(Level.INFO,"AdminBean.changePassword");
		
		if (!newPassword.equals(confirmNewPassword)) {
			//
		}
		else {
			
		}
		return "";
	}
}
