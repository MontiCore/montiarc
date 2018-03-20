package infrastructure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;

public class ExpectedErrorInfo {
  private static Pattern ERROR_CODE_PATTERN = Pattern.compile("xMA[0-9]{3}");
  
  private int numExpectedFindings = 0;
  
  private HashSet<String> expectedErrorCodes = new HashSet<>();
  
  private Predicate<String> containsExpectedErrorCode;
  
  /**
   * Only use for checking of non MontiArc Errors!
   * 
   * @param eRROR_CODE_PATTERN the eRROR_CODE_PATTERN to set
   */
  public static void setERROR_CODE_PATTERN(Pattern eRROR_CODE_PATTERN) {
    ERROR_CODE_PATTERN = eRROR_CODE_PATTERN;
  }
  
  public static void reset() {
    ERROR_CODE_PATTERN = Pattern.compile("xMA[0-9]{3}");
  }
  
  /**
   * Raises an error if the given error codes don't match the convention for error codes in test
   * cases (no leading zero, capital hexadecimal digits)
   */
  protected static void checkExpectedErrorCodes(String[] errorCodes) {
    
    for (String errorCode : errorCodes) {
      if (!ERROR_CODE_PATTERN.matcher(errorCode).matches()) {
        Log.error(String.format(
            "The given expected error code \"%s\" is not a valid error code (pattern: \"%s\")",
            errorCode, ERROR_CODE_PATTERN.pattern()));
      }
    }
  }
  
  protected static Set<String> collectErrorCodes(String findings) {
    Matcher matcher = ERROR_CODE_PATTERN.matcher(findings);
    
    Set<String> errorCodes = new HashSet<>();
    while (matcher.find()) {
      errorCodes.add(matcher.group());
    }
    
    return errorCodes;
  }
  
  private void initContainsExpectedErrorCode() {
    containsExpectedErrorCode = new Predicate<String>() {
      
      @Override
      public boolean test(String s) {
        for (String errorCode : expectedErrorCodes) {
          if (s.contains(errorCode)) {
            return true;
          }
        }
        
        return false;
      }
    };
  }
  
  public ExpectedErrorInfo() {
    this(0);
  }
  
  public ExpectedErrorInfo(int numExpectedFindings, String... expectedErrorCodes) {
    checkExpectedErrorCodes(expectedErrorCodes);
    
    this.numExpectedFindings = numExpectedFindings;
    this.expectedErrorCodes.addAll(Arrays.asList(expectedErrorCodes));
    
    initContainsExpectedErrorCode();
  }
  
  private String concatenateFindings(List<Finding> findings) {
    return findings.stream().map(f -> f.buildMsg())
        .collect(Collectors.joining("\n"));
  }
  
  public void checkExpectedPresent(List<Finding> findings, String emptyFindingsHint) {
    String findingsString = concatenateFindings(findings);
    
    if (findingsString.isEmpty()) {
      findingsString = emptyFindingsHint;
    }
    
    assertEquals(findingsString, numExpectedFindings,
        findings.stream().map(f -> f.buildMsg()).filter(containsExpectedErrorCode).count());
    
    assertTrue(collectErrorCodes(findingsString).containsAll(expectedErrorCodes));
  }
  
  public void checkOnlyExpectedPresent(List<Finding> findings) {
    checkOnlyExpectedPresent(findings, "");
  }
  
  public void checkOnlyExpectedPresent(List<Finding> findings, String emptyFindingsHint) {
    checkExpectedPresent(findings, emptyFindingsHint);
    
    checkNoAdditionalErrorCodesPresent(concatenateFindings(findings));
  }
  
  private void checkNoAdditionalErrorCodesPresent(String findingsString) {
    Set<String> actualErrorCodes = collectErrorCodes(findingsString);
    
    // check whether there are unexpected error codes
    Set<String> unexpectedErrorCodes = new HashSet<>(actualErrorCodes);
    unexpectedErrorCodes.removeAll(expectedErrorCodes);
    
    assertEquals(findingsString, 0, unexpectedErrorCodes.size());
  }
}
