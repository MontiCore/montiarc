/* (c) https://github.com/MontiCore/monticore */

/**
 * Invalid model: The component indirectly extends itself (cyclic inheritance).
 */
component CircularInheritanceWithComposition2B extends CircularInheritanceWithComposition2C {

  port in int i1;
  port out int o1;

}
