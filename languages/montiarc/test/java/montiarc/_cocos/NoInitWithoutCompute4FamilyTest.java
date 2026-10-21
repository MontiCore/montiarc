/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import variablearc._cocos.NoInitWithoutCompute4Family;
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.presence.PresenceResolver;

import java.util.stream.Stream;

import static montiarc.util.ArcComputeError.INIT_BLOCK_WITHOUT_COMPUTE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link NoInitWithoutCompute4Family}.
 */
class NoInitWithoutCompute4FamilyTest extends NoInitWithoutComputeTest {

  protected static MontiArcCoCoChecker checker() {
    WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoInitWithoutCompute4Family(withinComponentResolver, new PresenceResolver(withinComponentResolver)));
    return checker;
  }

  @ParameterizedTest
  @MethodSource("validModels")
  @MethodSource("validModelsWithVariability")
  @Override
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    // When
    checker().checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  @MethodSource("invalidModelsWithVariability")
  @Override
  void shouldReportError(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    // When
    checker().checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(errors));
    assertThat(Log.getFindings()).as("findings independent of unknown values are errors")
      .allMatch(Finding::isError);
  }

  protected static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      // init and compute block in the same variation point
      arg("""
        component ValidCompWithVariability1 {
          feature f;
          varif (f) {
            init { }
            compute { }
          }
        }"""
      ),
      // optional init block with mandatory compute block
      arg("""
        component ValidCompWithVariability2 {
          feature f;
          varif (f) {
            init { }
          }
          compute { }
        }"""
      ),
      // init and compute block in variation points of the same feature
      arg("""
        component ValidCompWithVariability3 {
          feature f;
          varif (f) {
            init { }
          }
          varif (f) {
            compute { }
          }
        }"""
      )
    );
  }

  protected static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // init block only in the variant without compute block
      arg("""
        component InvalidCompWithVariability1 {
          feature f;
          varif (f) {
            init { }
          } else {
            compute { }
          }
        }""",
        INIT_BLOCK_WITHOUT_COMPUTE
      ),
      // mandatory init block with optional compute block
      arg("""
        component InvalidCompWithVariability2 {
          feature f;
          init { }
          varif (f) {
            compute { }
          }
        }""",
        INIT_BLOCK_WITHOUT_COMPUTE
      ),
      // init and compute block in independent variation points
      arg("""
        component InvalidCompWithVariability3 {
          feature f1, f2;
          varif (f1) {
            init { }
          }
          varif (f2) {
            compute { }
          }
        }""",
        INIT_BLOCK_WITHOUT_COMPUTE
      ),
      // compute block in a dead variation point
      arg("""
        component InvalidCompWithVariability4 {
          feature f;
          init { }
          varif (f) {
            compute { }
          }
          constraint (!f);
        }""",
        INIT_BLOCK_WITHOUT_COMPUTE
      )
    );
  }
}
