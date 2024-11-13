package Utils;

import java.io.*;
import java.util.Date;

public class PrivateLogger {
private static PrivateLogger  s_Private_logger_instance_;
private        BufferedWriter m_logfileStream_;
private final  TARGET         m_target_;

private PrivateLogger() {
  m_target_        = TARGET.CONSOLE;
  m_logfileStream_ = new BufferedWriter(new OutputStreamWriter(System.out));
}

private PrivateLogger(String fp) {
  m_target_ = TARGET.FILE;
  try {
    File file = new File(fp);
    if (file.isFile() || ! file.exists()) {
      m_logfileStream_ = new BufferedWriter(new FileWriter(fp));
    }
  } catch (IOException e) {
    System.err.println(e.getMessage());
  }
}

private PrivateLogger(OutputStreamWriter outputStream) {
  m_target_        = TARGET.STREAM;
  m_logfileStream_ = new BufferedWriter(outputStream);
}

public static PrivateLogger CreateInstance() {
  return (s_Private_logger_instance_ =
          s_Private_logger_instance_ == null ? new PrivateLogger() : s_Private_logger_instance_);
}

public static PrivateLogger CreateInstance(String fp) {
  return (s_Private_logger_instance_ =
          s_Private_logger_instance_ == null ? new PrivateLogger(fp) : s_Private_logger_instance_);
}

public static PrivateLogger CreateInstance(OutputStreamWriter w) {
  return (s_Private_logger_instance_ =
          s_Private_logger_instance_ == null ? new PrivateLogger(w) : s_Private_logger_instance_);
}

public static PrivateLogger GetInstance() throws NullPointerException {
  if (s_Private_logger_instance_ == null) {
    throw new NullPointerException();
  }
  return s_Private_logger_instance_;
}

public static void DeleteInstance() {
  s_Private_logger_instance_ = null;
}

public static PrivateLogger ResetInstance() {
  return (s_Private_logger_instance_ = new PrivateLogger());
}

public boolean Debug(String s) {
  boolean success = true;
  try {
    if (TARGET.CONSOLE == m_target_) {
      m_logfileStream_.write(
          Colours.GetColour(Colours.GREEN, Colours.WHITE, Colours.BLOD) + "[DEBUG:" + new Date() + "] " +
          Colours.ResetColour());
    }
    m_logfileStream_.write(s);
    m_logfileStream_.flush();
  } catch (IOException e) {
    success = false;
    System.err.println(e);
  }
  return success;
}

private enum TARGET { CONSOLE, FILE, STREAM }

private static class Colours {
  public static final  String WHITE             = "30";
  public static final  String RED               = "31";
  public static final  String GREEN             = "32";
  public static final  String YELLOW            = "33";
  public static final  String BLUE              = "34";
  public static final  String PURPLE            = "35";
  public static final  String CYAN              = "36";
  public static final  String GRAY              = "37";
  public static final  String BACKGROUND_WHITE  = "40";
  public static final  String BACKGROUND_RED    = "41";
  public static final  String BACKGROUND_GREEN  = "42";
  public static final  String BACKGROUND_YELLOW = "43";
  public static final  String BACKGROUND_BLUE   = "44";
  public static final  String BACKGROUND_PURPLE = "45";
  public static final  String BACKGROUND_CYAN   = "46";
  public static final  String BACKGROUND_GRAY   = "47";
  public static final  String ENHANCED_WHITE    = "90";
  public static final  String ENHANCED_RED      = "91";
  public static final  String ENHANCED_GREEN    = "92";
  public static final  String ENHANCED_YELLOW   = "93";
  public static final  String ENHANCED_BLUE     = "94";
  public static final  String ENHANCED_PURPLE   = "95";
  public static final  String ENHANCED_CYAN     = "96";
  public static final  String ENHANCED_GRAY     = "97";
  public static final  String EMPTY             = "0";
  public static final  String BLOD              = "1";
  public static final  String UNDERLINED        = "4";
  public static final  String NEGATIVE          = "7";
  private static final String ESCAPE            = "\u001B[";
  private static final String COLOUR            = "m";
  private static final String RESET             = "\u001b[0m";

  public static String GetColour(String Foreground, String Background, String Format) {
    return ESCAPE + Foreground + ";" + Background + ";" + Format + COLOUR;
  }

  public static String ResetColour() {
    return RESET;
  }
}

}
