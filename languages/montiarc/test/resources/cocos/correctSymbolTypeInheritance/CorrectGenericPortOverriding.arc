/* (c) https://github.com/MontiCore/monticore */
package correctSymbolTypeInheritance;

import correctSymbolTypeInheritance.superComponents.*;

/*
 * Valid model.
 */
component CorrectGenericPortOverriding extends GenericIncomingAndOutgoingPort<int>() {
  port in int tIn,
      out int tOut;
}
