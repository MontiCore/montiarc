/* (c) https://github.com/MontiCore/monticore */
package composition;

/**
 * Invalid model: The types of source and target of various connectors do not
 * match:
 * * The type boolean of the component's port i does not match the type int
 *   of the port i of subcomponent sub1
 * * The type boolean of the port o of subcomponent sub1 does not match the
 *   type int of port i of subcomponent sub2
 * * The type boolean of the port o of subcomponent sub2 does not match the
 *   type int of the component's port o
 * The subcomponents are defined by external component ConnectorMismatchType4B,
 * which is resolved via a serialized symbol table.
 */
component ConnectorMismatchType4A {

  port in boolean i;
  port out int o;

  ConnectorMismatchType4B sub1, sub2;

  i -> sub1.i;

  sub1.o -> sub2.i;

  sub2.o -> o;

}
