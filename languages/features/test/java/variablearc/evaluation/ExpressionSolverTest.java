/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.IntExpr;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import variablearc.evaluation.exp2smt.IDeriveSMTExpr;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link ExpressionSolver}.
 */
public class ExpressionSolverTest {

  protected Context ctx;

  @BeforeEach
  public void setUpContext() {
    ctx = new Context();
  }

  @AfterEach
  public void closeContext() {
    ctx.close();
  }

  protected BoolExpr unknownBool(String name) {
    return ctx.mkBoolConst(IDeriveSMTExpr.UNKNOWN_MARKER + name);
  }

  protected IntExpr unknownInt(String name) {
    return ctx.mkIntConst(IDeriveSMTExpr.UNKNOWN_MARKER + name);
  }

  protected boolean isSatisfiableForAllUnknownValues(BoolExpr formula) {
    return ExpressionSolver.isSatisfiableForAllUnknownValues(ctx, ctx.mkSolver(), formula);
  }

  @Test
  public void isUnknownShouldOnlyMatchUnknowns() {
    assertThat(IDeriveSMTExpr.isUnknown(unknownBool("u"))).isTrue();
    assertThat(IDeriveSMTExpr.isUnknown(ctx.mkBoolConst("x"))).isFalse();
    assertThat(IDeriveSMTExpr.isUnknown(ctx.mkTrue())).isFalse();
  }

  @Test
  public void unsatisfiableFormulaShouldNotBeSatisfiableForAllUnknownValues() {
    BoolExpr x = ctx.mkBoolConst("x");
    assertThat(isSatisfiableForAllUnknownValues(ctx.mkAnd(x, ctx.mkNot(x)))).isFalse();
    assertThat(isSatisfiableForAllUnknownValues(ctx.mkFalse())).isFalse();
    assertThat(isSatisfiableForAllUnknownValues(ctx.mkAnd(unknownBool("u"), ctx.mkFalse()))).isFalse();
  }

  @Test
  public void formulaWithoutUnknownsShouldBeSatisfiableForAllUnknownValues() {
    assertThat(isSatisfiableForAllUnknownValues(ctx.mkBoolConst("x"))).isTrue();
    assertThat(isSatisfiableForAllUnknownValues(ctx.mkTrue())).isTrue();
  }

  @Test
  public void formulaIndependentOfUnknownsShouldBeSatisfiableForAllUnknownValues() {
    BoolExpr u = unknownBool("u");
    assertThat(isSatisfiableForAllUnknownValues(ctx.mkAnd(ctx.mkOr(u, ctx.mkNot(u)), ctx.mkBoolConst("x")))).isTrue();
    assertThat(isSatisfiableForAllUnknownValues(ctx.mkOr(u, ctx.mkTrue()))).isTrue();
  }

  @Test
  public void formulaAdaptingToUnknownsShouldBeSatisfiableForAllUnknownValues() {
    assertThat(isSatisfiableForAllUnknownValues(ctx.mkEq(ctx.mkBoolConst("x"), unknownBool("u")))).isTrue();
  }

  @Test
  public void formulaDependingOnUnknownsShouldNotBeSatisfiableForAllUnknownValues() {
    assertThat(isSatisfiableForAllUnknownValues(ctx.mkAnd(unknownBool("u"), ctx.mkBoolConst("x")))).isFalse();
    assertThat(isSatisfiableForAllUnknownValues(ctx.mkGt(unknownInt("i"), ctx.mkInt(0)))).isFalse();
    IntExpr p = ctx.mkIntConst("p");
    assertThat(isSatisfiableForAllUnknownValues(ctx.mkAnd(ctx.mkEq(p, unknownInt("i")), ctx.mkGt(p, ctx.mkInt(0))))).isFalse();
  }

  @Test
  public void unsatisfiableForSomeUnknownValuesShouldBeTheNegation() {
    assertThat(ExpressionSolver.isUnsatisfiableForSomeUnknownValues(ctx, ctx.mkSolver(),
      ctx.mkAnd(unknownBool("u"), ctx.mkBoolConst("x")))).isTrue();
    assertThat(ExpressionSolver.isUnsatisfiableForSomeUnknownValues(ctx, ctx.mkSolver(),
      ctx.mkBoolConst("x"))).isFalse();
  }
}
