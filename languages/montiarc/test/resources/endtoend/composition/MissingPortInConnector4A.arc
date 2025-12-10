/* (c) https://github.com/MontiCore/monticore */
package composition;

/**
 * Invalid model: The ports i and o of subcomponent sub, which are the target
 * and source of connectors, are missing (the ports cannot be resolved).
 *
 * The subcomponent is defined by external component MissingPortInConnector4B,
 * which is resolved via a serialized symbol table.
 */
component MissingPortInConnector4A {

  port in int i;
  port out int o;

  MissingPortInConnector4B sub;

  i -> sub.i;
  sub.o -> o;

}
