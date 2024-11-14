import QuestionManagement.QuestionManager;
import UserManagement.UserManager;
import Utils.PrivateLogger;
import Utils.ProgramInfo;
import xjtlu.cpt111.assignment.quiz.model.Difficulty;
import xjtlu.cpt111.assignment.quiz.model.Option;
import xjtlu.cpt111.assignment.quiz.model.Question;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

private static UserManager     s_userdb_;
private static QuestionManager s_questiondb_;

private static final String S_USER_INFO_DB_PATH_  = "E:/u.csv";
private static final String S_USER_SCORE_DB_PATH_ = "E:/s.csv";
private static final String S_QUIZ_INFO_DB_PATH_  = "C:\\Users\\Admin\\Desktop\\xjtlu\\CPT111\\CW3L1\\resources\\questionsBank";

/**
 * <p>Main function, program starts here</p>
 *
 * @param args
 */
public static void main(String[] args) {
  try {
    prepare();
    mainloop();
  } catch (Exception e) {
    e.printStackTrace();
  }
}

/**
 * <p>Main loop of program, call after main</p>
 * <p>Accept command switches values</p>
 */
private static void mainloop() {
  UI.PrintTitle();
  while (true) {
    if (UI.Dashboard("Main Menu: Task Selection:", UI.loginDashboard_hook, () -> {
      System.out.println("Execute Successful.");

      System.out.println(UI.splitBar);
      while (UI.loginStatus) {
        UI.WelcomePromopt(UI.currentLoginUser.GetUserName());
        if (UI.Dashboard("User Dashboard: Task Selection:", UI.userDashboard_hook, () -> {
          return true;
        }, () -> {
          UI.loginStatus      = false;
          UI.currentLoginUser = null;
          return true;
        })) {
          break;
        }
      }
      return true;
    }, () -> {
      return true;
    })) {
      break;
    }
  }
}

private static void prepare() {
  PrivateLogger logger = PrivateLogger.CreateInstance();
  try {
    s_userdb_ = UserManager.UserLoader.LoadUserInfo(S_USER_INFO_DB_PATH_);
    System.out.println("User Database Loaded...\n" + s_userdb_.GetUserNumbers() + " users' information is loaded");
    UserManager.UserLoader.LoadUserRecord(s_userdb_, S_USER_SCORE_DB_PATH_);
    System.out.println("User Score Record Loaded...\n" + s_userdb_.GetUserNumbers() + " users' score is loaded");
    s_questiondb_ = QuestionManager.QuestionLoader.LoadQuestion(S_QUIZ_INFO_DB_PATH_);
    System.out.println("Question Database Loaded...\n" + s_questiondb_.GetQuestions()
                                                                      .size() + " topics is loaded");
    UI.RegisterFunction();
  } catch (IOException e) {
    logger.Debug(e.toString());
  }
}

public enum ExitStatus {
  EXIT, CONTINUE,
}

public static final class UI {

  private static final String                    splitBar            = "=".repeat(70);
  private static final String                    splitBar2           = "-".repeat(70);
  private static final String                    splitBar3           = ".".repeat(70);
  private static final Map<String, FunctionHook> loginDashboard_hook = new HashMap<>();
  private static final Map<String, FunctionHook> userDashboard_hook  = new HashMap<>();
  private static       boolean                   loginStatus         = false;
  private static       UserManager.Users         currentLoginUser;

  public static void RegisterFunction() {

    loginDashboard_hook.put("Register User", () -> {
      System.out.println("Input User ID: ");
      var userid = new Scanner(System.in).nextLine();
      System.out.println("Input User's Real Name: ");
      var realname = new Scanner(System.in).nextLine();
      System.out.println("Input User's Passwd: ");
      var passwd = new Scanner(System.in).nextLine();
      try {
        s_userdb_.RegisterUser(new UserManager.Users(userid, realname, passwd));
      } catch (UserManager.UserExistException | UserManager.Users.UserInformationInvalidException e) {
        System.err.println(e.getMessage());
        return false;
      }
      return true;
    });
    loginDashboard_hook.put("Login", () -> {
      System.out.print("Login <Type User ID>: ");
      var user = new Scanner(System.in).nextLine();
      System.out.print("Enter password here: ");
      var console = System.console();
      var passwd  = console != null ? new String(console.readPassword()) : new Scanner(System.in).nextLine();
      currentLoginUser = (loginStatus = s_userdb_.CheckLogin(user, passwd)) && s_userdb_.CheckUser(user)
                         ? s_userdb_.GetUserInfo(user)
                         : null;
      return loginStatus;
    });
    loginDashboard_hook.put("Save Users", () -> {
      try {
        UserManager.UserSaver.Write(UserManager.UserSaver.PortToCSV(s_userdb_), "E:/u.csv");
        UserManager.UserSaver.Write(UserManager.UserSaver.ScoreToCSV(s_userdb_), "E:/s.csv");
      } catch (IOException e) {
        System.out.println(e.getMessage());
        return false;
      }
      return true;
    });
    loginDashboard_hook.put("Rank List", () -> {

      class ScoreRecord {
        public String username;
        public String topic;
        public int    score;

        public ScoreRecord(String name, String topic, int score) {
          username   = name;
          this.topic = topic;
          this.score = score;
        }
      }

      int[]                           current  = new int[] {1};
      Map<Integer, List<ScoreRecord>> topicMap = new HashMap<>();

      System.out.println(splitBar);
      System.out.println("Score Rank List: topic Selection: ");
      System.out.println(splitBar2);


      List<ScoreRecord> records = new ArrayList<>();
      s_userdb_.GetUsers()
               .forEach(x -> {
                 x.GetScoreRecord()
                  .forEach((y, z) -> {
                    records.add(new ScoreRecord(x.GetUserName(), y, z[0]));
                    records.add(new ScoreRecord(x.GetUserName(), y, z[1]));
                    records.add(new ScoreRecord(x.GetUserName(), y, z[2]));
                  });
               });
      records.sort(Comparator.comparingInt(x -> x.score));
      var topicrecord = records.stream()
                               .collect(Collectors.groupingBy(x -> x.topic));

      topicrecord.forEach((x, y) -> {
        System.out.println(" " + current[0] + ") " + x);
        topicMap.put(current[0]++, y);
        if (current[0] % 10 == 0) {
          System.out.print("Press <Enter> key to continue:");
          try {
            System.in.read();
          } catch (IOException e) {
            System.err.println(e.getMessage());
          }
        }
      });

      System.out.println(splitBar2);
      System.out.print("Select an option <Type a integer here, 'q' to exit>: ");
      var     input   = new Scanner(System.in);
      int     choice  = Integer.MIN_VALUE;
      boolean inputed = false;
      if (input.hasNextInt() && (inputed = true) && topicMap.containsKey(choice = input.nextInt())) {
        for (var e : topicMap.get(choice)) {
          System.out.println(e.username + " ... " + e.score);
        }
      } else if (inputed) {
        System.out.println("Illegal input.");
      } else {
        String choices = input.nextLine();
        if (choices.contains("q")) {
          loginStatus = false;
        } else {
          System.out.println("Illegal Input.");
        }
      }
      return true;
    });
    userDashboard_hook.put("Taking Quiz", () -> {

      int[]                current  = new int[] {1};
      Map<Integer, String> topicMap = new HashMap<>();

      System.out.println(splitBar);
      System.out.println("Question Selection: ");
      System.out.println(splitBar2);

      s_questiondb_.GetQuestions()
                   .forEach((x, y) -> {
                     System.out.println(" " + current[0] + ") " + x);
                     topicMap.put(current[0]++, x);
                     if (current[0] % 10 == 0) {
                       System.out.print("Press <Enter> key to continue:");
                       try {
                         System.in.read();
                       } catch (IOException e) {
                         System.err.println(e.getMessage());
                       }
                     }
                   });

      System.out.println(splitBar2);
      System.out.print("Select an option <Type a integer here, 'q' to exit>: ");
      var     input   = new Scanner(System.in);
      int     choice  = - 100;
      boolean inputed = false;
      if (input.hasNextInt() && (inputed = true) && topicMap.containsKey(choice = input.nextInt())) {
        QuizTaking(topicMap.get(choice), s_questiondb_.GetQuestions(topicMap.get(choice)));
      } else if (inputed) {
        System.out.println("Illegal input.");
      } else {
        String choices = input.nextLine();
        if (choices.contains("q")) {
          loginStatus = false;
        } else {
          System.out.println("Illegal Input.");
        }
      }
      return true;
    });

    userDashboard_hook.put("History", () -> {
      currentLoginUser.GetScoreRecord()
                      .forEach((x, y) -> {
                        System.out.println(x);
                        for (var score : y) {
                          if (score != Integer.MIN_VALUE) {
                            System.out.println(score);
                          }
                        }
                        System.out.println(splitBar3);
                      });
      return true;
    });
  }

  /**
   * <p>Print Titles</p>
   */
  public static void PrintTitle() {

    System.out.println(splitBar);
    System.out.println("# Question Management System #| Version: " + ProgramInfo.Version());
    System.out.println(splitBar2);
  }

  public static void WelcomePromopt(String user_name) {
    System.out.println("You are welcomed, " + user_name);
  }

  private static boolean Dashboard(String title, Map<String, FunctionHook> functionHookMap, FunctionHook execsuccess, FunctionHook execquit) {

    Map<Integer, FunctionHook> hookMap = new HashMap<>();
    final Integer[]            current = {1};
    boolean                    exit    = false;

    System.out.println(splitBar);
    System.out.println(title);
    System.out.println(splitBar);

    functionHookMap.forEach((x, y) -> {
      System.out.println(" " + current[0] + ") " + x);
      hookMap.put(current[0]++, y);
      if (current[0] % 10 == 0) {
        System.out.print("Press <Enter> key to continue:");
        try {
          System.in.read();
        } catch (IOException e) {
          System.err.println(e.getMessage());
        }
      }
    });
    System.out.println(splitBar);
    System.out.print("Select an option <Type a integer here, 'q' to exit>: ");
    var     input   = new Scanner(System.in);
    int     choice  = Integer.MIN_VALUE;
    boolean inputed = false;
    if (input.hasNextInt() && (inputed = true) && hookMap.containsKey(choice = input.nextInt())) {
      if (hookMap.get(choice)
                 .Invoke()) {
        execsuccess.Invoke();
      }
    } else if (inputed) {
      System.out.println("Illegal input.");
    } else {
      String choices = input.nextLine();
      if (choices.contains("q")) {
        execquit.Invoke();
        exit = true;
      } else {
        System.out.println("Illegal Input.");
      }
    }
    return exit;
  }

  public static void QuizTaking(String topic, Map<Difficulty, List<Question>> question) {

    List<Question> questionList = new ArrayList<>(question.values()
                                                          .stream()
                                                          .flatMap(x -> {
                                                            var   random   = new Random();
                                                            int[] currentn = new int[] {0};
                                                            return x.stream()
                                                                    .findAny()
                                                                    .isPresent()
                                                                   ? x.stream()
                                                                      .filter(a -> random.nextBoolean() ||
                                                                                   (currentn[0]++ > 0))
                                                                   : Stream.empty();
                                                          })
                                                          .toList());
    var totalnum = questionList.size();
    Collections.shuffle(questionList);

    var currentCorrect = 0;
    for (var q : questionList) {
      System.out.println(q.getQuestionStatement());
      System.out.println(splitBar3);
      var ansMap = new HashMap<Integer, Option>();
      var co     = 1;
      for (var o : q.getOptions()) {
        System.out.println(" " + co + ") " + o.getAnswer());
        ansMap.put(co++, o);
      }
      System.out.println(splitBar2);
      System.out.print("Select an option <Type a integer here, 'q' to exit>: ");

      var     input   = new Scanner(System.in);
      int     choice  = - 100;
      boolean inputed = false;
      if (input.hasNextInt() && (inputed = true) && ansMap.containsKey(choice = input.nextInt())) {
        if (ansMap.get(choice)
                  .isCorrectAnswer()) {
          currentCorrect++;
        }
      } else if (inputed) {
        System.out.println("Illegal input.");
      } else {
        String choices = input.nextLine();
        if (choices.contains("q")) {
          loginStatus = false;
        } else {
          System.out.println("Illegal Input.");
        }
      }
    }
    var finalscore = 100 * currentCorrect / totalnum;
    System.out.println(" Congratulation, you got: " + finalscore);
    currentLoginUser.AddRecord(topic, finalscore);
    return;
  }

  private interface FunctionHook {
    boolean Invoke();
  }

}
}
