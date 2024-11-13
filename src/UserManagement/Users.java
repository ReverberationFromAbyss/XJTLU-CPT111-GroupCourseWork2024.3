package UserManagement;

public final class Users {

private final String m_userID_;
private final String m_userName_;
private final String m_passwd_;

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

public static class UserInformationInvalidException
    extends RuntimeException {
  UserInformationInvalidException() {
    super();
  }

  UserInformationInvalidException(String msg) {
    super(msg);
  }
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

}
