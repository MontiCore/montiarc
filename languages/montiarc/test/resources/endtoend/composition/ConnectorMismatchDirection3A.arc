/* (c) https://github.com/MontiCore/monticore */
package composition;

/**
 * Invalid model: The direction of source and target of various connectors
 * mismatch:
 * * The component's port i is used as a target
 * * The component's port o is used as a source
 * * The port i of subcomponent sub is used as a source
 * * The port o of subcomponent sub is used as a target
 * The subcomponent sub is defined by external component ConnectorMismatchDirection3B.
 */
component ConnectorMismatchDirection3A {

  port in int i;
  port out int o;

  ConnectorMismatchDirection3B sub;

  o -> sub.o;
  sub.i -> i;

}
