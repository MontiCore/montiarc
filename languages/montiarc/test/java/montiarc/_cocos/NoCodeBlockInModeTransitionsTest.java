/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import modes._cocos.NoCodeBlockInModeTransitions;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static montiarc.util.ModesError.MODE_AUTOMATON_TRANSITION_CONTAINS_ACTION;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link NoCodeBlockInModeTransitions}.
 */
class NoCodeBlockInModeTransitionsTest extends MontiArcTestBase {

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoCodeBlockInModeTransitions());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoCodeBlockInModeTransitions());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> validModels() {
    return Stream.of(
      // mode transition without action block
      arg("""
        component ValidComp1 {
          mode automaton {
            initial mode A { }
            mode B { }
            A -> B;
          }
        }
        """
      ),
      // mode transition without action block
      arg("""
        component ValidComp2 {
          mode automaton {
            initial mode Init { }
            mode Ready { }
            Init -> Ready;
          }
        }
        """
      )
    );
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // mode transition with a non-empty action block
      arg("""
        component InvalidComp1 {
          port out int x;
          mode automaton {
            initial mode A { }
            mode B { }
            A -> B / { x = 1; }
          }
        }
        """,
        MODE_AUTOMATON_TRANSITION_CONTAINS_ACTION
      ),
      // mode transition with an empty action block
      arg("""
        component InvalidComp2 {
          mode automaton {
            initial mode A { }
            mode B { }
            A -> B / { }
          }
        }
        """,
        MODE_AUTOMATON_TRANSITION_CONTAINS_ACTION
      )
    );
  }
}
