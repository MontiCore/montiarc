/* (c) https://github.com/MontiCore/monticore */

/**
 * Invalid model: The type Missing of ports i and o of subcomponent sub,
 * defined by inner component Inner, are missing (the datatype cannot be
 * resolved). The ports are a source and a target of a connector.
 */
component MissingSymbolsInConnector3 {

  port in int i;
  port out int o;

  component Inner {
    port in Missing i;
    port out Missing o;
  }

  Inner sub;

  i -> sub.i;
  sub.o -> o;
}
