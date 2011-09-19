package controllers;

import models.Exam;
import models.GroupType;
import models.Question;
import models.Quiz;
import models.User;
import play.Play;
import play.mvc.Before;

public class AbstractController extends CRUD {

	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			User user = User.find("byEmail", Security.connected()).first();
			renderArgs.put("user", user);
			Exam exam = Exam.findForUser(user);
			if (exam != null) {
				renderArgs.put("myContest", exam);
			}
		}
	}

//	@Before
//	static void addDefaults() {
//		renderArgs.put("siteTitle",
//				Play.configuration.getProperty("site.title"));
//		if (renderArgs.get("siteBaseline") == null) {
//			renderArgs.put("siteBaseline",
//					Play.configuration.getProperty("site.baseline"));
//		}
//	}

	@Before
	static void loadInformations() {
		renderArgs.put("nbQuiz", Quiz.count());
		renderArgs.put("nbUser", User.count());
		renderArgs.put("nbExam", Exam.count());
		renderArgs.put("nbQuestion", Question.count());
	}

}
