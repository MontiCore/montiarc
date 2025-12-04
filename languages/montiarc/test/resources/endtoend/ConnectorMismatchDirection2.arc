/* (c) https://github.com/MontiCore/monticore */

/**
 * Invalid model: The direction of source and target of various connectors
 * mismatch:
 * * The component's port i1 is used as a target
 * * The component's port i2 is used as a target
 * * The component's port o is used as a source
 * * The port i of subcomponent sub is used as a source
 * * The port o1 of subcomponent sub is used as a target
 * * The port o2 of subcomponent sub is used as a target
 * The subcomponent sub is defined by inner component Inner.
 */
component ConnectorMismatchDirection2 {

  port in int i1, i2;
  port out int o;

  component Inner {
    port in int i;
    port out int o1, o2;
  }

  Inner sub;

  o -> sub.o1, sub.o2;
  sub.i -> i1, i2;

}
