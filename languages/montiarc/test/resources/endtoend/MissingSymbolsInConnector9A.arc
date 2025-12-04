/* (c) https://github.com/MontiCore/monticore */

/**
 * Invalid model: The ports i and o of subcomponent sub, which are the target
 * and source of connectors, are missing (the ports cannot be resolved).
 *
 * The subcomponent is defined by external component MissingSymbolsInConnector9B.
 */
component MissingSymbolsInConnector9A {

  port in int i;
  port out int o;

  MissingSymbolsInConnector9B sub;

  i -> sub.i;
  sub.o -> o;

}
