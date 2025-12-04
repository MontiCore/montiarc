/* (c) https://github.com/MontiCore/monticore */

/**
 * Invalid model: The direction of source and target of various connectors
 * mismatch:
 * * The component's port i is used as a target
 * * The component's port o is used as a source
 * * The port i of subcomponent sub is used as a source
 * * The port o of subcomponent sub is used as a target
 * The subcomponent sub is defined by inner component Inner.
 */
component ConnectorMismatchDirection1 {

  port in int i;
  port out int o;

  component Inner {
    port in int i;
    port out int o;
  }

  Inner sub;

  o -> sub.o;
  sub.i -> i;

}
