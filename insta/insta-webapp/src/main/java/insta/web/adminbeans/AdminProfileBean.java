package insta.web.adminbeans;

import insta.ejb.interfaces.InstaDAO;
import insta.persistence.entities.User;
import insta.persistence.enums.Gender;
import insta.web.helper.ExceptionUtil;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

@ViewScoped
@Named("adminprofile")
public class AdminProfileBean implements Serializable {
 
	private static final long serialVersionUID = 1L;
	
	private static final Logger log = Logger.getLogger( AdminProfileBean.class.getName() );

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
 
	public AdminProfileBean() {
	}
	
	public String getUserPrincipalName() {
        FacesContext context = FacesContext.getCurrentInstance();
        Principal principal = context.getExternalContext().getUserPrincipal();
        if(principal == null) {
        	log.log(Level.INFO,"AdminProfileBean.getUserPrincipalName is null");
            return null;
        }
        log.log(Level.INFO,"AdminProfileBean.getUserPrincipalName " + principal.getName());
        return principal.getName();
    }
	
	public void initUser() {
		log.log(Level.INFO,"AdminProfileBean.initUser");
		if(user == null) {
			user = instaDAO.getUserByEMail(getUserPrincipalName());
			user = instaDAO.getUser(user.getId());
			
			this.eMail = user.getEMail();
			this.firstName = user.getFirstName();
			this.lastName = user.getLastName();
			try {
				this.gender = user.getGender().toString();
			}
			catch (Exception e) {
				this.gender = Gender.Unknown.toString();
			}
		}else{
			this.eMail = "";
			this.firstName = "";
			this.lastName = "";
			this.gender = Gender.Unknown.toString();
		}
	}
 
	public String submit() {
		log.log(Level.INFO,"AdminProfileBean.submit");
		user.setId(this.user.getId());
		
		if(!firstName.isEmpty()){
			user.setFirstName(firstName);
		}
		if(!lastName.isEmpty()){
			user.setLastName(lastName);
		}

		user.setGender(Gender.valueOf(gender));
		
		try {
		instaDAO.update(user);
		}
		catch (EJBTransactionRolledbackException e) {
			FacesContext.getCurrentInstance().addMessage(null, 
					new FacesMessage("Submit new user data failed: Internal error"));
			log.log(Level.INFO, String.format("AdminProfileBean.submit unsuccessful: %s", ExceptionUtil.getRootCause(e).getMessage()));
		}
		
		return "/admin/profile?faces-redirect=true";
  }

	public String getFirstName() {
		return firstName;
	}

	public int getProfilePictureId() {
		return user.getProfileImageId();
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
	
	public String remove() {
		log.log(Level.INFO,"AdminProfileBean.remove");
    	if (user != null) {
    		instaDAO.remove(user.getId());
    		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    	}
    	user=null;
    	return "/index?faces-redirect=true";
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
	
	public void changePassword() {
		log.log(Level.INFO,"AdminProfileBean.changePassword");
		
		if (!newPassword.equals(confirmNewPassword)) {
			FacesContext.getCurrentInstance().addMessage("", new FacesMessage("Passwords do not match"));
		}
		else {
			MessageDigest md;
			try {
				md = MessageDigest.getInstance("SHA-256");
				
				byte[] digest = md.digest(newPassword.getBytes());
				String newPassword_ = Base64.getEncoder().encodeToString(digest);
				
				digest = md.digest(oldPassword.getBytes());
				String oldPassword_ = Base64.getEncoder().encodeToString(digest);
				
				try {
					instaDAO.changePassword(user, oldPassword_, newPassword_);
				}
	    		catch (RuntimeException e) {
	    			FacesContext.getCurrentInstance().addMessage(null, 
	    					new FacesMessage("Error during password change: Internal error"));
	    			log.log(Level.INFO, String.format("AdminProfileBean.changePassword unsuccessful: %s", ExceptionUtil.getRootCause(e).getMessage()));
				}
				
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
	}
}
