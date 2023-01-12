/* (c) https://github.com/MontiCore/monticore */
package montiarc._auxiliary;

import arcbasis._visitor.IFullPrettyPrinter;
import arcbasis.check.ArcBasisSynthesizeComponent;
import arcbasis.check.ISynthesizeComponent;
import arccore.ArcCoreMill;
import com.google.common.base.Preconditions;
import de.monticore.types.check.ISynthesize;
import genericarc.GenericArcMill;
import genericarc._prettyprint.GenericArcFullPrettyPrinter;
import genericarc.check.GenericArcTypeCalculator;
import montiarc.MontiArcMill;
import montiarc._prettyprint.MontiArcFullPrettyPrinter;
import montiarc.check.MontiArcSynthesizeComponent;
import montiarc.check.MontiArcTypeCalculator;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class GenericArcMillForMontiArcTest {

  protected static Stream<Arguments> setupAndExpectedClassForSymTabCompleterProvider() {
    Runnable setupGenericArc = () -> {
      GenericArcMill.reset();
      GenericArcMill.init();
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
      Arguments.of(setupGenericArc,
        GenericArcFullPrettyPrinter.class, ArcBasisSynthesizeComponent.class, GenericArcTypeCalculator.class),
      Arguments.of(setupArcCore,
        GenericArcFullPrettyPrinter.class, ArcBasisSynthesizeComponent.class, GenericArcTypeCalculator.class),
      Arguments.of(setupMontiArc,
        MontiArcFullPrettyPrinter.class, MontiArcSynthesizeComponent.class, MontiArcTypeCalculator.class)
    );
  }

  /**
   * Ensures that the symbol table completer is initialized with the expected type printer and component synthesizer
   * with respect to the initialized mill. That is, the mill should provide a symbol table completer that is initialized
   * with a {@link IFullPrettyPrinter} and a {@link ArcBasisSynthesizeComponent} when using the
   * {@link GenericArcMill}, respectively provide a symbol table completer that is initialized with a {@link
   * IFullPrettyPrinter} and {@link MontiArcSynthesizeComponent} when using the {@link MontiArcMill}.
   *
   * @param setup                   The setup to execute, e.g., initialize the respective mill.
   * @param expectedPrettyPrinter   The expected class of the type printer of the symbol table completer.
   * @param expectedCompSynthesizer The expected class of the component synthesizer of the symbol-table completer.
   * @param expectedTypeSynthesizer The expected class of the type synthesizer of the symbol-table completer.
   */
  @ParameterizedTest
  @MethodSource("setupAndExpectedClassForSymTabCompleterProvider")
  public void shouldProvideCompleterAsExpected(@NotNull Runnable setup,
                                               @NotNull Class<IFullPrettyPrinter> expectedPrettyPrinter,
                                               @NotNull Class<ISynthesizeComponent> expectedCompSynthesizer,
                                               @NotNull Class<ISynthesize> expectedTypeSynthesizer) {
    Preconditions.checkNotNull(setup);
    Preconditions.checkNotNull(expectedPrettyPrinter);
    Preconditions.checkNotNull(expectedCompSynthesizer);
    Preconditions.checkNotNull(expectedTypeSynthesizer);

    // When
    setup.run();

    // Then
    Assertions.assertAll(
      () -> Assertions.assertEquals(expectedPrettyPrinter,
        GenericArcMill.symbolTableCompleter().getTypePrinter().getClass()),
      () -> Assertions.assertEquals(expectedCompSynthesizer,
        GenericArcMill.symbolTableCompleter().getComponentSynthesizer().getClass()),
      () -> Assertions.assertEquals(expectedTypeSynthesizer,
        GenericArcMill.symbolTableCompleter().getTypeCalculator().getClass())
    );
  }
}