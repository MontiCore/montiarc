/* (c) https://github.com/MontiCore/monticore */
package fieldNameIsNoReservedKeyword;

/**
 * Valid model, as long as 'foo' and 'bar' and used variations are no illegal keywords that must not be used as port
 * names.
 */
component FieldNamesAreLegal {
  int foo0 = 0;
  boolean bar = true;
}
