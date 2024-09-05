/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.tests;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogPrintCollector;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.TestWatcher;

/**
 * An extension for test classes and methods that test generated MA2JSim code.
 * <br>
 * Sets up appropriate logging for the tests. Can be used on test classes or methods.
 * As the static SE-logger is involved, it is expected that parallel test execution
 * leads to unexpected behavior.
 */
public class JSimLogExtension implements BeforeEachCallback, TestWatcher {

  private static final String LOG_COLLECTOR_KEY = "logCollector";

  @Override
  public void beforeEach(ExtensionContext extensionContext) {
    LogPrintCollector logCollector = new LogPrintCollector();

    TestLogger.init();
    TestLogger.addLogHook(logCollector);

    getStore(extensionContext).put(LOG_COLLECTOR_KEY, logCollector);

    logInitialInfo(extensionContext);
  }

  private void logInitialInfo(ExtensionContext context) {
    String testInfo = String.format(
      "- TestClass = '%s'; TestMethod = '%s'; TestName = '%s'",
      context.getRequiredTestClass().getName(),
      context.getRequiredTestMethod().getName(),
      context.getDisplayName()
    );
    Log.info(testInfo,"TestExecution");
  }

  @Override
  public void testFailed(ExtensionContext context, Throwable cause) {
    LogPrintCollector logCollector = getStore(context).get(LOG_COLLECTOR_KEY, LogPrintCollector.class);

    String logOut =
      logCollector
        .getPrints().stream()
        .reduce(String::concat).orElse("")
        .trim();

    System.out.println(logOut);
    logCollector.clearPrints();
  }

  private Store getStore(ExtensionContext context) {
    return context.getStore(Namespace.create(getClass(), context.getRequiredTestMethod()));
  }
}
