/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.solver;

import montiarc.arc2fd.model.FDData;

import java.util.List;

/**
 * The outcome of {@link ConstraintSolver#trySolve()}.
 */
public sealed interface SolverResult {

  /**
   * The constraints cannot be satisfied.
   */
  record Unsatisfiable() implements SolverResult { }

  /**
   * The solver cannot determine whether the constraints are satisfiable.
   */
  record Unknown() implements SolverResult { }

  /**
   * The constraints are satisfiable.
   *
   * @param fd           The computed feature diagram
   * @param deadFeatures Features that can never be selected
   * @param incomplete   <code>true</code>, iff some solver queries returned unknown, i.e., the constraints may be
   *                     stricter than the feature diagram
   */
  record Solved(FDData fd, List<String> deadFeatures, boolean incomplete) implements SolverResult { }
}
