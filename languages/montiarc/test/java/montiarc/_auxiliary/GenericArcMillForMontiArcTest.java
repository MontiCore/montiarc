/* (c) https://github.com/MontiCore/monticore */
package montiarc._auxiliary;

import arcbasis.check.ArcBasisSynthesizeComponent;
import arcbasis.check.ISynthesizeComponent;
import arccore.ArcCoreMill;
import com.google.common.base.Preconditions;
import de.monticore.types.check.FullCompKindExprDeSer;
import genericarc.GenericArcMill;
import genericarc.check.GenericArcCompTypeExprDeSer;
import montiarc.MontiArcMill;
import montiarc.check.MontiArcCompTypeExprDeSer;
import montiarc.check.MontiArcSynthesizeComponent;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class GenericArcMillForMontiArcTest {

  protected static Stream<Arguments> setupAndExpectedClassForSymTabCompleterProvider() {
    return Stream.of(
      Arguments.of(genericArcMillSetup(), ArcBasisSynthesizeComponent.class),
      Arguments.of(arcCoreMillSetup(), ArcBasisSynthesizeComponent.class),
      Arguments.of(montiArcMillSetup(), MontiArcSynthesizeComponent.class)
    );
  }

  /**
   * Ensures that the scopes genitor p2 is initialized with the expected type
   * printer and component synthesizer with respect to the initialized mill.
   * That is, the mill should provide a scopes genitor p2 that is initialized
   * with a {@link ArcBasisSynthesizeComponent} when using the {@link GenericArcMill},
   * respectively provide a scopes genitor p2 that is initialized with a
   * {@link MontiArcSynthesizeComponent} when using the {@link MontiArcMill}.
   *
   * @param setup                   The setup to execute, e.g., initialize the respective mill.
   * @param expectedCompSynthesizer The expected class of the component synthesizer of the scopes genitor p2.
   */
  @ParameterizedTest
  @MethodSource("setupAndExpectedClassForSymTabCompleterProvider")
  public void shouldProvideCompleterAsExpected(@NotNull Runnable setup,
                                               @NotNull Class<ISynthesizeComponent> expectedCompSynthesizer) {
    Preconditions.checkNotNull(setup);
    Preconditions.checkNotNull(expectedCompSynthesizer);

    // When
    setup.run();

    // Then
    Assertions.assertAll(
      () -> Assertions.assertEquals(expectedCompSynthesizer,
        GenericArcMill.scopesGenitorP2().getComponentSynthesizer().getClass())
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
                                                @NotNull Class<FullCompKindExprDeSer> expectedDeSer) {
    Preconditions.checkNotNull(setup);
    Preconditions.checkNotNull(expectedDeSer);

    // When
    setup.run();

    // Then
    Assertions.assertInstanceOf(expectedDeSer, GenericArcMill.compTypeExprDeSer());
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