/* (c) https://github.com/MontiCore/monticore */
package variablearc.smt;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import org.junit.jupiter.api.Test;
import variablearc.VariableArcTestBase;
import variablearc.smt.exp2smt.IDeriveSMTExpr;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link FormulaSolver}.
 */
public class FormulaSolverUnknownValuesTest extends VariableArcTestBase {

  @Test
  public void shouldDecideSatisfiabilityForAllUnknownValues() {
    // Given
    FormulaSolver solver = new FormulaSolver();
    Context ctx = solver.getContext();
    BoolExpr x = ctx.mkBoolConst("x");
    BoolExpr u = ctx.mkBoolConst(IDeriveSMTExpr.UNKNOWN_MARKER + "u");
    IntExpr i = ctx.mkIntConst(IDeriveSMTExpr.UNKNOWN_MARKER + "i");
    IntExpr p = ctx.mkIntConst("p");

    // When && Then
    assertThat(solver.isSatisfiableForAllUnknownValues(ctx.mkAnd(x, ctx.mkNot(x)))).isFalse();
    assertThat(solver.isSatisfiableForAllUnknownValues(ctx.mkAnd(u, ctx.mkFalse()))).isFalse();
    assertThat(solver.isSatisfiableForAllUnknownValues(x)).isTrue();
    assertThat(solver.isSatisfiableForAllUnknownValues(ctx.mkAnd(ctx.mkOr(u, ctx.mkNot(u)), x))).isTrue();
    assertThat(solver.isSatisfiableForAllUnknownValues(ctx.mkEq(x, u))).isTrue();
    assertThat(solver.isSatisfiableForAllUnknownValues(ctx.mkAnd(u, x))).isFalse();
    assertThat(solver.isSatisfiableForAllUnknownValues(ctx.mkGt(i, ctx.mkInt(0)))).isFalse();
    assertThat(solver.isSatisfiableForAllUnknownValues(ctx.mkAnd(ctx.mkEq(p, i), ctx.mkGt(p, ctx.mkInt(0))))).isFalse();
  }

  @Test
  public void shouldDecideSatisfiabilityForAllUnknownValuesUnderQuantifiers() {
    // Given
    FormulaSolver solver = new FormulaSolver();
    Context ctx = solver.getContext();
    BoolExpr h = ctx.mkBoolConst("h");
    BoolExpr u = ctx.mkBoolConst(IDeriveSMTExpr.UNKNOWN_MARKER + "u");
    BoolExpr hiddenAndUnknown = ctx.mkExists(new Expr<?>[]{h}, ctx.mkAnd(h, u), 1, null, null, null, null);

    // When && Then
    assertThat(solver.constantsOf(hiddenAndUnknown)).isEqualTo(Set.of(u));
    assertThat(solver.isSatisfiableForAllUnknownValues(ctx.mkNot(hiddenAndUnknown))).isFalse();
  }

  @Test
  public void shouldOnlyMarkUnknownsAsUnknown() {
    // Given
    Context ctx = new FormulaSolver().getContext();

    // When && Then
    assertThat(IDeriveSMTExpr.isUnknown(ctx.mkBoolConst(IDeriveSMTExpr.UNKNOWN_MARKER + "u"))).isTrue();
    assertThat(IDeriveSMTExpr.isUnknown(ctx.mkBoolConst("x"))).isFalse();
    assertThat(IDeriveSMTExpr.isUnknown(ctx.mkTrue())).isFalse();
  }
}
