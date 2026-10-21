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
import variablearc._cocos.NoOtherInputPortInMsgTransition4Family;
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.presence.PresenceResolver;

import java.util.stream.Stream;

import static montiarc.util.ArcError.IN_PORT_REF_IN_INVALID_CONTEXT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link NoOtherInputPortInMsgTransition4Family}.
 */
class NoOtherInputPortInMsgTransition4FamilyTest extends NoOtherInputPortInMsgTransitionTest {

  protected static MontiArcCoCoChecker checker() {
    WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoOtherInputPortInMsgTransition4Family(withinComponentResolver, new PresenceResolver(withinComponentResolver)));
    return checker;
  }

  @ParameterizedTest
  @MethodSource("validModels")
  @MethodSource("validModelsWithVariability")
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
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    // When
    checker().checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
    assertThat(Log.getFindings()).as("findings independent of unknown values are errors")
      .allMatch(Finding::isError);
  }

  static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      // read value from other input port in message-event triggered transition, dead variation point
      arg("""
        component ValidCompWithVariability1 {
          port in int i1;
          port in int i2;
          varif (false) {
            automaton {
              initial state S;
              S -> S i1 / { int x = i2; }
            }
          }
        }
        """
      ),
      // read value from other input port in message-event triggered transition, dead feature
      arg("""
        component ValidCompWithVariability2 {
          feature f;
          port in int i1;
          port in int i2;
          varif (f) {
            automaton {
              initial state S;
              S -> S i1 / { int x = i2; }
            }
          }
          constraint (!f);
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // read value from other input port in message-event triggered transition, tautological variation point
      arg("""
          component InvalidCompWithVariability1 {
            port in int i1;
            port in int i2;
            varif (true) {
              automaton {
                initial state S;
                S -> S i1 / { int x = i2; }
              }
            }
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // read value from other input port in message-event triggered transition, core feature
      arg("""
          component InvalidCompWithVariability2 {
            feature f;
            port in int i1;
            port in int i2;
            varif (f) {
              automaton {
                initial state S;
                S -> S i1 / { int x = i2; }
              }
            }
            constraint (f);
          }
          """,
        IN_PORT_REF_IN_INVALID_CONTEXT
      )
    );
  }
}
