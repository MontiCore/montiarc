/* (c) https://github.com/MontiCore/monticore */
package parameterNameIsNoReservedKeyword;

/**
 * Valid model, as long as 'foo' and 'bar' and used variations are no illegal keywords that must not be used as port
 * names.
 */
component ParameterNamesAreLegal (
  int foo,
  float foo2,
  double bar = 23.2,
  boolean bar2 = true
) { }
