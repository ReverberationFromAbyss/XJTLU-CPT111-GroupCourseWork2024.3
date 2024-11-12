import java.io.FileInputStream;
import java.io.IOException;

public class Main {

/**
 * <p>Main function, program starts here</p>
 *
 * @param args
 */
public static void main(String[] args) {
  mainloop();
}

/**
 * <p>Main loop of program, call after main</p>
 * <p>Accept command switches values</p>
 */
private static void mainloop() {
  StringBuilder stringBuilder = new StringBuilder();
  try (var fileInputStream = new FileInputStream("C:/Users/Admin/Desktop/xjtlu/CPT111/CW3L1/resources/users.csv")) {
    byte c;
    while ((c = (byte)fileInputStream.read()) != - 1) {
      stringBuilder.append((char)c);
    }
  } catch (IOException e) {

  }

}
}
