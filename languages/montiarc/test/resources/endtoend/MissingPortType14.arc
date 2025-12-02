/* (c) https://github.com/MontiCore/monticore */

/**
 * Invalid model: The type Missing (simple name) of outgoing port o of
 * subcomponent sub is missing (the datatype cannot be resolved). The component
 * sub is of type MissingPortType2, which is defined externally. The port is
 * source of a connector.
 */
component MissingPortType14 {

  port in int i;
  port out int o;

  MissingPortType2 sub;

  i -> sub.i;
  sub.o -> o;
}
