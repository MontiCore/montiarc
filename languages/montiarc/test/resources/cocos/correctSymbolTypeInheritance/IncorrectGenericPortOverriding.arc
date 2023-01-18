/* (c) https://github.com/MontiCore/monticore */
package correctSymbolTypeInheritance;

import correctSymbolTypeInheritance.superComponents.*;

/*
 * Invalid model.
 */
component IncorrectGenericPortOverriding extends GenericIncomingAndOutgoingPort<int>() {
  port in byte tIn,
      out double tOut;
}
