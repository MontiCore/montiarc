/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

import montiarc.rte.oracle.OracleFactory;

/**
 * Describes the context of a {@link MaUnitTest @MaUnitTest}.
 */
public interface MaUnitTestContext {

  /**
   * @return The number of different test cases the {@link MaUnitTest @MaUnitTest} has.
   */
  int testCount();

  /**
   * @param testIndex the test case index.
   * @return the name of the give test case.
   */
  String getDisplayName(int testIndex);

  /**
   * @param testIndex the test case index.
   * @return how many ticks are to be executed for the given test case.
   */
  default int getTickCount(int testIndex) {
    return 1;
  }

  /**
   * @param testIndex the test case index.
   * @return the simulated time between ticks in nanoseconds.
   */
  default long getSimulatedTickLength(int testIndex) {
    return 0;
  }

  /**
   * Returns the component's constructor arguments.
   * {@param parameterIndex} one (Component name) and two (schedule) are not influenced by this method. They are part of the broader test execution and cannot be overridden.
   * <p>
   * Should throw a {@link ParameterResolutionException} if no value can be resolved.
   *
   * @param testIndex      the test case index.
   * @param parameterIndex the index of the component constructor argument.
   * @return the value for the given parameter in the given test case.
   */
  default Object resolveParameter(int testIndex, int parameterIndex) {
    throw new ParameterResolutionException();
  }

  /**
   * @param testIndex the test case index.
   * @return the class the expected throwable is an instance of for the given test case, or {@code null} if none is expected.
   */
  default Class<?> getExpectedException(int testIndex) {
    return null;
  }

  /**
   * @param testIndex the test case index.
   * @return if the given test case is expected to throw an exception/error.
   */
  default boolean isExceptionExpected(int testIndex) {
    return getExpectedException(testIndex) != null;
  }

  /**
   * @param testIndex the test case index.
   * @return the oracle factory for the given test case.
   */
  default OracleFactory getOracleFactory(int testIndex) {
    return OracleFactory.withDefaultStrategy(OracleFactory.preferFirst());
  }
}
