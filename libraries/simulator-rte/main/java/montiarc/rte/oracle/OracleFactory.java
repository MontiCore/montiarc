/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.oracle;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Creates {@link Oracle}s on demand for component instances.<br>
 * {@link #set4Comp(String, Supplier)} and {@link #set4Comp(String, Oracle)}
 * allow for configuring individual oracle strategies for different
 * decomposition parts.<br>
 * If no specific strategy is configured for a component, an oracle with a
 * default strategy will be created. The default strategy can be configured with
 * {@link #withDefaultStrategy(Supplier)} and
 * {@link #setDefaultStrategy(Supplier)}. If left unconfigured, the default
 * strategy is to use {@link LowestHashValueOracle}.<br>
 * An OracleFactory is typically created for a decomposition, possibly with
 * multiple hierarchical levels. As a consequence, it is related to the root
 * component of the decomposition and component names as used in the
 * {@link #createOracleFor(String)} and {@code set4Comp} methods related to that
 * root component. I.e., if the root component has the instance name
 * {@code "topComp"}, then the corresponding oracle to use can be set and
 * retrieved by calling the respective methods with the component name
 * {@code "topComp"}. Furthermore, using the name {@code "topComp.subComp"}
 * sets / gets the oracle for topComp's sub component {@code "subComp"}.
 */
public class OracleFactory {

  protected Supplier<Oracle> defaultStrategy = PreferFirstOracle::new;
  protected Map<String, Supplier<Oracle>> subCompToOracleMap = new HashMap<>();

  /**
   * Create an oracle strategy with a given default oracle strategy that is used
   * for all component instances for which no individual strategy is configured.
   * <br>
   * Configure oracle strategies for individual components with
   * {@link #set4Comp(String, Supplier)} and {@link #set4Comp(String, Oracle)}.
   */
  public OracleFactory setDefaultStrategy(Supplier<Oracle> defaultStrategy) {
    this.defaultStrategy = defaultStrategy;

    return this;
  }


  public Supplier<Oracle> getDefaultStrategy() {
    return this.defaultStrategy;
  }

  /** @return An oracle created newly with the default strategy */
  public Oracle createDefaultOracle() {
    return defaultStrategy.get();
  }

  /**
   * Configures this factory to supply the component named {@code subCompName}
   * with the oracle {@code oracle} upon calls to
   * {@link #createOracleFor(String)}.<br>
   * When considering dynamic components with mode automata, a sub component may
   * be instantiated again and again if its mode is entered repeatedly. Using
   * this method, the same oracle will be reused across mode changes, including
   * its state if the oracle has one. Consider using
   * {@link #set4Comp(String, Supplier)} so that state preservation is avoided
   * and re-instantiated sub component instances in dynamic components obtain a
   * newly instantiated oracle during their own instantiation.
   *
   * @param compName The qualified name of the component instance which is
   *                    configured. If the component is part of a decomposition
   *                    hierarchy within a mode automaton, the name also
   *                    includes mode names as name parts. Examples:<br>
   *                    {@code "topComp.subComp1.subSubComp"},
   *                    {@code "topComp.subComp2.modeName.subCompInMode"}<br>
   *                    Note: the name parts reflect the instance names and not
   *                    the type names of the components!
   * @return this factory
   *
   * @see OracleFactory#set4Comp(String, Supplier)
   */
  public OracleFactory set4Comp(String compName, Oracle oracle) {
    return set4Comp(compName, () -> oracle);
  }

  /**
   * Configures this factory to supply the component named {@code compName}
   * with an oracle created by {@code oracleSupplier} upon calls to
   * {@link #createOracleFor(String)}.<br>
   * On every call to {@link #createOracleFor(String)}, a new oracle will be
   * crated by evaluating {@code oracleSupplier}.
   *
   * @param compName The qualified name of the component instance which is
   *                    configured. If the component is part of a decomposition
   *                    hierarchy within a mode automaton, the name also
   *                    includes mode names as name parts. Examples:<br>
   *                    {@code "topComp.subComp1.subSubComp"},
   *                    {@code "topComp.subComp2.modeName.subCompInMode"}<br>
   *                    Note: the name parts reflect the instance names and not
   *                    the type names of the components!
   * @return this factory
   *
   * @see OracleFactory#set4Comp(String, Oracle)
   */
  public OracleFactory set4Comp(String compName, Supplier<Oracle> oracleSupplier) {
    subCompToOracleMap.put(compName, oracleSupplier);
    return this;
  }

  /**
   * Returns the oracle for component instance {@code compName} as configured by
   * {@link #set4Comp(String, Supplier)} and {@link #set4Comp(String, Oracle)}.
   * <br>
   * If no specific oracle has been configured for {@code compName}, then an
   * oracle, according to a default strategy is created, which can be
   * configured with {@link #withDefaultStrategy(Supplier) and
   * {@link #setDefaultStrategy(Supplier)}}.
   *
   * @param compName The simple name of the component instance for which the
   *                 oracle should be retrieved
   */
  public Oracle createOracleFor(String compName) {
    return subCompToOracleMap.getOrDefault(compName, defaultStrategy).get();
  }

  /**
   * Creates a new {@code OracleFactory} with the given default strategy.
   */
  public static OracleFactory withDefaultStrategy(Supplier<Oracle> defaultStrategy) {
    OracleFactory factory = new OracleFactory();
    return factory.setDefaultStrategy(defaultStrategy);
  }

  /**
   * A supplier that creates new {@link LowestHashValueOracle}s on each
   * evaluation.
   */
  public static Supplier<Oracle> lowestHash() {
    return LowestHashValueOracle::new;
  }

  /**
   * A supplier that creates new {@link PreferFirstOracle}s on each evaluation.
   */
  public static Supplier<Oracle> preferFirst() {
    return PreferFirstOracle::new;
  }

  /**
   * A supplier that creates new {@link PreferLastOracle}s on each evaluation.
   */
  public static Supplier<Oracle> preferLast() {
    return PreferLastOracle::new;
  }

  /**
   * A supplier that creates new {@link PreferUnexploredOracle} on each
   * evaluation.
   * @param fallbackStrategy The oracle strategy that selects an unexplored
   *                         option among multiple unexplored options or, if all
   *                         options are already explored, one explored option.
   */
  public static Supplier<Oracle> preferUnexplored(Supplier<Oracle> fallbackStrategy) {
    return () -> new PreferUnexploredOracle(fallbackStrategy.get());
  }

  /**
   * A supplier that creates new {@link RandomOracle}s on each evaluation.
   */
  public static Supplier<Oracle> random() {
    return RandomOracle::new;
  }

  /**
   * A supplier that creates new {@link PreferLeastUsedOracle} on each
   * evaluation.
   * @param fallbackStrategy The oracle strategy that selects an option among
   *                         multiple least used options.
   */
  public static Supplier<Oracle> preferLeastUsed(Supplier<Oracle> fallbackStrategy) {
    return () -> new PreferLeastUsedOracle(fallbackStrategy.get());
  }
}
