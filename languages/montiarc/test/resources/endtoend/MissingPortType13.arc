/* (c) https://github.com/MontiCore/monticore */

/**
 * Invalid model: The type Missing (simple name) of incoming port i of
 * subcomponent sub is missing (the datatype cannot be resolved). The component
 * sub is of type MissingPortType1, which is defined externally. The port is
 * target of a connector.
 */
component MissingPortType13 {

  port in int i;
  port out int o;

  MissingPortType1 sub;

  i -> sub.i;
  sub.o -> o;
}
