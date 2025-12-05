/* (c) https://github.com/MontiCore/monticore */
package composition;

/**
 * Invalid model: Non-synchronous sources are connected to synchronous targets 
 * in various connectors:
 * * The component's non-synchronous port i is connected to synchronous ports
 *   i1 and i2 of subcomponent sub1.
 * * The non-synchronous port o of subcomponent sub1 is connected to
 *   synchronous ports i1 and i2 of subcomponent sub2.
 * * The non-synchronous port o of subcomponent sub2 is connected to the
 *   component's synchronous ports o1 and o2.
 * The subcomponents are defined by inner component Inner.
 */
component ConnectorMismatchTiming2 {

  port in int i;
  port sync out int o1, o2;

  component Inner {
    port sync in int i1, i2;
    port out int o;
  }

  Inner sub1, sub2;

  i -> sub1.i1, sub1.i2;

  sub1.o -> sub2.i1, sub2.i2;

  sub2.o -> o1, o2;

}
