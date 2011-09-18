package controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.Post;
import models.User;
import models.User.UserState;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;

import play.Play;
import play.data.validation.Required;
import play.db.jpa.JPABase;
import play.libs.Mail;
import play.mvc.Before;

public class Application extends AbstractController {

	// public static void index() {
	// Post frontPost = Post.find("order by postedAt desc").first();
	// List<Post> olderPosts =
	// Post.find("order by postedAt desc").from(1).fetch(10);
	// render(frontPost, olderPosts);
	// }

	private static final int UNKNOWN_EMAIL = 1;
	private static final int CORRUPTED_VALIDATION_NUMBER = 2;

	public static void index() {
		List<Post> posts = Post.findAll();
		render(posts);
	}

	public static void show(Long id) {
		Post post = Post.findById(id);
		render(post);
	}

	public static void logout() {
		render();
	}

	public static void postComment(Long postId, @Required String author,
			@Required String content) {
		Post post = Post.findById(postId);
		if (validation.hasErrors()) {
			render("Application/show.html", post);
		}
		post.addComment(author, content);
		flash.success("Thanks for posting %s", author);
		show(postId);
	}

	public static void signing() {
		render();
	}

	public static void signingOk(@Required String firstname,
			@Required String lastname, @Required String email,
			@Required String confirm_email, @Required String password,
			@Required String confirm_password, @Required String birthdate) {

		if (validation.hasErrors()) {
			render("Application/signing.html", firstname, lastname, email,
					birthdate);
		}
		Date parsedDate = null;
		if (birthdate != null && birthdate.length() > 0) {
			try {
				parsedDate = new SimpleDateFormat("yyyy-MM-dd")
						.parse(birthdate);
			} catch (ParseException e) {
				// -- Add error
				// validation.errors().add(new Error(key, message, variables));
				System.out.println("Bad date");
			}
		}
		User user = new User(email, password, firstname, lastname, parsedDate);
		user.save();

		// SEND mail for confirmation
		System.out.println("Confirmation code /validation/user/"
				+ user.validationNumber + "/?mail=" + user.email);

		render();
	}

	public static void validateUser(Integer validationNumber, String mail) {
		for (JPABase u : User.findAll()) {
			System.out.println(((User) u).email + " : "
					+ ((User) u).validationNumber);
		}
		User user = User.findByEmail(mail);
		int errorCode;
		if (user == null) {
			errorCode = Application.UNKNOWN_EMAIL;
			render("Application/validationKo.html", errorCode);
		} else if (user.validationNumber != validationNumber) {
			errorCode = Application.CORRUPTED_VALIDATION_NUMBER;
			render("Application/validationKo.html", errorCode);
		} else if (user.state == UserState.WAITING_VALIDATION) {
			user.state = UserState.CREATED;
			user.save();
		}
		index();
	}

	@Before
	static void addDefaults() {
		renderArgs.put("blogTitle",
				Play.configuration.getProperty("blog.title"));
		renderArgs.put("blogBaseline",
				Play.configuration.getProperty("blog.baseline"));
	}

}