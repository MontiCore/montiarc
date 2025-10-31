/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos.util;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import com.microsoft.z3.Z3Exception;
import variablearc.evaluation.ExpressionSolver;

import java.util.List;

public class ExpressionSolverService {

  private static final ExpressionSolver expressionSolver;
  private static final Solver solver;
  private static final Context ctx;
  private static Status lastStatus;
  private static Model lastModel;

  static {
    try {
      expressionSolver = new ExpressionSolver();
      ctx = expressionSolver.getContext();
      solver = expressionSolver.getContext().mkSolver();
      lastStatus = Status.UNKNOWN;

    } catch (Z3Exception e) {
      throw new RuntimeException("Failed to initialize Z3", e);
    }
  }

  public static synchronized Status solve(List<BoolExpr> constraints) {
    solver.push();
    solver.add(constraints.toArray(new BoolExpr[0]));
    lastStatus = solver.check();
    if (lastStatus == Status.SATISFIABLE) {
      lastModel = solver.getModel();
    } else {
      lastModel = null;
    }
    solver.pop();
    return lastStatus;
  }

  public static Model getModel() {
    if (lastStatus == Status.SATISFIABLE && lastModel != null) {
      return lastModel;
    } else {
      throw new IllegalStateException("Model is not available. Last result was: " + lastStatus);
    }
  }

  public static Context getContext() {
    return ctx;
  }

  public static Solver getSolver() {
    return solver;
  }

  public static ExpressionSolver getExpressionSolver() {
    return expressionSolver;
  }

  public void close() {
    ctx.close();
  }
}
