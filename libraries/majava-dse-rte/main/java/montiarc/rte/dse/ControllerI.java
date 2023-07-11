/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.dse;

import com.microsoft.z3.Model;

import java.util.Set;
import java.util.function.Function;

public interface ControllerI<In, Out> extends TestControllerI {

  /**
   * initializes a new controller and initializes the corresponding TestController
   */
  void init();

  /**
   * starts the dse run for the given function
   *
   * @param initialInput initial input for the dse run
   * @param evalModel    initial model for the solver
   * @param sut          function to be analyzed by DSE
   * @return list of input output pairs
   * @throws Exception if the specified function sut is null
   */
  ResultI<In, Out> startTest(In initialInput, Function<Model, In> evalModel,
                             Function<In, Out> sut) throws Exception;

}
