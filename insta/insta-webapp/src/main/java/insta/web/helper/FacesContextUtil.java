package insta.web.helper;

import javax.faces.application.Application;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

public class FacesContextUtil {
	public static void clearFields() {
		FacesContext context = FacesContext.getCurrentInstance();
		Application application = context.getApplication();
		ViewHandler viewHandler = application.getViewHandler();
		UIViewRoot viewRoot = viewHandler.createView(context, context
		.getViewRoot().getViewId());
		context.setViewRoot(viewRoot);
	}
}
