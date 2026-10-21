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
import variablearc._cocos.MaxOneInit4Family;
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.presence.PresenceResolver;

import java.util.stream.Stream;

import static montiarc.util.ArcComputeError.MULTIPLE_INIT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link MaxOneInit4Family}.
 */
class MaxOneInit4FamilyTest extends MaxOneInitTest {

  protected static MontiArcCoCoChecker checker() {
    WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new MaxOneInit4Family(withinComponentResolver, new PresenceResolver(withinComponentResolver)));
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
      // init blocks in mutually exclusive variation points
      arg("""
        component ValidCompWithVariability1 {
          feature f;
          varif (f) {
            init { }
          } else {
            init { }
          }
          compute { }
        }"""
      ),
      // init blocks in variation points excluded by a constraint
      arg("""
        component ValidCompWithVariability2 {
          feature f1, f2;
          varif (f1) {
            init { }
          }
          varif (f2) {
            init { }
          }
          compute { }
          constraint (!(f1 && f2));
        }"""
      ),
      // second init block only in the decomposed variant
      arg("""
        component ValidCompWithVariability3 {
          feature f;
          component Inner { }
          init { }
          varif (f) {
            Inner sub;
            init { }
          }
        }"""
      )
    );
  }

  protected static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // optional second init block
      arg("""
        component InvalidCompWithVariability1 {
          feature f;
          varif (f) {
            init { }
          }
          init { }
          compute { }
        }""",
        MULTIPLE_INIT
      ),
      // init blocks in independent variation points
      arg("""
        component InvalidCompWithVariability2 {
          feature f1, f2;
          varif (f1) {
            init { }
          }
          varif (f2) {
            init { }
          }
          compute { }
        }""",
        MULTIPLE_INIT
      ),
      // second init block of a core feature
      arg("""
        component InvalidCompWithVariability3 {
          feature f;
          init { }
          varif (f) {
            init { }
          }
          compute { }
          constraint (f);
        }""",
        MULTIPLE_INIT
      )
    );
  }
}
