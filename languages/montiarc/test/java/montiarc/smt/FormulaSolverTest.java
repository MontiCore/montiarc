/* (c) https://github.com/MontiCore/monticore */
package montiarc.smt;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import montiarc.MontiArcTestBase;
import org.junit.jupiter.api.Test;
import variablearc.smt.FormulaSolver;
import variablearc.smt.exp2smt.IDeriveSMTExpr;

import static org.assertj.core.api.Assertions.assertThat;
import static variablearc.smt.FormulaSolver.Satisfiability.SATISFIABLE;
import static variablearc.smt.FormulaSolver.Satisfiability.UNKNOWN;
import static variablearc.smt.FormulaSolver.Satisfiability.UNSATISFIABLE;

/**
 * The class under test is {@link FormulaSolver}.
 */
public class FormulaSolverTest extends MontiArcTestBase {

  @Test
  public void shouldSimplifyConnectives() {
    // Given
    FormulaSolver solver = new FormulaSolver();
    Context z3 = solver.getContext();
    BoolExpr f = z3.mkBoolConst("f");
    BoolExpr g = z3.mkBoolConst("g");

    // When && Then
    assertThat(solver.and(f, solver.top(), solver.and(g, f)).toString()).isEqualTo("(and f g)");
    assertThat(solver.or(f, solver.bottom(), solver.or(g, f)).toString()).isEqualTo("(or f g)");
    assertThat(solver.and(f, solver.bottom()).isFalse()).isTrue();
    assertThat(solver.or(f, solver.top()).isTrue()).isTrue();
    assertThat(solver.not(solver.not(f))).isEqualTo(f);
    assertThat(solver.and().isTrue()).isTrue();
    assertThat(solver.or().isFalse()).isTrue();
  }

  @Test
  public void shouldDecideSatisfiability() {
    // Given
    FormulaSolver solver = new FormulaSolver();
    Context z3 = solver.getContext();
    BoolExpr a = z3.mkBoolConst("A");
    BoolExpr subA = z3.mkBoolConst("sub.A");

    // When && Then
    assertThat(solver.check(solver.and(a, solver.not(a)))).isEqualTo(UNSATISFIABLE);
    assertThat(solver.check(solver.and(subA, solver.not(a)))).isEqualTo(SATISFIABLE);
    assertThat(solver.check(solver.top())).isEqualTo(SATISFIABLE);
    assertThat(solver.check(solver.bottom())).isEqualTo(UNSATISFIABLE);
  }

  @Test
  public void shouldDecideSatisfiabilityForAllUnknownValuesSatisfyingTheAssumption() {
    // Given
    FormulaSolver unrestricted = new FormulaSolver();
    FormulaSolver restricted = new FormulaSolver();
    Context z3 = unrestricted.getContext();
    BoolExpr unknown = z3.mkBoolConst(IDeriveSMTExpr.UNKNOWN_MARKER + "d");
    BoolExpr x = z3.mkBoolConst("x");
    restricted.assume(unknown);

    // When && Then
    assertThat(unrestricted.check(unrestricted.and(unknown, x))).isEqualTo(UNKNOWN);
    assertThat(restricted.check(restricted.and(unknown, x))).isEqualTo(SATISFIABLE);
    assertThat(restricted.check(restricted.and(restricted.not(unknown), x))).isEqualTo(UNKNOWN);
  }

  @Test
  public void shouldDetectObscureConditions() {
    // Given
    FormulaSolver solver = new FormulaSolver();
    Context z3 = solver.getContext();
    BoolExpr obscure = z3.mkBoolConst(FormulaSolver.OBSCURE_MARKER + IDeriveSMTExpr.UNKNOWN_MARKER + "c");
    BoolExpr unknown = z3.mkBoolConst(IDeriveSMTExpr.UNKNOWN_MARKER + "d");

    // When && Then
    assertThat(solver.isObscure(solver.and(obscure, z3.mkBoolConst("x")))).isTrue();
    assertThat(solver.isObscure(unknown)).isFalse();
  }
}
