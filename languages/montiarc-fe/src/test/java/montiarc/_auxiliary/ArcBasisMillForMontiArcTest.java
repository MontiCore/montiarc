/* (c) https://github.com/MontiCore/monticore */
package montiarc._auxiliary;

import arcbasis.ArcBasisMill;
import arcbasis.check.ArcBasisSynthesizeComponent;
import arcbasis.check.ArcBasisSynthesizeType;
import arcbasis.check.ISynthesizeComponent;
import arccore.ArcCoreMill;
import com.google.common.base.Preconditions;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.monticore.types.prettyprint.MCSimpleGenericTypesFullPrettyPrinter;
import montiarc.MontiArcMill;
import montiarc.check.MontiArcSynthesizeComponent;
import montiarc.check.MontiArcSynthesizeType;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class ArcBasisMillForMontiArcTest {

  /**
   * Ensures that the symbol table completer is initialized with the expected type printer and component synthesizer
   * with respect to the initialized mill. That is, the mill should provide a symbol table completer that is initialized
   * with a {@link MCBasicTypesFullPrettyPrinter}, a {@link ArcBasisSynthesizeComponent}, and a {@link
   * ArcBasisSynthesizeType} when using the  {@link ArcBasisMill}, respectively provide a symbol table completer that is
   * initialized with a {@link MCSimpleGenericTypesFullPrettyPrinter}, a {@link ArcBasisSynthesizeComponent}, and a
   * {@link MontiArcSynthesizeType} when using the {@link MontiArcMill}.
   *
   * @param setup                      The setup to execute, e.g., initialize the respective mill.
   * @param expectedPrettyPrinter      The expected class of the type printer of the symbol table completer.
   * @param expectedCompSynthesizer    The expected class of the component synthesizer of the symbol-table completer.
   * @param expectedSymTypeSynthesizer The expected class of the sym type synthesizer of the symbol-table completer.
   */
  @ParameterizedTest
  @MethodSource("setupAndExpectedClassForSymTabCompleterProvider")
  public void shouldProvideCompleterAsExpected(@NotNull Runnable setup,
                                               @NotNull Class<MCBasicTypesFullPrettyPrinter> expectedPrettyPrinter,
                                               @NotNull Class<ISynthesizeComponent> expectedCompSynthesizer,
                                               @NotNull Class<ISynthesize> expectedSymTypeSynthesizer) {
    Preconditions.checkNotNull(setup);
    Preconditions.checkNotNull(expectedPrettyPrinter);
    Preconditions.checkNotNull(expectedCompSynthesizer);
    Preconditions.checkNotNull(expectedSymTypeSynthesizer);

    // When
    setup.run();

    // Then
    Assertions.assertAll(
      () -> Assertions.assertEquals(expectedPrettyPrinter,
        ArcBasisMill.symbolTableCompleter().getTypePrinter().getClass()),
      () -> Assertions.assertEquals(expectedCompSynthesizer,
        ArcBasisMill.symbolTableCompleter().getComponentSynthesizer().getClass()),
      () -> Assertions.assertEquals(expectedSymTypeSynthesizer,
        ArcBasisMill.symbolTableCompleter().getTypeSynthesizer().getClass())
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
        ArcBasisSynthesizeComponent.class, ArcBasisSynthesizeType.class),
      Arguments.of(setupArcCore, MCBasicTypesFullPrettyPrinter.class,
        ArcBasisSynthesizeComponent.class, ArcBasisSynthesizeType.class),
      Arguments.of(setupMontiArc, MCSimpleGenericTypesFullPrettyPrinter.class,
        MontiArcSynthesizeComponent.class, MontiArcSynthesizeType.class)
    );
  }
}