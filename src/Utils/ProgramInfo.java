package Utils;

public class ProgramInfo {
  private final static int MAJOR_VERSION = 0;
  private final static int MINOR_VERSION = 0;
  private final static int PATCH_VERSION = 0;

  private static String ExtraDescription = "";

  public static String Version() {
    return MAJOR_VERSION + "." + MINOR_VERSION + "." + PATCH_VERSION +
           (ExtraDescription.isEmpty() ? "" : "-" + ExtraDescription);
  }

  public static void SetExtraDescription(String description) {
    ExtraDescription = description;
  }
}
