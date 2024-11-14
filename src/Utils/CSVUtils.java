package Utils;

import java.util.ArrayList;
import java.util.LinkedList;

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

protected LinkedList<ArrayList<String>> m_table_    = new LinkedList<>();
protected boolean                       m_hasTitle_ = false;
protected int                           m_rows_     = 0;
protected int                           m_cols_     = 0;

public LinkedList<ArrayList<String>> GetCSV() {
  return m_table_;
}

public CSVUtils SetTitle(String... title) {
  m_hasTitle_ = true;
  m_cols_     = title.length;
  m_table_.add(0, new ArrayList<>(m_cols_));
  m_rows_++;
  for (var t : title) {
    m_table_.get(0)
            .add(t);
  }
  return this;
}

public CSVUtils RemoveTitle() {
  if (m_hasTitle_) {
    m_table_.remove(0);
  }
  return this;
}

public String GetElement(int row, int col) {
  return m_table_.get(row)
                 .get(col);
}

public CSVUtils InsertLine() {
  m_table_.add(new ArrayList<>(m_cols_));
  m_rows_++;
  return this;
}

public CSVUtils InsertLine(int row) {
  m_table_.add(row, new ArrayList<>(m_cols_));
  m_rows_++;
  return this;
}

public CSVUtils InsertElement(int row, String val) {
  m_table_.get(row)
          .add(val);
  return this;
}

public CSVUtils SetLine(int ros, ArrayList<String> val) {
  m_table_.set(ros, val);
  return this;
}

public CSVUtils SetElement(int row, int col, String val) {
  m_table_.get(row)
          .set(col, val);
  return this;
}

public String ClearElement(int row, int col) {
  return m_table_.get(row)
                 .set(col, "");
}

public ArrayList<String> RemoveLine(int row) {
  m_rows_--;
  return m_table_.remove(row);
}

public CSVUtils DetCols() {
  if (m_hasTitle_) {
    m_cols_ = m_table_.get(0)
                      .size();
  } else {
    for (var l : m_table_) {
      m_cols_ = Math.max(m_cols_, l.size());
    }
  }
  return this;
}

public CSVUtils ReSetCols() {
  return this;
}

public static class ReadCSV {

  /**
   * Get Next Token From source
   *
   * @param src Source String to be processed
   * @return Next token, emtpy if reaches end of string
   * @throws IllegalSyntaxException If the source is not in a valid csv format
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
   * Parse Token to Final String
   *
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
   * Parse Source String into CSVUtils object
   *
   * @param src Source String to be processed
   * @return a csv object to be manipulated
   */
  public static CSVUtils ConstructCSV(String src) {
    CSVUtils csvUtils = new CSVUtils();
    csvUtils.m_table_    = new LinkedList<>();
    csvUtils.m_hasTitle_ = false;
    boolean lastSplitor = false;
    csvUtils.m_table_.add(new ArrayList<>());
    String token;
    char   c = '\0';
    for (int rows = 0, cols = 0, maxcols = Integer.MIN_VALUE; ! (token = NextToken(src)).isEmpty(); ) {
      c = token.charAt(0);
      switch (c) {
        case '\n': case '\r':
          csvUtils.m_table_.add(new ArrayList<>());
          maxcols = Math.max(maxcols, cols);
          cols = 0;
          rows++;
        case ',':
          if (lastSplitor) {
            csvUtils.m_table_.get(rows - (c != ',' ? 1 : 0))
                             .add("");
            cols++;
          }
          lastSplitor = true;
          break;
        default:
          csvUtils.m_table_.get(rows)
                           .add(ParseToken(token));
          cols++;
          lastSplitor = false;
          break;
      }
      src = src.substring(token.length());
    }
    if (c == '\n' || c == '\r') {
      csvUtils.m_table_.removeLast();
    }
    csvUtils.DetCols().m_rows_ = csvUtils.m_table_.size();
    return csvUtils;
  }

  public static CSVUtils ConstructCSV(String src, boolean hasTitle) {
    CSVUtils csvUtils = new CSVUtils();
    csvUtils.m_table_    = new LinkedList<>();
    csvUtils.m_hasTitle_ = hasTitle;
    boolean lastSplitor = false;
    csvUtils.m_table_.add(new ArrayList<>());
    String token;
    char   c = '\0';
    for (int rows = 0, cols = 0, maxcols = Integer.MIN_VALUE; ! (token = NextToken(src)).isEmpty(); ) {
      c = token.charAt(0);
      switch (c) {
        case '\n': case '\r':
          csvUtils.m_table_.add(new ArrayList<>());
          maxcols = Math.max(maxcols, cols);
          cols = 0;
          rows++;
        case ',':
          if (lastSplitor) {
            csvUtils.m_table_.get(rows - (c != ',' ? 1 : 0))
                             .add("");
            cols++;
          }
          lastSplitor = true;
          break;
        default:
          csvUtils.m_table_.get(rows)
                           .add(ParseToken(token));
          cols++;
          lastSplitor = false;
          break;
      }
      src = src.substring(token.length());
    }
    if (c == '\n' || c == '\r') {
      csvUtils.m_table_.removeLast();
    }
    csvUtils.DetCols().m_rows_ = csvUtils.m_table_.size();
    return csvUtils;
  }

}

public static class PortCSV {

  /**
   * Convert a string into a valid csv element token
   *
   * @param src string to be converted
   * @return A token
   */
  public static String GenerateToken(String src) {
    StringBuilder stringBuilder = new StringBuilder().append('"');
    for (int i = 0; ! src.isEmpty() && i < src.length(); i++) {
      char c = src.charAt(i);
      if (c == '"') {
        stringBuilder.append("\"");
      }
      stringBuilder.append(c);
    }
    return stringBuilder.append('"')
                        .toString();
  }

  /**
   * Convert whole csv object into text
   *
   * @param csv csv to be ported
   * @return formatted text
   */
  public static String GenerateContent(CSVUtils csv) {
    StringBuilder stringBuilder = new StringBuilder();
    for (var l : csv.m_table_) {
      for (int i = 0; i < l.size(); i++) {
        stringBuilder.append(l.get(i));
        stringBuilder.append(i < l.size() - 1 ? "," : "");
      }
      stringBuilder.append('\n');
    }
    return stringBuilder.toString();
  }
}

}
