package QuestionManagement;

import xjtlu.cpt111.assignment.quiz.model.Difficulty;
import xjtlu.cpt111.assignment.quiz.model.Option;
import xjtlu.cpt111.assignment.quiz.model.Question;
import xjtlu.cpt111.assignment.quiz.util.IOUtilities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QuestionManager {

private final Map<String, Map<Difficulty, List<Question>>> m_question_ = new HashMap<>();

public Map<String, Map<Difficulty, List<Question>>> GetQuestions() {
  return m_question_;
}

public Map<Difficulty, List<Question>> GetQuestions(String topic) {
  return m_question_.get(topic);
}

public static class QuestionLoader {
  public static QuestionManager LoadQuestion(String fp) {
    var questionManagement = new QuestionManager();
    Arrays.stream(IOUtilities.readQuestions(fp))
          .collect(Collectors.groupingBy(Question::getTopic))
          .forEach((x, y) -> {
            questionManagement.m_question_.put(x, y.stream()
                                                   .filter(q -> q.getOptions().length > 1 && ! q.getQuestionStatement()
                                                                                                .isEmpty() &&
                                                                Arrays.stream(q.getOptions())
                                                                      .filter(Option::isCorrectAnswer)
                                                                      .count() == 1)
                                                   .toList()
                                                   .stream()
                                                   .collect(Collectors.groupingBy(Question::getDifficulty)));
          });
    return questionManagement;
  }

}


}
