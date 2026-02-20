/* (c) https://github.com/MontiCore/monticore */
package components;

/**
 * Invalid model: fields directly depend on each other (cyclic field dependency).
 */
component CircularFieldDependency3 {
  int a = 1 + b;
  int b = 2 + a;
}
