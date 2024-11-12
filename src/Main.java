import UserManagement.UserLoader;
import UserManagement.UserManagement;
import Utils.PrivateLogger;
import xjtlu.cpt111.assignment.quiz.model.Question;
import xjtlu.cpt111.assignment.quiz.util.IOUtilities;

import java.io.IOException;

public class Main {

private static UserManagement s_userdb_;
private static Question[]     s_questiondb_;

/**
 * <p>Main function, program starts here</p>
 *
 * @param args
 */
public static void main(String[] args) {
  prepare();
  mainloop();
}


/**
 * <p>Main loop of program, call after main</p>
 * <p>Accept command switches values</p>
 */
private static void mainloop() {
}

private static void prepare() {
  PrivateLogger logger = PrivateLogger.CreateInstance();
  try {
    s_userdb_     = UserLoader.LoadUserInfo("C:\\Users\\Admin\\Desktop\\xjtlu\\CPT111\\CW3L1\\resources\\users.csv");
    s_questiondb_ = IOUtilities.readQuestions(
        "C:\\Users\\Admin\\Desktop\\xjtlu\\CPT111\\CW3L1\\resources\\questionsBank");
  } catch (IOException e) {
    logger.Debug(e.toString());
  }
}
}
