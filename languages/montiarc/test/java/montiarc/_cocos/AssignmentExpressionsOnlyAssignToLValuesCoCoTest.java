/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTAssignmentExpressionCoCo;
import de.monticore.expressions.assignmentexpressions.cocos.AssignmentExpressionsOnlyAssignToLValuesCoCo;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import montiarc.util.MCError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Holds test for {@link AssignmentExpressionsOnlyAssignToLValuesCoCo}
 */
public class AssignmentExpressionsOnlyAssignToLValuesCoCoTest extends MontiArcTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    // no assignment expression
    "component Comp1 { }",
    // assignment expression with parameter
    "component Comp2(int p) { " +
      "automaton { " +
      "initial state s; " +
      "s -> s / { " +
      "p = 1; " +
      "}; " +
      "}}",
    // assignment expression with field
    "component Comp3 { " +
      "int f = 1;" +
      "automaton { " +
      "initial state s; " +
      "s -> s / { " +
      "f = 1; " +
      "}; " +
      "}}",
    // assignment expression with local variable
    "component Comp4 { " +
      "automaton { " +
      "initial state s; " +
      "s -> s / { " +
      "int v = 1; " +
      "v = 2; " +
      "}; " +
      "}}",
    // assignment expression with port
    "component Comp5 { " +
      "port out int o;" +
      "automaton { " +
      "initial state s; " +
      "s -> s / { " +
      "o = 2; " +
      "}; " +
      "}}",
    // increment expression with parameter
    "component Comp6(int p) { " +
      "automaton { " +
      "initial state s; " +
      "s -> s / { " +
      "p++; " +
      "}; " +
      "}}",
    // increment expression with field
    "component Comp7 { " +
      "int f = 1;" +
      "automaton { " +
      "initial state s; " +
      "s -> s / { " +
      "f++; " +
      "}; " +
      "}}",
    // increment expression with local variable
    "component Comp8 { " +
      "automaton { " +
      "initial state s; " +
      "s -> s / { " +
      "int v = 1; " +
      "v++; " +
      "}; " +
      "}}",
    // decrement expression with parameter
    "component Comp9(int p) { " +
      "automaton { " +
      "initial state s; " +
      "s -> s / { " +
      "p--; " +
      "}; " +
      "}}",
    // decrement expression with field
    "component Comp10 { " +
      "int f = 1;" +
      "automaton { " +
      "initial state s; " +
      "s -> s / { " +
      "f--; " +
      "}; " +
      "}}",
    // decrement expression with local variable
    "component Comp11 { " +
      "automaton { " +
      "initial state s; " +
      "s -> s / { " +
      "int v = 1; " +
      "v--; " +
      "}; " +
      "}}",
    // prefix increment expression with parameter
    "component Comp12(int p) { " +
      "automaton { " +
      "initial state s; " +
      "s -> s / { " +
      "++p; " +
      "}; " +
      "}}",
    // prefix increment expression with field
    "component Comp13 { " +
      "int f = 1;" +
      "automaton { " +
      "initial state s; " +
      "s -> s / { " +
      "++f; " +
      "}; " +
      "}}",
    // prefix increment expression with local variable
    "component Comp14 { " +
      "automaton { " +
      "initial state s; " +
      "s -> s / { " +
      "int v = 1; " +
      "++v; " +
      "}; " +
      "}}",
    // prefix decrement expression with parameter
    "component Comp15(int p) { " +
      "automaton { " +
      "initial state s; " +
      "s -> s / { " +
      "--p; " +
      "}; " +
      "}}",
    // prefix decrement expression with field
    "component Comp16 { " +
      "int f = 1;" +
      "automaton { " +
      "initial state s; " +
      "s -> s / { " +
      "--f; " +
      "}; " +
      "}}",
    // prefix decrement expression with local variable
    "component Comp17 { " +
      "automaton { " +
      "initial state s; " +
      "s -> s / { " +
      "int v = 1; " +
      "--v; " +
      "}; " +
      "}}",
    // assignment expression in compute
    "component Comp18(int p) {" +
      "port out int o;" +
      "int f = 1;" +
      "compute { " +
      "int v = 0;" +
      "p = 1; f = 1; v = 1; o = 1;" +
      "p++; f++; v++; o++;" +
      "p--; f--; v--; o--;" +
      "++p; ++f; ++v; ++o;" +
      "--p; --f; --v; --o;" +
      "} " +
      "}",
    // nested assignment expression
    "component Comp19 { " +
      "port out int o;" +
      "automaton { " +
      "initial state s; " +
      "s -> s / { " +
      "int v = 1; " +
      "o = v = 1; " +
      "o = v++;" +
      "o = v--;" +
      "o = ++v;" +
      "o = --v;" +
      "o = o = v = v;" +
      "}; " +
      "}}"
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo((AssignmentExpressionsASTAssignmentExpressionCoCo) new AssignmentExpressionsOnlyAssignToLValuesCoCo());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  public void shouldReportError(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo((AssignmentExpressionsASTAssignmentExpressionCoCo) new AssignmentExpressionsOnlyAssignToLValuesCoCo());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // assignment expression with literal value
      arg("component Comp1 { " +
          "automaton { " +
          "initial state s; " +
          "s -> s / { " +
          "0 = 1; " +
          "}; " +
          "}}",
        MCError.EXPRESSION_LVALUE),
      // inc expression with literal value
      arg("component Comp2 { " +
          "automaton { " +
          "initial state s; " +
          "s -> s / { " +
          "0++; " +
          "}; " +
          "}}",
        MCError.EXPRESSION_LVALUE),
      // dec expression with literal value
      arg("component Comp3 { " +
          "automaton { " +
          "initial state s; " +
          "s -> s / { " +
          "0--; " +
          "}; " +
          "}}",
        MCError.EXPRESSION_LVALUE),
      // prefix inc expression with literal value
      arg("component Comp4 { " +
          "automaton { " +
          "initial state s; " +
          "s -> s / { " +
          "--0; " +
          "}; " +
          "}}",
        MCError.EXPRESSION_LVALUE),
      // prefix dec expression with literal value
      arg("component Comp5 { " +
          "automaton { " +
          "initial state s; " +
          "s -> s / { " +
          "--0; " +
          "}; " +
          "}}",
        MCError.EXPRESSION_LVALUE),
      // nested assignment expression with literal value
      arg("component Comp6 { " +
          "automaton { " +
          "initial state s; " +
          "s -> s / { " +
          "int i = 1++; " +
          "}; " +
          "}}",
        MCError.EXPRESSION_LVALUE),
      // multiple nested assignment expression with literal value
      arg("component Comp7 { " +
          "automaton { " +
          "initial state s; " +
          "s -> s / { " +
          "1++ = 1 = 1; " +
          "}; " +
          "}}",
        MCError.EXPRESSION_LVALUE,
        MCError.EXPRESSION_LVALUE,
        MCError.EXPRESSION_LVALUE),
      // assignment expression with function
      arg("component Comp8 { " +
          "automaton { " +
          "initial state s; " +
          "s -> s / { " +
          "func() = 1; " +
          "}; " +
          "}}",
        MCError.EXPRESSION_LVALUE)
    );
  }
}
