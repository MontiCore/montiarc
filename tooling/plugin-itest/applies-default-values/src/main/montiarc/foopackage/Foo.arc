/* (c) https://github.com/MontiCore/monticore */
package foopackage;

import barpackage.Bar;

component Foo {
  port
    <<sync>> in int inPort,
    <<sync>> out int outPort;

  Bar bar;

  inPort -> bar.inPort;
  bar.outPort -> outPort;

}