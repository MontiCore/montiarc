/* (c) https://github.com/MontiCore/monticore */
package portNameIsNoReservedKeyword;

/**
 * Valid model, as long as 'foo' and 'bar' and used variations are no illegal keywords that must not be used as port
 * names.
 */
component PortNamesAreLegal {
  port in int foo0, foo1,
       out int foo2, foo3,
       in int foo4,
       in int foo5,
       out int foo6;

  port out int bar0, bar1;
}