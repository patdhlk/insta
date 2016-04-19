package insta.servlets;

import insta.ejb.interfaces.InstaDAO;

import java.io.IOException;
import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ImageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger( ImageServlet.class.getName() );

	@EJB
	private InstaDAO instaDAO;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		
		String requestUri = request.getRequestURI();
		Principal userPrincipal = request.getUserPrincipal();
		
		if(userPrincipal!=null){
			
			//Add check here using userPrincipal.getName() if user is allowed to access image
			log.log(Level.INFO, "Accessing image as user:"+userPrincipal.getName());
			
			String imageId = requestUri.substring(requestUri.lastIndexOf('/') + 1);
			log.log(Level.INFO,"Retrieving image from servlet [{}]=>[{}]..."+requestUri+" "+imageId);
			response.setHeader("Content-Type", "image/jpeg");
			response.setHeader("Content-Disposition", "inline; filename=\"" + imageId + ".jpg\"");
			try {
				if(instaDAO.getFile(Integer.parseInt(imageId))!=null){
					response.getOutputStream().write(instaDAO.getFile(Integer.parseInt(imageId)));
				}
			} catch (NumberFormatException e) {
				log.log(Level.INFO,"Wrong URI, has to end with number");
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			log.log(Level.INFO, "User not logged in, so not allowed to access image.");
		}
	}

}
