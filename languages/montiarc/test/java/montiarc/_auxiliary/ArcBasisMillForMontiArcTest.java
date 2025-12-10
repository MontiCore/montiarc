/* (c) https://github.com/MontiCore/monticore */
package montiarc._auxiliary;

import arcbasis.ArcBasisMill;
import com.google.common.base.Preconditions;
import de.monticore.types.check.FullSynthesizeCompKindFromMCSimpleGenericTypes;
import de.monticore.types.check.ISynthesizeComponent;
import montiarc.MontiArcMill;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class ArcBasisMillForMontiArcTest {

  protected static Stream<Arguments> setupAndExpectedClassForScopesGenitorP2Provider() {
    return Stream.of(
      Arguments.of(arcBasisMillSetup(), FullSynthesizeCompKindFromMCSimpleGenericTypes.class),
      Arguments.of(montiArcMillSetup(), FullSynthesizeCompKindFromMCSimpleGenericTypes.class)
    );
  }

  /**
   * Ensures that the scopes genitor p2 is initialized with the expected type
   * printer and component synthesizer with respect to the initialized mill.
   * That is, the mill should provide a scopes genitor p2 that is initialized
   * with a {@link FullSynthesizeCompKindFromMCSimpleGenericTypes} when using the {@link ArcBasisMill},
   * respectively provide a scopes genitor p2 that is initialized with a
   * {@link FullSynthesizeCompKindFromMCSimpleGenericTypes}, when using the {@link MontiArcMill}.
   *
   * @param setup                   The setup to execute, e.g., initialize the respective mill.
   * @param expectedCompSynthesizer The expected class of the component synthesizer of the scopes genitor p2.
   */
  @ParameterizedTest
  @MethodSource("setupAndExpectedClassForScopesGenitorP2Provider")
  void shouldProvideCompleterAsExpected(@NotNull Runnable setup,
                                        @NotNull Class<ISynthesizeComponent> expectedCompSynthesizer) {
    Preconditions.checkNotNull(setup);
    Preconditions.checkNotNull(expectedCompSynthesizer);

    // When
    setup.run();

    // Then
    Assertions.assertEquals(expectedCompSynthesizer,
      ArcBasisMill.scopesGenitorP2().getComponentSynthesizer().getClass());
  }

  /**
   * @return a Runnable that configures the {@link ArcBasisMill} as the Mill to use.
   */
  protected static Named<Runnable> arcBasisMillSetup() {
    return Named.of("ArcBasisMill",
      () -> {
        ArcBasisMill.reset();
        ArcBasisMill.init();
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
