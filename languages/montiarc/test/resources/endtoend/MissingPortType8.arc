/* (c) https://github.com/MontiCore/monticore */

/**
 * Invalid model: The type Missing (simple name) of outgoing port o of inner
 * component Inner is missing (the datatype cannot be resolved). The port is
 * source of a connector.
 */
component MissingPortType8 {

  port in int i;
  port out int o;

  component Inner {
    port in int i;
    port out Missing o;
  }

  Inner sub;

  i -> sub.i;
  sub.o -> o;
}
