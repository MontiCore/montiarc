/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcstructuraltypes._ast.ASTMCIntersectionType;
import de.monticore.types.typeparameters._ast.ASTTypeParameter;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._symboltable.TransitiveScopeSetter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the union and intersection types introduced by MCStructuralTypes.
 */
class StructuralTypeCheckTest extends MontiArcTestBase {

  @ParameterizedTest
  @MethodSource("structuralPortTypes")
  void shouldCreateStructuralPortTypes(String type, String expectedType) {
    ASTMACompilationUnit compilationUnit = compile("component StructuralPort<T, U> { port in " + type + " value; }");

    SymTypeExpression actualType = compilationUnit.getArcComponentType().getSymbol()
      .getPort("value")
      .orElseThrow()
      .getType();

    assertThat(Log.getFindings()).isEmpty();
    assertThat(actualType.printFullName()).isEqualTo(expectedType);
  }

  @ParameterizedTest
  @MethodSource("structuralExpressions")
  void shouldDeriveStructuralExpressionTypes(String expression, String expectedType) {
    ASTExpression ast = parseInStructuralScope(expression);

    SymTypeExpression actualType = TypeCheck3.typeOf(ast);

    // accesses are lifted over unions, e.g., pair[1] yields 'double | double'
    assertThat(Log.getFindings()).isEmpty();
    assertThat(SymTypeRelations.normalize(actualType).printFullName()).isEqualTo(expectedType);
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // not every A is an A & B
    "intersection = a",
    // union does not support the operators of its members
    "choice + 1",
    "choice == 1",
    // a union is not assignable to one of its members
    "x = choice",
    "a = aOrB"
  })
  void shouldRejectInvalidStructuralExpressions(String expression) {
    ASTExpression ast = parseInStructuralScope(expression);

    SymTypeExpression actualType = TypeCheck3.typeOf(ast);

    assertThat(actualType.isObscureType()).isTrue();
    assertThat(Log.getFindings()).isNotEmpty();
  }

  @Disabled("MontiCore lifts assignments over the alternatives of a union target (TypeVisitorLifting.liftDefault), "
    + "hence 'choice = 1' is checked as 'boolean = 1', too")
  @ParameterizedTest
  @ValueSource(strings = {
    "choice = 1",
    "choice = true",
    "choice = choice",
    "aOrB = a",
    "aOrB = b",
    "pair = (true, 1.0)"
  })
  void shouldAssignToUnions(String expression) {
    ASTExpression ast = parseInStructuralScope(expression);

    TypeCheck3.typeOf(ast);

    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  void shouldParseIntersectionTypeParameterBound() {
    ASTMACompilationUnit compilationUnit = compile("component Bounded<A, B, T extends A & B> { }");

    ASTTypeParameter typeParameter = compilationUnit.getArcComponentType().getHead()
      .getTypeParameters()
      .getTypeParameter(2);

    // MCStructuralTypes makes 'A & B' a single intersection type instead of two separate bounds
    assertThat(Log.getFindings()).isEmpty();
    assertThat(typeParameter.getMCTypeList()).singleElement().isInstanceOf(ASTMCIntersectionType.class);
    assertThat(typeParameter.getSymbol().getSuperTypesList())
      .singleElement()
      .extracting(SymTypeExpression::printFullName)
      .isEqualTo("Bounded.A & Bounded.B");
  }

  private ASTExpression parseInStructuralScope(String expression) {
    ASTMACompilationUnit compilationUnit = compile(
      "component StructuralScope<A, B>(A a, B b) {" +
        "  int | boolean choice = 1;" +
        "  A | B aOrB = a;" +
        "  A & B intersection = a;" +
        "  (int | boolean, double) pair = (1, 2.0);" +
        "  int x = 0;" +
        "  double y = 0.0;" +
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

  private static Stream<Arguments> structuralPortTypes() {
    return Stream.of(
      Arguments.of("int | boolean", "boolean | int"),
      Arguments.of("boolean | int", "boolean | int"),
      Arguments.of("(int | boolean)", "boolean | int"),
      Arguments.of("int | boolean | String", "String | boolean | int"),
      Arguments.of("T | U", "StructuralPort.T | StructuralPort.U"),
      Arguments.of("T & U", "StructuralPort.T & StructuralPort.U"),
      // intersection binds stronger than union
      Arguments.of("int | boolean & String", "(String & boolean) | int"),
      Arguments.of("(int | boolean) & String", "(boolean | int) & String"),
      // structural types nest with tuples
      Arguments.of("(int | boolean, double)", "((boolean | int), double)"),
      Arguments.of("(int, boolean) | double", "(int, boolean) | double")
    );
  }

  private static Stream<Arguments> structuralExpressions() {
    return Stream.of(
      Arguments.of("choice", "boolean | int"),
      Arguments.of("aOrB", "StructuralScope.A | StructuralScope.B"),
      Arguments.of("intersection", "StructuralScope.A & StructuralScope.B"),
      Arguments.of("pair[0]", "boolean | int"),
      Arguments.of("pair[1]", "double"),
      Arguments.of("pair[1] + 1.0", "double"),
      Arguments.of("y = pair[1]", "double"),
      // an intersection is assignable to each of its members
      Arguments.of("a = intersection", "StructuralScope.A"),
      Arguments.of("b = intersection", "StructuralScope.B")
    );
  }
}
