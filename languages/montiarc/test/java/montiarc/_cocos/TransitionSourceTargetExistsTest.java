/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.scbasis._cocos.TransitionSourceTargetExists;
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

import static montiarc.util.SCError.CANT_FIND_SOURCE;
import static montiarc.util.SCError.CANT_FIND_TARGET;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link TransitionSourceTargetExists}.
 */
class TransitionSourceTargetExistsTest extends MontiArcTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    // no automaton
    "component ValidComp1 { }",
    // automaton without transitions
    "component ValidComp2 { automaton { } }",
    // self-loop
    "component ValidComp3 { automaton { initial state s; s -> s; } }",
    // single transition
    "component ValidComp4 { automaton { initial state s1; state s2; s1 -> s2; } }",
    // multiple transitions
    "component ValidComp5 { automaton { initial state s1; state s2; state s3; s1 -> s2; s2 -> s3; s3 -> s1; } }",
    // single transition before its states are declared
    "component ValidComp6 { automaton { s1 -> s2; initial state s1; state s2; } }",
    // multiple transitions before their states are declared
    "component ValidComp7 { automaton { s1 -> s2; s2 -> s3; s3 -> s1; initial state s1; state s2; state s3; } }",
    // transition between sibling states in a hierarchical state
    "component ValidComp8 { automaton { initial state s1 { state s2; state s3; s2 -> s3; } } }",
    // transition from a hierarchical state up to a sibling state
    "component ValidComp9 { automaton { initial state s1 { state s2; } s1 -> s2; } }",
    // transition from a sibling state down into a hierarchical state
    "component ValidComp10 { automaton { initial state s1 { state s2; } s2 -> s1; } }"
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new TransitionSourceTargetExists());

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
    checker.addCoCo(new TransitionSourceTargetExists());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // single transition with a missing source state
      arg("component InvalidComp1 { automaton { state s2; s1 -> s2; } }",
        CANT_FIND_SOURCE),
      // single transition with a missing target state
      arg("component InvalidComp2 { automaton { initial state s1; s1 -> s2; } }",
        CANT_FIND_TARGET),
      // single transition with missing source and target states
      arg("component InvalidComp3 { automaton { s1 -> s2; } }",
        CANT_FIND_SOURCE,
        CANT_FIND_TARGET),
      // self-loop with a missing state
      arg("component InvalidComp4 { automaton { s -> s; } }",
        CANT_FIND_SOURCE,
        CANT_FIND_TARGET),
      // multiple transitions with a missing source state
      arg("component InvalidComp5 { automaton { state s2; s1 -> s2; s1 -> s2; } }",
        CANT_FIND_SOURCE,
        CANT_FIND_SOURCE),
      // multiple transitions with a missing target state
      arg("component InvalidComp6 { automaton { initial state s1; s1 -> s2; s1 -> s2; } }",
        CANT_FIND_TARGET,
        CANT_FIND_TARGET)
    );
  }
}
