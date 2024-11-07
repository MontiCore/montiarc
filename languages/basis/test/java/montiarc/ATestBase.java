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

import java.util.Arrays;

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

  protected String[] getLoggedErrorCodes() {
    return Log.getFindings().stream()
      .map(Finding::getMsg)
      .map(msg -> msg.substring(0, 7))
      .toArray(String[]::new);
  }

  protected String[] getErrorCodes(@NotNull Error... e) {
    return Arrays.stream(e).map(Error::getErrorCode).toArray(String[]::new);
  }
}
