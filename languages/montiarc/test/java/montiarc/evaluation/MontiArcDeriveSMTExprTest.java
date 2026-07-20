/* (c) https://github.com/MontiCore/monticore */
package montiarc.evaluation;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Symbol;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._symboltable.IMontiArcScope;
import montiarc._symboltable.TransitiveScopeSetter;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.function.Function;
import java.util.stream.Stream;

import static de.monticore.symbols.basicsymbols.BasicSymbolsMill.INT;
import static de.monticore.types.check.SymTypeExpressionFactory.createPrimitive;
import static montiarc.MontiArcMill.parser;
import static montiarc.MontiArcMillTOP.variableSymbolBuilder;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link MontiArcDeriveSMTExpr}.
 */
class MontiArcDeriveSMTExprTest extends MontiArcTestBase {

  @ParameterizedTest
  @MethodSource("boolExpr")
  void testToBool(@NotNull String expr, @NotNull String smtLibExpr) throws IOException {
    try (Context ctx = new Context()) {
      // Given
      ASTExpression ast = parser().parse_StringExpression(expr).orElseThrow();

      IMontiArcScope scope = MontiArcMill.scope();
      scope.add(MontiArcMill.arcFeatureSymbolBuilder().setName("f1").setEnclosingScope(scope).build());
      scope.add(MontiArcMill.arcFeatureSymbolBuilder().setName("f2").setEnclosingScope(scope).build());
      scope.add(MontiArcMill.arcFeatureSymbolBuilder().setName("f3").setEnclosingScope(scope).build());
      scope.add(variableSymbolBuilder().setName("i1").setType(createPrimitive(INT)).setEnclosingScope(scope).build());
      scope.add(variableSymbolBuilder().setName("i2").setType(createPrimitive(INT)).setEnclosingScope(scope).build());
      scope.add(variableSymbolBuilder().setName("i3").setType(createPrimitive(INT)).setEnclosingScope(scope).build());

      TransitiveScopeSetter setScope = new TransitiveScopeSetter();
      setScope.setScope(ast, scope);

      Symbol[] symbols = new Symbol[]{
        ctx.mkSymbol("f1"),
        ctx.mkSymbol("f2"),
        ctx.mkSymbol("f3"),
      };

      FuncDecl<?>[] functions = new FuncDecl<?>[]{
        ctx.mkBoolConst("f1").getFuncDecl(),
        ctx.mkBoolConst("f2").getFuncDecl(),
        ctx.mkBoolConst("f3").getFuncDecl(),
      };

      MontiArcDeriveSMTExpr toSmt = new MontiArcDeriveSMTExpr(ctx);

      BoolExpr expected = ctx.parseSMTLIB2String("(assert " + smtLibExpr + ")", null, null, symbols, functions)[0];

      // When
      BoolExpr actual = toSmt.toBool(ast).orElseThrow();

      // Then
      assertThat(actual).isEqualTo(expected);
    }
  }

  @ParameterizedTest
  @MethodSource("intExpr")
  void testToInt(@NotNull String expr, @NotNull String smtLibExpr) throws IOException {
    try (Context ctx = new Context()) {
      // Given
      ASTExpression ast = parser().parse_StringExpression(expr).orElseThrow();

      IMontiArcScope scope = MontiArcMill.scope();
      scope.add(variableSymbolBuilder().setName("i1").setType(createPrimitive(INT)).setEnclosingScope(scope).build());
      scope.add(variableSymbolBuilder().setName("i2").setType(createPrimitive(INT)).setEnclosingScope(scope).build());

      TransitiveScopeSetter setScope = new TransitiveScopeSetter();
      setScope.setScope(ast, scope);

      Symbol[] symbols = new Symbol[]{
        ctx.mkSymbol("i1"),
        ctx.mkSymbol("i2")
      };

      FuncDecl<?>[] functions = new FuncDecl<?>[]{
        ctx.mkIntConst("i1").getFuncDecl(),
        ctx.mkIntConst("i2").getFuncDecl()
      };

      MontiArcDeriveSMTExpr toSmt = new MontiArcDeriveSMTExpr(ctx);

      smtLibExpr = "(assert (> " + smtLibExpr + " 0))";
      BoolExpr expected = ctx.parseSMTLIB2String(smtLibExpr, null, null, symbols, functions)[0];

      // When
      IntExpr actual = toSmt.toInt(ast).orElseThrow();

      // Then
      assertThat(actual).isEqualTo(expected.getArgs()[0]);
    }
  }

  @ParameterizedTest
  @MethodSource("bitExpr")
  void testToInt4Bit(@NotNull String expr, @NotNull Int expected) throws IOException {
    try (Context ctx = new Context()) {
      // Given
      ASTExpression ast = parser().parse_StringExpression(expr).orElseThrow();

      IMontiArcScope scope = MontiArcMill.scope();
      scope.add(variableSymbolBuilder().setName("i1").setType(createPrimitive(INT)).setEnclosingScope(scope).build());
      scope.add(variableSymbolBuilder().setName("i2").setType(createPrimitive(INT)).setEnclosingScope(scope).build());

      TransitiveScopeSetter setScope = new TransitiveScopeSetter();
      setScope.setScope(ast, scope);

      MontiArcDeriveSMTExpr toSmt = new MontiArcDeriveSMTExpr(ctx);

      // When
      IntExpr actual = toSmt.toInt(ast).orElseThrow();

      // Then
      assertThat(actual).isEqualTo(expected.expr().apply(ctx));
    }
  }

  static Stream<Arguments> boolExpr() {
    return Stream.of(
      arg("true", "true"),
      arg("false", "false"),
      arg("f1", "f1"),
      arg("f2", "f2"),
      arg("f3", "f3"),
      arg("!true", "(not true)"),
      arg("~true", "(not true)"),
      arg("f1 ? f2 : f3", "(ite f1 f2 f3)"),
      arg("1 <= 2", "(<= 1 2)"),
      arg("1 >= 2", "(>= 1 2)"),
      arg("1 < 2", "(< 1 2)"),
      arg("1 > 2", "(> 1 2)"),
      arg("f1 == f2", "(= f1 f2)"),
      arg("f1 != f2", "(not (= f1 f2))"),
      arg("f1 && f2", "(and f1 f2)"),
      arg("f1 || f2", "(or f1 f2)"),
      arg("f1 & f2", "(and f1 f2)"),
      arg("f1 | f2", "(or f1 f2)"),
      arg("f1 ^ f2", "(xor f1 f2)"),
      arg("(f1 && !f2) || (f2 && !f1)", "(or (and f1 (not f2)) (and f2 (not f1)))"),
      arg("0.2f > 0", "(> 0.20000000298023224 0)"),
      arg("0.2F > 1L", "(> 0.20000000298023224 1)")
    );
  }

  static Stream<Arguments> intExpr() {
    return Stream.of(
      arg("1", "1"),
      arg("i1", "i1"),
      arg("i2", "i2"),
      arg("i1 + i2", "(+ i1 i2)"),
      arg("i1 - i2", "(- i1 i2)"),
      arg("i1 * i2", "(* i1 i2)"),
      arg("i1 / i2", "(div i1 i2)")
    );
  }

  static Stream<Arguments> bitExpr() {
    return Stream.of(
      arg("(i1 & i2)", new Int((Context ctx) -> ctx.mkBV2Int(
        ctx.mkBVAND(
          ctx.mkInt2BV(64, ctx.mkIntConst("i1")),
          ctx.mkInt2BV(64, ctx.mkIntConst("i2"))
        ), true))
      ),
      arg("(i1 | i2)", new Int((Context ctx) -> ctx.mkBV2Int(
        ctx.mkBVOR(
          ctx.mkInt2BV(64, ctx.mkIntConst("i1")),
          ctx.mkInt2BV(64, ctx.mkIntConst("i2"))
        ), true))
      ),
      arg("(i1 ^ i2)", new Int((Context ctx) -> ctx.mkBV2Int(
        ctx.mkBVXOR(
          ctx.mkInt2BV(64, ctx.mkIntConst("i1")),
          ctx.mkInt2BV(64, ctx.mkIntConst("i2"))
        ), true))
      ),
      arg("(i1 >> i2)", new Int((Context ctx) -> ctx.mkBV2Int(
        ctx.mkBVASHR(
          ctx.mkInt2BV(64, ctx.mkIntConst("i1")),
          ctx.mkInt2BV(64, ctx.mkIntConst("i2"))
        ), true))
      ),
      arg("(i1 >>> i2)", new Int((Context ctx) -> ctx.mkBV2Int(
        ctx.mkBVLSHR(
          ctx.mkInt2BV(64, ctx.mkIntConst("i1")),
          ctx.mkInt2BV(64, ctx.mkIntConst("i2"))
        ), true))
      ),
      arg("(i1 << i2)", new Int((Context ctx) -> ctx.mkBV2Int(
        ctx.mkBVSHL(
          ctx.mkInt2BV(64, ctx.mkIntConst("i1")),
          ctx.mkInt2BV(64, ctx.mkIntConst("i2"))
        ), true))
      )
    );
  }

  record Int(Function<Context, IntExpr> expr) {}
}
