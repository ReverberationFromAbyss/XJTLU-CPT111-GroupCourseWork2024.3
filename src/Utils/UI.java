package Utils;

public class UI {
  /**
   * <p>Print Titles</p>
   */
  public static void PrintTitle() {
    final String splitBar = "=".repeat(70);

    System.out.println(splitBar);
    System.out.println("# Question Management System #| Version: " + ProgramInfo.Version());
    System.out.println(splitBar);
  }

}
