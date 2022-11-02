/* (c) https://github.com/MontiCore/monticore */
package correctSymbolTypeInheritance;

import correctSymbolTypeInheritance.superComponents.*;

/*
 * Valid model.
 */
component CorrectPortOverriding extends MultipleIncomingAndOutgoingPorts {
  port in double intIn,
       in String stringIn,
       in Person personIn,
       in Person studentIn,
      out byte intOut,
      out String stringOut,
      out Student personOut,
      out Student studentOut;
}