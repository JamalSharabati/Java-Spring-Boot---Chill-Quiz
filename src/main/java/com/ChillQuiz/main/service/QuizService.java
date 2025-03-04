package com.ChillQuiz.main.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.ChillQuiz.main.model.Question;
import com.ChillQuiz.main.model.QuestionForm;
import com.ChillQuiz.main.model.Result;
import com.ChillQuiz.main.repository.QuestionRepo;
import com.ChillQuiz.main.repository.ResultRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class QuizService {

	@Autowired
	private QuestionRepo qRepo;

	@Autowired
	private ResultRepo rRepo;

	// Fetch 5 random questions from the database
	public QuestionForm getQuestions() {
		List<Question> allQues = qRepo.findAll();
		List<Question> qList = new ArrayList<>();

		Random random = new Random();

		for (int i = 0; i < 5 && !allQues.isEmpty(); i++) {
			int rand = random.nextInt(allQues.size());
			qList.add(allQues.get(rand));
			allQues.remove(rand); // Remove to avoid duplicate questions in the same quiz
		}

		QuestionForm qForm = new QuestionForm();
		qForm.setQuestions(qList);

		return qForm;
	}

	// Compare user answers with correct answers
	public int getResult(QuestionForm qForm) {
		int correct = 0;
		for (Question q : qForm.getQuestions()) {
			if (q.getAns() == q.getChose()) {
				correct++;
			}
		}
		return correct;
	}

	// Save user quiz score
	public void saveScore(Result result) {
		Result saveResult = new Result();
		saveResult.setUsername(result.getUsername());
		saveResult.setTotalCorrect(result.getTotalCorrect());
		rRepo.save(saveResult);
	}

	// Fetch top scores sorted by highest total correct answers
	public List<Result> getTopScore() {
		return rRepo.findAll(Sort.by(Sort.Direction.DESC, "totalCorrect"));
	}

	// Save a new question to the database and append it to data.sql
	public void saveQuestion(Question question) {
		qRepo.save(question);  // Save question in database
		appendQuestionToSQLFile(question); // Append to data.sql
	}

	// Append the question to data.sql
	private void appendQuestionToSQLFile(Question question) {
		String sqlFilePath = "src/main/resources/data.sql"; // Path to data.sql
		String sqlInsert = String.format(
				"INSERT INTO questions (ques_id, title, optionA, optionB, optionC, ans, chose) " +
						"VALUES (NULL, '%s', '%s', '%s', '%s', %d, -1);\n",
				question.getTitle().replace("'", "''"),  // Escape single quotes
				question.getOptionA().replace("'", "''"),
				question.getOptionB().replace("'", "''"),
				question.getOptionC().replace("'", "''"),
				question.getAns()
		);

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(sqlFilePath, true))) {
			writer.write(sqlInsert);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
