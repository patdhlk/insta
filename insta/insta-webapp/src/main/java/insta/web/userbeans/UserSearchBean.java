package insta.web.userbeans;

import insta.ejb.interfaces.InstaDAO;
import insta.persistence.entities.User;
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
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@SessionScoped
@Named("search")
public class UserSearchBean implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private static final Logger log = Logger.getLogger( UserSearchBean.class.getName() );
	
	@EJB
	private InstaDAO instaDAO;
	private User user;
	private String searchText;
	
	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public String getUserPrincipalName() {
        FacesContext context = FacesContext.getCurrentInstance();
        Principal principal = context.getExternalContext().getUserPrincipal();
        if(principal == null) {
        	log.log(Level.INFO,"UserSearchBean.getUserPrincipalName is null");
            return null;
        }
        log.log(Level.INFO,"UserSearchBean.getUserPrincipalName " + principal.getName());
        return principal.getName();
    }
	
	public void initUser() {
		log.log(Level.INFO,"UserSearchBean.initUser");
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

	public List<User> getAllUsers(){
		List<User> allUser = new ArrayList<User>();
		allUser = instaDAO.getAllUsers();
		return allUser;
	}
	
	public String follow(int userId) {
		log.log(Level.INFO,"UserSearchBean.follow " + userId);
		if (user != null) {
			try {
			instaDAO.follow(user, userId);
			}
			catch (EJBTransactionRolledbackException e) {
				FacesContext.getCurrentInstance().addMessage(null, 
						new FacesMessage("Error during follow user: Internal error"));
				log.log(Level.INFO, String.format("UserPhotosBean.follow unsuccessful: %s", ExceptionUtil.getRootCause(e).getMessage()));
			}	
		}
		return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath()+"?faces-redirect=true";
	}
	
	public String unfollow(int userId) {
		log.log(Level.INFO,"UserSearchBean.unfollow " + userId);
		if (user != null) {
			try {
			instaDAO.unfollow(user, userId);
			}
			catch (EJBTransactionRolledbackException e) {
				FacesContext.getCurrentInstance().addMessage(null, 
						new FacesMessage("Error during set unfollow user: Internal error"));
				log.log(Level.INFO, String.format("UserPhotosBean.unfollow unsuccessful: %s", ExceptionUtil.getRootCause(e).getMessage()));
			}	
		}
		return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath()+"?faces-redirect=true";
	}
	
	public List<User> getUsers(){
		List<User> allUser = new ArrayList<User>();
		if (searchText != null) {
			allUser = instaDAO.searchUsers(searchText);
		}
		return allUser.stream().filter(a -> a.getId()!=user.getId()).collect(Collectors.toList());//Remove itself from list. 
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
	
	public String searchUser() {
		return "/users/search?faces-redirect=true";
	}
	
}
