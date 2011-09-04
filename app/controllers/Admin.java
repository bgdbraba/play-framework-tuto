package controllers;

import models.User;
import play.Play;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Admin extends CRUD {

    @Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            renderArgs.put("user", user.firstname + " " + user.lastname);
        }
    }
 
    public static void index() {
        render();
    }

	@Before
	static void addDefaults() {
		renderArgs.put("siteTitle", Play.configuration.getProperty("site.title"));
		renderArgs.put("siteBaseline", Play.configuration.getProperty("site.baseline"));
	}

}