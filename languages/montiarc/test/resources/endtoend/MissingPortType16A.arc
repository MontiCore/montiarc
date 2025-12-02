/* (c) https://github.com/MontiCore/monticore */

/**
 * Invalid model: The type Missing (simple name) of the outgoing port o of
 * subcomponent sub is missing (the datatype cannot be resolved). The component
 * sub is of type MissingPortType16B, which is defined in a serialized symbol
 * table. The port is target of a connector.
 */
component MissingPortType16A {

  port in int i;
  port out int o;

  MissingPortType16B sub;

  i -> sub.i;
  sub.o -> o;

}
