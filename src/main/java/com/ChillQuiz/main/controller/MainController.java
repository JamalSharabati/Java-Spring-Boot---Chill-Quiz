package com.ChillQuiz.main.controller;

import com.ChillQuiz.main.model.Question;
import com.ChillQuiz.main.model.QuestionForm;
import com.ChillQuiz.main.model.Result;
import com.ChillQuiz.main.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MainController {

	@Autowired
	Result result;

	@Autowired
	QuizService qService;

	Boolean submitted = false;

	@ModelAttribute("result")
	public Result getResult() {
		return result;
	}

	@GetMapping("/")
	public String home() {
		return "index.html";
	}

	@PostMapping("/quiz")
	public String quiz(@ModelAttribute QuestionForm qForm, Model m) {
		submitted = false;
		result.setUsername("User");
		QuestionForm questions = qService.getQuestions();
		m.addAttribute("qForm", questions);
		return "quiz.html";
	}

	@PostMapping("/submit")
	public String submit(@ModelAttribute QuestionForm qForm, Model m) {
		if (!submitted) {
			result.setTotalCorrect(qService.getResult(qForm));
			qService.saveScore(result);
			submitted = true;
		}
		return "result.html";
	}

	@GetMapping("/score")
	public String score(Model m) {
		m.addAttribute("sList", qService.getTopScore());
		return "scoreboard.html";
	}

	// Handle displaying the add question page
	@GetMapping("/addQ")
	public String addQuestionPage(Model model) {
		model.addAttribute("question", new Question());
		return "addq.html"; // This should match the Thymeleaf file name
	}

	// Handle saving the question
	@PostMapping("/saveQuestion")
	public String saveQuestion(@ModelAttribute Question question, Model model) {
		qService.saveQuestion(question);
		return "redirect:/";
	}
}
