package Utils;

//public class CSVUtils {
//
///**
// * CSV Format illegal
// */
//public static class IllegalSyntaxException
//    extends RuntimeException {
//  IllegalSyntaxException() {
//  }
//}
//
//protected String                         m_string_;
//protected LinkedList<LinkedList<String>> m_table_;
//
///**
// * Get Next Token From the CSV File
// *
// * @return Next Token, empty if reach the end of file
// * @throws IllegalSyntaxException If next token read in src is illegal
// */
//protected String NextToken() throws CSVUtils.IllegalSyntaxException {
//  StringBuilder stringBuilder      = new StringBuilder(); // Return
//  int           currentCharPos;                           // Store where we are processing, offset to begin of restString
//  boolean       startWithQuotation = false;               // if start with Quotation
//  boolean       hasQuotation       = false;               // if last character is Quotation
//
//  loop:
//  for (currentCharPos = 0; currentCharPos < m_string_.length(); currentCharPos++) {
//    char c = m_string_.charAt(currentCharPos);
//    switch (c) {
//      case ' ': default:
//        if (startWithQuotation && hasQuotation) {
//          throw new CSVUtils.IllegalSyntaxException();
//        }
//        stringBuilder.append(c);
//        break;
//      case '"':
//        if (currentCharPos == 0) {
//          startWithQuotation = true;
//          hasQuotation       = false;
//          continue;
//        }
//        if (startWithQuotation) {
//          hasQuotation = ! hasQuotation;
//          if (hasQuotation) {
//            continue;
//          }
//        }
//        stringBuilder.append(c);
//        break;
//      case ',': case '\r': case '\n':
//        if (currentCharPos == 0) {
//          stringBuilder.append(c);
//          currentCharPos++;
//          break loop;
//        }
//        if (! startWithQuotation || hasQuotation) {
//          // Using short-circuit logic to present (!startWithQuotation) || (startWithQuotation && hasQuotation)
//          break loop; // End the current token parsing
//        }
//        stringBuilder.append(c);
//        break;
//    }
//  }
//  m_string_ = m_string_.substring(currentCharPos);
//  return stringBuilder.toString();
//}
//
///**
// * Parse CSV string provided with
// *
// * @param src Input String That prepared to be processed
// * @return Processed CSV, all in String, Can be then put into an instance
// */
//public static CSVUtils ParseCSV(String src) {
//  String v;
//  var    csv = new CSVUtils(src);
//  csv.m_table_ = new LinkedList<LinkedList<String>>();
//  csv.m_table_.add(new LinkedList<>());
//  int     rows        = 0;
//  boolean lastSplitor = false;
//  while (! (v = csv.NextToken()).isEmpty()) {
//    char c = v.charAt(0);
//    switch (c) {
//      case '\n': case '\r': case ',':
//        if (lastSplitor) {
//          csv.m_table_.get(rows)
//                      .add("");
//        }
//        if (c != ',') {
//          csv.m_table_.add(new LinkedList<>());
//          rows++;
//        }
//        lastSplitor = true;
//        break;
//      default:
//        csv.m_table_.get(rows)
//                    .add(v);
//        lastSplitor = false;
//        break;
//    }
//  }
//  return csv;
//}
//
//public String PortToString() {
//  StringBuilder stringBuilder = new StringBuilder();
//  for (var l : m_table_) {
//    for (var e : l) {
//      String c = "\"" + e.replace("\"", "\"\"") + "\"";
//      stringBuilder.append(c);
//    }
//    stringBuilder.append('\n');
//  }
//  return stringBuilder.toString();
//}
//
//protected CSVUtils() {
//  m_string_ = "";
//}
//
//protected CSVUtils(String src) {
//  m_string_ = src;
//}
//
//}


public class CSVUtils {

public static class IllegalSyntaxException
    extends ArithmeticException {
  /**
   * Constructs an {@code ArithmeticException} with no detail
   * message.
   */
  public IllegalSyntaxException() {
    super();
  }

  /**
   * Constructs an {@code ArithmeticException} with the specified
   * detail message.
   *
   * @param s the detail message.
   */
  public IllegalSyntaxException(String s) {
    super(s);
  }
}

/**
 * Get Next Token From source
 *
 * @param src Source String to be processed
 * @return Next token
 */
public static String NextToken(String src) throws IllegalSyntaxException {
  StringBuilder stringBuilder      = new StringBuilder();
  boolean       startWithQuotation = false;
  boolean       hasQuotation       = false;

  loop:
  for (int currentPosition = 0; ! src.isEmpty() && currentPosition < src.length(); currentPosition++) {
    Character c = src.charAt(currentPosition);
    switch (c) {
      case ',': case '\n': case '\r':
        if (0 == currentPosition) {
          stringBuilder.append(c);
        }
        if (! startWithQuotation || hasQuotation) {
          break loop;
        }
      case '"':
        if (0 == currentPosition && '"' == c) {
          startWithQuotation = true;
        }
        hasQuotation = ('"' == c) && (! (currentPosition == 0));
      case ' ': default:
        if (startWithQuotation && hasQuotation && c != '"') {
          throw new IllegalSyntaxException();
        }
        stringBuilder.append(c);
    }
  }
  return stringBuilder.toString();
}

/**
 * @param token Token to be parse
 * @return parsed token
 */
public static String ParseToken(String token) throws IllegalSyntaxException {
  StringBuilder stringBuilder      = new StringBuilder();
  boolean       startWithQuotation = false;
  boolean       hasQuotation       = false;

  loop:
  for (int currentPosition = 0; ! token.isEmpty() && currentPosition < token.length(); currentPosition++) {
    Character c = token.charAt(currentPosition);
    switch (c) {
      case ' ': default:
        if (startWithQuotation && hasQuotation) {
          throw new CSVUtils.IllegalSyntaxException();
        }
        stringBuilder.append(c);
        break;
      case '"':
        if (currentPosition == 0) {
          startWithQuotation = true;
          hasQuotation       = false;
          continue;
        }
        if (startWithQuotation) {
          hasQuotation = ! hasQuotation;
          if (hasQuotation) {
            continue;
          }
        }
        stringBuilder.append(c);
        break;
      case ',': case '\r': case '\n':
        if (currentPosition == 0) {
          stringBuilder.append(c);
          break loop;
        }
        if (! startWithQuotation || hasQuotation) {
          // Using short-circuit logic to present (!startWithQuotation) || (startWithQuotation && hasQuotation)
          break loop; // End the current token parsing
        }
        stringBuilder.append(c);
        break;
    }
  }
  if (startWithQuotation && ! hasQuotation) {
    throw new IllegalSyntaxException();
  }
  return stringBuilder.toString();
}

/**
 * @param src
 * @return
 */
public static String GenerateToken(String src) {
  StringBuilder stringBuilder = new StringBuilder();
  return stringBuilder.toString();
}

/**
 * @param src
 * @return
 */
public static CSVUtils ConstructCSV(String src) {
  CSVUtils csvUtils = new CSVUtils();
  return csvUtils;
}

/**
 * @param csv
 * @return
 */
public static String GenerateContent(CSVUtils csv) {
  StringBuilder stringBuilder = new StringBuilder();
  return stringBuilder.toString();
}

}
