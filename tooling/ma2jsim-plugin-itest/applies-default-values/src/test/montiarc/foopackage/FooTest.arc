/* (c) https://github.com/MontiCore/monticore */
package foopackage;

component FooTest {
  port
    <<sync>> in int inPort,
    <<sync>> out int outPort;

  Foo foo;

  inPort -> foo.inPort;
  foo.outPort -> outPort;

}