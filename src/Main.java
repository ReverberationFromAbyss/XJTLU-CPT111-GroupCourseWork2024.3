import QuestionManagement.QuestionManager;
import UserManagement.UserManager;
import UserManagement.Users;
import Utils.PrivateLogger;
import Utils.ProgramInfo;
import xjtlu.cpt111.assignment.quiz.model.Difficulty;
import xjtlu.cpt111.assignment.quiz.model.Question;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

private static UserManager     s_userdb_;
private static QuestionManager s_questiondb_;

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
  UI.PrintTitle();
  while (true) {
    UI.Menu();
  }
}

private static void prepare() {
  PrivateLogger logger = PrivateLogger.CreateInstance();
  try {
    s_userdb_ = UserManager.UserLoader.LoadUserInfo(
        "C:\\Users\\Admin\\Desktop\\xjtlu\\CPT111\\CW3L1\\resources\\users.csv");
    System.out.println("User Database Loaded...\n" + s_userdb_.GetUserNumbers() + " users' information is loaded");
    s_questiondb_ = QuestionManager.QuestionLoader.LoadQuestion(
        "C:\\Users\\Admin\\Desktop\\xjtlu\\CPT111\\CW3L1\\resources\\questionsBank");
    System.out.println("Question Database Loaded...\n" + s_questiondb_.GetQuestions()
                                                                      .size() + " topics is loaded");
    UI.RegisterFunction();
  } catch (IOException e) {
    logger.Debug(e.toString());
  }
}

public static final class UI {

  private static final String                         splitBar       = "=".repeat(70);
  private static final Map<String, UserOperationHook> function_hooks = new HashMap<>();
  private static       boolean                        loginStatus    = false;
  private static       String                         currentLoginUser;

  public static void RegisterFunction() {
    function_hooks.put("Register User", () -> {
      System.out.println("Input User ID: ");
      var userid = new Scanner(System.in).nextLine();
      System.out.println("Input User's Real Name: ");
      var realname = new Scanner(System.in).nextLine();
      System.out.println("Input User's Passwd: ");
      var passwd = new Scanner(System.in).nextLine();
      try {
        s_userdb_.RegisterUser(new Users(userid, realname, passwd));
      } catch (UserManager.UserExistException | Users.UserInformationInvalidException e) {
        System.err.println(e.getMessage());
        return false;
      }
      return true;
    });
    function_hooks.put("Login", () -> {
      System.out.print("Login <Type User ID>: ");
      var user = new Scanner(System.in).nextLine();
      System.out.print("Enter password here: ");
      var console = System.console();
      var passwd  = console != null ? new String(console.readPassword()) : new Scanner(System.in).nextLine();
      currentLoginUser = s_userdb_.CheckUser(user) ? s_userdb_.GetUserInfo(user)
                                                              .GetUserName() : "";
      return loginStatus = s_userdb_.CheckLogin(user, passwd);
    });
    function_hooks.put("Save Users", () -> {
      try {
        UserManager.UserSaver.Write(UserManager.UserSaver.PortToCSV(s_userdb_), "");
      } catch (IOException e) {
        System.out.println(e.getMessage());
        return false;
      }
      return true;
    });
  }

  /**
   * <p>Print Titles</p>
   */
  public static void PrintTitle() {

    System.out.println(splitBar);
    System.out.println("# Question Management System #| Version: " + ProgramInfo.Version());
    System.out.println(splitBar);
  }

  public static void WelcomePromopt(String user_name) {
    System.out.println("You are welcomed, " + user_name);
  }

  public static void Menu() {
    Map<Integer, UserOperationHook> hookMap = new HashMap<>();
    final Integer[]                 current = {1};

    System.out.println(splitBar);
    System.out.println("Main Menu: Task Selection: ");
    System.out.println(splitBar);

    function_hooks.forEach((x, y) -> {
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
    int     choice  = - 100;
    boolean inputed = false;
    if (input.hasNextInt() && (inputed = true) && hookMap.containsKey(choice = input.nextInt())) {
      if (hookMap.get(choice)
                 .operation()) {
        System.out.println("Execute Successful.");
      }
      while (loginStatus) {
        WelcomePromopt(currentLoginUser);
        QuestionMenu();
      }
    } else if (inputed) {
      System.out.println("Illegal input.");
    } else {
      String choices = input.nextLine();
      if (choices.contains("q")) {
        System.exit(0);
      } else {
        System.out.println("Illegal Input.");
      }
    }
  }

  public static void QuestionMenu() {
    int[]                current  = new int[1];
    Map<Integer, String> topicMap = new HashMap<>();

    System.out.println(splitBar);
    System.out.println("User DashBoard: Question Selection: ");
    System.out.println(splitBar);

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

    System.out.println(splitBar);
    System.out.print("Select an option <Type a integer here, 'q' to exit>: ");
    var     input   = new Scanner(System.in);
    int     choice  = - 100;
    boolean inputed = false;
    if (input.hasNextInt() && (inputed = true) && topicMap.containsKey(choice = input.nextInt())) {
      DifficulitySelection(s_questiondb_.GetQuestions(topicMap.get(choice)));
    } else if (inputed) {
      System.out.println("Illegal input.");
    } else {
      String choices = input.nextLine();
      if (choices.contains("q")) {
        loginStatus = false;
        return;
      } else {
        System.out.println("Illegal Input.");
      }
    }

  }

  public static void DifficulitySelection(Map<Difficulty, List<Question>> question) {
    int[]                    current       = new int[] {1};
    Map<Integer, Difficulty> difficuityMap = new HashMap<>();

    System.out.println(splitBar);
    System.out.println("Difficulty Selection: ");
    System.out.println(splitBar);

    question.forEach((x, y) -> {
      System.out.println(" " + current[0] + ") " + x);
      difficuityMap.put(current[0]++, x);
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
    int     choice  = - 100;
    boolean inputed = false;
    if (input.hasNextInt() && (inputed = true) && difficuityMap.containsKey(choice = input.nextInt())) {
      // TODO:
    } else if (inputed) {
      System.out.println("Illegal input.");
    } else {
      String choices = input.nextLine();
      if (choices.contains("q")) {
        return;
      } else {
        System.out.println("Illegal Input.");
      }
    }

  }

  private interface UserOperationHook {
    boolean operation();
  }
}
}
