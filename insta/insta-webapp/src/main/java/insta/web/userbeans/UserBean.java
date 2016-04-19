package insta.web.userbeans;

import insta.ejb.interfaces.InstaDAO;
import insta.persistence.entities.User;

import java.io.Serializable;
import java.security.Principal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

@ViewScoped
@Named("users")
public class UserBean implements Serializable{

	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger( UserBean.class.getName() );
	
	@EJB
	private InstaDAO instaDAO;
	private User user;

	public String getUserPrincipalName() {
        FacesContext context = FacesContext.getCurrentInstance();
        Principal principal = context.getExternalContext().getUserPrincipal();
        if(principal == null) {
        	log.log(Level.INFO,"UserBean.getUserPrincipalName is null");
            return null;
        }
        log.log(Level.INFO,"UserBean.getUserPrincipalName " + principal.getName());
        return principal.getName();
    }
	
	public void initUser() {
		log.log(Level.INFO,"UserBean.initUser");
		if(user == null) {
			String userPrincipalName=getUserPrincipalName();
			if(userPrincipalName!=null){
				user = instaDAO.getUserByEMail(getUserPrincipalName());
				user = instaDAO.getUser(user.getId());
			} else{
				log.log(Level.INFO,"userPrincipalName was null. User not refreshed.");
			}
		}
	}
	
    public String logout() {
    	FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    	return "/index?faces-redirect=true";
    }
	
	public String getFirstName(){
		return user.getFirstName();
	}
	
	public String getLastName(){
		return user.getLastName();
	}
	
	public String getEMail(){
		return user.getEMail();
	}
	
	public String getGender(){
		return user.getGender().name();
	}
	
	public List<User> getFollower(){
		return user.getFollower();
	}
	
}
