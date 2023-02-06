/* (c) https://github.com/MontiCore/monticore */
package montiarc._auxiliary;

import arcbasis._visitor.IFullPrettyPrinter;
import arcbasis.check.ArcBasisSynthesizeComponent;
import arcbasis.check.ISynthesizeComponent;
import arcbasis.check.deser.ComposedCompTypeExprDeSer;
import arccore.ArcCoreMill;
import com.google.common.base.Preconditions;
import de.monticore.types.check.ISynthesize;
import genericarc.GenericArcMill;
import genericarc._prettyprint.GenericArcFullPrettyPrinter;
import genericarc.check.GenericArcCompTypeExprDeSer;
import genericarc.check.GenericArcTypeCalculator;
import montiarc.MontiArcMill;
import montiarc._prettyprint.MontiArcFullPrettyPrinter;
import montiarc.check.MontiArcCompTypeExprDeSer;
import montiarc.check.MontiArcSynthesizeComponent;
import montiarc.check.MontiArcTypeCalculator;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class GenericArcMillForMontiArcTest {

  protected static Stream<Arguments> setupAndExpectedClassForSymTabCompleterProvider() {
    return Stream.of(
      Arguments.of(genericArcMillSetup(),
        GenericArcFullPrettyPrinter.class, ArcBasisSynthesizeComponent.class, GenericArcTypeCalculator.class),
      Arguments.of(arcCoreMillSetup(),
        GenericArcFullPrettyPrinter.class, ArcBasisSynthesizeComponent.class, GenericArcTypeCalculator.class),
      Arguments.of(montiArcMillSetup(),
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

  protected static Stream<Arguments> setupAndExpectedClassForCompTypeExprDeSerProvider() {
    return Stream.of(
      Arguments.of(genericArcMillSetup(), GenericArcCompTypeExprDeSer.class),
      Arguments.of(arcCoreMillSetup(), GenericArcCompTypeExprDeSer.class),  // Will change later to include generics
      Arguments.of(montiArcMillSetup(), MontiArcCompTypeExprDeSer.class)  // Will change later to include generics
    );
  }

  /**
   * Ensures that the component type expression (de)serializer has the right type with respect to the initialized mill.
   *
   * @param setup         The setup to execute, e.g., initialize the respective mill.
   * @param expectedDeSer The class of the (de)serializer that the mill should instantiate.
   */
  @ParameterizedTest
  @MethodSource("setupAndExpectedClassForCompTypeExprDeSerProvider")
  void shouldProvideCompTypeExprDeSerAsExpected(@NotNull Runnable setup,
                                                @NotNull Class<ComposedCompTypeExprDeSer> expectedDeSer) {
    Preconditions.checkNotNull(setup);
    Preconditions.checkNotNull(expectedDeSer);

    // When
    setup.run();

    // Then
    Assertions.assertInstanceOf(expectedDeSer, GenericArcMill.millCompTypeExprDeSer());
  }

  /**
   * @return a Runnable that configures the {@link GenericArcMill} as the Mill to use.
   */
  protected static Named<Runnable> genericArcMillSetup() {
    return Named.of("GenericArcMill",
      () -> {
        GenericArcMill.reset();
        GenericArcMill.init();
      }
    );
  }

  /**
   * @return a Runnable that configures the {@link ArcCoreMill} as the Mill to use.
   */
  protected static Named<Runnable> arcCoreMillSetup() {
    return Named.of("ArcCoreMill",
      () -> {
        ArcCoreMill.reset();
        ArcCoreMill.init();
      }
    );
  }

  /**
   * @return a Runnable that configures the {@link MontiArcMill} as the Mill to use.
   */
  protected static Named<Runnable> montiArcMillSetup() {
    return Named.of("MontiArcMill",
      () -> {
        MontiArcMill.reset();
        MontiArcMill.init();
      }
    );
  }
}