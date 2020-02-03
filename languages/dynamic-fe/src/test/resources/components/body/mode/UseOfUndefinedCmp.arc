/* (c) https://github.com/MontiCore/monticore */
package components.body.mode;

/*
 * Invalid model.
 */
component UseOfUndefinedCmp {
  
  port
    in Integer num1,
    in Integer num2,
	out Integer result;
  
  component Adder ad;

  modeautomaton {
    mode Adding {
      use ad;
    }

    mode Multiplying {
      // Error: m does not exist.
      use m;
    }

    mode Multiplying, Logging {
      // Error: doesnotexist does not exist.
       use doesnotexist;
    }

    initial Adding;
  
    Adding -> Multiplying [true];
    Multiplying -> Adding [true];
  }
  
}