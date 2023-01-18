/* (c) https://github.com/MontiCore/monticore */
package inheritance;

/**
 * Component with ports, variables, and parameters
 */
component Parent(int a, int b = 0) {
  port out int parentOut;

  int parentVar = a;
}
