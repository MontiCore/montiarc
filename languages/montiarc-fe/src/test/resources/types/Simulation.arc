/* (c) https://github.com/MontiCore/monticore */
package types;

import types.SimulationData.*;

/*
 * Valid model.
 */
component Simulation {
  port in MyMessage msgs;
  
  component ErrorFilter {
    port in  MyMessage msgs,
         out MyError errors;
  }
  
  component MessageDisplay {
    port in MyMessage msgs;
  }
  
  component MessageDisplay errorDisplay;
  
  connect ErrorFilter.errors -> errorDisplay.msgs;
}
