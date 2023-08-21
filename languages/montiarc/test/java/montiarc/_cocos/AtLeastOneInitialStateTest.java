/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.scbasis._cocos.AtLeastOneInitialState;
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
 * The class under test is {@link AtLeastOneInitialState}.
 */
public class AtLeastOneInitialStateTest extends MontiArcAbstractTest {

  @ParameterizedTest
  @ValueSource(strings = {
    // no automaton
    "component Comp1 { }",
    // automaton with initial state
    "component Comp2 { " +
      "automaton { " +
      "initial state s; " +
      "} " +
      "}",
    // automaton with two initial states
    "component Comp3 { " +
      "automaton { " +
      "initial state s1; " +
      "initial state s2; " +
      "} " +
      "}",
    // inner automaton with initial states
    "component Comp4 { " +
      "component Inner { " +
      "automaton { " +
      "initial state s; " +
      "} " +
      "} " +
      "}",
    //
    // mode automaton with initial state
    "component Comp5 { " +
      "mode automaton { " +
      "initial mode s { } " +
      "} " +
      "}",
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new AtLeastOneInitialState(MontiArcMill.inheritanceTraverser()));

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
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new AtLeastOneInitialState(MontiArcMill.inheritanceTraverser()));

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // automaton no initial state
      arg("component Comp1 { " +
          "automaton { } " +
          "}",
        SCError.NO_INITIAL_STATE),
      // inner automaton no initial state
      arg("component Comp2 { " +
          "component Inner { " +
          "automaton { } " +
          "} " +
          "}",
        SCError.NO_INITIAL_STATE),
      // inner with initial state and outer automaton
      arg("component Comp3 { " +
          "automaton { } " +
          "component Inner { " +
          "automaton { " +
          "initial state s; " +
          "} " +
          "} " +
          "}",
        SCError.NO_INITIAL_STATE),
      // outer with initial state and inner automaton
      arg("component Comp4 { " +
          "automaton { " +
          "initial state s; " +
          "} " +
          "component Inner { " +
          "automaton { } " +
          "} " +
          "}",
        SCError.NO_INITIAL_STATE),
      // mode automaton no initial state
      arg("component Comp5 { " +
          "mode automaton { } " +
          "}",
        SCError.NO_INITIAL_STATE),
      // mode automaton no initial state and automaton
      arg("component Comp6 { " +
          "mode automaton { } " +
          "automaton { " +
          "initial state s; " +
          "} "+
          "}",
        SCError.NO_INITIAL_STATE),
      // automaton no initial state and mode automaton
      arg("component Comp7 { " +
          "automaton { } " +
          "mode automaton { " +
          "initial mode s { } " +
          "} "+
          "}",
        SCError.NO_INITIAL_STATE)
    );
  }
}
