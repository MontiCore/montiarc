/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.scbasis._cocos.MaxOneInitialState;
import de.monticore.scstatehierarchy.NoSubstatesHandler;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._visitor.MontiArcTraverser;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static montiarc.util.SCError.MORE_THAN_ONE_INITIAL_STATE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link MaxOneInitialState}.
 */
class MaxOneInitialStateTest extends MontiArcTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    // no automaton
    "component ValidComp1 { }",
    // automaton without states
    "component ValidComp2 { automaton { } }",
    // automaton without an initial state (multiple states)
    "component ValidComp3 { automaton { state s1; state s2; } }",
    // automaton with an initial state
    "component ValidComp4 { automaton { initial state s; } }",
    // automaton with an initial state (multiple states)
    "component ValidComp5 { automaton { initial state s1; state s2; state s3; } }",
    // nested component type with an automaton with an initial state
    "component ValidComp6 { component Inner { automaton { initial state s; } } }",
    // two nested component types, each with an automaton with an initial state
    "component ValidComp7 { component Inner1 { automaton { initial state s1; } } component Inner2 { automaton { initial state s2; } } }",
    // mode automaton without modes
    "component ValidComp8 { mode automaton { } }",
    // mode automaton and automaton, both without states
    "component ValidComp9 { mode automaton { } automaton { } }",
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    MontiArcTraverser traverser = MontiArcMill.inheritanceTraverser();
    traverser.setSCStateHierarchyHandler(new NoSubstatesHandler());
    checker.addCoCo(new MaxOneInitialState(traverser));

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
    MontiArcTraverser traverser = MontiArcMill.inheritanceTraverser();
    traverser.setSCStateHierarchyHandler(new NoSubstatesHandler());
    checker.addCoCo(new MaxOneInitialState(traverser));

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // automaton with two initial states
      arg("component InvalidComp1 { automaton { initial state s1; initial state s2; } }",
        MORE_THAN_ONE_INITIAL_STATE),
      // nested component type with an automaton with two initial states
      arg("component InvalidComp2 { component Inner { automaton { initial state s1; initial state s2; } } }",
        MORE_THAN_ONE_INITIAL_STATE),
      // mode automaton with two initial modes
      arg("component InvalidComp3 { mode automaton { initial mode s1 { } initial mode s2 { } } }",
        MORE_THAN_ONE_INITIAL_STATE),
      // mode automaton with two initial modes, alongside a regular automaton
      arg("component InvalidComp4 { mode automaton { initial mode s1 { } initial mode s2 { } } automaton { } }",
        MORE_THAN_ONE_INITIAL_STATE)
    );
  }
}
