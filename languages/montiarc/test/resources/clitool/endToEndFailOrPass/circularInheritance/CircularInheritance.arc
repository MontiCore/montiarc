/* (c) https://github.com/MontiCore/monticore */
package circularInheritance;

/*
 * Invalid model. The component extends itself
 */
component CircularInheritance extends CircularInheritance {
  port in int pInInt;
}