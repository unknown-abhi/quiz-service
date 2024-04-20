package com.abhi.quizservice.controller;

import com.abhi.quizservice.model.QuestionResponse;
import com.abhi.quizservice.model.QuestionWrapper;
import com.abhi.quizservice.model.QuizDto;
import com.abhi.quizservice.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("quiz")
public class QuizController {

    @Autowired
    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping("createQuiz")
    public ResponseEntity<String> createQuiz(@RequestBody QuizDto dto) {
        return quizService.createQuiz(dto.getCategoryName(), dto.getNumQuestions(), dto.getTitle());
    }

    @GetMapping("getQuiz/{id}")
    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(@PathVariable Integer id) {
        return quizService.getQuizQuestions(id);
    }

    @PostMapping("submit/{id}")
    public ResponseEntity<?> submitQuiz(@PathVariable Integer id, @RequestBody List<QuestionResponse> questionResponses) {
        return quizService.calculateResult(id, questionResponses);
    }
}
