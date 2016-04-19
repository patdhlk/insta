package insta.ejb.beans;

import insta.ejb.interfaces.InstaDAO;
import insta.persistence.entities.User;
import insta.persistence.enums.Gender;
import insta.persistence.enums.Role;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton
@Startup
public class Initialisation {

	private static final Logger log = Logger.getLogger( Initialisation.class.getName() );
	
	@EJB
	private InstaDAO instaDAO;
	
	@PostConstruct
	private void init(){

		if (instaDAO.getUsersByRole(Role.ADMIN)==null){
			log.log(Level.INFO,"No admin user in Database. Creating default admin user.");
			
			User admin1 = new User();
			admin1.setFirstName("admin1");
			admin1.setLastName("admin1");
			admin1.setGender(Gender.Male);
			admin1.setRole(Role.ADMIN);
			admin1.setPassword("jGl25bVBBBW96Qi9Te4V37Fnqchz/Eu4qB9vKrRIqRg=");
			admin1.setEMail("admin1@test.de");
			
			instaDAO.create(admin1);
		}
		
		if (instaDAO.getUsersByRole(Role.USER)==null){
			log.log(Level.INFO,"No user in Database. Creating 3 default users.");
		
			User user1 = new User();
			user1.setFirstName("user1");
			user1.setLastName("user1");
			user1.setGender(Gender.Male);
			user1.setRole(Role.USER);
			user1.setPassword("BPiZbadjt6lpsQKO4wB1aerzpjVIbdqyEdUSyFud+Ps=");
			user1.setEMail("user1@test.de");
			
			User user2 = new User();
			user2.setFirstName("user2");
			user2.setLastName("user2");
			user1.setGender(Gender.Female);
			user2.setRole(Role.USER);
			user2.setPassword("BPiZbadjt6lpsQKO4wB1aerzpjVIbdqyEdUSyFud+Ps=");
			user2.setEMail("user2@test.de");
			
			User user3 = new User();
			user3.setFirstName("user3");
			user3.setLastName("user3");
			user3.setGender(Gender.Unknown);
			user3.setRole(Role.USER);
			user3.setPassword("BPiZbadjt6lpsQKO4wB1aerzpjVIbdqyEdUSyFud+Ps=");
			user3.setEMail("user3@test.de");
			
			user1 = instaDAO.create(user1);
			user2 = instaDAO.create(user2);
			user3 = instaDAO.create(user3);
			
			user1.getFollower().add(user1);
			user1.getFollower().add(user2);
			user1.getFollower().add(user3);
			
			user2.getFollower().add(user2);
			
			user3.getFollower().add(user3);
			
			instaDAO.update(user1);
			instaDAO.update(user2);
			instaDAO.update(user3);			
		}
	}
}
