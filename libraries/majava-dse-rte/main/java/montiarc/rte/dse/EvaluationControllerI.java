/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.dse;

import java.util.Set;

public interface EvaluationControllerI {

  /**
   * this function is needed for the evaluation to get information about the executed dse run
   *
   * @return the visited states of the controller
   */
  Set<StatesList> getVisitedStates();

  /**
   * this function is needed for the evaluation to get information about the number of solver calls
   */
  int getSolverCalls();

  /**
   * this function is needed for the evaluation to get information about the number of satisfiable branching conditions.
   */
  int getSatPaths();
}
