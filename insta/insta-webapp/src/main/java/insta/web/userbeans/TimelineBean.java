package insta.web.userbeans;

import insta.ejb.interfaces.InstaDAO;
import insta.helperobjects.TimelineElement;
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
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@SessionScoped
@Named("timeline")
public class TimelineBean implements Serializable{

	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger( TimelineBean.class.getName() );
	
	@EJB
	private InstaDAO instaDAO;
	private User user;
	private int entryPerPage = 3;
	private int pageInTimeline = 0;
	
	public String getUserPrincipalName() {
        FacesContext context = FacesContext.getCurrentInstance();
        Principal principal = context.getExternalContext().getUserPrincipal();
        if(principal == null) {
        	log.log(Level.INFO,"TimelineBean.getUserPrincipalName is null");
            return null;
        }
        log.log(Level.INFO,"TimelineBean.getUserPrincipalName " + principal.getName());
        return principal.getName();
    }
	
	public void initUser() {
		log.log(Level.INFO,"TimelineBean.initUser");
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
	
	public String like(Photo photo) {
		log.log(Level.INFO,"TimelineBean.like");
		try {
			instaDAO.like(this.user, photo);
		}
		catch (EJBTransactionRolledbackException e) {
			FacesContext.getCurrentInstance().addMessage(null, 
					new FacesMessage("Error during like photo: Internal error"));
			log.log(Level.INFO, String.format("TimelineBean.like unsuccessful: %s", ExceptionUtil.getRootCause(e).getMessage()));
		}			
		return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath()+"?faces-redirect=true";
	}
	
	public String unlike(Photo photo) {
		log.log(Level.INFO,"TimelineBean.unlike");
		try {
			instaDAO.unlike(this.user, photo);
		}
		catch (EJBTransactionRolledbackException e) {
			FacesContext.getCurrentInstance().addMessage(null, 
					new FacesMessage("Error during unlike photo: Internal error"));
			log.log(Level.INFO, String.format("TimelineBean.unlike unsuccessful: %s", ExceptionUtil.getRootCause(e).getMessage()));
		}		
		return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath()+"?faces-redirect=true";
	}

	public List<TimelineElement> getTimelineElements(){
		int skip = this.pageInTimeline*this.entryPerPage;
		int count = this.entryPerPage;
		return instaDAO.getTimelineElements(user.getId(), skip, count);
	}

	public String nextPage(){
		this.pageInTimeline++;
		return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath()+"?faces-redirect=true";
	}
	
	public String previousPage(){
		if(this.pageInTimeline!=0) this.pageInTimeline--;
		return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath()+"?faces-redirect=true";
	}
	
	public String firstPage(){
		this.pageInTimeline=0;
		return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath()+"?faces-redirect=true";
	}
	
	public boolean nextPageAvailable(){
		nextPage();
		if(getTimelineElements().size()>0){
			previousPage();
			return true;
		} else{
			previousPage();
			return false;
		}
	}
	
	public boolean previousPageAvailable(){
		if(this.pageInTimeline==0) return false;
		else return true;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getPageInTimeline() {
		return pageInTimeline;
	}

	public void setPageInTimeline(int pageInTimeline) {
		this.pageInTimeline = pageInTimeline;
	}
}
