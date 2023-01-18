/* (c) https://github.com/MontiCore/monticore */
package correctSymbolTypeInheritance;

import correctSymbolTypeInheritance.superComponents.*;

/*
 * Valid model.
 */
component NoPortOverriding extends MultipleIncomingAndOutgoingPorts {
  port in int intIn,
       in String stringIn,
       in Person personIn,
       in Student studentIn,
      out int intOut,
      out String stringOut,
      out Person personOut,
      out Student studentOut;
}
