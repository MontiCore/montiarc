/* (c) https://github.com/MontiCore/monticore */
package montiarc._auxiliary;

import arcbasis.ArcBasisMill;
import arcbasis.check.ISynthesizeComponent;
import arcbasis.check.SynthesizeComponentFromMCBasicTypes;
import arccore.ArcCoreMill;
import com.google.common.base.Preconditions;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.monticore.types.prettyprint.MCSimpleGenericTypesFullPrettyPrinter;
import montiarc.MontiArcMill;
import montiarc.check.SynthesizeComponentFromMCSimpleGenericTypes;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class ArcBasisMillForMontiArcTest {

  /**
   * Ensures that the scope genitor is initialized with the expected type printer with respect to the initialized mill.
   * That is, the mill should provide a scope genitor initialized with a {@link MCBasicTypesFullPrettyPrinter} when
   * using the {@link ArcBasisMill}, and initialized with a {@link MCSimpleGenericTypesFullPrettyPrinter} when using the
   * {@link MontiArcMill}.
   *
   * @param setup    The setup to execute, e.g., to initialize the respective mill.
   * @param expected The expected class of the type printer of the genitor.
   */
  @ParameterizedTest
  @MethodSource("setupAndExpectedClassForGenitorProvider")
  public void shouldProvideGenitorAsExpected(@NotNull Runnable setup,
                                             @NotNull Class<MCBasicTypesFullPrettyPrinter> expected) {
    Preconditions.checkNotNull(setup);
    Preconditions.checkNotNull(expected);

    // When
    setup.run();

    // Then
    Assertions.assertEquals(expected, ArcBasisMill.scopesGenitor().getTypePrinter().getClass());
  }

  protected static Stream<Arguments> setupAndExpectedClassForGenitorProvider() {
    Runnable setupArcBasis = () -> {
      ArcBasisMill.reset();
      ArcBasisMill.init();
    };
    Runnable setupArcCore = () -> {
      ArcCoreMill.reset();
      ArcCoreMill.init();
    };
    Runnable setupMontiArc = () -> {
      MontiArcMill.reset();
      MontiArcMill.init();
    };
    return Stream.of(
      Arguments.of(setupArcBasis, MCBasicTypesFullPrettyPrinter.class),
      Arguments.of(setupArcCore, MCBasicTypesFullPrettyPrinter.class),
      Arguments.of(setupMontiArc, MCSimpleGenericTypesFullPrettyPrinter.class)
    );
  }

  /**
   * Ensures that the symbol table completer is initialized with the expected type printer and component synthesizer
   * with respect to the initialized mill. That is, the mill should provide a symbol table completer that is initialized
   * with a {@link MCBasicTypesFullPrettyPrinter} and a {@link SynthesizeComponentFromMCBasicTypes} when using the
   * {@link ArcBasisMill}, respectively provide a symbol table completer that is initialized with a {@link
   * MCSimpleGenericTypesFullPrettyPrinter} and {@link SynthesizeComponentFromMCSimpleGenericTypes} when using the
   * {@link MontiArcMill}.
   *
   * @param setup                   The setup to execute, e.g., initialize the respective mill.
   * @param expectedPrettyPrinter   The expected class of the type printer of the symbol table completer.
   * @param expectedCompSynthesizer The expected class of the component synthesizer of the symbol-table completer.
   */
  @ParameterizedTest
  @MethodSource("setupAndExpectedClassForSymTabCompleterProvider")
  public void shouldProvideCompleterAsExpected(@NotNull Runnable setup,
                                               @NotNull Class<MCBasicTypesFullPrettyPrinter> expectedPrettyPrinter,
                                               @NotNull Class<ISynthesizeComponent> expectedCompSynthesizer) {
    Preconditions.checkNotNull(setup);
    Preconditions.checkNotNull(expectedPrettyPrinter);
    Preconditions.checkNotNull(expectedCompSynthesizer);

    // When
    setup.run();

    // Then
    Assertions.assertAll(
      () -> Assertions.assertEquals(expectedPrettyPrinter, ArcBasisMill.scopesGenitor().getTypePrinter().getClass()),
      () -> Assertions.assertEquals(expectedPrettyPrinter, ArcBasisMill.scopesGenitor().getTypePrinter().getClass())
    );
  }

  protected static Stream<Arguments> setupAndExpectedClassForSymTabCompleterProvider() {
    Runnable setupArcBasis = () -> {
      ArcBasisMill.reset();
      ArcBasisMill.init();
    };
    Runnable setupArcCore = () -> {
      ArcCoreMill.reset();
      ArcCoreMill.init();
    };
    Runnable setupMontiArc = () -> {
      MontiArcMill.reset();
      MontiArcMill.init();
    };
    return Stream.of(
      Arguments.of(setupArcBasis, MCBasicTypesFullPrettyPrinter.class,
        SynthesizeComponentFromMCBasicTypes.class),
      Arguments.of(setupArcCore, MCBasicTypesFullPrettyPrinter.class,
        SynthesizeComponentFromMCBasicTypes.class),
      Arguments.of(setupMontiArc, MCSimpleGenericTypesFullPrettyPrinter.class,
        SynthesizeComponentFromMCSimpleGenericTypes.class)
    );
  }
}