package controllers;

import java.util.Arrays;
import java.util.List;

import models.GroupType;
import models.Question;
import models.Question.QuestionType;
import play.data.validation.Required;
import play.mvc.With;
import controllers.CRUD.For;

@Check({ "MANAGER" })
@With(Secure.class)
@For(Question.class)
public class Questions extends AbstractController { 

	public static void show(Long questionId) {
		Question question = Question.findById(questionId);
		render(question);
	}

	public static void storeQuestion(@Required String title, @Required int difficulty, @Required int second,
			@Required Long groupType, @Required String content, String explanation, @Required String questionType,
			String correct, String response1, String response2, String response3, String response4, String response5,
			Boolean correct1, Boolean correct2, Boolean correct3, Boolean correct4, Boolean correct5) {

		Question question =
				new Question(title, content, explanation, difficulty, second,
						(GroupType) GroupType.findById(groupType), QuestionType.valueOf(questionType));

		if (QuestionType.valueOf(questionType).equals(QuestionType.SIMPLE_CHOICE)) {
			question.addResponse(response1, "1".equals(correct));
			question.addResponse(response2, "2".equals(correct));
			question.addResponse(response3, "3".equals(correct));
			question.addResponse(response4, "4".equals(correct));
			question.addResponse(response5, "5".equals(correct));
		} else if (QuestionType.valueOf(questionType).equals(QuestionType.TEXT_ANSWER)) {
			question.addResponse(response1, true);
			question.addResponse(response2, true);
			question.addResponse(response3, true);
			question.addResponse(response4, true);
			question.addResponse(response5, true);
		} else if (QuestionType.valueOf(questionType).equals(QuestionType.MULTIPLE_CHOICE)) {
			question.addResponse(response1, correct1);
			question.addResponse(response2, correct2);
			question.addResponse(response3, correct3);
			question.addResponse(response4, correct4);
			question.addResponse(response5, correct5);
		} else {
			throw new RuntimeException("Problem with Form");
		}
		if (validation.hasErrors()) {
			render("Questions/create.html");
		}
		question.create();
		// show(question.id);
	}

	public static void search(String title, Integer difficulty, Integer second, Long groupType) {
		List<Question> questions = Question.search(title, difficulty, second, Arrays.asList(groupType));

		render(questions);
	}

	public static void create() {
		render();
	}

}