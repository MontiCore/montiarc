/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import variablearc._cocos.FeatureSelectable;
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.presence.PresenceResolver;

import java.util.stream.Stream;

import static montiarc.util.VariableArcError.FEATURE_NOT_SELECTABLE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link FeatureSelectable}.
 */
class FeatureSelectableTest extends MontiArcTestBase {

  protected static MontiArcCoCoChecker checker() {
    WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new FeatureSelectable(new PresenceResolver(withinComponentResolver)));
    return checker;
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // no features
    "component ValidComp1 { }",
    // unconstrained feature
    "component ValidComp2 { feature f; }",
    // feature required by a constraint
    "component ValidComp3 { feature f; constraint(f); }",
    // feature restricted but selectable
    "component ValidComp4 { feature f, g; constraint(!f || g); }",
    // feature bound to a parameter
    "component ValidComp5(boolean p) { feature f; constraint(f == p); }",
    // no valid configuration at all, reported by ConstraintSatisfied4Comp
    "component ValidComp6 { feature f; constraint(false); }"
  })
  void shouldNotReportWarning(@NotNull String model) {
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
  void shouldReportWarning(@NotNull String model, int warnings) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    // When
    checker().checkAll(ast);

    // Then
    assertThat(Log.getFindings()).allMatch(Finding::isWarning)
      .extracting(Finding::getMsg)
      .hasSize(warnings)
      .allMatch(msg -> msg.startsWith(FEATURE_NOT_SELECTABLE.getErrorCode()));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // feature excluded by a constraint
      Arguments.of("component InvalidComp1 { feature f; constraint(!f); }", 1),
      // feature excluded by its combination with a required feature
      Arguments.of("component InvalidComp2 { feature f, g; constraint(!(f && g) && g); }", 1),
      // two features excluded
      Arguments.of("component InvalidComp3 { feature f, g; constraint(f == g && !g); }", 2)
    );
  }
}
