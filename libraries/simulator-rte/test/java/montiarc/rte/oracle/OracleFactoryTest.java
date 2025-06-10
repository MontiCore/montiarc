/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.oracle;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

/** Tests {@link OracleFactory} */
class OracleFactoryTest {

  @Test
  void shouldReturnDefaultOracle() {
    // Given
    OracleFactory factory = new OracleFactory();
    Supplier<Oracle> strategy = () -> Mockito.mock(Oracle.class);

    // When
    factory.setDefaultStrategy(strategy);
    Supplier<Oracle> returnedStrategy = factory.getDefaultStrategy();

    // Then
    assertThat(returnedStrategy).isSameAs(strategy);
  }

  @Test
  void shouldCreateDefaultOracle() {
    // Given
    OracleFactory factory = new OracleFactory();
    Oracle oracleMock = Mockito.mock(Oracle.class);
    Supplier<Oracle> strategy = () -> oracleMock;

    // When
    factory.setDefaultStrategy(strategy);
    Oracle createdOracle = factory.createDefaultOracle();

    // Then
    assertThat(createdOracle).isSameAs(oracleMock);
  }

  /** Tests {@link OracleFactory#createOracleFor(java.lang.String)} with the default strategy */
  @Test
  void shouldUseDefaultStrategy() {
    // Given
    OracleFactory factory = new OracleFactory();
    factory.setDefaultStrategy(PreferFirstOracle::new);

    // When
    Oracle oracle = factory.createOracleFor("AnyComp");

    // Then
    assertThat(oracle).isInstanceOf(PreferFirstOracle.class);
  }

  /** Tests {@link OracleFactory#createOracleFor(java.lang.String)} with the default strategy */
  @Test
  void shouldCreateNewOracleInstanceWithDefaultStrategyForDifferentComps() {
    // Given
    OracleFactory factory = new OracleFactory();
    factory.setDefaultStrategy(LowestHashValueOracle::new);

    // When
    Oracle oracle1 = factory.createOracleFor("compThatIsNotRegistered");
    Oracle oracle2 = factory.createOracleFor("compThatIsNotRegistered2");

    // Then
    assertThat(oracle1).isNotSameAs(oracle2);
  }

  /** Tests {@link OracleFactory#createOracleFor(java.lang.String)} with the default strategy */
  @Test
  void shouldCreateNewOracleInstanceWithDefaultStrategyForSameComp() {
    // Given
    OracleFactory factory = new OracleFactory();
    factory.setDefaultStrategy(LowestHashValueOracle::new);

    // When
    Oracle oracle1 = factory.createOracleFor("compThatIsNotRegistered");
    Oracle oracle2 = factory.createOracleFor("compThatIsNotRegistered");

    // Then
    assertThat(oracle1).isNotSameAs(oracle2);
  }

  /**
   * Tests {@link OracleFactory#createOracleFor(java.lang.String)} with a custom oracle supplier set
   * via {@link OracleFactory#set4Comp(String, Supplier)}.
   */
  @Test
  void shouldUseIndividualSupplier() {
    // Given
    OracleFactory factory = new OracleFactory();

    // When
    factory.set4Comp("compThatIsRegistered", PreferFirstOracle::new);
    Oracle oracle = factory.createOracleFor("compThatIsRegistered");

    // Then
    assertThat(oracle).isInstanceOf(PreferFirstOracle.class);
  }

  /**
   * Tests {@link OracleFactory#createOracleFor(java.lang.String)} with a custom oracle supplier set
   * via {@link OracleFactory#set4Comp(String, Supplier)}.
   */
  @Test
  void shouldCreateNewOracleInstanceWithIndividualSupplier() {
    // Given
    OracleFactory factory = new OracleFactory();

    // When
    factory.set4Comp("compThatIsRegistered", PreferFirstOracle::new);
    Oracle oracle1 = factory.createOracleFor("compThatIsRegistered");
    Oracle oracle2 = factory.createOracleFor("compThatIsRegistered");

    // Then
    assertThat(oracle1).isNotSameAs(oracle2);
  }

  /**
   * Tests {@link OracleFactory#createOracleFor(java.lang.String)} with a custom oracle set
   * via {@link OracleFactory#set4Comp(String, Oracle)}.
   */
  @Test
  void shouldUseSameOracleInstanceWithIndividualOracle() {
    // Given
    OracleFactory factory = new OracleFactory();
    PreferFirstOracle oracle = new PreferFirstOracle();

    // When
    factory.set4Comp("compThatIsRegistered", oracle);
    Oracle producedOracle1 = factory.createOracleFor("compThatIsRegistered");
    Oracle producedOracle2 = factory.createOracleFor("compThatIsRegistered");

    // Then
    assertAll(
      () -> assertThat(oracle).isSameAs(producedOracle1),
      () -> assertThat(oracle).isSameAs(producedOracle2)
    );
  }

  /**
   * Tests {@link OracleFactory#createOracleFor(java.lang.String)} in combination
   * with {@link OracleFactory#set4Comp(String, Oracle)}.
   */
  @Test
  void testQualifiedAccessInSet4CompWithSupplier() {
    // Given
    OracleFactory factory = new OracleFactory();
    PreferFirstOracle oracle = new PreferFirstOracle();

    // When
    factory.set4Comp("sub.comp", () -> oracle);
    Oracle producedOracle = factory.createOracleFor("sub.comp");

    // Then
    assertThat(producedOracle).isSameAs(oracle);
  }

  /**
   * Tests {@link OracleFactory#createOracleFor(java.lang.String)} in combination
   * with {@link OracleFactory#set4Comp(String, Oracle)}.
   */
  @Test
  void testNestedQualifiedAccessInSet4CompWithSupplier() {
    // Given
    OracleFactory factory = new OracleFactory();
    PreferFirstOracle oracle = new PreferFirstOracle();

    // When
    factory.set4Comp("sub.comp.inst", () -> oracle);
    Oracle producedOracle = factory.createOracleFor("sub.comp.inst");

    // Then
    assertThat(producedOracle).isSameAs(oracle);
  }

  /**
   * Tests {@link OracleFactory#createOracleFor(java.lang.String)} in combination
   * with {@link OracleFactory#set4Comp(String, Oracle)}.
   */
  @Test
  void testQualifiedAccessInSet4CompWithOracle() {
    // Given
    OracleFactory factory = new OracleFactory();
    PreferFirstOracle oracle = new PreferFirstOracle();

    // When
    factory.set4Comp("sub.comp", oracle);
    Oracle producedOracle = factory.createOracleFor("sub.comp");

    // Then
    assertThat(producedOracle).isSameAs(oracle);
  }

  /**
   * Tests {@link OracleFactory#createOracleFor(java.lang.String)} in combination
   * with {@link OracleFactory#set4Comp(String, Oracle)}.
   */
  @Test
  void testNestedQualifiedAccessInSet4CompWithOracle() {
    // Given
    OracleFactory factory = new OracleFactory();
    PreferFirstOracle oracle = new PreferFirstOracle();

    // When
    factory.set4Comp("sub.comp.inst", oracle);
    Oracle producedOracle = factory.createOracleFor("sub.comp.inst");

    // Then
    assertThat(producedOracle).isSameAs(oracle);
  }

}
