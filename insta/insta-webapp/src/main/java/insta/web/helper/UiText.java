package insta.web.helper;

import java.util.ResourceBundle;

import insta.persistence.entities.User;

import javax.faces.context.FacesContext;
import javax.validation.ConstraintViolation;
import javax.validation.Path;

public class UiText {
	public static String toUiText(@SuppressWarnings("rawtypes") Class _class, Path propertyPath) {
		String result = propertyPath.toString();
		
		if (_class == User.class) {
			ResourceBundle messages = ResourceBundle.getBundle("mls.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());

			return messages.getString("user." + result);

		}

		return result;
	}
	
	public static String toUiText(ConstraintViolation<User> violation) {
		String result = "";
		String messageTemplate =violation.getMessageTemplate();
		if (!messageTemplate.isEmpty()) {
			ResourceBundle messages = ResourceBundle.getBundle("mls.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
			
			messageTemplate = messageTemplate.substring(1, messageTemplate.length() - 1);
			
			messageTemplate = messages.getString(messageTemplate);
			
			return messageTemplate;
		}
		return result;
	}
}
