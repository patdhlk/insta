package insta.web.userbeans;

import insta.ejb.interfaces.InstaDAO;
import insta.persistence.entities.User;
import insta.persistence.enums.Role;
import insta.web.helper.ExceptionUtil;

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

@ViewScoped
@Named("allusers")
public class AllUserBean implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private static final Logger log = Logger.getLogger( AllUserBean.class.getName() );
	
	@EJB
	private InstaDAO instaDAO;
	private User user;
	
	public String getUserPrincipalName() {
        FacesContext context = FacesContext.getCurrentInstance();
        Principal principal = context.getExternalContext().getUserPrincipal();
        if(principal == null) {
        	log.log(Level.INFO,"AllUserBean.getUserPrincipalName is null");
            return null;
        }
        log.log(Level.INFO,"AllUserBean.getUserPrincipalName " + principal.getName());
        return principal.getName();
    }
	
	public void initUser() {
		log.log(Level.INFO,"AllUserBean.initUser");
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

	public List<User> getAllUsers(){//Just shows other users, no admins
		List<User> allUser = new ArrayList<User>();
		allUser = instaDAO.getUsersByRole(Role.USER);
		return allUser.stream().filter(a -> a.getId()!=user.getId()).collect(Collectors.toList());//Remove itself from list. 
	}

	public String follow(int userId) {
		log.log(Level.INFO,"AllUserBean.follow " + userId);
		if (user != null) {
			try {
				instaDAO.follow(user, userId);
			}
			catch (EJBTransactionRolledbackException e) {
				FacesContext.getCurrentInstance().addMessage(null, 
						new FacesMessage("Error during follow user: Internal error"));
				log.log(Level.INFO, String.format("AllUserBean.follow unsuccessful: %s", ExceptionUtil.getRootCause(e).getMessage()));
			}
		}
		return "/users/allusers?faces-redirect=true";
	}
	
	public String unfollow(int userId) {
		log.log(Level.INFO,"AllUserBean.unfollow " + userId);
		if (user != null) {
			try {
				instaDAO.unfollow(user, userId);
			}
			catch (EJBTransactionRolledbackException e) {
				FacesContext.getCurrentInstance().addMessage(null, 
						new FacesMessage("Error during unfollow user: Internal error"));
				log.log(Level.INFO, String.format("AllUserBean.unfollow unsuccessful: %s", ExceptionUtil.getRootCause(e).getMessage()));
			}			
		}
		return "/users/allusers?faces-redirect=true";
	}
	
	public boolean followes(int userId){
		log.log(Level.INFO,"AllUserBean.followes " + userId);
		if (user != null) {
			User tmpUser;
			tmpUser=instaDAO.getUser(userId);
			if(tmpUser != null){
				return user.getFollower().stream().anyMatch(a -> a.getId()==tmpUser.getId());
			}
			else return false;
		}
		return false;
	}
	
	public User getUser() {
		return user;
	}
}
