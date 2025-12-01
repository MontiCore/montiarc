/* (c) https://github.com/MontiCore/monticore */

/**
 * Invalid model: The type Missing (simple name) of outgoing port o is missing
 * 1(the datatype cannot be resolved). The port is target of a connector.
 */
component MissingPortType6 {

  port in int i;
  port out Missing o;

  component Inner {
    port in int i;
    port out int o;
  }

  Inner sub;

  i -> sub.i;
  sub.o -> o;
}
