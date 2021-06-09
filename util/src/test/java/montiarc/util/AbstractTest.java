/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class AbstractTest {

  protected static final String RELATIVE_MODEL_PATH = "src/test/resources";
  private Pattern errorCodePattern;

  public static void addBasicTypes2Scope() {
    BasicSymbolsMill.initializePrimitives();
  }

  @BeforeEach
  public void cleanUpLog() {
    Log.getFindings().clear();
    Log.enableFailQuick(false);
    errorCodePattern = supplyErrorCodePattern();
    assert errorCodePattern != null;
  }

  /**
   * @return pattern used to find error-codes in raw console output
   */
  protected abstract Pattern supplyErrorCodePattern();

  /**
   * @return a buffered pattern, which was {@link #supplyErrorCodePattern() created} previously
   */
  protected Pattern getErrorCodePattern() {
    return errorCodePattern;
  }

  /**
   * makes sure that all errors contained in the second collection are also contained by the first.
   * {@link #checkNoAdditionalErrorsPresent(Error[]) opposite action}
   * @param expErrors expected errors (contains the same or less errors than the first collection)
   */
  protected void checkExpectedErrorsPresent(Error[] expErrors) {
    checkBoundsForFoundErrors(true, Log.getFindings(), expErrors, "Some expected errors did not occur");
  }

  /**
   * makes sure that all errors contained in the first collection are also contained by the second.
   * {@link #checkExpectedErrorsPresent(Error[]) opposite action}
   * @param expErrors expected errors (contains the same or more errors than the first collection)
   */
  protected void checkNoAdditionalErrorsPresent(Error[] expErrors) {
    checkBoundsForFoundErrors(false, Log.getFindings(), expErrors, "There were additional errors found that were not expected");
  }

  /**
   * Checks whether one of the given collections contains a part of the other one, but not more.
   *
   * @param moreAllowed if
   *                    <code>true</code> the test will succeed if all expected errors and possibly more errors occurred
   *                    <code>false</code> the test will succeed if there are no additional errors than expected
   * @param findings log that contains the occurred errors
   * @param errors upper or lower bound for expected errors
   */
  protected void checkBoundsForFoundErrors(boolean moreAllowed, List<Finding> findings, Error[] errors, String message){
    List<String> actual = collectErrorCodes(findings);
    List<String> expected = collectErrorCodes(errors);
    List<String> bigger = moreAllowed ? actual : expected;
    List<String> lesser = moreAllowed ? expected : actual;

    // do not use 'removeAll' because that behaves differently to remove when a list contains one element multiple times
    List<String> matched = bigger.stream().filter(lesser::remove).collect(Collectors.toList());

    Assertions.assertEquals(
        moreAllowed ? expected : Collections.emptyList(),
        moreAllowed ? Collections.emptyList() : actual,
        String.format("%s\nCould match: %s",message, matched));
  }

  /**
   * Searches console-output for error codes, filters out noise and then compares those errors to the given errors.
   * {@link Assertions#fail() Fails} if the collections of errors do not contain the same error codes.
   * This is like calling {@link #checkExpectedErrorsPresent(Error[]) this}
   * and {@link #checkNoAdditionalErrorsPresent(Error[]) this}
   * @param expErrors expected errors (the order does not matter)
   */
  protected void checkOnlyExpectedErrorsPresent(Error... expErrors) {
    checkOnlyExpectedErrorsPresent(expErrors, null);
  }

  /**
   * see {@link #checkOnlyExpectedErrorsPresent(Error...) base method}.
   * @param location optional parameter that describes where the error was found.
   */
  protected void checkOnlyExpectedErrorsPresent(Error[] expErrors, Path location) {
    Assertions.assertEquals(
        collectErrorCodes(expErrors),
        collectErrorCodes(Log.getFindings()),
        "Expected errors do not match the found ones"+(location==null?'.':" in \n"+location.toString()));
  }

  /**
   * turns this collection of errors into their respective error codes
   * @param errors expected errors
   * @return all error codes of the given errors, sorted, in a list
   */
  protected List<String> collectErrorCodes(Error[] errors) {
    return Arrays.stream(errors)
      .map(Error::getErrorCode)
      .sorted(String::compareTo)
      .collect(Collectors.toList());
  }

  /**
   * searches a collection of console outputs to find error codes
   * @param findings logger outputs
   * @return all error codes, in a sorted order
   */
  protected List<String> collectErrorCodes(List<Finding> findings) {
    return findings.stream()
      .map(Finding::getMsg)
      .map(this::collectErrorCodes)
      .flatMap(Collection::stream)
      .sorted(String::compareTo)
      .collect(Collectors.toList());
  }

  /**
   * extracts error codes from a message
   */
  protected List<String> collectErrorCodes(String msg) {
    List<String> errorCodes = new ArrayList<>();
    Matcher matcher = getErrorCodePattern().matcher(msg);
    while (matcher.find()) {
      errorCodes.add(matcher.group());
    }
    return errorCodes;
  }
}