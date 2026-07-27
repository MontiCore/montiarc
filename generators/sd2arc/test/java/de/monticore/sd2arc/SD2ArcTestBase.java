/* (c) https://github.com/MontiCore/monticore */
package de.monticore.sd2arc;

import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import montiarc.ATestBase;
import org.junit.jupiter.api.Assertions;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public abstract class SD2ArcTestBase extends ATestBase {

  protected static final String SYMBOL_PATH = "test/resources";

  /**
   * makes sure that all errors contained in the second collection are also contained by the first.
   * {@link #checkNoAdditionalErrorsPresent(String[]) opposite action}
   *
   * @param expErrors expected errors (contains the same or fewer errors than the first collection)
   */
  protected void checkExpectedErrorsPresent(String[] expErrors) {
    checkBoundsForFoundErrors(true, Log.getFindings(), expErrors, "Some expected errors did not occur");
  }

  /**
   * makes sure that all errors contained in the first collection are also contained by the second.
   * {@link #checkExpectedErrorsPresent(String[]) opposite action}
   *
   * @param expErrors expected errors (contains the same or more errors than the first collection)
   */
  protected void checkNoAdditionalErrorsPresent(String[] expErrors) {
    checkBoundsForFoundErrors(false, Log.getFindings(), expErrors, "There were additional errors found that were not expected");
  }

  /**
   * Checks whether one of the given collections contains a part of the other one, but not more.
   *
   * @param moreAllowed if
   *                    <code>true</code> the test will succeed if all expected errors and possibly more errors occurred
   *                    <code>false</code> the test will succeed if there are no additional errors than expected
   * @param findings    log that contains the occurred errors
   * @param errors      upper or lower bound for expected errors
   */
  protected void checkBoundsForFoundErrors(boolean moreAllowed, List<Finding> findings, String[] errors, String message) {
    List<String> actual = collectErrorCodes(findings);
    List<String> expected = collectErrorCodes(errors);
    List<String> bigger = moreAllowed ? actual : expected;
    List<String> lesser = moreAllowed ? expected : actual;

    // do not use 'removeAll' because that behaves differently to remove when a list contains one element multiple times
    List<String> matched = bigger.stream().filter(lesser::remove).toList();

    Assertions.assertEquals(moreAllowed ? expected : Collections.emptyList(), moreAllowed ? Collections.emptyList() : actual, String.format("%s\nCould match: %s", message, matched));
  }

  /**
   * Searches console-output for String codes, filters out noise and then compares those errors to the given errors.
   * {@link Assertions#fail() Fails} if the collections of errors do not contain the same String codes.
   * This is like calling {@link #checkExpectedErrorsPresent(String[]) this}
   * and {@link #checkNoAdditionalErrorsPresent(String[]) this}
   *
   * @param expErrors expected errors (the order does not matter)
   */
  protected void checkOnlyExpectedErrorsPresent(String... expErrors) {
    checkOnlyExpectedErrorsPresent(expErrors, null);
  }

  /**
   * see {@link #checkOnlyExpectedErrorsPresent(String...) base method}.
   *
   * @param location optional parameter that describes where the String was found.
   */
  protected void checkOnlyExpectedErrorsPresent(String[] expErrors, Path location) {
    Assertions.assertEquals(collectErrorCodes(expErrors), collectErrorCodes(Log.getFindings()), "Expected errors do not match the found ones" + (location == null ? '.' : " in \n" + location));
  }

  /**
   * turns this collection of errors into their respective String codes
   *
   * @param errors expected errors
   * @return all String codes of the given errors, sorted, in a list
   */
  protected List<String> collectErrorCodes(String[] errors) {
    return Arrays.stream(errors).sorted(String::compareTo).collect(Collectors.toList());
  }

  /**
   * searches a collection of console outputs to find String codes
   *
   * @param findings logger outputs
   * @return all String codes, in a sorted order
   */
  protected List<String> collectErrorCodes(List<Finding> findings) {
    return findings.stream().map(Finding::getMsg).map(this::collectErrorCodes).flatMap(Collection::stream).sorted(String::compareTo).collect(Collectors.toList());
  }

  /**
   * extracts error codes from a message
   */
  protected List<String> collectErrorCodes(String msg) {
    List<String> errorCodes = new ArrayList<>();
    Matcher matcher = Pattern.compile("0x[0-9a-fA-F]{5}").matcher(msg);
    while (matcher.find()) {
      errorCodes.add(matcher.group());
    }
    return errorCodes;
  }
}
