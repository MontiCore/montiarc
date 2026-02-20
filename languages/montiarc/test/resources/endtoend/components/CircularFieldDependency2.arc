/* (c) https://github.com/MontiCore/monticore */
package components;

/**
 * Invalid model: fields indirectly depend on each other (cyclic field dependency).
 */
component CircularFieldDependency2 {
  int a = c;
  int b = a;
  int c = b;
}
