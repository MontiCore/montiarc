/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.scbasis._cocos.MaxOneInitialState;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import montiarc.util.SCError;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * The class under test is {@link MaxOneInitialState}.
 */
public class MaxOneInitialStateTest extends MontiArcAbstractTest {

  @ParameterizedTest
  @ValueSource(strings = {
    // no automaton
    "component Comp1 { }",
    // automaton no states
    "component Comp2 {" +
      "automaton { } " +
      "}",
    // automaton no initial state (multiple states)
    "component Comp3 { " +
      "automaton { " +
      "state s1; " +
      "state s2; " +
      "} " +
      "}",
    // automaton with initial state
    "component Comp4 { " +
      "automaton { " +
      "initial state s; " +
      "} " +
      "}",
    // automaton with initial state (multiple states)
    "component Comp5 { " +
      "automaton { " +
      "initial state s1; " +
      "state s2; " +
      "state s3; " +
      "} " +
      "}",
    // inner automaton with initial state
    "component Comp6 { " +
      "component Inner { " +
      "automaton { " +
      "initial state s; " +
      "} " +
      "} " +
      "}",
    // two inner automata with initial state
    "component Comp7 { " +
      "component Inner1 { " +
      "automaton { " +
      "initial state s1; " +
      "} " +
      "} " +
      "component Inner2 { " +
      "automaton { " +
      "initial state s2; " +
      "} " +
      "} " +
      "}"
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP2Delegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP3Delegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new MaxOneInitialState());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  public void shouldReportError(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP2Delegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP3Delegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new MaxOneInitialState());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // automaton with two initial states
      arg("component Comp1 { " +
          "automaton { " +
          "initial state s1; " +
          "initial state s2; " +
          "} " +
          "}",
        SCError.MORE_THAN_ONE_INITIAL_STATE),
      // inner automaton with two initial states
      arg("component Comp2 { " +
          "component Inner { " +
          "automaton { " +
          "initial state s1; " +
          "initial state s2; " +
          "} " +
          "} " +
        "}",
        SCError.MORE_THAN_ONE_INITIAL_STATE)
    );
  }
}
