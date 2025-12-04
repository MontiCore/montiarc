/* (c) https://github.com/MontiCore/monticore */

/**
 * Invalid model: The types of source and target of various connectors do not
 * match:
 * * The type boolean of the component's port i does not match the type int
 *   of the port i1 of subcomponent sub1
 * * The type boolean of the component's port i does not match the type int
 *   of the port i2 of subcomponent sub1
 * * The type boolean of the port o of subcomponent sub1 does not match the
 *   type int of port i1 of subcomponent sub2
 * * The type boolean of the port o of subcomponent sub1 does not match the
 *   type int of port i2 of subcomponent sub2
 * * The type boolean of the port o of subcomponent sub2 does not match the
 *   type int of the component's port o1
 * * The type boolean of the port o of subcomponent sub2 does not match the
 *   type int of the component's port o2
 * The subcomponents are defined by inner component Inner.
 */
component ConnectorMismatchType2 {

  port in boolean i;
  port out int o1, o2;

  component Inner {
    port in int i1, i2;
    port out boolean o;
  }

  Inner sub1, sub2;

  i -> sub1.i1, sub1.i2;

  sub1.o -> sub2.i1, sub2.i2;

  sub2.o -> o1, o2;

}
