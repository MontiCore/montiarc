/* (c) https://github.com/MontiCore/monticore */
package composition;

/**
 * Invalid model: The type Missing of ports i and o of subcomponent sub,
 * defined by external component MissingPortTypeInConnector4B, resolved from a
 * serialized symbol table, is missing (the datatype cannot be resolved).
 * The ports are a source and a target of a connector. As the serialized symbol
 * table is not checked and as the source and target of the connector mismatch,
 * mismatching connector types should be reported instead.
 */
component MissingPortTypeInConnector4A {

  port in int i;
  port out int o;

  MissingPortTypeInConnector4B sub;

  i -> sub.i;
  sub.o -> o;

}
