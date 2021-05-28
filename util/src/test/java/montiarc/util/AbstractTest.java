/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

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

  protected void add2Scope(IOOSymbolsScope scope, OOTypeSymbol... types) {
    Arrays.stream(types).forEach(type -> {
      scope.add(type);
      type.setEnclosingScope(scope);
    });
  }

  protected void add2Scope(IOOSymbolsScope scope, FieldSymbol... fields) {
    Arrays.stream(fields).forEach(field -> {
      scope.add(field);
      field.setEnclosingScope(scope);
    });
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
   * {@link #checkNoAdditionalErrorsPresent(List, Error[]) opposite action}
   * @param findings collection of found errors
   * @param expErrors expected errors (contains the same or less errors than the first collection)
   */
  protected void checkExpectedErrorsPresent(List<Finding> findings,
                                            Error[] expErrors) {
    List<String> actualErrorCodes = collectErrorCodes(findings);
    List<String> expErrorCodes = collectErrorCodes(expErrors);

    Assertions.assertTrue(actualErrorCodes.containsAll(expErrorCodes), String.format("Expected "
      + "error codes: " + expErrorCodes.toString() + " Actual error codes: "
      + actualErrorCodes.toString()));
  }

  /**
   * makes sure that all errors contained in the first collection are also contained by the second.
   * {@link #checkExpectedErrorsPresent(List, Error[]) opposite action}
   * @param findings collection of found errors
   * @param expErrors expected errors (contains the same or more errors than the first collection)
   */
  protected void checkNoAdditionalErrorsPresent(List<Finding> findings,
                                                Error[] expErrors) {
    List<String> actualErrorCodes = collectErrorCodes(findings);
    List<String> expErrorCodes = collectErrorCodes(expErrors);

    actualErrorCodes.removeAll(expErrorCodes);

    Assertions.assertEquals(Collections.emptyList(), actualErrorCodes, "There were additional errors found that were not expected");
  }

  /**
   * compares two collections of errors and {@link Assertions#fail() fails} if they do not contain the same error codes.
   * This is like calling {@link #checkExpectedErrorsPresent(List, Error[]) this}
   * and {@link #checkNoAdditionalErrorsPresent(List, Error[]) this}
   * @param findings actual errors and other noise found in the console output
   * @param expErrors expected errors (the order does not matter)
   */
  protected void checkOnlyExpectedErrorsPresent(List<Finding> findings, Error[] expErrors) {
    Assertions.assertEquals(
        collectErrorCodes(expErrors),
        collectErrorCodes(findings),
        "Expected errors do not match the found ones");
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