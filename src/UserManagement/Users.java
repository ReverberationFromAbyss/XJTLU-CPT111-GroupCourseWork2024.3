package UserManagement;

public final class Users {

private final String m_userID_;
private final String m_userName_;
private final String m_passwd_;

public Users(String ID, String name, String passwd) {
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

}
