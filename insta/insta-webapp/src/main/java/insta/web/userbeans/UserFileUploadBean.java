package insta.web.userbeans;

import insta.ejb.interfaces.InstaDAO;
import insta.persistence.entities.Photo;
import insta.persistence.entities.User;
import insta.web.helper.ExceptionUtil;
import insta.web.helper.FacesContextUtil;
import insta.web.helper.ResourceUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.Part;

@ViewScoped
@Named("fileupload")
public class UserFileUploadBean implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger( UserFileUploadBean.class.getName() );
	
	@EJB
	private InstaDAO instaDAO;
	private Part uploadPicture;
	private User user;
	private String fileDescription = "";
	
	public Part getUploadPicture() {
		return uploadPicture;
	}

	public void setUploadPicture(Part uploadPicture) {
		this.uploadPicture = uploadPicture;
	}
	
	public void validatePicture(FacesContext context, UIComponent component, Object value) {
		log.log(Level.INFO, "Validating picture...");
		List<FacesMessage> messages = new ArrayList<>();
		Part file = (Part) value;
		if (file.getSize() > 1024 * 1024) {
			log.log(Level.WARNING, "Picture is too large [{}]KB...", file.getSize() / 1024);
			messages.add(new FacesMessage("Picture is too large.  Max is 1MB."));
		} else if (!"image/jpeg".equals(file.getContentType())) {
			log.log(Level.WARNING, "Incorrect context type [{}].  Expected 'images/jpeg'.", file.getContentType());
			messages.add(new FacesMessage("Picture must be a JPEG."));
		}
		if (!messages.isEmpty()) {
			log.log(Level.WARNING, "Picture validation failed. [{}]", messages);
			throw new ValidatorException(messages);
		} else{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Picture upload successful"));
		}
	}

	public String getUserPrincipalName() {
        FacesContext context = FacesContext.getCurrentInstance();
        Principal principal = context.getExternalContext().getUserPrincipal();
        if(principal == null) {
        	log.log(Level.INFO,"UserFileUploadBean.getUserPrincipalName is null");
            return null;
        }
        log.log(Level.INFO,"UserFileUploadBean.getUserPrincipalName " + principal.getName());
        return principal.getName();
    }
	
	public void initUser() {
		log.log(Level.INFO,"UserFileUploadBean.initUser");
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
	
    public String getFileDescription() {
		return fileDescription;
	}

	public void setFileDescription(String fileDescription) {
		this.fileDescription = fileDescription;
	}
	
	public String uploadFile() {
		if (user != null) {
			log.log(Level.INFO,"uploadFile");
			byte[] bytes = null;
			try (InputStream is = uploadPicture.getInputStream()) {
				bytes = ResourceUtil.toByteArray(is);
				
				if (bytes != null) {
					ResourceUtil.resizeImage(bytes, 500, 700);
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				log.log(Level.WARNING, "uploadFile: " + e.getMessage());
			}
			if (bytes != null) {
				Photo photo = new Photo();
				photo.setImageFile(bytes);
				photo.setDescription(this.getFileDescription());
				try {
					instaDAO.uploadFile(user, photo);
				}
	    		catch (EJBTransactionRolledbackException e) {
	    			FacesContext.getCurrentInstance().addMessage(null, 
	    					new FacesMessage("Error during upload file: Internal error"));
	    			log.log(Level.INFO, String.format("UserFileUploadBean.uploadFile unsuccessful: %s", ExceptionUtil.getRootCause(e).getMessage()));
				}				
			}
			FacesContextUtil.clearFields();
			return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath()+"?faces-redirect=true";
		}
		return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath()+"?faces-redirect=true";
	}
}
