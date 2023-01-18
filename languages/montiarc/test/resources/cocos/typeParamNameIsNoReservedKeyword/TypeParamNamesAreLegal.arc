/* (c) https://github.com/MontiCore/monticore */
package typeParamNameIsNoReservedKeyword;

/**
 * Valid model, as long as 'foo' and 'bar' and used variations are no illegal keywords that must not be used as port
 * names.
 */
component TypeParamNamesAreLegal <
  Foo,
  Bar extends int,
  Bar2 extends int & boolean,
  Foo2
> { }
