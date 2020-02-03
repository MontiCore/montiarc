/* (c) https://github.com/MontiCore/monticore */
package components.body.mode;

/*
 * Invalid model.
 */
component ModeNameLowercase {

  port
    in Integer num1,
    in Integer num2,
	out Integer result;

  component Adder ad;
  component Multiplier m;

  Integer myIntVar;

  modeautomaton {
    mode adding {
      use ad;
      connect num1 -> ad.summand1;
      connect num2 -> ad.summand2;
      connect ad.sum -> result;
    }

    mode multiplying {
      use m;
      connect num1 -> m.factor1;
      connect num2 -> m.factor2;
      connect m.product -> result;
    }

    mode adding, multiplying {
      connect num2 -> logger.log;
    }

    mode forwardNum1 {
      connect num1 -> result;
    }

    initial adding;

    adding -> multiplying [num1 == 1];
    multiplying -> adding;
  }

}