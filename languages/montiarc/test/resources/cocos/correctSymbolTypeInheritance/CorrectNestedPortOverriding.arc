/* (c) https://github.com/MontiCore/monticore */
package correctSymbolTypeInheritance;

import correctSymbolTypeInheritance.superComponents.*;

/*
 * Valid model.
 */
component CorrectNestedPortOverriding  {
  component A extends MultipleIncomingAndOutgoingPorts {
    port in double intIn;
  }

  component B extends A {
    port in double intIn,
         in String stringIn,
         in Person personIn,
         in Person studentIn,
        out byte intOut,
        out String stringOut,
        out Student personOut,
        out Student studentOut;
  }
}
