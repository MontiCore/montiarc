/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.tests.logging;

import montiarc.rte.logging.HookBuilder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies a method supplying {@link de.se_rwth.commons.logging.ILogHook}s to be used in the test.
 * <br>
 * The method must be static, have no arguments, and return a Collection of {@link HookBuilder}s.
 * If {@link #value()} is not set, the method with the same name as the test method will be searched.
 * If the environment variable {@link montiarc.rte.logging.Configuration#LOG_BASE_PATH} is set, it
 * will be used as the base path for log files.
 * <br>
 * Exemplary use:
 *
 * <pre>
 *   &#64;ParameterizedTest
 *   &#64;MethodSource("io")
 *   &#64;LogHookSupplier("hooks")
 *   void testIO(...) {...}
 *
 *   static Collection&lt;HookBuilder&gt; hooks() {
 *     return List.of(
 *       new HookBuilder()
 *         .reportToConsole()  // Only recommended for debugging, as it also prints to the console in case of success
 *         .reportToFile(Path.of("myOwnFavorite.log"), FieldTest.class)
 *         .monitor("sut")
 *         .addAspect(montiarc.rte.logging.Aspects.RECEIVE_EVENT)
 *     );
 *   }
 * </pre>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogHookSupplier {
  /**
   * Method in the same class with adequate signature. The default value is the name of the test method,
   * so a method with the same name will be searched to supply the log hooks
   */
  String value() default "";
}
