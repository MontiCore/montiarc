/* (c) https://github.com/MontiCore/monticore */
package composition;

/**
 * Invalid model: The type Missing of ports i and o is missing (the datatype
 * cannot be resolved). The ports are and source and a target of a connector.
 */
component MissingSymbolsInConnector2 {

  port in Missing i;
  port out Missing o;

  component Inner {
    port in int i;
    port out int o;
  }

  Inner sub;

  i -> sub.i;
  sub.o -> o;
}
