/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.tests.logging;

import de.se_rwth.commons.logging.FileLogHook;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogPrintCollector;
import montiarc.rte.logging.Configuration;
import montiarc.rte.logging.HookBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.TestWatcher;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.HierarchyTraversalMode;
import org.junit.platform.commons.support.ModifierSupport;
import org.junit.platform.commons.support.ReflectionSupport;
import org.junit.platform.commons.util.Preconditions;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An extension for test classes and methods that test generated MA2JSim code.
 * <br>
 * Sets up appropriate logging for the tests. Can be used on test classes or methods.
 * As the static SE-logger is involved, it is expected that parallel test execution
 * leads to unexpected behavior.
 * <br>
 * Filtered logs can be defined for test methods by annotating them with {@link LogHookSupplier}.
 */
public class JSimLogExtension implements BeforeEachCallback, AfterEachCallback, TestWatcher {
  private static final String LOG_COLLECTOR_KEY = "ma2jsim-test-logCollector";
  private static final String TEST_INDEX_KEY = "ma2jsim-test-index";

  private final String logBasePath;

  public JSimLogExtension() {
    this.logBasePath = System.getenv(Configuration.LOG_BASE_PATH);
  }

  @Override
  public void beforeEach(ExtensionContext context) {
    determineTestIndex(context);
    initLogger(context);

    addFailedTestReportingLogger(context);
    addMaybeDefaultFileLogger(context);
    addUserLoggers(context);

    logInitialInfo(context);
  }

  private void initLogger(ExtensionContext context) {
    Method testMethod = context.getRequiredTestMethod();
    Class<?> testClass = context.getRequiredTestClass();

    if (AnnotationSupport.isAnnotated(testMethod, LogTraces.class)
      || AnnotationSupport.isAnnotated(testClass, LogTraces.class)) {
      TestLogger.initTRACE();
    } else {
      TestLogger.init();
    }
  }

  /**
   * If the test case to be executed is built from a template (such as @ParameterizedTest),
   * we will store its index in the extension store under {@link #TEST_INDEX_KEY} for later use.
   * <br>
   * If the test case is <i>not</i> built from a template, we will store an empty string.
   */
  private void determineTestIndex(ExtensionContext context) {
    // Note: parsing the test index from the uniqueID is a bit hacky, but there is no other way.
    String testId = context.getUniqueId();
    Matcher testIndexMatcher = Pattern.compile("\\[test-template-invocation:#\\d*]")
                                      .matcher(testId);
    if (testIndexMatcher.find()) {
      String occurrence = testIndexMatcher.group();
      String index = occurrence.substring(occurrence.indexOf("#") + 1, occurrence.length() - 1);
      getStore(context).put(TEST_INDEX_KEY, index);
    } else {
      getStore(context).put(TEST_INDEX_KEY, "");
    }
  }

  /**
   * Adds a logger that collects all log messages for the case that a test fails so that in that case all messages can
   * be logged to the console. This is performed by {@link #testFailed(ExtensionContext, Throwable)} */
  private void addFailedTestReportingLogger(ExtensionContext context) {
    LogPrintCollector logCollector = new LogPrintCollector();
    TestLogger.addLogHook(logCollector);
    getStore(context).put(LOG_COLLECTOR_KEY, logCollector);
  }

  /** Adds a default file logger if {@link Configuration#LOG_BASE_PATH} is set. */
  private void addMaybeDefaultFileLogger(ExtensionContext context) {
    if (logBasePath == null) {
      return;
    }

    Method testMethod = context.getRequiredTestMethod();
    Class<?> testClass = context.getRequiredTestClass();

    String reportDir = testClass.getCanonicalName().replace('.', File.separatorChar);
    StringBuilder reportFileName = new StringBuilder(testMethod.getName());

    // If there are other methods with the same name, append an unambiguity suffix to the file name
    List<Method> methodsWithSameName = ReflectionSupport.findMethods(
      testClass,
      m -> m.getName().equals(testMethod.getName())
        && AnnotationSupport.isAnnotated(m, Test.class),
      HierarchyTraversalMode.TOP_DOWN
    );
    if (methodsWithSameName.size() > 1) {
      int index = methodsWithSameName.indexOf(testMethod);
      reportFileName.append("_").append(index);
    }

    // If the test is built from a template (such as @ParameterizedTest),
    // append the index of the invocation to the file name.
    String currentTestIndex = getStore(context).get(TEST_INDEX_KEY, String.class);
    if (!currentTestIndex.isEmpty()) {
      reportFileName.append("_").append(currentTestIndex);
    }

    reportFileName.append(".log");

    String reportFilePath = String.join(File.separator, logBasePath, reportDir, reportFileName);
    Log.addLogHook(new FileLogHook(reportFilePath));
  }

  private void addUserLoggers(ExtensionContext context) {
    Class<?> testClass = context.getRequiredTestClass();
    Method testMethod = context.getRequiredTestMethod();
    Optional<LogHookSupplier> supplierAnnotation = AnnotationSupport.findAnnotation(testMethod, LogHookSupplier.class);

    if (supplierAnnotation.isPresent()) {
      String rawValue = supplierAnnotation.get().value();
      String supplierName = !rawValue.isEmpty() ? rawValue : testMethod.getName();

      Optional<Method> supplier = ReflectionSupport.findMethod(testClass, supplierName);

      Preconditions.condition(
        supplier.isPresent()
        && ModifierSupport.isStatic(supplier.get())
        && Collection.class.isAssignableFrom(supplier.get().getReturnType()),
        () -> String.format("Did not find an adequate log hook supplier '%s' for test '%s'." +
          " Must be static, parameter-less, and return a collection of log hooks",
          supplierName, testMethod.getName())
      );

      Collection<?> hookBuilders = (Collection<?>) context.getExecutableInvoker().invoke(supplier.get());
      for (Object uncastedBuilder : hookBuilders) {
        Preconditions.condition(uncastedBuilder instanceof HookBuilder, () ->
          String.format("Log hook supplier supplied an argument of type '%s', while only 'HookBuilder's are allowed",
            uncastedBuilder.getClass().getSimpleName())
        );

        HookBuilder builder = (HookBuilder) uncastedBuilder;

        String currentTestIndex = getStore(context).get(TEST_INDEX_KEY, String.class);
        if (!currentTestIndex.isEmpty()) {
          builder.transformFilePaths(p -> appendCurrentTestIndexToPath(p, currentTestIndex));
        }

        TestLogger.addLogHook(builder.build());
      }
    }
  }

  private Path appendCurrentTestIndexToPath(Path path, String currentIndex) {
    String fileName = path.getFileName().toString();

    String newFileName;
    if (fileName.contains(".")) {
      newFileName =
        fileName.substring(0, fileName.lastIndexOf('.'))
          + "_" + currentIndex
          + fileName.substring(fileName.lastIndexOf('.'));
    } else {
      newFileName = fileName + "_" + currentIndex;
    }

    Path newPath;
    if (path.getParent() != null) {
      newPath = Path.of(path.getParent().toString(), newFileName);
    } else {
      newPath = Path.of(newFileName);
    }

    return newPath;
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
    // Print logs to command line
    LogPrintCollector logCollector = getStore(context).get(LOG_COLLECTOR_KEY, LogPrintCollector.class);

    if (logCollector != null) {
      String logOut =
        logCollector
          .getPrints().stream()
          .reduce(String::concat).orElse("")
          .trim();

      System.out.println("Complete log output:");
      System.out.println(logOut);

      logCollector.clearPrints();
    }
  }

  @Override
  public void afterEach(ExtensionContext context) {
    getStore(context).put(TEST_INDEX_KEY, null);
  }

  private Store getStore(ExtensionContext context) {
    return context.getStore(Namespace.create(getClass(), context.getRequiredTestMethod()));
  }
}
