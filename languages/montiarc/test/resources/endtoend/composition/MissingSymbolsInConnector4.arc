/* (c) https://github.com/MontiCore/monticore */
package composition;

import components.MissingPortType3;

/**
 * Invalid model: The type Missing of ports i and o of subcomponent sub,
 * defined by external component MissingPortType3, is missing (the datatype
 * cannot be resolved). The ports are a source and a target of a connector.
 */
component MissingSymbolsInConnector4 {

  port in int i;
  port out int o;

  MissingPortType3 sub;

  i -> sub.i;
  sub.o -> o;
}
