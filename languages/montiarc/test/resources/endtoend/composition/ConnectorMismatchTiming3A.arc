/* (c) https://github.com/MontiCore/monticore */
package composition;

/**
 * Invalid model: Non-synchronous sources are connected to synchronous targets 
 * in various connectors:
 * * The component's non-synchronous port i is connected to synchronous port i
 *   of subcomponent sub1.
 * * The non-synchronous port o of subcomponent sub1 is connected to
 *   synchronous port i of subcomponent sub2.
 * * The non-synchronous port o of subcomponent sub2 is connected to the
 *   component's synchronous port o.
 * The subcomponents are defined by external component ConnectorMismatchTiming3B.
 */
component ConnectorMismatchTiming3A {

  port in int i;
  port sync out int o;

  ConnectorMismatchTiming3B sub1, sub2;

  i -> sub1.i;

  sub1.o -> sub2.i;

  sub2.o -> o;

}
