/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.solver;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Tests for {@link ConstraintSolver}
 */
public class ConstraintSolverTest {

  @Test
  void testUnsatisfiableConstraints() {
    try (Context context = new Context()) {
      Solver solver = context.mkSolver();
      BoolExpr a = context.mkBoolConst("a");

      solver.add(new BoolExpr[]{a, context.mkNot(a)});

      SolverResult result = new ConstraintSolver(context, solver, "Root", Map.of("a", a)).trySolve();

      assertInstanceOf(SolverResult.Unsatisfiable.class, result);
    }
  }

  @Test
  void testDeadFeatureIsReported() {
    try (Context context = new Context()) {
      Solver solver = context.mkSolver();
      BoolExpr a = context.mkBoolConst("a");
      BoolExpr b = context.mkBoolConst("b");

      solver.add(new BoolExpr[]{context.mkNot(a)});

      SolverResult result = new ConstraintSolver(context, solver, "Root", Map.of("a", a, "b", b)).trySolve();

      SolverResult.Solved solved = assertInstanceOf(SolverResult.Solved.class, result);
      assertEquals(List.of("a"), solved.deadFeatures());
      assertFalse(solved.incomplete());
    }
  }
}
