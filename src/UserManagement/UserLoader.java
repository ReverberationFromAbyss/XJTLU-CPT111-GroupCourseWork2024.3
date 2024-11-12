package UserManagement;

import Utils.CSVUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class UserLoader {

public static UserManagement LoadUserInfo(String fp) throws IOException {
  File            file           = new File(fp);
  byte[]          content        = new byte[(int)file.length()];
  FileInputStream inputStream    = new FileInputStream(file);
  int             v              = inputStream.read(content);
  String          s              = new String(content);
  var             csv            = CSVUtils.ReadCSV.ConstructCSV(s);
  UserManagement  userManagement = new UserManagement();
  for (var l : csv.GetCSV()) {
    if (l.isEmpty()) {
      break;
    }
    userManagement.RegisterUser(new Users(l.get(0), l.get(1), l.get(2)));
  }
  inputStream.close();
  return userManagement;
}

}
