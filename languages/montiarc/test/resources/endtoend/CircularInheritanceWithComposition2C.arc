/* (c) https://github.com/MontiCore/monticore */

/**
 * Invalid model: The component indirectly extends itself (cyclic inheritance).
 */
component CircularInheritanceWithComposition2C extends CircularInheritanceWithComposition2B {

  port in int i2;
  port out int o2;

}
