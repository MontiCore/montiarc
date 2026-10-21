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
import variablearc._cocos.VariationPointPresent;
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.presence.PresenceResolver;

import java.util.stream.Stream;

import static montiarc.util.VariableArcError.VARIATION_POINT_NOT_PRESENT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link VariationPointPresent}.
 */
class VariationPointPresentTest extends MontiArcTestBase {

  protected static MontiArcCoCoChecker checker() {
    WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new VariationPointPresent(new PresenceResolver(withinComponentResolver)));
    return checker;
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // both branches present in some configuration
    "component ValidComp1 { feature f; varif (f) { port in int i; } else { port in int j; } }",
    // nested branch present in some configuration
    "component ValidComp2 { feature f, g; varif (f) { varif (g) { port in int i; } } }",
    // branch depending on a parameter
    "component ValidComp3(boolean p) { varif (p) { port in int i; } }",
    // no valid configuration at all, reported by ConstraintSatisfied4Comp
    "component ValidComp4 { feature f; constraint(false); varif (f) { port in int i; } }"
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
      .allMatch(msg -> msg.startsWith(VARIATION_POINT_NOT_PRESENT.getErrorCode()));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // else branch excluded by a constraint
      Arguments.of("component InvalidComp1 { feature f; constraint(f); varif (f) { port in int i; } else { port in int j; } }", 1),
      // nested branches of an absent branch are not reported again
      Arguments.of("component InvalidComp2 { feature f; constraint(!f); varif (f) { varif (f) { port in int i; } } }", 1),
      // nested branch contradicting its enclosing branch
      Arguments.of("component InvalidComp3 { feature f, g; constraint(f == g); varif (f) { varif (!g) { port in int i; } } }", 1),
      // both branches of different varifs absent
      Arguments.of("component InvalidComp4 { feature f; constraint(f); varif (!f) { port in int i; } varif (f) { } else { port in int j; } }", 2)
    );
  }
}
