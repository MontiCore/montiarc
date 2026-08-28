/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import modes._cocos.StatechartContainsNoMode;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static montiarc.util.ModesError.STATECHART_CONTAINS_MODE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link StatechartContainsNoMode}.
 */
class StatechartContainsNoModeTest extends MontiArcTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    // no states, no modes
    "component ValidComp1 { automaton { } }",
    // one state, no modes
    "component ValidComp2 { automaton { state s0; } }",
    // two states, no modes
    "component ValidComp3 { automaton { state s0; state s1; } }",
    // three states, no modes
    "component ValidComp4 { automaton { state s0; state s1; state s2; } }"
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new StatechartContainsNoMode());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new StatechartContainsNoMode());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // no states, one mode
      arg("component InvalidComp1 { automaton { mode m1 { } } }",
        STATECHART_CONTAINS_MODE),
      // two states, one mode (mode declared between states)
      arg("component InvalidComp2 { automaton { state s1; mode m1 { } state s2; } }",
        STATECHART_CONTAINS_MODE),
      // one state, one mode
      arg("component InvalidComp3 { automaton { state s0; mode m0 { } } }",
        STATECHART_CONTAINS_MODE),
      // three states, one mode
      arg("component InvalidComp4 { automaton { state s0; state s1; state s2; mode m0 { } } }",
        STATECHART_CONTAINS_MODE),
      // no states, two modes
      arg("component InvalidComp5 { automaton { mode m0 { } mode m1 { } } }",
        STATECHART_CONTAINS_MODE),
      // one state, two modes
      arg("component InvalidComp6 { automaton { state s0; mode m0 { } mode m1 { } } }",
        STATECHART_CONTAINS_MODE),
      // nested component type with two automata, one with a mode
      arg("component InvalidComp7 { component Inner { automaton { state s1; } automaton { state s1; mode m1 { } } } }",
        STATECHART_CONTAINS_MODE)
    );
  }
}
