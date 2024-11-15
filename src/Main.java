import QuestionManagement.QuestionManager;
import UserManagement.UserManager;
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

private static final String S_USER_INFO_DB_PATH_  = "resources/u.csv";
private static final String S_USER_SCORE_DB_PATH_ = "resources/s.csv";
private static final String S_QUIZ_INFO_DB_PATH_  = "resources/questionsBank";

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
      System.out.println(UI.splitBar3);

      // if operation above login a user
      while (UI.loginStatus) {
        UI.WelcomePromopt(UI.currentLoginUser.GetUserName());
        // if User Dashboard reported that user input a 'q'
        if (UI.Dashboard("User Dashboard: Task Selection:", UI.userDashboard_hook, () -> true, () -> {
          UI.loginStatus      = false;
          UI.currentLoginUser = null;

          // if exit
          return true;
        })) {
          break;
        }
      }

      return true;

      // if Main Menu reported that user input a 'q'
      // if exit
    }, () -> true)) {
      break;
    }
  }
  UI.SaveUserInformationHook();
}

/**
 * <p>Load databases before the program enter the main loop</p>
 */
private static void prepare() {
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
    System.err.println(e.getMessage());
    System.exit(- 1);
  }
}

public static final class UI {

  private static final String splitBar  = "=".repeat(70);// string used to split text
  private static final String splitBar2 = "-".repeat(70);// string used to split text
  private static final String splitBar3 = ".".repeat(70);// string used to split text

  private static final Map<String, FunctionHook> loginDashboard_hook = new HashMap<>(); // hooks for Login Session
  private static final Map<String, FunctionHook> userDashboard_hook  = new HashMap<>(); // hooks for User Dashboard Session
  private static       boolean                   loginStatus         = false; // check weather there is a user logging in now
  private static       UserManager.Users         currentLoginUser;// current logging in user

  /**
   * <p>Register hooks before the functions actually being called.</p>
   */
  public static void RegisterFunction() {

    loginDashboard_hook.put("Register User", UI::UserRegisterHook);
    loginDashboard_hook.put("Login", UI::UserLoginHook);
    loginDashboard_hook.put("Save Users", UI::SaveUserInformationHook);
    loginDashboard_hook.put("Rank List", UI::RankList);

    userDashboard_hook.put("Taking Quiz", UI::QuesitongSelection);
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

  /**
   * <p>Print Welcome information.</p>
   *
   * @param user_name current logging in user's name
   */
  public static void WelcomePromopt(String user_name) {
    System.out.println("You are welcomed, " + user_name);
  }

  /**
   * Register a user
   *
   * @return quit?
   */
  private static boolean UserRegisterHook() {
    boolean quit = true; // if execute success, then quit

    // Get basic information
    System.out.println("Leave all empty to quit.");
    System.out.print("Input User ID: ");
    var userid = new Scanner(System.in).nextLine();
    System.out.print("Input User's Real Name: ");
    var realname = new Scanner(System.in).nextLine();
    System.out.print("Input User's Passwd: ");
    var passwd = new Scanner(System.in).nextLine();
    System.out.print("Input User's Passwd again: ");
    var passwd1 = new Scanner(System.in).nextLine();

    if (userid.isEmpty() && realname.isEmpty() && passwd1.isEmpty() && passwd.isEmpty()) {
      System.out.println("Sure, I'd like to quit now.");
    } else if (! passwd1.equals(passwd)) {
      quit = false;
    } else {
      try {
        s_userdb_.RegisterUser(new UserManager.Users(userid, realname, passwd));
      } catch (UserManager.UserExistException | UserManager.Users.UserInformationInvalidException e) {
        System.err.println(e.getMessage());
        quit = false;
      }
    }
    return quit;
  }

  /**
   * <p>Check if the User id is matching the User Password</p>
   *
   * @return quit?
   */
  private static boolean UserLoginHook() {
    boolean quit = false;

    System.out.println("Leave both empty to quit");
    System.out.print("Login <Type User ID>: ");
    var user = new Scanner(System.in).nextLine();
    System.out.print("Enter password here: ");
    var console = System.console();
    var passwd  = console != null ? new String(console.readPassword()) : new Scanner(System.in).nextLine();

    // if the passwd is matching the user id, save it to current logging in user
    // if the loginStatus is false, the user is either not exist or having not matching password
    currentLoginUser =
        (loginStatus = s_userdb_.CheckLogin(user, passwd)) && (quit = true) ? s_userdb_.GetUserInfo(user) : null;

    if (user.equals(passwd) && user.isEmpty()) {
      System.out.println("Sure to quit?");
      quit = true;
    }

    return quit;
  }

  /**
   * <p>A hook to save user information, including user register information and scores</p>
   *
   * @return execute success?
   */
  private static boolean SaveUserInformationHook() {
    boolean success = true;
    try {
      UserManager.UserSaver.Write(UserManager.UserSaver.PortToCSV(s_userdb_), S_USER_INFO_DB_PATH_);
      UserManager.UserSaver.Write(UserManager.UserSaver.ScoreToCSV(s_userdb_), S_USER_SCORE_DB_PATH_);
    } catch (IOException e) {
      System.out.println(e.getMessage());
      success = false;
    }
    return success;
  }

  /**
   * <p>Display user score rank list according to topic.</p>
   *
   * @return execute success?
   */
  private static boolean RankList() {

    class ScoreRecord {
      public final String username;
      public final String topic;
      public final int    score;

      public ScoreRecord(String name, String topic, int score) {
        username   = name;
        this.topic = topic;
        this.score = score;
      }
    }

    int[]                           current  = new int[] {1};
    Map<Integer, List<ScoreRecord>> topicMap = new HashMap<>();
    boolean                         exit     = false;

    System.out.println(splitBar);
    System.out.println("Score Rank List: topic Selection: ");
    System.out.println(splitBar2);

    // register all user's records
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
    var topicrecord = records.stream()
                             .collect(Collectors.groupingBy(x -> x.topic));

    // Print Topic for selection
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
      // Sort the records, larger on the upper
      var r = topicMap.get(choice);

      r.sort((x, y) -> y.score - x.score);
      for (var e : r) {
        if (e.score >= 0) {
          System.out.println(e.username + " ... " + e.score);
        }
      }
    } else if (inputed) {
      System.out.println("Illegal input.");
    } else {
      String choices = input.nextLine();
      if (choices.contains("q")) {
        exit = true;
      } else {
        System.out.println("Illegal Input.");
      }
    }

    return exit;
  }

  /**
   * <p>Display dashboard</p>
   * <p>Show menus according to the hook list</p>
   * <p>And interact according to the rest provided hooks</p>
   *
   * @param title           Dashboard title
   * @param functionHookMap hook list, key is item name, value is hook
   * @param execsuccess     if the hook function returns true
   * @param execquit        if the quit is invoked
   * @return if it is needed to exit
   */
  private static boolean Dashboard(String title, Map<String, FunctionHook> functionHookMap, FunctionHook execsuccess, FunctionHook execquit) {

    Map<Integer, FunctionHook> hookMap = new HashMap<>();
    final Integer[]            current = {1};
    boolean                    exit    = false;

    System.out.println(splitBar);
    System.out.println(title);
    System.out.println(splitBar2);

    // Show Items, and associate item id with hook
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

    // Show selection
    System.out.println(splitBar);
    System.out.print("Select an option <Type a integer here, 'q' to exit>: ");
    var     input   = new Scanner(System.in);
    int     choice  = Integer.MIN_VALUE;
    boolean inputed = false;
    // If the input is number, then it should choose a task displayed above
    // using && short-circuit operation to assign input if it is a number
    if (input.hasNextInt() && (inputed = true) && hookMap.containsKey(choice = input.nextInt())) {
      while (! hookMap.get(choice)
                      .Invoke()) {
        System.out.println("Try again.");
      }
      execsuccess.Invoke();
    } else if (inputed) {
      // if the input number is not within choice list, then it should be illegal
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

  private static boolean QuesitongSelection() {

    int[]                current  = new int[] {1};
    Map<Integer, String> topicMap = new HashMap<>();
    boolean              exit     = false;

    System.out.println(splitBar);
    System.out.println("Question Selection: ");
    System.out.println(splitBar2);

    // Print Topics for selection
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
    int     choice  = Integer.MIN_VALUE;
    boolean inputed = false;
    if (input.hasNextInt() && (inputed = true) && topicMap.containsKey(choice = input.nextInt())) {
      QuizTaking(topicMap.get(choice), s_questiondb_.GetQuestions(topicMap.get(choice)));
    } else if (inputed) {
      System.out.println("Illegal input.");
    } else {
      String choices = input.nextLine();
      if (choices.contains("q")) {
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
    loop:
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
          break;
        } else {
          System.out.println("Illegal Input.");
        }
      }
    }

    var finalscore = 100 * currentCorrect / totalnum;
    System.out.println(" Congratulation, you got: " + finalscore);
    currentLoginUser.AddRecord(topic, finalscore);
  }

  private interface FunctionHook {
    boolean Invoke();
  }

}
}
