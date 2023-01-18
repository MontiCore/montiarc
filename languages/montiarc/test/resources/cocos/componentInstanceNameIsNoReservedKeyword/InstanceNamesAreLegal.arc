/* (c) https://github.com/MontiCore/monticore */
package componentInstanceNameIsNoReservedKeyword;

/**
 * Valid model, as long as 'foo' and 'bar' and used variations are no illegal keywords that must not be used as port
 * names.
 */
component InstanceNamesAreLegal {
  component Inner foo1, foo2 {}
  Inner bar1, bar2;
}
