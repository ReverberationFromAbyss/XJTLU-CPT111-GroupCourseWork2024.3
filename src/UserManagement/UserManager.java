package UserManagement;

import Utils.CSVUtils;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class UserManager {

/**
 * Get a list of all users.
 *
 * @return a list of all users.
 */
public List<Users> GetUsers() {
  return m_users_.values()
                 .stream()
                 .toList();
}

private final Map<String, Users> m_users_ = new HashMap<>();

/**
 * Registers a new user.
 *
 * @param usr the user to register.
 * @throws UserExistException if the user already exists.
 * @throws Users.UserInformationInvalidException if the user information is invalid.
 */
public void RegisterUser(Users usr) throws UserExistException, Users.UserInformationInvalidException {
  if (usr.GetUserID()
         .isEmpty()) {
    throw new Users.UserInformationInvalidException("User ID cannot be empty.");
  }
  if (usr.GetUserName()
         .isEmpty()) {
    throw new Users.UserInformationInvalidException("User Name cannot be empty.");
  }
  if (usr.GetPasswd()
         .isEmpty()) {
    throw new Users.UserInformationInvalidException("User Password cannot be empty.");
  }
  m_users_.forEach((x, y) -> {
    if (usr.GetUserID()
           .equals(y.GetUserID())) {
      throw new UserExistException();
    }
  });
  m_users_.put(usr.GetUserID(), usr);
}

/**
 * Checks if the login credentials are valid.
 *
 * @param id the user ID.
 * @param passwd the user password.
 * @return true if the credentials are valid, false otherwise.
 */
public boolean CheckLogin(String id, String passwd) {
  return m_users_.containsKey(id) && m_users_.get(id)
                                             .GetPasswd()
                                             .equals(passwd);
}

/**
 * Checks if a user exists.
 *
 * @param id the user ID.
 * @return true if the user exists, false otherwise.
 */
public boolean CheckUser(String id) {
  return m_users_.containsKey(id);
}

/**
 * Get user information.
 *
 * @param id the user ID.
 * @return the user information.
 */
public Users GetUserInfo(String id) {
  return m_users_.get(id);
}

public static final class Users {

  private final String             m_userID_;
  private final String             m_userName_;
  private final String             m_passwd_;
  private final Map<String, int[]> m_scoreRecord_ = new HashMap<>();

  /**
   * Constructs a new 'Users' object.
   *
   * @param ID the user ID.
   * @param name the user name.
   * @param passwd the user password.
   * @throws UserInformationInvalidException if any user information is invalid.
   */
  public Users(String ID, String name, String passwd) throws UserInformationInvalidException {
    if (ID.isEmpty()) {
      throw new UserInformationInvalidException("User ID cannot be empty.");
    }
    if (name.isEmpty()) {
      throw new UserInformationInvalidException("User name cannot be empty.");
    }
    if (passwd.isEmpty()) {
      throw new UserInformationInvalidException("User passwd cannot be empty.");
    }
    m_userID_   = ID;
    m_userName_ = name;
    m_passwd_   = passwd;
  }

  public String GetUserID() {
    return m_userID_;
  }

  public String GetUserName() {
    return m_userName_;
  }

  public String GetPasswd() {
    return m_passwd_;
  }

  /**
   * Adds a score record for a specific topic.
   *
   * @param topic the topic.
   * @param score the score.
   * @return the updated Users.
   * @throws IllegalScoreException if the score is invalid.
   */
  public Users AddRecord(String topic, int score) throws IllegalScoreException {
    if (! m_scoreRecord_.containsKey(topic)) {
      m_scoreRecord_.put(topic, new int[] {Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE});
    }
    if (score < 0) {
      throw new IllegalScoreException();
    }
    m_scoreRecord_.get(topic)[2] = m_scoreRecord_.get(topic)[1];
    m_scoreRecord_.get(topic)[1] = m_scoreRecord_.get(topic)[0];
    m_scoreRecord_.get(topic)[0] = score;
    return this;
  }

  /**
   * Get the score records.
   *
   * @return a map of score records.
   */
  public Map<String, int[]> GetScoreRecord() {
    return m_scoreRecord_;
  }

  /**
   * Exception thrown when user information is invalid.
   */
  public static class UserInformationInvalidException
      extends RuntimeException {
    UserInformationInvalidException() {
      super();
    }

    UserInformationInvalidException(String msg) {
      super(msg);
    }
  }

  /**
   * Exception thrown when a score is invalid.
   */
  public static class IllegalScoreException
      extends RuntimeException {
    IllegalScoreException() {
      super();
    }

    IllegalScoreException(String msg) {
      super(msg);
    }
  }

}

public int GetUserNumbers() {
  return m_users_.size();
}

/**
 * load user information and records from files.
 */
public static class UserLoader {

  /**
   * Loads user information from a file.
   *
   * @param fp the file path.
   * @return 'UserManager' with loaded user information.
   * @throws IOException if an I/O error occurs.
   */
  public static UserManager LoadUserInfo(String fp) throws IOException {
    File            file        = new File(fp);
    byte[]          content     = new byte[(int)file.length()];
    FileInputStream inputStream = new FileInputStream(file);
    int             v           = inputStream.read(content);
    String          s           = new String(content);
    var             csv         = CSVUtils.ReadCSV.ConstructCSV(s);
    UserManager     userManager = new UserManager();
    for (var l : csv.GetCSV()) {
      if (l.isEmpty()) {
        break;
      }
      else if (l.size() < 3) {
        continue;
      }
      userManager.RegisterUser(new Users(l.get(0), l.get(1), l.get(2)));
    }
    inputStream.close();
    return userManager;
  }

  /**
   * Loads user score records from a file.
   *
   * @param usert the 'UserManager' object to load records into.
   * @param fp the file path.
   * @return the 'UserManager' object with loaded score records.
   * @throws IOException if an I/O error occurs.
   */
  public static UserManager LoadUserRecord(UserManager usert, String fp) throws IOException {
    File            file        = new File(fp);
    byte[]          content     = new byte[(int)file.length()];
    FileInputStream inputStream = new FileInputStream(file);
    int             v           = inputStream.read(content);
    String          s           = new String(content);
    var             csv         = CSVUtils.ReadCSV.ConstructCSV(s);
    for (var l : csv.GetCSV()) {
      if (l.isEmpty()) {
        break;
      }
      if (usert.m_users_.containsKey(l.get(0))) {
        usert.m_users_.get(l.get(0))
                      .AddRecord(l.get(1), l.get(2)
                                            .isEmpty() ? 0 : Integer.parseInt(l.get(2)))
                      .AddRecord(l.get(1), l.get(3)
                                            .isEmpty() ? 0 : Integer.parseInt(l.get(3)))
                      .AddRecord(l.get(1), l.get(4)
                                            .isEmpty() ? 0 : Integer.parseInt(l.get(4)));
      }
    }
    inputStream.close();
    return usert;
  }

}

/**
 * save user information and records to files.
 */
public static class UserSaver {

  /**
   * Converts user information to CSV format.
   *
   * @param uset the 'UserManager' object.
   * @return the 'CSVUtils' object containing user information.
   */
  public static CSVUtils PortToCSV(UserManager uset) {
    var csv = new CSVUtils();
    for (var u : uset.m_users_.values()) {
      csv.InsertLine()
         .InsertElement(csv.GetCSV()
                           .size() - 1, u.GetUserID())
         .InsertElement(csv.GetCSV()
                           .size() - 1, u.GetUserName())
         .InsertElement(csv.GetCSV()
                           .size() - 1, u.GetPasswd())
         .DetCols();
    }
    return csv;
  }

  /**
   * Converts user score records to CSV format.
   *
   * @param usert the 'UserManager' object.
   * @return the 'CSVUtils' object containing user score records.
   */
  public static CSVUtils ScoreToCSV(UserManager usert) {
    var csv = new CSVUtils();
    for (var u : usert.m_users_.values()) {
      u.GetScoreRecord()
       .forEach((x, y) -> {
         csv.InsertLine()
            .InsertElement(csv.GetCSV()
                              .size() - 1, u.GetUserID())
            .InsertElement(csv.GetCSV()
                              .size() - 1, x)
            .InsertElement(csv.GetCSV()
                              .size() - 1, y[0] == Integer.MIN_VALUE ? "" : Integer.toString(y[0]))
            .InsertElement(csv.GetCSV()
                              .size() - 1, y[1] == Integer.MIN_VALUE ? "" : Integer.toString(y[1]))
            .InsertElement(csv.GetCSV()
                              .size() - 1, y[2] == Integer.MIN_VALUE ? "" : Integer.toString(y[2]))
            .DetCols();
       });
    }
    return csv;
  }

  /**
   * Writes CSV content to a file.
   *
   * @param csvUtils the 'CSVUtils' object containing CSV content.
   * @param fp the file path.
   * @throws IOException if an I/O error occurs.
   */
  public static void Write(CSVUtils csvUtils, String fp) throws IOException {
    File file = new File(fp);
    if (file.isDirectory()) {
      throw new IOException("Cannot be written");
    }
    try (var outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file))) {
      outputStreamWriter.write(CSVUtils.PortCSV.GenerateContent(csvUtils));
      outputStreamWriter.flush();
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }

  }


}

/**
 * Exception thrown when a user already exists.
 */
public static class UserExistException
    extends RuntimeException {
  public UserExistException() {
    super();
  }

  public UserExistException(String s) {
    super(s);
  }
}

}
