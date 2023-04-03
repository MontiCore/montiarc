/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.sctransitions4code._cocos.TransitionPreconditionsAreBoolean;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.check.MontiArcTypeCalculator;
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
 * The class under test is {@link TransitionPreconditionsAreBoolean}.
 */
public class TransitionPreconditionIsBooleanTest extends MontiArcAbstractTest {

  @ParameterizedTest
  @ValueSource(strings = {
    // no automaton
    "component Comp1 { }",
    // no transition
    ("component Comp2 { " +
      "automaton { } " +
      "}"),
    // no precondition
    "component Comp3 { " +
      "automaton { " +
      "initial state s; " +
      "s -> s; " +
      "} " +
      "}",
    // single boolean precondition
    "component Comp4 { " +
      "automaton { " +
      "initial state s; " +
      "s -> s [true]; " +
      "} " +
      "}",
    // two boolean preconditions
    "component Comp5 { " +
      "automaton { " +
      "initial state s; " +
      "s -> s [true]; " +
      "s -> s [false]; " +
      "} " +
      "}",
    // boolean precondition (access port)
    "component Comp6 { " +
      "port in boolean i; " +
      "automaton { " +
      "initial state s; " +
      "s -> s [i]; " +
      "} " +
      "}",
    // boolean precondition (access parameter)
    "component Comp7(boolean p) { " +
      "automaton { " +
      "initial state s; " +
      "s -> s[p]; " +
      "} " +
      "}",
    // boolean precondition (access variable)
    "component Comp8 { " +
      "boolean v = true; " +
      "automaton { " +
      "initial state s; " +
      "s -> s [v]; " +
      "} " +
      "}"
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.symbolTableCompleterDelegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new TransitionPreconditionsAreBoolean(new MontiArcTypeCalculator()));

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
    MontiArcMill.symbolTableCompleterDelegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new TransitionPreconditionsAreBoolean(new MontiArcTypeCalculator()));

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // single non-boolean precondition
      arg("component Comp1 { " +
          "automaton { " +
          "initial state s; " +
          "s -> s [1]; " +
          "} " +
          "}",
        SCError.PRECONDITION_NOT_BOOLEAN),
      // one boolean and one non-boolean precondition
      arg("component Comp2 { " +
          "automaton { " +
          "initial state s; " +
          "s -> s [true]; " +
          "s -> s [1]; " +
          "} " +
          "}",
        SCError.PRECONDITION_NOT_BOOLEAN),
      // two non-boolean preconditions
      arg("component Comp3 { " +
          "automaton { " +
          "initial state s; " +
          "s -> s [1]; " +
          "s -> s [1]; " +
          "} " +
          "}",
        SCError.PRECONDITION_NOT_BOOLEAN,
        SCError.PRECONDITION_NOT_BOOLEAN),
      // single non-boolean precondition (port access)
      arg("component Comp4 { " +
          "port in int i; " +
          "automaton { " +
          "initial state s; " +
          "s -> s [i]; " +
          "} " +
          "}",
        SCError.PRECONDITION_NOT_BOOLEAN),
      // single non-boolean precondition (parameter access)
      arg("component Comp5(int p) { " +
          "automaton { " +
          "initial state s; " +
          "s -> s [p]; " +
          "} " +
          "}",
        SCError.PRECONDITION_NOT_BOOLEAN),
      // single non-boolean precondition (variable access)
      arg("component Comp6 { " +
          "int v = 1; " +
          "automaton { " +
          "initial state s; " +
          "s -> s [v]; " +
          "} " +
          "}",
        SCError.PRECONDITION_NOT_BOOLEAN)
    );
  }
}
