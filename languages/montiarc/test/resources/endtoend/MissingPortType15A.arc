/* (c) https://github.com/MontiCore/monticore */

/**
 * Invalid model: The type Missing (simple name) of the incoming port i of
 * subcomponent sub is missing. The component sub is of type MissingPortType15B,
 * which is defined in a serialized symbol table. The type int of port i
 * does not match the missing type Missing of port i of subcomponent sub.
 */
component MissingPortType15A {

  port in int i;
  port out int o;

  MissingPortType15B sub;

  i -> sub.i;
  sub.o -> o;

}
