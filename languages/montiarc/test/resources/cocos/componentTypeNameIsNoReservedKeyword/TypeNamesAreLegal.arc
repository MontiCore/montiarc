/* (c) https://github.com/MontiCore/monticore */
package componentTypeNameIsNoReservedKeyword;

/**
 * Valid model, as long as 'foo' and 'bar' and used variations are no illegal keywords that must not be used as port
 * names.
 */
component TypeNamesAreLegal {
  component Foo1 {}
  Foo1 foo1, foo2;

  component Bar1 bar1, bar2 {}
}