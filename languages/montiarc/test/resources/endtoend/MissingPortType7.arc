/* (c) https://github.com/MontiCore/monticore */

/**
 * Invalid model: The type Missing (simple name) of incoming port i of inner
 * component Inner is missing (the datatype cannot be resolved). The port is
 * target of a connector.
 */
component MissingPortType7 {

  port in int i;
  port out int o;

  component Inner {
    port in Missing i;
    port out int o;
  }

  Inner sub;

  i -> sub.i;
  sub.o -> o;
}
