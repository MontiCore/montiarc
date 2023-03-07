/* (c) https://github.com/MontiCore/monticore */
package correctSymbolTypeInheritance;

import correctSymbolTypeInheritance.superComponents.*;

/*
 * Invalid model.
 */
component IncorrectNestedPortOverriding {
  component A extends MultipleIncomingAndOutgoingPorts {
    port in byte intIn;
  }

  component B extends A {
    port in double intIn,
         in int stringIn,
         in Student personIn,
         in int studentIn,
        out double intOut,
         in Student stringOut, // Wrong type and direction
        out int personOut,
        out Person studentOut;
  }
}
