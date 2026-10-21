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
import variablearc._cocos.BehaviorInDecomposed4Family;
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.presence.PresenceResolver;

import java.util.stream.Stream;

import static montiarc.util.ArcError.DECOMPOSED_COMPONENT_WITH_BEHAVIOR;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link BehaviorInDecomposed4Family}.
 */
class BehaviorInDecomposed4FamilyTest extends BehaviorInDecomposedTest {

  protected static MontiArcCoCoChecker checker() {
    WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new BehaviorInDecomposed4Family(withinComponentResolver, new PresenceResolver(withinComponentResolver)));
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
  void shouldReportError(@NotNull String model, @NotNull Error[] errors) {
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
      // behavior only in the variant without subcomponent
      arg("""
        component ValidCompWithVariability1 {
          feature f;
          varif (f) {
            a.b.A sub;
          } else {
            automaton { }
          }
        }"""
      ),
      // subcomponent in a dead variation point
      arg("""
        component ValidCompWithVariability2 {
          feature f;
          varif (f) {
            a.b.A sub;
          }
          automaton { }
          constraint (!f);
        }"""
      ),
      // behavior and subcomponent in mutually exclusive variation points
      arg("""
        component ValidCompWithVariability3 {
          feature f1, f2;
          varif (f1) {
            a.b.A sub;
          }
          varif (f2) {
            compute { }
          }
          constraint (!(f1 && f2));
        }"""
      )
    );
  }

  protected static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // behavior in a component that is decomposed in some variant
      arg("""
        component InvalidCompWithVariability1 {
          feature f;
          varif (f) {
            a.b.A sub;
          }
          automaton { }
        }""",
        DECOMPOSED_COMPONENT_WITH_BEHAVIOR
      ),
      // optional behavior in a decomposed component
      arg("""
        component InvalidCompWithVariability2 {
          feature f;
          a.b.A sub;
          varif (f) {
            compute { }
          }
        }""",
        DECOMPOSED_COMPONENT_WITH_BEHAVIOR
      ),
      // behavior and subcomponent of a core feature
      arg("""
        component InvalidCompWithVariability3 {
          feature f;
          varif (f) {
            a.b.A sub;
          }
          automaton { }
          constraint (f);
        }""",
        DECOMPOSED_COMPONENT_WITH_BEHAVIOR
      )
    );
  }
}
