package com.abhi.quizservice.service;

import com.abhi.quizservice.dao.QuizDao;
import com.abhi.quizservice.feign.QuizInterface;
import com.abhi.quizservice.model.QuestionResponse;
import com.abhi.quizservice.model.QuestionWrapper;
import com.abhi.quizservice.model.Quiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    @Autowired
    private final QuizDao quizDao;

    @Autowired
    QuizInterface quizInterface;

    public QuizService(QuizDao quizDao) {
        this.quizDao = quizDao;
    }


    public ResponseEntity<String> createQuiz(String category, int num, String title) {
        try {
            List<Integer> questions = quizInterface.getQuestionsForQuiz(category, num).getBody();

            if (questions.isEmpty()) {
                throw new Exception("No questions found for the given category.");
            }

            Quiz quiz = new Quiz();
            quiz.setTitle(title);
            quiz.setQuestionIds(questions);

            quizDao.save(quiz);
            return new ResponseEntity<>("Success", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id) {
        try {
            Optional<Quiz> quizOptional = quizDao.findById(id);

            if (quizOptional.isEmpty()) {
                throw new Exception("Quiz not found.");
            }

            Quiz quiz = quizOptional.get();
            List<Integer> questionsIdsFromDB = quiz.getQuestionIds();
            if (questionsIdsFromDB.isEmpty()) {
                throw new Exception("No questions found in the quiz.");
            }

            List<QuestionWrapper> questionForUser = quizInterface.getQuestionsFromId(questionsIdsFromDB).getBody();

            return new ResponseEntity<>(questionForUser, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> calculateResult(Integer id, List<QuestionResponse> questionResponses) {
        try {
            ResponseEntity<Integer> score = (ResponseEntity<Integer>) quizInterface.getScore(questionResponses);

            return score;
        } catch (Exception e) {
            // Log the exception (optional, depending on the logging framework)
            e.printStackTrace();

            // Return a generic error response
            return new ResponseEntity<>("An error occurred while calculating the result", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
