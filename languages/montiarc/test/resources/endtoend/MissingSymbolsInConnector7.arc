/* (c) https://github.com/MontiCore/monticore */

/**
 * Invalid model: The ports i and o, which are the target and source of
 * connectors, are missing (the ports cannot be resolved).
 */
component MissingSymbolsInConnector7 {

  component Inner {
    port in int i;
    port out int o;
  }

  Inner sub;

  i -> sub.i;
  sub.o -> o;

}
