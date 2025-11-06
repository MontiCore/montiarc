/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import arcbasis.ArcBasisMill;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Arrays;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The base of every test. Handles initialization and reset of the log stub.
 */
public abstract class ATestBase {

  protected static final String TEST_RESOURCE = "test/resources/";

  /**
   * We initialize the log before all tests. The log is therefore available
   * to the parameter provider of parameterized tests.
   */
  @BeforeAll
  protected static void initLog() {
    LogStub.init();
  }

  /**
   * We catch errors logged before test execution. These may indicate erroneous
   * parameter providers and log-caches persisting across tests.
   */
  @BeforeEach
  protected void assertLogCleared() {
    assertThat(Log.getFindings()).isEmpty();
  }

  /**
   * We clear the log after every test ensuring findings are removed between
   * tests. We do not clear the log before tests so that errors logged by
   * parameter providers may be caught.
   */
  @AfterEach
  protected void clearLog() {
    LogStub.clearFindings();
    LogStub.clearPrints();
  }

  /**
   * We clear the global scope after each test. Don't assume that symbols added
   * before or during a test are available to the next test.
   */
  @AfterEach
  protected void clearGlobalScope() {
    ArcBasisMill.globalScope().clear();
  }

  public static String[] getLoggedErrorCodes() {
    return Log.getFindings().stream()
      .map(Finding::getMsg)
      .map(msg -> msg.substring(0, 7))
      .filter(Error.ERROR_CODE_PATTERN.asPredicate())
      .toArray(String[]::new);
  }

  public static String[] getErrorCodes(@NotNull Error... e) {
    return Arrays.stream(e).map(Error::getErrorCode).toArray(String[]::new);
  }

  /**
   * Factory method creating {@code Arguments} of the given {@code objects}
   * that can be used as result of a method source of a parameterized test.
   *
   * <p>This method is an <em>alias</em> and shorthand notation for
   * {@link Arguments#of} and {@link Arguments#arguments}.
   *
   * @return an instance of {@link Arguments} of the given objects
   */
  public static Arguments arg(Object... objects) {
    return Arguments.of(objects);
  }

  /**
   * Factory method creating {@code Arguments} of the given {@code objects}
   * and {@code errors} that can be provided inside a stream as arguments
   * (junit method source) to a parameterized test.
   *
   * <p>This method is an <em>alias</em> and shorthand notation for
   * {@link Arguments#of} and {@link Arguments#arguments}.
   *
   * @return an instance of {@link Arguments} of the given arguments
   */
  public static Arguments arg(Object obj, Error... errors) {
    return Arguments.of(obj, errors);
  }

  /**
   * @see ATestBase#arg(Object, Error...)
   */
  public static Arguments arg(Object obj1, Object obj2, Error... errors) {
    return Arguments.of(obj1, obj2, errors);
  }

  /**
   * @see ATestBase#arg(Object, Error...)
   */
  public static Arguments arg(Object obj1, Object obj2,
                              Object obj3, Error... errors) {
    return Arguments.of(obj1, obj2, obj3, errors);
  }

  /**
   * @see ATestBase#arg(Object, Error...)
   */
  public static Arguments arg(Object obj1, Object obj2, Object obj3,
                              Object obj4, Error... errors) {
    return Arguments.of(obj1, obj2, obj3, obj4, errors);
  }

  /**
   * @see ATestBase#arg(Object, Error...)
   */
  public static Arguments arg(Object obj1, Object obj2, Object obj3,
                              Object obj4, Object obj5, Error... errors) {
    return Arguments.of(obj1, obj2, obj3, obj4, obj5, errors);
  }

  /**
   * Factory method creating {@code Arguments} of the given {@code objects}
   * and {@code findings} that can be provided inside a stream as arguments
   * (junit method source) to a parameterized test.
   *
   * <p>This method is an <em>alias</em> and shorthand notation for
   * {@link Arguments#of} and {@link Arguments#arguments}.
   *
   * @return an instance of {@link Arguments} of the given arguments
   */
  public static Arguments arg_f(Object obj, Finding... findings) {
    return Arguments.of(obj, findings);
  }

  /**
   * @see ATestBase#arg_f(Object, Finding...)
   */
  public static Arguments arg_f(Object obj1, Object obj2, Finding... findings) {
    return Arguments.of(obj1, obj2, findings);
  }

  /**
   * @see ATestBase#arg_f(Object, Finding...)
   */
  public static Arguments arg_f(Object obj1, Object obj2,
                                Object obj3, Finding... findings) {
    return Arguments.of(obj1, obj2, obj3, findings);
  }

  /**
   * @see ATestBase#arg_f(Object, Finding...)
   */
  public static Arguments arg_f(Object obj1, Object obj2, Object obj3,
                                Object obj4, Finding... findings) {
    return Arguments.of(obj1, obj2, obj3, obj4, findings);
  }

  /**
   * @see ATestBase#arg_f(Object, Finding...)
   */
  public static Arguments arg_f(Object obj1, Object obj2, Object obj3,
                                Object obj4, Object obj5, Finding... findings) {
    return Arguments.of(obj1, obj2, obj3, obj4, obj5, findings);
  }
}
