/* (c) https://github.com/MontiCore/monticore */
package components.body.mode;

/*
 * Invalid model.
 */
component NoInitialMode {

  port
    in Integer num1,
    in Integer num2,
	out Integer result;

  component Adder ad;

  modeautomaton {
    mode Adding {}
    mode Multiplying {}

    Adding -> Multiplying;
  }
  // 1 Error: No initial mode

}