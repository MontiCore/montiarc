/* (c) https://github.com/MontiCore/monticore */
package components.body.mode;

/*
 * Invalid model.
 */
component SubCompPortConnectedTwice {

  port
    in Integer num1,
    in Integer num2,
	out Integer result;

  component Adder ad;
  component Multiplier m;

  Integer myIntVar;

  modeautomaton {
    mode Adding {
      use ad, m;
      connect num1 -> ad.summand1;
      connect num2 -> ad.summand1;
      connect ad.sum -> result;
    }

    mode Multiplying {
      use m;
      connect num1 -> m.factor1;
      connect num2 -> m.factor2;
      connect m.product -> result;
    }

    mode Adding, Multiplying {
      connect num2 -> logger.log;
    }

    mode ForwardNum1 {
      connect num1 -> result;
    }

    initial Adding;

    Adding -> Multiplying [num1 == 1];
    Multiplying -> Adding;
  }

}