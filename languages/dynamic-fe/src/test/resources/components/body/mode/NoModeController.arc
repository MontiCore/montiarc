/* (c) https://github.com/MontiCore/monticore */
package components.body.mode;

/*
 * Invalid model.
 * TODO Add test
 */
component NoModeTransitions {

  port
    in Integer num1,
    in Integer num2,
	out Integer result;

  component Adder ad;
  component Multiplier m;

  modeautomaton {
    mode Adding {
      connect num1 -> ad.summand1;
      connect num2 -> ad.summand2;
      connect ad.sum -> result;
    }

    mode Multiplying {
      connect num1 -> m.factor1;
      connect num2 -> m.factor2;
      connect m.product -> result;
    }

    initial Adding;
  }
}