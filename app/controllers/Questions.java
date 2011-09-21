package controllers;

import java.util.List;

import models.GroupType;
import models.Question;
import models.Question.QuestionType;
import play.data.validation.Min;
import play.data.validation.Required;
import play.mvc.Before;
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

	public static void storeQuestion(@Required(message = "Title is required") String questionTitle,
			@Required(message = "Difficulty is required") @Min(value = 1) int difficulty,
			@Required(message = "Time is required") @Min(value = 1) int second,
			@Required(message = "Type is required") Long groupType,
			@Required(message = "Content is required") String content, String explanation,
			@Required(message = "Response type is required") String questionType, String correct, String response1,
			String response2, String response3, String response4, String response5, boolean correct1, boolean correct2,
			boolean correct3, boolean correct4, boolean correct5) {

		Question question =
				new Question(questionTitle, content, explanation, difficulty, second,
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
		show(question.id);
	}

	public static void search(String questionTitle, Integer difficulty, Integer second, Long groupType) {
		List<Question> questions =
				Question.search(questionTitle, difficulty, second, groupType == null ? new Long[]{}
						: new Long[]{ groupType });

		render(questions, questionTitle, difficulty, second, groupType);
	}

	public static void create() {
		render();
	}

	@Before
	static void addDefaults() {
		renderArgs.put("siteBaseline", "Question");
	}

}