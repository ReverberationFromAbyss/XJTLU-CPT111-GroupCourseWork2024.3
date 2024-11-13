package UserManagement;

import Utils.CSVUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public final class UserManager {

private final Map<String, Users> m_users_ = new HashMap<>();

public void RegisterUser(Users usr) throws UserExistException, Users.UserInformationInvalidException {
  m_users_.forEach((x, y) -> {
    if (usr.GetUserID()
           .equals(y.GetUserID())) {
      throw new UserExistException();
    }
  });
  m_users_.put(usr.GetUserID(), usr);
}

public boolean CheckLogin(String id, String passwd) {
  return m_users_.containsKey(id) && m_users_.get(id)
                                             .GetPasswd()
                                             .equals(passwd);
}

public boolean CheckUser(String id) {
  return m_users_.containsKey(id);
}

public Users GetUserInfo(String id) {
  return m_users_.get(id);
}

public int GetUserNumbers() {
  return m_users_.size();
}

public static class UserLoader {

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
      userManager.RegisterUser(new Users(l.get(0), l.get(1), l.get(2)));
    }
    inputStream.close();
    return userManager;
  }

}

public static class UserSaver {

  public static CSVUtils PortToCSV(UserManager uset) {
    var csv = new CSVUtils();
    for (var u : uset.m_users_.values()) {
      csv.InsertLine();
      csv.InsertElement(csv.GetCSV()
                           .size() - 1, u.GetUserID());
      csv.InsertElement(csv.GetCSV()
                           .size() - 1, u.GetUserName());
      csv.InsertElement(csv.GetCSV()
                           .size() - 1, u.GetPasswd());
    }
    return csv;
  }

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
