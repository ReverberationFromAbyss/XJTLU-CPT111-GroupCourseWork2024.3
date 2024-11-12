package Utils;

import junit.framework.TestCase;
import org.junit.Test;

public class CSVUtilsTest
    extends TestCase {

private static final String testString = "Name,rbq,12345\n" + "\"St\nupid\",s\"s,\"5,4321\"\n" + "\"\"\"\",,\n";

@Test
public void NextToken() {
  //String   string   = "Name,rbq,12345\n" + "\"St\nupid\",s\"s,\"5,4321\"\n" + "\"\"\"\",,\n";
  //CSVUtils csvUtils = new CSVUtils(string);
  //String   v;
  //String[] testcases = {
  //    "Name", ",", "rbq", ",", "12345", "\n", "St\nupid", ",", "s\"s", ",", "5,4321", "\n", "\"", ",", ",", "\n"
  //};

  //for (int i = 0; ! (v = csvUtils.NextToken()).isEmpty() && i < testcases.length; i++) {
  //  assertEquals(testcases[i], v);
  //}
}

@Test
public void testParse() {
  String string = "Name,rbq,12345\n" + "\"St\nupid\",s\"s,\"5,4321\"\n" + "\"\"\"\",,\n";
}

@Test
public void testNextToken() {
  String s = testString;
  String[] testCases = {
      "Name",
      ",",
      "rbq",
      ",",
      "12345",
      "\n",
      "\"St\nupid\"",
      ",",
      "s\"s",
      ",",
      "\"5,4321\"",
      "\n",
      "\"\"\"\"",
      ",",
      ",",
      "\n"
  };

  String r;
  for (int i = 0; ! (r = CSVUtils.ReadCSV.NextToken(s)).isEmpty() && i < testCases.length; i++) {
    assertEquals(testCases[i], r);
    s = s.substring(r.length());
  }
}

@Test
public void testParseToken() {
  String s = testString + ",\"\"";
  String[] testCases = {
      "Name",
      ",",
      "rbq",
      ",",
      "12345",
      "\n",
      "St\nupid",
      ",",
      "s\"s",
      ",",
      "5,4321",
      "\n",
      "\"",
      ",",
      ",",
      "\n",
      ",",
      ""
  };

  String r;
  for (int i = 0; ! (r = CSVUtils.ReadCSV.NextToken(s)).isEmpty() && i < testCases.length; i++) {
    assertEquals(testCases[i], CSVUtils.ReadCSV.ParseToken(r));
    s = s.substring(r.length());
  }
}

@Test
public void testGenerateToken() {
  assertEquals("\"Student\"", CSVUtils.PortCSV.GenerateToken("Student"));
  assertEquals("\",\"", CSVUtils.PortCSV.GenerateToken(","));
  assertEquals("\"\"\"\"", CSVUtils.PortCSV.GenerateToken("\""));
  assertEquals("\"12,\"", CSVUtils.PortCSV.GenerateToken("12,"));
  assertEquals("\"\"\"\"\"\"", CSVUtils.PortCSV.GenerateToken("\"\""));

}

@Test
public void testConstructCSV() {
  var v = CSVUtils.ReadCSV.ConstructCSV(testString);
}

@Test
public void generateContent() {
}
}