/* (c) https://github.com/MontiCore/monticore */

/**
 * Invalid model: The ports i and o of subcomponent sub, which are the target
 * and source of connectors, are missing (the ports cannot be resolved).
 *
 * The subcomponent is defined by external component MissingSymbolsInConnector10B,
 * which is resolved via a serialized symbol table.
 */
component MissingSymbolsInConnector10A {

  port in int i;
  port out int o;

  MissingSymbolsInConnector10B sub;

  i -> sub.i;
  sub.o -> o;

}
