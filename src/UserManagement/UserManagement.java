package UserManagement;

import java.util.HashMap;
import java.util.Map;

public final class UserManagement {

private Map<String, Users> m_users_ = new HashMap<>();

public void RegisterUser(Users usr) throws UserExistException {
  m_users_.forEach((x, y) -> {
    if (usr.GetUserID()
           .equals(y.GetUserID())) {
      throw new UserExistException();
    }
  });
  m_users_.put(usr.GetUserID(), usr);
}

public boolean CheckLogin(String id, String passwd) {
  return m_users_.get(id)
                 .GetPasswd()
                 .equals(passwd);
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
