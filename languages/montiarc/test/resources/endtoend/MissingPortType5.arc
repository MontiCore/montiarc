/* (c) https://github.com/MontiCore/monticore */

/**
 * Invalid model: The type Missing (simple name) of incoming port i is missing
 * (the datatype cannot be resolved). The port is source of a connector.
 */
component MissingPortType5 {

  port in Missing i;
  port out int o;

  component Inner {
    port in int i;
    port out int o;
  }

  Inner sub;

  i -> sub.i;
  sub.o -> o;
}
