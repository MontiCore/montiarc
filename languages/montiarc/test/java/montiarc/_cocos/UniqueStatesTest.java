/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.scbasis._cocos.UniqueStates;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static montiarc.util.SCError.DUPLICATE_STATE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link UniqueStates}.
 */
class UniqueStatesTest extends MontiArcTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    // no automaton
    "component ValidComp1 { }",
    // automaton without states
    "component ValidComp2 { automaton { } }",
    // automaton with one state
    "component ValidComp3 { automaton { initial state s; } }",
    // automaton with multiple uniquely named states
    "component ValidComp4 { automaton { initial state s1; state s2; state s3; } }",
    // nested component with a state of the same name as the outer automaton's state
    "component ValidComp5 { automaton { initial state s; } component Inner { automaton { initial state s2; } } }"
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new UniqueStates());

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
    checker.addCoCo(new UniqueStates());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // two states with the same name
      arg("""
        component InvalidComp1 {
          automaton {
            state s;
            state s;
          }
        }
        """,
        DUPLICATE_STATE
      ),
      // three states with the same name
      arg("""
        component InvalidComp2 {
          automaton {
            state s;
            state s;
            state s;
          }
        }
        """,
        DUPLICATE_STATE
      ),
      // nested state with the same name as an outer state
      arg("""
        component InvalidComp3 {
          automaton {
            state s1;
            state s2 {
              state s1;
            }
          }
        }
        """,
        DUPLICATE_STATE
      )
    );
  }
}
