/* (c) https://github.com/MontiCore/monticore */
package components;

/**
 * Invalid model: fields directly depend on each other (cyclic field dependency).
 */
component CircularFieldDependency1 {
  int a = b;
  int b = a;
}
