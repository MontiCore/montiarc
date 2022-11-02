/* (c) https://github.com/MontiCore/monticore */
package correctSymbolTypeInheritance;

import correctSymbolTypeInheritance.superComponents.*;

/*
 * Invalid model.
 */
component IncorrectPortOverriding extends MultipleIncomingAndOutgoingPorts {
  port in byte intIn,
       in int stringIn,
       in Student personIn,
       in int studentIn,
      out double intOut,
       in Student stringOut, // Wrong type and direction
      out int personOut,
      out Person studentOut;
}