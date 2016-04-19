package insta.web.userbeans;

import insta.ejb.interfaces.InstaDAO;
import insta.persistence.entities.User;
import insta.persistence.enums.Gender;
import insta.persistence.enums.Role;
import insta.web.helper.ExceptionUtil;
import insta.web.helper.FacesContextUtil;
import insta.web.helper.UiText;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

@ViewScoped
@Named("register")
public class RegisterBean implements Serializable {
 
	private static final long serialVersionUID = 1L;
	
	private static final Logger log = Logger.getLogger( RegisterBean.class.getName() );

	@EJB
	private InstaDAO instaDAO;
	
	private String firstName;
	private String lastName;
	private String eMail;
	private String password;
	private String gender;
 
	public RegisterBean() {
	}
 
	public void submit() {       
		log.log(Level.INFO,"RegisterBean.submit");
						
		if(instaDAO.getUserByEMail(eMail)!=null){//Email exists already
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Email exists already, please choose another"));
		} else{
			User user=new User();
			user.setRole(Role.USER);
			user.setEMail(eMail);
			user.setFirstName(firstName);
			user.setLastName(lastName);
			
			MessageDigest md;
			try {
				md = MessageDigest.getInstance("SHA-256");
				byte[] digest = md.digest(password.getBytes());
				user.setPassword(Base64.getEncoder().encodeToString(digest));
				
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			
			user.setGender(Gender.valueOf(gender));
			user.getFollower().add(user);
			
			Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

			Set<ConstraintViolation<User>> constraintViolations = validator.validate( user );

			for ( ConstraintViolation<User> violation : constraintViolations ) {
				FacesContext.getCurrentInstance().addMessage(null, 
						new FacesMessage(String.format("%s: %s", UiText.toUiText(User.class, violation.getPropertyPath()), UiText.toUiText(violation))));
			}
			
			if (constraintViolations.size() == 0) {
				try {
					instaDAO.create(user);
					FacesContextUtil.clearFields();
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Registration successful, login now"));
				}
				catch (EJBTransactionRolledbackException e) {
					FacesContextUtil.clearFields();
					FacesContext.getCurrentInstance().addMessage(null, 
							new FacesMessage("Registration unsuccessful: Internal error"));
					log.log(Level.INFO, String.format("RegisterBean.submit unsuccessful: %s", ExceptionUtil.getRootCause(e).getMessage()));
				}
			}
		}
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
}
