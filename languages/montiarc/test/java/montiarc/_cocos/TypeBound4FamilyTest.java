/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import variablearc._cocos.TypeBound4Family;
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.presence.PresenceResolver;

import java.util.stream.Stream;

import static montiarc.util.ArcError.TYPE_ARG_IGNORES_UPPER_BOUND;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link TypeBound4Family}.
 */
class TypeBound4FamilyTest extends TypeBoundTest {

  protected static MontiArcCoCoChecker checker() {
    WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo((ArcBasisASTArcComponentTypeCoCo) new TypeBound4Family(withinComponentResolver,
      new PresenceResolver(withinComponentResolver)));
    return checker;
  }

  @ParameterizedTest
  @MethodSource("validHeritageModels")
  @MethodSource("validHeritageModelsWithVariability")
  @Override
  void shouldNotReportErrorForHeritage(@NotNull String model) {
    this.shouldNotReportError(model);
  }

  @ParameterizedTest
  @MethodSource("invalidHeritageModels")
  @Override
  void shouldReportErrorForHeritage(@NotNull String model, @NotNull Error... errors) {
    this.shouldReportError(model, errors);
  }

  @ParameterizedTest
  @MethodSource("validSubcomponentModels")
  @MethodSource("validSubcomponentModelsWithVariability")
  @Override
  void shouldNotReportErrorForSubcomponent(@NotNull String model) {
    this.shouldNotReportError(model);
  }

  @ParameterizedTest
  @MethodSource("invalidSubcomponentModels")
  @MethodSource("invalidSubcomponentModelsWithVariability")
  @Override
  void shouldReportErrorForSubcomponent(@NotNull String model, @NotNull Error... errors) {
    this.shouldReportError(model, errors);
  }

  @ParameterizedTest
  @MethodSource("validRefinementModels")
  @Override
  void shouldNotReportErrorForRefinement(@NotNull String model) {
    this.shouldNotReportError(model);
  }

  @ParameterizedTest
  @MethodSource("invalidRefinementModels")
  @Override
  void shouldReportErrorForRefinement(@NotNull String model, @NotNull Error... errors) {
    this.shouldReportError(model, errors);
  }

  @Test
  void shouldWarnIfPresenceDependsOnUnknownValues() {
    // Given
    ASTMACompilationUnit ast = compile("""
      component UnknownComp(int n) {
        varif (java.lang.Math.abs(n) > 3) {
          a.b.D<boolean> sub;
        }
      }""");
    assertThat(Log.getFindings()).as("findings of the compilation").isEmpty();

    // When
    checker().checkAll(ast);

    // Then
    assertThat(Log.getFindings()).singleElement()
      .matches(Finding::isWarning)
      .extracting(Finding::getMsg).asString()
      .startsWith(TYPE_ARG_IGNORES_UPPER_BOUND.getErrorCode());
  }

  protected void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    // When
    checker().checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  protected void shouldReportError(@NotNull String model, @NotNull Error... errors) {
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

  protected static Stream<Arguments> validHeritageModelsWithVariability() {
    return Stream.of(
      // violated upper bound of a super component of a component type without configurations
      arg("""
        component ValidCompWithVariability1 extends a.b.D<boolean> {
          feature f;
          constraint (f && !f);
        }"""
      )
    );
  }

  protected static Stream<Arguments> validSubcomponentModelsWithVariability() {
    return Stream.of(
      // violated upper bound only in a variant excluded by a constraint
      arg("""
        component ValidCompWithVariability2 {
          feature f;
          varif (f) {
            a.b.D<boolean> sub;
          }
          constraint (!f);
        }"""
      ),
      // satisfied upper bounds in mutually exclusive variation points
      arg("""
        component ValidCompWithVariability3 {
          feature f;
          varif (f) {
            a.b.D<int> sub;
          } else {
            a.b.E<java.lang.Integer> sub;
          }
        }"""
      )
    );
  }

  protected static Stream<Arguments> invalidSubcomponentModelsWithVariability() {
    return Stream.of(
      // violated upper bound of an optional subcomponent
      arg("""
        component InvalidCompWithVariability1 {
          feature f;
          varif (f) {
            a.b.D<boolean> sub;
          }
        }""",
        TYPE_ARG_IGNORES_UPPER_BOUND
      ),
      // violated upper bound of a subcomponent of a core feature
      arg("""
        component InvalidCompWithVariability2 {
          feature f;
          varif (f) {
            a.b.E<java.lang.String> sub;
          }
          constraint (f);
        }""",
        TYPE_ARG_IGNORES_UPPER_BOUND
      )
    );
  }
}
