package controllers;

import play.*;
import play.data.validation.Required;
import play.mvc.*;

import java.util.*;

import models.*;

public class ExamController extends Controller {

	public static void go(String examKey) {
		Exam exam = Exam.findByKey(examKey);
		render(exam);
	}

	public static void show(Long id) {
		Post post = Post.findById(id);
		render(post);
	}

	public static void storeCandidate(Long examId, @Required String email,
			@Required String password, @Required String confirmPassword, Date birthdate) {
		Exam exam = Exam.findById(examId);
		if (validation.hasErrors()) {
			render("ExamController/go.html", exam);
		}
		go(exam.examKey);
	}

	@Before
	static void addDefaults() {
		renderArgs.put("siteTitle",
				Play.configuration.getProperty("site.title"));
		renderArgs.put("siteBaseline",
				Play.configuration.getProperty("site.baseline"));
	}

}