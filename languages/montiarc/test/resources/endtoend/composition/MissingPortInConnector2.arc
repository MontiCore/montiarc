/* (c) https://github.com/MontiCore/monticore */
package composition;

/**
 * Invalid model: The ports i and o of subcomponent sub, which are the target
 * and source of connectors, are missing (the ports cannot be resolved).
 *
 * The subcomponent is defined by inner component Inner.
 */
component MissingPortInConnector2 {

  port in int i;
  port out int o;

  component Inner { }

  Inner sub;

  i -> sub.i;
  sub.o -> o;

}
