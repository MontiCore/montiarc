/* (c) https://github.com/MontiCore/monticore */
package parser;

component ComponentInstanceArguments {
  int x = 2;
  int z = 3;
  component A(int y) {
  }

  A a1(5);
  A a2(x);
  A a3(y=x);
  A a4(y=z=x);
}
