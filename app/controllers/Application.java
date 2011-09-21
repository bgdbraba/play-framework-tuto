package controllers;

import java.util.Date;
import java.util.List;

import models.Post;
import models.User;
import models.User.UserState;
import play.data.binding.As;
import play.data.validation.Email;
import play.data.validation.Equals;
import play.data.validation.Password;
import play.data.validation.Required;
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

	public static void logout() {
		render();
	}

	// public static void show(Long id) {
	// Post post = Post.findById(id);
	// render(post);
	// }
	//
	// public static void postComment(Long postId, @Required String author, @Required String content) {
	// Post post = Post.findById(postId);
	// if (validation.hasErrors()) {
	// render("Application/show.html", post);
	// }
	// post.addComment(author, content);
	// flash.success("Thanks for posting %s", author);
	// show(postId);
	// }

	public static void signing() {
		addDefaults(true);
		render();
	}

	public static void signingOk(
			@Required(message = "Firstname is required") String firstname,
			@Required(message = "Lastname is required") String lastname,
			@Required(message = "Email is required") @Email(message = "Corrupted email") String email,
			@Required(message = "Confirmation is required") @Equals(value = "email", message = "Different email") String confirmEmail,
			@Required(message = "Password is required") @Password String password,
			@Required(message = "Confirmation is required") @Equals(value = "password", message = "Different password") String confirmPassword,
			@As(value = { "yyyy-MM-dd" }) Date birthdate) {

		addDefaults(true);

		if (validation.hasErrors()) {
			render("Application/signing.html", firstname, lastname, email, confirmEmail, birthdate);
		}

		User user = new User(email, password, firstname, lastname, birthdate);
		user.save();

		// SEND mail for confirmation
		System.out.println("Confirmation code /validation/user/" + user.validationNumber + "/?mail=" + user.email);

		render();
	}

	/**
	 * 
	 * @param validationNumber
	 * @param mail
	 */
	public static void validateUser(Integer validationNumber, String mail) {

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

	/**
	 * 
	 * @param isNotHome
	 */
	@Before
	static void addDefaults(boolean isNotHome) {
		if (isNotHome) {
			renderArgs.put("siteBaseline", "");
		} else {
			renderArgs.put("siteBaseline", "Home");
		}
	}

}