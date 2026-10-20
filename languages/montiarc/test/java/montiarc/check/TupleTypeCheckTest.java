/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._symboltable.TransitiveScopeSetter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class TupleTypeCheckTest extends MontiArcTestBase {

  @ParameterizedTest
  @MethodSource("tupleExpressions")
  void shouldDeriveTupleExpressionTypes(String expression, String expectedType) throws IOException {
    ASTMACompilationUnit compilationUnit = compile("component TupleScope {}");
    ASTExpression ast = MontiArcMill.parser().parse_StringExpression(expression).orElseThrow();
    new TransitiveScopeSetter().setScope(ast, compilationUnit.getArcComponentType().getSpannedScope());

    SymTypeExpression actualType = TypeCheck3.typeOf(ast);

    assertThat(actualType.printFullName()).isEqualTo(expectedType);
  }

  @ParameterizedTest
  @MethodSource("tupleIndexAccesses")
  void shouldDeriveTupleIndexAccessTypes(String expression, String expectedType) {
    ASTExpression ast = parseInTupleScope(expression);

    SymTypeExpression actualType = TypeCheck3.typeOf(ast);

    assertThat(Log.getFindings()).isEmpty();
    assertThat(actualType.printFullName()).isEqualTo(expectedType);
  }

  @ParameterizedTest
  @MethodSource("invalidTupleIndexAccesses")
  void shouldRejectInvalidTupleIndexAccesses(String expression, String expectedError) {
    ASTExpression ast = parseInTupleScope(expression);

    SymTypeExpression actualType = TypeCheck3.typeOf(ast);

    assertThat(actualType.isObscureType()).isTrue();
    assertThat(getLoggedErrorCodes()).containsExactly(expectedError);
  }

  @ParameterizedTest
  @MethodSource("tuplePortTypes")
  void shouldCreateTuplePortTypes(String type, String expectedType) {
    ASTMACompilationUnit compilationUnit = compile("component TuplePort { port in " + type + " value; }");

    SymTypeExpression actualType = compilationUnit.getArcComponentType().getSymbol()
      .getPort("value")
      .orElseThrow()
      .getType();

    assertThat(actualType.printFullName()).isEqualTo(expectedType);
  }

  private static ASTExpression parseInTupleScope(String expression) {
    ASTMACompilationUnit compilationUnit = compile(
      "component TupleScope {" +
        "  (int, boolean) pair = (1, true);" +
        "  ((int, boolean), double) nested = ((1, true), 2.0);" +
        "  int i = 0;" +
        "}"
    );
    try {
      ASTExpression ast = MontiArcMill.parser().parse_StringExpression(expression).orElseThrow();
      new TransitiveScopeSetter().setScope(ast, compilationUnit.getArcComponentType().getSpannedScope());
      return ast;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static Stream<Arguments> tupleIndexAccesses() {
    return Stream.of(
      Arguments.of("pair[0]", "int"),
      Arguments.of("pair[1]", "boolean"),
      Arguments.of("nested[0]", "(int, boolean)"),
      Arguments.of("nested[0][0]", "int"),
      Arguments.of("nested[0][1]", "boolean"),
      Arguments.of("nested[1]", "double"),
      Arguments.of("(1, true)[1]", "boolean"),
      Arguments.of("pair[0] + 1", "int"),
      Arguments.of("!pair[1]", "boolean")
    );
  }

  private static Stream<Arguments> invalidTupleIndexAccesses() {
    return Stream.of(
      Arguments.of("pair[2]", "0xFD3F0"),
      Arguments.of("nested[0][2]", "0xFD3F0"),
      Arguments.of("pair[i]", "0xFD3F1")
    );
  }

  private static Stream<Arguments> tupleExpressions() {
    return Stream.of(
      Arguments.of("(1, true)", "(int, boolean)"),
      Arguments.of("((1, true), 2.0)", "((int, boolean), double)"),
      Arguments.of("(1)", "int")
    );
  }

  private static Stream<Arguments> tuplePortTypes() {
    return Stream.of(
      Arguments.of("(int, boolean)", "(int, boolean)"),
      Arguments.of("((int, boolean), double)", "((int, boolean), double)"),
      Arguments.of("(int)", "int")
    );
  }
}
