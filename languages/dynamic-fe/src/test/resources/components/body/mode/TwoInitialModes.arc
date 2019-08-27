/* (c) https://github.com/MontiCore/monticore */
package components.body.mode;

/*
 * Invalid model.
 */
component TwoInitialModes {
  
  port
    in Integer num1,
    in Integer num2,
	out Integer result;
  
  component Adder ad;

  modeautomaton {
    mode Adding {}

    mode Multiplying {}

    // Two errors: One error for each initial statement.
    initial Adding;
    initial Multiplying;

    Adding -> Multiplying;
  }
}